package com.qubacy.geoqq.domain.mate.request

import android.net.Uri
import com.qubacy.geoqq.common.BitmapMockContext
import com.qubacy.geoqq.common.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.mate.request.operation.MateRequestAnswerProcessedOperation
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

class MateRequestUseCaseTest {
    companion object {
        init {
            BitmapMockContext.mockBitmapFactory()
            UriMockContext.mockUri()
        }
    }

    private lateinit var mMateRequestsUseCase: MateRequestsUseCase

    private lateinit var mMateRequestsStateAtomicRef: AtomicReference<MateRequestsState?>

    private fun emitOriginalState(originalMateRequestsState: MateRequestsState) = runBlocking {
        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("mStateFlow")
            .apply { isAccessible = true }
        val stateFlow = stateFlowFieldReflection.get(mMateRequestsUseCase)
                as MutableStateFlow<MateRequestsState>

        stateFlow.emit(originalMateRequestsState)
    }

    private fun initMateRequestsUseCase(
        getTokensResult: GetTokensResult = GetTokensResult(String(), String()),
        getMateRequestsResult: GetMateRequestsResult = GetMateRequestsResult(listOf()),
        usersResults: GetUsersByIdsResult = GetUsersByIdsResult(listOf(), true),
        imagesResults: GetImagesResult = GetImagesResult(mapOf(), true),
        originalMateRequestsState: MateRequestsState? = null
    ) = runBlocking {
        val errorDataRepository = Mockito.mock(ErrorDataRepository::class.java)

        val tokenDataRepository = Mockito.mock(TokenDataRepository::class.java)

        Mockito.`when`(tokenDataRepository.getTokens()).thenReturn(getTokensResult)

        val imageDataRepository = Mockito.mock(ImageDataRepository::class.java)

        Mockito.`when`(imageDataRepository.getImages(Mockito.anyList(), Mockito.anyString()))
            .thenAnswer { invocation ->
                imagesResults
            }

        val userDataRepository = Mockito.mock(UserDataRepository::class.java)

        Mockito.`when`(userDataRepository.getUsersByIds(
            Mockito.anyList(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())
        ).thenAnswer { invocation -> usersResults }
        Mockito.`when`(userDataRepository.resultFlow).thenAnswer { MutableSharedFlow<Result>() }

        val mateRequestDataRepository = Mockito.mock(MateRequestDataRepository::class.java)

        Mockito.`when`(mateRequestDataRepository.getMateRequests(Mockito.anyString(), Mockito.anyInt()))
            .thenAnswer { getMateRequestsResult }
        Mockito.`when`(mateRequestDataRepository.resultFlow).thenAnswer { MutableSharedFlow<Result>() }

        mMateRequestsUseCase = MateRequestsUseCase(
            errorDataRepository, tokenDataRepository, mateRequestDataRepository,
            userDataRepository, imageDataRepository
        )

        val processResultMethodReflection = ConsumingUseCase::class.memberFunctions
            .find { it.name == "processResult" }!!.apply {
                isAccessible = true
            }
        if (originalMateRequestsState != null)
            emitOriginalState(originalMateRequestsState)

        Mockito.`when`(mateRequestDataRepository.getMateRequests(Mockito.anyString(), Mockito.anyInt()))
            .thenAnswer {
                runBlocking {
                    processResultMethodReflection.callSuspend(mMateRequestsUseCase, getMateRequestsResult)
                }
            }

        mMateRequestsStateAtomicRef = AtomicReference(null)

        GlobalScope.launch(Dispatchers.IO) {
            mMateRequestsUseCase.stateFlow.collect {
                if (it == null) return@collect

                mMateRequestsStateAtomicRef.set(it)
            }
        }
    }

    @Before
    fun setup() {
        initMateRequestsUseCase()
    }

    @Test
    fun getTwoMateRequestsTest() {
        val imagesResults = GetImagesResult(
            mapOf(
                0L to Uri.parse(String())
            ),
            false
        )
        val usersResults = GetUsersByIdsResult(
            listOf(
                DataUser(0, "test", "test", 0L, true),
                DataUser(1, "test", "test", 0L, true)
            ),
            false
        )
        val requestsResults = GetMateRequestsResult(
            listOf(
                DataMateRequest(0, 1),
                DataMateRequest(0, 2)
            )
        )

        initMateRequestsUseCase(
            imagesResults = imagesResults,
            usersResults = usersResults,
            getMateRequestsResult = requestsResults
        )

        mMateRequestsUseCase.getMateRequests(requestsResults.mateRequests.size)

        while (mMateRequestsStateAtomicRef.get() == null) { }

        val gottenMateRequestsState = mMateRequestsStateAtomicRef.get()

        for (gottenMateRequest in gottenMateRequestsState!!.mateRequests) {
            Assert.assertNotNull(requestsResults.mateRequests.find { it.id == gottenMateRequest.id })
        }
    }

    @Test
    fun answerMateRequestTest() {
        val mateRequestId = 0L
        val isAccepted = true

        initMateRequestsUseCase()

        runBlocking {
            mMateRequestsUseCase.answerMateRequest(mateRequestId, isAccepted)

            while (mMateRequestsStateAtomicRef.get() == null) { }

            val gottenState = mMateRequestsStateAtomicRef.get()

            Assert.assertNotNull(gottenState!!.newOperations.find {
                it::class == MateRequestAnswerProcessedOperation::class })
        }
    }
}