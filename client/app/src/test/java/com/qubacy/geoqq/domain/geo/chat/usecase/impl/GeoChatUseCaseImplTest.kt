package com.qubacy.geoqq.domain.geo.chat.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._common._test.mock.AuthDataRepositoryMockContainer
import com.qubacy.geoqq.data.geo.message.repository._common.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository._common.result.get.GetGeoMessagesDataResult
import com.qubacy.geoqq.data.geo.message.repository.impl._common._test.context.GeoMessageDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.geo._common.model.toGeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.update.UpdateGeoMessagesDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.UserUseCase
import com.qubacy.geoqq.domain.user.usecase._common._test.mock.InterlocutorUseCaseMockContainer
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._common._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._common._test.mock.MateRequestUseCaseMockContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class GeoChatUseCaseImplTest : UseCaseTest<GeoChatUseCaseImpl>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
        val DEFAULT_DATA_MESSAGE = GeoMessageDataRepositoryTestContext.DEFAULT_DATA_MESSAGE
    }

    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mMateRequestUseCaseMockContainer: MateRequestUseCaseMockContainer
    private lateinit var mInterlocutorUseCaseMockContainer: InterlocutorUseCaseMockContainer
    private lateinit var mLogoutUseCaseMockContainer: LogoutUseCaseMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer
    private lateinit var mAuthDataRepositoryMockContainer: AuthDataRepositoryMockContainer

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

        mAuthDataRepositoryMockContainer = AuthDataRepositoryMockContainer()
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
            .plus(mUserDataRepositoryMockContainer.userDataRepositoryMock)
            .plus(mAuthDataRepositoryMockContainer.authDataRepositoryMock)
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
        mUseCase = GeoChatUseCaseImpl(
            dependencies[0] as LocalErrorDatabaseDataSource,
            dependencies[1] as MateRequestUseCase,
            dependencies[2] as UserUseCase,
            dependencies[3] as LogoutUseCase,
            dependencies[4] as GeoMessageDataRepository,
            dependencies[5] as UserDataRepository,
            dependencies[6] as AuthDataRepository
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