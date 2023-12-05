package com.qubacy.geoqq.domain.common.usecase.chat

import android.net.Uri
import com.auth0.android.jwt.Claim
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.message.result.GetMessagesResult
import com.qubacy.geoqq.data.common.util.generator.DataUserGeneratorUtility
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geochat.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.CreateMateRequestResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenPayloadResult
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.chat.ApproveNewMateRequestCreationOperation
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.chat.ChatState
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.util.Date
import java.util.concurrent.atomic.AtomicReference

abstract class ChatUseCaseTest<StateType : ChatState> {
    protected lateinit var mChatUseCase: ChatUseCase<StateType>

    protected lateinit var mChatStateAtomicRef: AtomicReference<StateType?>

    protected fun generateAccessTokenPayloadResult(userId: Long): GetAccessTokenPayloadResult {
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

    protected abstract fun generateDefaultGetMessagesResult(): GetMessagesResult
    protected abstract fun generateChatState(
        messages: List<Message> = listOf(),
        users: List<User> = listOf(),
        operations: List<Operation> = listOf()
    ): StateType
    protected abstract suspend fun generateUseCase(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository,
        getMessagesResult: GetMessagesResult
    ): ChatUseCase<StateType>

    protected fun emitOriginalState(originalChatState: StateType) = runBlocking {
        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("mStateFlow")
            .apply { isAccessible = true }
        val stateFlow = stateFlowFieldReflection.get(mChatUseCase) as MutableStateFlow<StateType>

        stateFlow.emit(originalChatState)
    }

    protected open fun initChatUseCase(
        getTokensResult: GetTokensResult = GetTokensResult(String(), String()),
        getAccessTokenPayloadResult: GetAccessTokenPayloadResult = GetAccessTokenPayloadResult(mapOf()),
        getMessagesResult: GetMessagesResult = generateDefaultGetMessagesResult(),
        usersResults: GetUsersByIdsResult = GetUsersByIdsResult(listOf(), true),
        imagesResults: GetImagesResult = GetImagesResult(mapOf(), true),
        createMateRequestResult: CreateMateRequestResult = CreateMateRequestResult(),
        originalChatState: StateType? = null
    ) = runBlocking {
        val errorDataRepository = Mockito.mock(ErrorDataRepository::class.java)

        val tokenDataRepository = Mockito.mock(TokenDataRepository::class.java)

        Mockito.`when`(tokenDataRepository.getTokens()).thenReturn(getTokensResult)
        Mockito.`when`(tokenDataRepository.getAccessTokenPayload())
            .thenReturn(getAccessTokenPayloadResult)

        val geoMessageDataRepository = Mockito.mock(GeoMessageDataRepository::class.java)

        Mockito.`when`(geoMessageDataRepository.resultFlow).thenAnswer {
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

        Mockito.`when`(mateRequestDataRepository.createMateRequest(
            Mockito.anyString(), Mockito.anyLong())
        ).thenAnswer { createMateRequestResult }

        mChatUseCase = generateUseCase(
            errorDataRepository, tokenDataRepository,
            imageDataRepository, userDataRepository,
            mateRequestDataRepository,
            getMessagesResult
        )

        if (originalChatState != null) emitOriginalState(originalChatState)

        mChatStateAtomicRef = AtomicReference(null)

        GlobalScope.launch(Dispatchers.IO) {
            mChatUseCase.stateFlow.collect {
                if (it == null) return@collect

                mChatStateAtomicRef.set(it)
            }
        }
    }

    @Test
    fun getUserDetailsTest() {
        val imagesResults = GetImagesResult(
            mapOf(
                0L to Uri.parse(String())
            ), false)
        val originalUserToGetDetails = UserGeneratorUtility.generateUsers(1).first()
        val updatedUserToGetDetails = DataUserGeneratorUtility.generateDataUsers(1).first()

        val originalChatState = generateChatState(users = listOf(originalUserToGetDetails))

        val usersResults = GetUsersByIdsResult(
            listOf(updatedUserToGetDetails),
            false
        )
        val getAccessTokenPayloadResult = generateAccessTokenPayloadResult(0L)

        initChatUseCase(
            getAccessTokenPayloadResult = getAccessTokenPayloadResult,
            imagesResults = imagesResults,
            usersResults = usersResults,
            originalChatState = originalChatState
        )

        mChatUseCase.getUserDetails(originalUserToGetDetails.id)

        while (mChatStateAtomicRef.get() == null) { }
        while (mChatStateAtomicRef.get()!!.newOperations.isEmpty()) { }

        val gottenChatState = mChatStateAtomicRef.get()!!

        val gottenSetUserDetailsOperation = gottenChatState.newOperations
            .find { it::class == SetUsersDetailsOperation::class } as SetUsersDetailsOperation

        Assert.assertNotNull(gottenSetUserDetailsOperation)
        Assert.assertNotNull(gottenSetUserDetailsOperation.usersIds.find { it == originalUserToGetDetails.id })
        Assert.assertNotNull(gottenChatState.users.find { it.id == updatedUserToGetDetails.id })
    }

    @Test
    fun createMateRequestTest() {
        val userToGetMatesWith = UserGeneratorUtility.generateUsers(1, 1).first()

        val originalChatState = generateChatState(users = listOf(userToGetMatesWith))

        val getAccessTokenPayloadResult = generateAccessTokenPayloadResult(0L)

        initChatUseCase(
            getAccessTokenPayloadResult = getAccessTokenPayloadResult,
            originalChatState = originalChatState,
            createMateRequestResult = CreateMateRequestResult()
        )

        mChatUseCase.createMateRequest(userToGetMatesWith.id)

        while (mChatStateAtomicRef.get() == null) { }
        while (mChatStateAtomicRef.get()!!.newOperations.isEmpty()) { }

        val gottenChatState = mChatStateAtomicRef.get()!!

        val gottenApproveNewMateRequestCreationOperation = gottenChatState.newOperations
            .find { it::class == ApproveNewMateRequestCreationOperation::class }
                as ApproveNewMateRequestCreationOperation

        Assert.assertNotNull(gottenApproveNewMateRequestCreationOperation)
    }
}