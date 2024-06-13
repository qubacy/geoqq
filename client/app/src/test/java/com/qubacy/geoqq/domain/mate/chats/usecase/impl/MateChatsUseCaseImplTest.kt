package com.qubacy.geoqq.domain.mate.chats.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._common._test.mock.AuthDataRepositoryMockContainer
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository._common._test.context.MateChatDataRepositoryTestContext
import com.qubacy.geoqq.data.mate.chat.repository._common.result.added.MateChatAddedDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.get.GetChatsDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.updated.MateChatUpdatedDataResult
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.aspect.user.UserAspectUseCaseTest
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.base._test.util.runCoroutineTestCase
import com.qubacy.geoqq.domain._common.usecase.updatable.UpdatableUseCaseTest
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._common._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate._common.model.chat.toMateChat
import com.qubacy.geoqq.domain.mate.chats.projection.MateChatChunk
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.added.MateChatAddedDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chat.updated.MateChatUpdatedDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.get.GetMateChatChunkDomainResult
import com.qubacy.geoqq.domain.mate.chats.usecase._common.result.chunk.update.UpdateMateChatChunkDomainResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MateChatsUseCaseImplTest : UpdatableUseCaseTest<MateChatsUseCaseImpl>(), UserAspectUseCaseTest {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
        val DEFAULT_DATA_MATE_CHAT = MateChatDataRepositoryTestContext.DEFAULT_DATA_MATE_CHAT
    }

    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mLogoutUseCaseMockContainer: LogoutUseCaseMockContainer

    private lateinit var mAuthDataRepositoryMockContainer: AuthDataRepositoryMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mGetChatsDataResults: List<GetChatsDataResult>? = null
    private val mResultFlow: MutableSharedFlow<DataResult> = MutableSharedFlow()

    private var mGetChatsCallFlag = false

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mLogoutUseCaseMockContainer = LogoutUseCaseMockContainer()
        mAuthDataRepositoryMockContainer = AuthDataRepositoryMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val mateChatDataRepositoryMock = mockMateChatDataRepository()

        return superDependencies
            .plus(mLogoutUseCaseMockContainer.logoutUseCaseMock)
            .plus(mateChatDataRepositoryMock)
            .plus(mAuthDataRepositoryMockContainer.authDataRepositoryMock)
            .plus(mUserDataRepositoryMockContainer.userDataRepositoryMock)
    }

    private fun mockMateChatDataRepository(): MateChatDataRepository {
        val mateChatDataRepositoryMock = Mockito.mock(MateChatDataRepository::class.java)

        runTest {
            Mockito.`when`(mateChatDataRepositoryMock.getChats(
                AnyMockUtil.anyObject(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )).thenAnswer {
                mGetChatsCallFlag = true

                if (mErrorDataSourceMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataSourceMockContainer.getError!!)

                val resultLiveData = MutableLiveData<GetChatsDataResult>()

                CoroutineScope(Dispatchers.Unconfined).launch {
                    for (result in mGetChatsDataResults!!) {
                        resultLiveData.postValue(result)
                    }
                }

                resultLiveData
            }
            Mockito.`when`(mateChatDataRepositoryMock.resultFlow).thenAnswer {
                mResultFlow
            }
        }

        return mateChatDataRepositoryMock
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = MateChatsUseCaseImpl(
            dependencies[0] as LocalErrorDatabaseDataSource,
            dependencies[1] as LogoutUseCase,
            dependencies[2] as MateChatDataRepository,
            dependencies[3] as AuthDataRepository,
            dependencies[4] as UserDataRepository
        )
    }

    override fun clear() {
        super.clear()

        mGetChatsDataResults = null

        mGetChatsCallFlag = false
    }

    @Test
    fun getChatChunkFailedTest() = runTest {
        val loadedChatIds = listOf<Long>()
        val chunkIndex = 0
        val expectedError = TestError.normal

        mErrorDataSourceMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.getChatChunk(loadedChatIds, chunkIndex)

            val gottenResult = awaitItem()

            Assert.assertFalse(gottenResult.isSuccessful())
            Assert.assertEquals(expectedError, gottenResult.error)
        }
    }

    @Test
    fun getChatChunkSucceededTest() = runTest {
        val loadedChatIds = listOf<Long>()
        val chunkIndex = 1
        val count = MateChatsUseCase.DEFAULT_CHAT_CHUNK_SIZE
        val offset = count * chunkIndex

        val localDataChats = listOf(
            DEFAULT_DATA_MATE_CHAT
        )
        val remoteDataChats = listOf(
            DEFAULT_DATA_MATE_CHAT,
            DEFAULT_DATA_MATE_CHAT
        )

        val localChats = localDataChats.map { it.toMateChat() }
        val remoteChats = remoteDataChats.map { it.toMateChat() }

        val localGetChatsResult = GetChatsDataResult(false, offset, localDataChats)
        val remoteGetChatsResult = GetChatsDataResult(true, offset, remoteDataChats)

        val expectedLocalChatChunk = MateChatChunk(chunkIndex, localChats)
        val expectedRemoteChatChunk = MateChatChunk(chunkIndex, remoteChats)

        mGetChatsDataResults = listOf(localGetChatsResult, remoteGetChatsResult)

        mUseCase.resultFlow.test {
            mUseCase.getChatChunk(loadedChatIds, chunkIndex)

            val gottenLocalResult = awaitItem()

            Assert.assertEquals(GetMateChatChunkDomainResult::class, gottenLocalResult::class)

            val gottenLocalChatChunk = (gottenLocalResult as GetMateChatChunkDomainResult).chunk

            Assert.assertEquals(expectedLocalChatChunk, gottenLocalChatChunk)

            val gottenRemoteResult = awaitItem()

            Assert.assertEquals(UpdateMateChatChunkDomainResult::class, gottenRemoteResult::class)

            val gottenRemoteChatChunk = (gottenRemoteResult as UpdateMateChatChunkDomainResult).chunk

            Assert.assertEquals(expectedRemoteChatChunk, gottenRemoteChatChunk)
        }
    }

    @Test
    fun processMateChatAddedDataResultTest() = runCoroutineTestCase(mUseCase) {
        val dataChat = DEFAULT_DATA_MATE_CHAT
        val dataResult = MateChatAddedDataResult(dataChat)

        val expectedChat = dataChat.toMateChat()

        mUseCase.resultFlow.test {
            mResultFlow.emit(dataResult)

            val result = awaitItem()

            Assert.assertEquals(MateChatAddedDomainResult::class, result::class)

            val gottenChat = (result as MateChatAddedDomainResult).chat

            Assert.assertEquals(expectedChat, gottenChat)
        }
    }

    @Test
    fun processMateChatUpdatedDataResultTest() = runCoroutineTestCase(mUseCase) {
        val dataChat = DEFAULT_DATA_MATE_CHAT
        val dataResult = MateChatUpdatedDataResult(dataChat)

        val expectedChat = dataChat.toMateChat()

        mUseCase.resultFlow.test {
            mResultFlow.emit(dataResult)

            val result = awaitItem()

            Assert.assertEquals(MateChatUpdatedDomainResult::class, result::class)

            val gottenChat = (result as MateChatUpdatedDomainResult).chat

            Assert.assertEquals(expectedChat, gottenChat)
        }
    }

    override fun getUserAspectUseCaseTestUseCase(): UseCase {
        return mUseCase
    }

    override fun getUserAspectUseCaseTestUserDataRepositoryMockContainer(

    ): UserDataRepositoryMockContainer {
        return mUserDataRepositoryMockContainer
    }
}