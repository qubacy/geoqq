package com.qubacy.geoqq.domain.geochat.chat

import android.net.Uri
import com.qubacy.geoqq.common.util.mock.BitmapMockContext
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.message.result.GetMessagesResult
import com.qubacy.geoqq.data.common.util.generator.DataMessageGeneratorUtility
import com.qubacy.geoqq.data.common.util.generator.DataUserGeneratorUtility
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geochat.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.usecase.chat.ChatUseCase
import com.qubacy.geoqq.domain.common.usecase.chat.ChatUseCaseTest
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.geochat.chat.state.GeoChatState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

class GeoChatUseCaseTest : ChatUseCaseTest<GeoChatState>() {
    companion object {
        init {
            BitmapMockContext.mockBitmapFactory()
            UriMockContext.mockUri()
        }
    }

    override fun generateDefaultGetMessagesResult(): GetMessagesResult {
        return GetMessagesResult(listOf())
    }

    override fun generateChatState(
        messages: List<Message>,
        users: List<User>,
        operations: List<Operation>
    ): GeoChatState {
        return GeoChatState(messages, users, operations)
    }

    override suspend fun generateUseCase(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository,
        getMessagesResult: GetMessagesResult
    ): ChatUseCase<GeoChatState> {
        val geoMessageDataRepository = Mockito.mock(GeoMessageDataRepository::class.java)

        Mockito.`when`(geoMessageDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

        val processResultMethodReflection = ConsumingUseCase::class.memberFunctions
            .find { it.name == "processResult" }!!.apply {
                isAccessible = true
            }
        Mockito.`when`(geoMessageDataRepository
            .getGeoMessages(Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString())
        )
            .thenAnswer {
                runBlocking {
                    processResultMethodReflection.callSuspend(mChatUseCase, getMessagesResult)
                }
            }

        return GeoChatUseCase(
            errorDataRepository, tokenDataRepository,
            geoMessageDataRepository, imageDataRepository,
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
            getMessagesResult = GetMessagesResult(messages),
            imagesResults = imagesResults,
            usersResults = usersResults
        )

        val radius = 0
        val latitude = 0.0
        val longitude = 0.0

        (mChatUseCase as GeoChatUseCase).getGeoChat(radius, latitude, longitude)

        while (mChatStateAtomicRef.get() == null) { }

        val gottenMateChatState = mChatStateAtomicRef.get()

        for (gottenMessage in gottenMateChatState!!.messages) {
            Assert.assertNotNull(messages.find { it.id == gottenMessage.id })
        }
    }
}