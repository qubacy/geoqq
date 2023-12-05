package com.qubacy.geoqq.domain.mate.chat

import android.net.Uri
import com.qubacy.geoqq.common.util.mock.BitmapMockContext
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.message.result.GetMessagesResult
import com.qubacy.geoqq.data.common.util.generator.DataMessageGeneratorUtility
import com.qubacy.geoqq.data.common.util.generator.DataUserGeneratorUtility
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMateMessagesResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenPayloadResult
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.util.generator.MessageGeneratorUtility
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import com.qubacy.geoqq.domain.mate.chat.operation.AddPrecedingMessagesOperation
import com.qubacy.geoqq.domain.common.operation.chat.SetMessagesOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.usecase.chat.ChatUseCase
import com.qubacy.geoqq.domain.common.usecase.chat.ChatUseCaseTest
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
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
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

class MateChatUseCaseTest(

) : ChatUseCaseTest<MateChatState>() {
    companion object {
        init {
            BitmapMockContext.mockBitmapFactory()
            UriMockContext.mockUri()
        }
    }

    override fun generateDefaultGetMessagesResult(): GetMessagesResult {
        return GetMateMessagesResult(listOf(), true, true)
    }

    override fun generateChatState(
        messages: List<Message>,
        users: List<User>,
        operations: List<Operation>
    ): MateChatState {
        return MateChatState(messages, users, operations)
    }

    override suspend fun generateUseCase(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository,
        getMessagesResult: GetMessagesResult
    ): ChatUseCase<MateChatState> {
        val mateMessageDataRepository = Mockito.mock(MateMessageDataRepository::class.java)

        Mockito.`when`(mateMessageDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

        val processResultMethodReflection = ConsumingUseCase::class.memberFunctions
            .find { it.name == "processResult" }!!.apply {
                isAccessible = true
            }

        Mockito.`when`(mateMessageDataRepository
            .getMessages(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt())
        )
            .thenAnswer {
                runBlocking {
                    processResultMethodReflection.callSuspend(mChatUseCase, getMessagesResult)
                }
            }

        return MateChatUseCase(
            errorDataRepository, tokenDataRepository,
            mateMessageDataRepository, imageDataRepository,
            userDataRepository, mateRequestDataRepository
        )
    }

    @Before
    fun setup() {
        initChatUseCase()
    }

    @Test
    fun getInitChatTest() {
        val imagesResults = GetImagesResult(
            mapOf(
                0L to Uri.parse(String())
            ), false)
        val usersResults = GetUsersByIdsResult(
            DataUserGeneratorUtility.generateDataUsers(2),
            false
        )
        val messages = DataMessageGeneratorUtility.generateDataMessages(2)
        val getAccessTokenPayloadResult = generateAccessTokenPayloadResult(0L)

        initChatUseCase(
            getAccessTokenPayloadResult = getAccessTokenPayloadResult,
            getMessagesResult = GetMateMessagesResult(messages, false, true),
            imagesResults = imagesResults,
            usersResults = usersResults
        )

        (mChatUseCase as MateChatUseCase).getChat(0, 1, messages.size)

        while (mChatStateAtomicRef.get() == null) { }

        val gottenMateChatState = mChatStateAtomicRef.get()

        for (gottenMessage in gottenMateChatState!!.messages) {
            Assert.assertNotNull(messages.find { it.id == gottenMessage.id })
        }
    }

    @Test
    fun getPrecedingMessageChunkTest() {
        val imagesResults = GetImagesResult(
            mapOf(
                0L to Uri.parse(String())
            ), false)
        val precedingUsers = UserGeneratorUtility.generateUsers(2)
        val newUsersResults = GetUsersByIdsResult(
            DataUserGeneratorUtility.generateDataUsers(2),
            false
        )
        val precedingMessages = DataMessageGeneratorUtility.generateDataMessages(10)
        val newMessages = MessageGeneratorUtility
            .generateMessages(20, precedingMessages.size.toLong())

        val prevState = MateChatState(newMessages, precedingUsers, listOf(SetMessagesOperation()))

        val getAccessTokenPayloadResult = generateAccessTokenPayloadResult(0L)

        initChatUseCase(
            getAccessTokenPayloadResult = getAccessTokenPayloadResult,
            getMessagesResult = GetMateMessagesResult(precedingMessages, false, false),
            imagesResults = imagesResults,
            usersResults = newUsersResults,
            originalChatState = prevState
        )

        (mChatUseCase as MateChatUseCase).getChat(0, 1, precedingMessages.size)

        while (mChatStateAtomicRef.get() == null) { }
        while (mChatStateAtomicRef.get()!!.newOperations.first()::class
            != AddPrecedingMessagesOperation::class) { }

        val gottenMateChatState = mChatStateAtomicRef.get()
        val gottenAddPrecedingMessagesOperation = gottenMateChatState!!.newOperations.first()
                as AddPrecedingMessagesOperation

        for (gottenPrecedingMessage in gottenAddPrecedingMessagesOperation.precedingMessages) {
            Assert.assertNotNull(precedingMessages.find { it.id == gottenPrecedingMessage.id })
        }
    }
}