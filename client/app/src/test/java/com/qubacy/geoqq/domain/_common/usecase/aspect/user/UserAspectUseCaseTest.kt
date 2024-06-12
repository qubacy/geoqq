package com.qubacy.geoqq.domain._common.usecase.aspect.user

import app.cash.turbine.test
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository._common.result.updated.UserUpdatedDataResult
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.base._test.util.runCoroutineTestCase
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result.update.UserUpdatedDomainResult
import org.junit.Assert
import org.junit.Test

interface UserAspectUseCaseTest {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
    }

    fun getUserAspectUseCaseTestUseCase(): UseCase
    fun getUserAspectUseCaseTestUserDataRepositoryMockContainer(): UserDataRepositoryMockContainer

    @Test
    fun processUserUpdatedDataResultTest() = runCoroutineTestCase(
        getUserAspectUseCaseTestUseCase()
    ) {
        val dataUser = DEFAULT_DATA_USER
        val userUpdatedDataResult = UserUpdatedDataResult(dataUser)

        val expectedUser = dataUser.toUser()

        getUserAspectUseCaseTestUseCase().resultFlow.test {
            getUserAspectUseCaseTestUserDataRepositoryMockContainer()
                .resultFlow.emit(userUpdatedDataResult)

            val result = awaitItem()

            Assert.assertEquals(UserUpdatedDomainResult::class, result::class)

            val gottenUser = (result as UserUpdatedDomainResult).interlocutor

            Assert.assertEquals(expectedUser, gottenUser)
        }
    }
}