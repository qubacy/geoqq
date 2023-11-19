package com.qubacy.geoqq.domain.mate.chat

import android.net.Uri
import com.auth0.android.jwt.Claim
import com.qubacy.geoqq.common.BitmapMockContext
import com.qubacy.geoqq.common.UriMockContext
import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenPayloadResult
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
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
                MateChatUseCase.USER_ID_TOKEN_PAYLOAD_KEY to object : Claim {
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
        getMateMessagesResult: GetMessagesResult = GetMessagesResult(listOf()),
        usersResults: Map<Long, GetUserByIdResult> = mapOf(),
        imagesResults: Map<Long, GetImageResult> = mapOf(),
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

        Mockito.`when`(imageDataRepository.getImage(Mockito.anyLong(), Mockito.anyString()))
            .thenAnswer { invocation ->
                val imageId = invocation.arguments[0] as Long

                imagesResults[imageId]!!
            }
        Mockito.`when`(imageDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

        val userDataRepository = Mockito.mock(UserDataRepository::class.java)

        Mockito.`when`(userDataRepository.getUserById(Mockito.anyLong(), Mockito.anyString()))
            .thenAnswer { invocation ->
                val userId = invocation.arguments[0] as Long

                usersResults[userId]!!
            }
        Mockito.`when`(userDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

        mMateChatUseCase = MateChatUseCase(
            errorDataRepository, tokenDataRepository, mateMessageDataRepository,
            imageDataRepository, userDataRepository
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
    fun getChatTest() {
        val imagesResults = mapOf(
            0L to GetImageResult(Uri.parse(String()))
        )
        val usersResults = mapOf(
            0L to GetUserByIdResult(
                DataUser(0, "test", "test", 0L, true)),
            1L to GetUserByIdResult(
                DataUser(1, "test", "test", 0L, true))
        )
        val messages = listOf(
            DataMessage(0, 0, "test", 100),
            DataMessage(1, 1, "test", 200)
        )
        val getAccessTokenPayloadResult = generateAccessTokenPayloadResult(0L)

        initMateChatsUseCase(
            getAccessTokenPayloadResult = getAccessTokenPayloadResult,
            getMateMessagesResult = GetMessagesResult(messages),
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
}