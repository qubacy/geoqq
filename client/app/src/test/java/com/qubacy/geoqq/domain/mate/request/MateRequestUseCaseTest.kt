package com.qubacy.geoqq.domain.mate.request

import android.net.Uri
import com.qubacy.geoqq.common.util.mock.BitmapMockContext
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.util.generator.DataMateRequestGeneratorUtility
import com.qubacy.geoqq.data.common.util.generator.DataUserGeneratorUtility
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
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
        getMateRequestsResultHashMap: HashMap<Int, GetMateRequestsResult> = hashMapOf(0 to GetMateRequestsResult(listOf(), true)),
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

        Mockito.`when`(mateRequestDataRepository.getMateRequests(
            Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean())
        ).thenAnswer { getMateRequestsResultHashMap }
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

        Mockito.`when`(mateRequestDataRepository.getMateRequests(
            Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean())
        ).thenAnswer {
            val offset = it.arguments[2] as Int

            runBlocking {
                processResultMethodReflection.callSuspend(mMateRequestsUseCase, getMateRequestsResultHashMap[offset])
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
            DataUserGeneratorUtility.generateDataUsers(2),
            false
        )
        val requestsResults = GetMateRequestsResult(
            listOf(
                DataMateRequest(0, 1),
                DataMateRequest(0, 2)
            ),
            false
        )

        initMateRequestsUseCase(
            imagesResults = imagesResults,
            usersResults = usersResults,
            getMateRequestsResultHashMap = hashMapOf(0 to requestsResults)
        )

        mMateRequestsUseCase.getMateRequests(requestsResults.mateRequests.size, 0, false)

        while (mMateRequestsStateAtomicRef.get() == null) { }

        val gottenMateRequestsState = mMateRequestsStateAtomicRef.get()!!

        Assert.assertEquals(1, gottenMateRequestsState.mateRequestChunks.size)

        for (sourceMateRequest in requestsResults.mateRequests) {
            Assert.assertNotNull(
                gottenMateRequestsState.mateRequestChunks.values.find { requests ->
                    requests.find { it.id == sourceMateRequest.id } != null
                }
            )
        }
    }

    @Test
    fun getTwoMateRequestChunksTest() {
        val imagesResults = GetImagesResult(
            mapOf(
                0L to Uri.parse(String())
            ),
            false
        )
        val chunkSize = 5

        val getMateRequestsResultHashMap = hashMapOf(
            0 to GetMateRequestsResult(
                DataMateRequestGeneratorUtility.generateDataMateRequests(chunkSize),
                false
            ),
            chunkSize to GetMateRequestsResult(
                DataMateRequestGeneratorUtility.generateDataMateRequests(chunkSize, chunkSize.toLong()),
                false
            )
        )

        initMateRequestsUseCase(
            imagesResults = imagesResults,
            getMateRequestsResultHashMap = getMateRequestsResultHashMap
        )

        mMateRequestsUseCase.getMateRequests(chunkSize, 0, false)
        mMateRequestsUseCase.getMateRequests(chunkSize, chunkSize, false)

        while (mMateRequestsStateAtomicRef.get() == null) { }
        while (mMateRequestsStateAtomicRef.get()!!.mateRequestChunks.size < 2) { }

        val gottenMateRequestsState = mMateRequestsStateAtomicRef.get()!!

        for (sourceMateRequest in getMateRequestsResultHashMap.values.map { it.mateRequests }.flatten()) {
            Assert.assertNotNull(
                gottenMateRequestsState.mateRequestChunks.values.find { requests ->
                    requests.find { it.id == sourceMateRequest.id } != null
                }
            )
        }
    }

    @Test
    fun answerMateRequestTest() {
        val mateRequestId = 0L
        val isAccepted = true

        val originalMateRequestsState = MateRequestsState(
            hashMapOf(),
            listOf(),
            listOf()
        )

        initMateRequestsUseCase(originalMateRequestsState = originalMateRequestsState)

        runBlocking {
            mMateRequestsUseCase.answerMateRequest(mateRequestId, isAccepted)

            while (mMateRequestsStateAtomicRef.get() == null) { }
            while (mMateRequestsStateAtomicRef.get()!!.newOperations.isEmpty()) { }

            val gottenState = mMateRequestsStateAtomicRef.get()

            Assert.assertNotNull(gottenState!!.newOperations.find {
                it::class == MateRequestAnswerProcessedOperation::class })
        }
    }
}