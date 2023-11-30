package com.qubacy.geoqq.domain.mate.chats

import android.net.Uri
import com.qubacy.geoqq.common.util.mock.BitmapMockContext
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.util.generator.DataMateChatGeneratorUtility
import com.qubacy.geoqq.data.common.util.generator.DataUserGeneratorUtility
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.util.generator.MateChatGeneratorUtility
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import com.qubacy.geoqq.domain.mate.chats.operation.AddPrecedingChatsOperation
import com.qubacy.geoqq.domain.mate.chats.operation.SetMateChatsOperation
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
        getMateChatsResult: GetChatsResult = GetChatsResult(listOf(), true, true),
        usersResults: GetUsersByIdsResult = GetUsersByIdsResult(listOf(), true),
        imagesResults: GetImagesResult = GetImagesResult(mapOf(), true),
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

        Mockito.`when`(imageDataRepository.getImages(Mockito.anyList(), Mockito.anyString()))
            .thenAnswer { invocation ->
                imagesResults
            }

        val userDataRepository = Mockito.mock(UserDataRepository::class.java)

        Mockito.`when`(userDataRepository.getUsersByIds(
            Mockito.anyList(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())
        ).thenAnswer { invocation -> usersResults }
        Mockito.`when`(userDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

        val mateRequestDataRepository = Mockito.mock(MateRequestDataRepository::class.java)

        Mockito.`when`(mateRequestDataRepository.getMateRequestCount(Mockito.anyString()))
            .thenAnswer { GetMateRequestCountResult(mateRequestCount) }
        Mockito.`when`(mateRequestDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

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
        val imagesResults = GetImagesResult(
            mapOf(
                0L to Uri.parse(String())
            ), false)
        val usersResults = GetUsersByIdsResult(
            DataUserGeneratorUtility.generateDataUsers(2),
            false
        )
        val chatsResult = GetChatsResult(
            listOf(
                DataMateChat(0, 0, 0, null),
                DataMateChat(1, 1, 0, null)
            ),
            false,
            true
        )
        val mateRequestCount = 2

        initMateChatsUseCase(
            imagesResults = imagesResults,
            usersResults = usersResults,
            getMateChatsResult = chatsResult,
            mateRequestCount = mateRequestCount
        )

        mMateChatsUseCase.getMateChats(chatsResult.chats.size)

        while (mMateChatsStateAtomicRef.get() == null) { }

        val gottenMateChatsState = mMateChatsStateAtomicRef.get()

        for (gottenChat in gottenMateChatsState!!.chats) {
            Assert.assertNotNull(chatsResult.chats.find { it.id == gottenChat.chatId })
        }

        Assert.assertEquals(mateRequestCount, gottenMateChatsState.mateRequestCount)
    }

    @Test
    fun getPrecedingMessageChunkTest() {
        val imagesResults = GetImagesResult(
            mapOf(
                0L to Uri.parse(String())
            ), false)
        val precedingUsersResult = GetUsersByIdsResult(
            DataUserGeneratorUtility.generateDataUsers(10, 1),
            false
        )
        val localUser = UserGeneratorUtility.generateUsers(1).first()
        val newUsers = UserGeneratorUtility.generateUsers(
            20, precedingUsersResult.users.size.toLong() + 1) + localUser
        val precedingChats = DataMateChatGeneratorUtility.generateDataChats(10)
        val newChats = MateChatGeneratorUtility
            .generateMateChats(20, precedingChats.size.toLong())

        val prevState = MateChatsState(
            newChats, newUsers, 0, listOf(SetMateChatsOperation()))

        initMateChatsUseCase(
            getMateChatsResult = GetChatsResult(precedingChats, false, false),
            imagesResults = imagesResults,
            usersResults = precedingUsersResult,
            originalMateChatsState = prevState
        )

        mMateChatsUseCase.getMateChats(precedingChats.size)

        while (mMateChatsStateAtomicRef.get() == null) { }
        while (mMateChatsStateAtomicRef.get()!!.newOperations.first()::class
            != AddPrecedingChatsOperation::class) { }

        val gottenMateChatsState = mMateChatsStateAtomicRef.get()
        val gottenAddPrecedingChatsOperation = gottenMateChatsState!!.newOperations.first()
                as AddPrecedingChatsOperation

        for (gottenPrecedingChat in gottenAddPrecedingChatsOperation.precedingChats) {
            Assert.assertNotNull(precedingChats.find { it.id == gottenPrecedingChat.chatId })
        }
    }
}