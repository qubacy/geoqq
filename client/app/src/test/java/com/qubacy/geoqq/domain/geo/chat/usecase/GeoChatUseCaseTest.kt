package com.qubacy.geoqq.domain.geo.chat.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.geo.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository.result.GetGeoMessagesDataResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain._common.usecase.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.geo.chat.model.toGeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase.result.message.update.UpdateGeoMessagesDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.interlocutor.usecase._test.mock.InterlocutorUseCaseMockContainer
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._test.mock.MateRequestUseCaseMockContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class GeoChatUseCaseTest : UseCaseTest<GeoChatUseCase>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryMockContainer.DEFAULT_DATA_USER
        val DEFAULT_DATA_MESSAGE = DataMessage(0, DEFAULT_DATA_USER, "test", 0)
    }

    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mMateRequestUseCaseMockContainer: MateRequestUseCaseMockContainer
    private lateinit var mInterlocutorUseCaseMockContainer: InterlocutorUseCaseMockContainer
    private lateinit var mLogoutUseCaseMockContainer: LogoutUseCaseMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mGeoMessageGetMessagesResults: List<GetGeoMessagesDataResult>? = null

    private var mGeoMessageGetMessagesCallFlag = false
    private var mGeoMessageSendMessageCallFlag = false

    override fun clear() {
        super.clear()

        mGeoMessageGetMessagesResults = null

        mGeoMessageGetMessagesCallFlag = false
        mGeoMessageSendMessageCallFlag = false
    }

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mMateRequestUseCaseMockContainer = MateRequestUseCaseMockContainer()
        mInterlocutorUseCaseMockContainer = InterlocutorUseCaseMockContainer()
        mLogoutUseCaseMockContainer = LogoutUseCaseMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val geoMessageDataRepositoryMock = mockGeoMessageDataRepository()

        return superDependencies
            .plus(mMateRequestUseCaseMockContainer.mateRequestUseCaseMock)
            .plus(mInterlocutorUseCaseMockContainer.interlocutorUseCaseMock)
            .plus(mLogoutUseCaseMockContainer.logoutUseCaseMock)
            .plus(geoMessageDataRepositoryMock)
            .plus(mUserDataRepositoryMockContainer.userDataRepository)
    }

    private fun mockGeoMessageDataRepository(): GeoMessageDataRepository {
        val geoMessageDataRepository = Mockito.mock(GeoMessageDataRepository::class.java)

        runTest {
            Mockito.`when`(geoMessageDataRepository.getMessages(
                Mockito.anyInt(),
                Mockito.anyFloat(),
                Mockito.anyFloat()
            )).thenAnswer {
                mGeoMessageGetMessagesCallFlag = true

                val resultLiveData = MutableLiveData<GetGeoMessagesDataResult>()

                CoroutineScope(Dispatchers.Unconfined).launch {
                    for (result in mGeoMessageGetMessagesResults!!)
                        resultLiveData.postValue(result)
                }

                resultLiveData
            }
            Mockito.`when`(geoMessageDataRepository.sendMessage(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyFloat(),
                Mockito.anyFloat()
            )).thenAnswer {
                mGeoMessageSendMessageCallFlag = true

                Unit
            }
        }

        return geoMessageDataRepository
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = GeoChatUseCase(
            dependencies[0] as LocalErrorDataSource,
            dependencies[1] as MateRequestUseCase,
            dependencies[2] as InterlocutorUseCase,
            dependencies[3] as LogoutUseCase,
            dependencies[4] as GeoMessageDataRepository,
            dependencies[5] as UserDataRepository
        )
    }

    @Test
    fun getMessagesTest() = runTest {
        val radius = 0
        val longitude = 0f
        val latitude = 0f

        val localUser = DEFAULT_DATA_USER
        val remoteUser = localUser.copy(username = "remote user")

        val localGeoMessages = listOf(
            DEFAULT_DATA_MESSAGE.copy(user = localUser)
        )
        val remoteGeoMessages = listOf(
            DEFAULT_DATA_MESSAGE.copy(user = remoteUser)
        )

        val localGeoMessageGetMessagesResult = GetGeoMessagesDataResult(
            false,
            localGeoMessages
        )
        val remoteGeoMessageGetMessagesResult = GetGeoMessagesDataResult(
            true,
            remoteGeoMessages
        )

        val expectedLocalGeoMessages = localGeoMessages.map { it.toGeoMessage() }
        val expectedRemoteGeoMessages = remoteGeoMessages.map { it.toGeoMessage() }

        mGeoMessageGetMessagesResults = listOf(
            localGeoMessageGetMessagesResult,
            remoteGeoMessageGetMessagesResult
        )

        mUseCase.resultFlow.test {
            mUseCase.getMessages(radius, longitude, latitude)

            val localResult = awaitItem()

            Assert.assertTrue(mGeoMessageGetMessagesCallFlag)
            Assert.assertEquals(GetGeoMessagesDomainResult::class, localResult::class)

            val gottenLocalGeoMessages = (localResult as GetGeoMessagesDomainResult).messages!!

            AssertUtils.assertEqualContent(expectedLocalGeoMessages, gottenLocalGeoMessages)

            val remoteResult = awaitItem()

            Assert.assertEquals(UpdateGeoMessagesDomainResult::class, remoteResult::class)

            val gottenRemoteGeoMessages = (remoteResult as UpdateGeoMessagesDomainResult).messages!!

            AssertUtils.assertEqualContent(expectedRemoteGeoMessages, gottenRemoteGeoMessages)
        }
    }

    @Test
    fun sendMessageTest() = runTest {
        val text = "test"
        val radius = 0
        val longitude = 0f
        val latitude = 0f

        mUseCase.resultFlow.test {
            mUseCase.sendMessage(text, radius, latitude, longitude)

            val result = awaitItem()

            Assert.assertTrue(mGeoMessageSendMessageCallFlag)
            Assert.assertEquals(SendMessageDomainResult::class, result::class)
        }
    }
}