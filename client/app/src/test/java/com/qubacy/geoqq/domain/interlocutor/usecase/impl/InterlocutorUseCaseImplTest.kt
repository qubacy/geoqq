package com.qubacy.geoqq.domain.interlocutor.usecase.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository._common.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.interlocutor.usecase._common.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._common._test.mock.LogoutUseCaseMockContainer
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class InterlocutorUseCaseImplTest : UseCaseTest<InterlocutorUseCaseImpl>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
    }

    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mLogoutUseCaseMockContainer: LogoutUseCaseMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    override fun initDependencies(): List<Any> {
        val superDependencies =  super.initDependencies()

        mLogoutUseCaseMockContainer = LogoutUseCaseMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        return superDependencies
            .plus(mLogoutUseCaseMockContainer.logoutUseCaseMock)
            .plus(mUserDataRepositoryMockContainer.userDataRepository)
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = InterlocutorUseCaseImpl(
            dependencies[0] as LocalErrorDatabaseDataSource,
            dependencies[1] as LogoutUseCase,
            dependencies[2] as UserDataRepository
        )
    }

    @Test
    fun getInterlocutorTest() = runTest {
        val remoteUser = DEFAULT_DATA_USER.copy(username = "updated user")

        val remoteGetUsersByIdsResult = GetUsersByIdsDataResult(true, listOf(remoteUser))

        val expectedRemoteUser = remoteUser.toUser()

        mUserDataRepositoryMockContainer.getUsersByIdsResult = remoteGetUsersByIdsResult

        mUseCase.resultFlow.test {
            mUseCase.getInterlocutor(remoteUser.id)

            Assert.assertTrue(mUserDataRepositoryMockContainer.getUsersByIdsCallFlag)

            val remoteResult = awaitItem()

            Assert.assertEquals(GetInterlocutorDomainResult::class, remoteResult::class)
            Assert.assertTrue(remoteResult.isSuccessful())

            val gottenRemoteUser = (remoteResult as GetInterlocutorDomainResult).interlocutor

            Assert.assertEquals(expectedRemoteUser, gottenRemoteUser)
        }
    }
}