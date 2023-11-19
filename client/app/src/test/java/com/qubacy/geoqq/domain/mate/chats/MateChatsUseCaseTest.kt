package com.qubacy.geoqq.domain.mate.chats

import android.net.Uri
import com.qubacy.geoqq.common.BitmapMockContext
import com.qubacy.geoqq.common.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
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

class MateChatsUseCaseTest {
    companion object {
        init {
            BitmapMockContext.mockBitmapFactory()
            UriMockContext.mockUri()
        }
    }

    private lateinit var mMateChatsUseCase: MateChatsUseCase

    private lateinit var mMateChatsStateAtomicRef: AtomicReference<MateChatsState?>

    private fun emitOriginalState(originalMateChatsState: MateChatsState) = runBlocking {
        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("mStateFlow")
            .apply { isAccessible = true }
        val stateFlow = stateFlowFieldReflection.get(mMateChatsUseCase)
                as MutableStateFlow<MateChatsState>

        stateFlow.emit(originalMateChatsState)
    }

    private fun initMateChatsUseCase(
        getTokensResult: GetTokensResult = GetTokensResult(String(), String()),
        getMateChatsResult: GetChatsResult = GetChatsResult(listOf()),
        usersResults: Map<Long, GetUserByIdResult> = mapOf(),
        imagesResults: Map<Long, GetImageResult> = mapOf(),
        mateRequestCount: Int = 0,
        originalMateChatsState: MateChatsState? = null
    ) = runBlocking {
        val errorDataRepository = Mockito.mock(ErrorDataRepository::class.java)

        val tokenDataRepository = Mockito.mock(TokenDataRepository::class.java)

        Mockito.`when`(tokenDataRepository.getTokens()).thenReturn(getTokensResult)

        val mateChatDataRepository = Mockito.mock(MateChatDataRepository::class.java)

        Mockito.`when`(mateChatDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

        val imageDataRepository = Mockito.mock(ImageDataRepository::class.java)

        Mockito.`when`(imageDataRepository.getImage(Mockito.anyLong(), Mockito.anyString()))
            .thenAnswer { invocation ->
                val imageId = invocation.arguments[0] as Long

                imagesResults[imageId]!!
            }

        val userDataRepository = Mockito.mock(UserDataRepository::class.java)

        Mockito.`when`(userDataRepository.getUserById(Mockito.anyLong(), Mockito.anyString()))
            .thenAnswer { invocation ->
                val userId = invocation.arguments[0] as Long

                usersResults[userId]!!
            }

        val mateRequestDataRepository = Mockito.mock(MateRequestDataRepository::class.java)

        Mockito.`when`(mateRequestDataRepository.getMateRequestCount(Mockito.anyString()))
            .thenAnswer { GetMateRequestCountResult(mateRequestCount) }

        mMateChatsUseCase = MateChatsUseCase(
            errorDataRepository, tokenDataRepository, mateChatDataRepository,
            imageDataRepository, userDataRepository, mateRequestDataRepository
        )

        val processResultMethodReflection = ConsumingUseCase::class.memberFunctions
            .find { it.name == "processResult" }!!.apply {
                isAccessible = true
            }
        if (originalMateChatsState != null)
            emitOriginalState(originalMateChatsState)

        Mockito.`when`(mateChatDataRepository.getChats(Mockito.anyString(), Mockito.anyInt()))
            .thenAnswer {
                runBlocking {
                    processResultMethodReflection.callSuspend(mMateChatsUseCase, getMateChatsResult)
                }
            }

        mMateChatsStateAtomicRef = AtomicReference(null)

        GlobalScope.launch(Dispatchers.IO) {
            mMateChatsUseCase.stateFlow.collect {
                if (it == null) return@collect

                mMateChatsStateAtomicRef.set(it)
            }
        }
    }

    @Before
    fun setup() {
        initMateChatsUseCase()
    }

    @Test
    fun getTwoMateChatsTest() {
        val imagesResults = mapOf(
            0L to GetImageResult(Uri.parse(String()))
        )
        val usersResults = mapOf(
            0L to GetUserByIdResult(
                DataUser(0, "test", "test", 0, false)),
            1L to GetUserByIdResult(
                DataUser(1, "test", "test", 0, false)),
        )
        val chats = listOf(
            DataMateChat(0, 0, 0, null),
            DataMateChat(1, 1, 0, null)
        )
        val mateRequestCount = 2

        initMateChatsUseCase(
            imagesResults = imagesResults,
            usersResults = usersResults,
            getMateChatsResult = GetChatsResult(chats),
            mateRequestCount = mateRequestCount
        )

        mMateChatsUseCase.getMateChats(chats.size)

        while (mMateChatsStateAtomicRef.get() == null) { }

        val gottenMateChatsState = mMateChatsStateAtomicRef.get()

        for (gottenChat in gottenMateChatsState!!.chats) {
            Assert.assertNotNull(chats.find { it.id == gottenChat.chatId })
        }

        Assert.assertEquals(mateRequestCount, gottenMateChatsState.mateRequestCount)
    }
}