package com.qubacy.geoqq.domain.mate.chat

import android.net.Uri
import com.auth0.android.jwt.Claim
import com.qubacy.geoqq.common.util.mock.BitmapMockContext
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
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
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.util.generator.MessageGeneratorUtility
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import com.qubacy.geoqq.domain.mate.chat.operation.AddPrecedingMessagesOperation
import com.qubacy.geoqq.domain.common.operation.chat.SetMessagesOperation
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
import java.util.Date
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

class MateChatUseCaseTest(

) {
    companion object {
        init {
            BitmapMockContext.mockBitmapFactory()
            UriMockContext.mockUri()
        }
    }

    private lateinit var mMateChatUseCase: MateChatUseCase

    private lateinit var mMateChatStateAtomicRef: AtomicReference<MateChatState?>

    private fun emitOriginalState(originalMateChatState: MateChatState) = runBlocking {
        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("mStateFlow")
            .apply { isAccessible = true }
        val stateFlow = stateFlowFieldReflection.get(mMateChatUseCase)
                as MutableStateFlow<MateChatState>

        stateFlow.emit(originalMateChatState)
    }

    private fun generateAccessTokenPayloadResult(userId: Long): GetAccessTokenPayloadResult {
        return GetAccessTokenPayloadResult(
            mapOf(
                UserExtension.USER_ID_TOKEN_PAYLOAD_KEY to object : Claim {
                    override fun asBoolean(): Boolean? = null
                    override fun asInt(): Int? = null
                    override fun asLong(): Long? = userId
                    override fun asDouble(): Double? = null
                    override fun asString(): String? = null
                    override fun asDate(): Date? = null
                    override fun <T : Any?> asArray(tClazz: Class<T>?): Array<T> = asArray(tClazz)
                    override fun <T : Any?> asList(tClazz: Class<T>?): MutableList<T> = mutableListOf()
                    override fun <T : Any?> asObject(tClazz: Class<T>?): T? = null
                }
            )
        )
    }

    private fun initMateChatsUseCase(
        getTokensResult: GetTokensResult = GetTokensResult(String(), String()),
        getAccessTokenPayloadResult: GetAccessTokenPayloadResult = GetAccessTokenPayloadResult(mapOf()),
        getMateMessagesResult: GetMateMessagesResult = GetMateMessagesResult(listOf(), true, true),
        usersResults: GetUsersByIdsResult = GetUsersByIdsResult(listOf(), true),
        imagesResults: GetImagesResult = GetImagesResult(mapOf(), true),
        mateRequestCount: Long = 0,
        originalMateChatState: MateChatState? = null
    ) = runBlocking {
        val errorDataRepository = Mockito.mock(ErrorDataRepository::class.java)

        val tokenDataRepository = Mockito.mock(TokenDataRepository::class.java)

        Mockito.`when`(tokenDataRepository.getTokens()).thenReturn(getTokensResult)
        Mockito.`when`(tokenDataRepository.getAccessTokenPayload())
            .thenReturn(getAccessTokenPayloadResult)

        val mateMessageDataRepository = Mockito.mock(MateMessageDataRepository::class.java)

        Mockito.`when`(mateMessageDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

        val imageDataRepository = Mockito.mock(ImageDataRepository::class.java)

        Mockito.`when`(imageDataRepository.getImages(Mockito.anyList(), Mockito.anyString()))
            .thenAnswer { invocation -> imagesResults }
        Mockito.`when`(imageDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
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
            .thenAnswer { mateRequestCount }

        mMateChatUseCase = MateChatUseCase(
            errorDataRepository, tokenDataRepository, mateMessageDataRepository,
            imageDataRepository, userDataRepository, mateRequestDataRepository
        )

        val processResultMethodReflection = ConsumingUseCase::class.memberFunctions
            .find { it.name == "processResult" }!!.apply {
                isAccessible = true
            }
        if (originalMateChatState != null)
            emitOriginalState(originalMateChatState)

        Mockito.`when`(mateMessageDataRepository
            .getMessages(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt())
        )
            .thenAnswer {
                runBlocking {
                    processResultMethodReflection.callSuspend(mMateChatUseCase, getMateMessagesResult)
                }
            }

        mMateChatStateAtomicRef = AtomicReference(null)

        GlobalScope.launch(Dispatchers.IO) {
            mMateChatUseCase.stateFlow.collect {
                if (it == null) return@collect

                mMateChatStateAtomicRef.set(it)
            }
        }
    }

    @Before
    fun setup() {
        initMateChatsUseCase()
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

        initMateChatsUseCase(
            getAccessTokenPayloadResult = getAccessTokenPayloadResult,
            getMateMessagesResult = GetMateMessagesResult(messages, false, true),
            imagesResults = imagesResults,
            usersResults = usersResults
        )

        mMateChatUseCase.getChat(0, 1, messages.size)

        while (mMateChatStateAtomicRef.get() == null) { }

        val gottenMateChatState = mMateChatStateAtomicRef.get()

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

        initMateChatsUseCase(
            getAccessTokenPayloadResult = getAccessTokenPayloadResult,
            getMateMessagesResult = GetMateMessagesResult(precedingMessages, false, false),
            imagesResults = imagesResults,
            usersResults = newUsersResults,
            originalMateChatState = prevState
        )

        mMateChatUseCase.getChat(0, 1, precedingMessages.size)


        while (mMateChatStateAtomicRef.get() == null) { }
        while (mMateChatStateAtomicRef.get()!!.newOperations.first()::class
            != AddPrecedingMessagesOperation::class) { }

        val gottenMateChatState = mMateChatStateAtomicRef.get()
        val gottenAddPrecedingMessagesOperation = gottenMateChatState!!.newOperations.first()
                as AddPrecedingMessagesOperation

        for (gottenPrecedingMessage in gottenAddPrecedingMessagesOperation.precedingMessages) {
            Assert.assertNotNull(precedingMessages.find { it.id == gottenPrecedingMessage.id })
        }
    }
}