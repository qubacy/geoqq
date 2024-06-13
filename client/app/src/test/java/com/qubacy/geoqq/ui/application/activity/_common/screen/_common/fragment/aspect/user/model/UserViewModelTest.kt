package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model

import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.turbine.extension.awaitAllItems
import com.qubacy.geoqq.domain._common._test.context.UseCaseTestContext
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result.update.UserUpdatedDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

interface UserViewModelTest<ViewModelType : BusinessViewModel<*, *>> {
    companion object {
        val DEFAULT_USER = UseCaseTestContext.DEFAULT_USER
    }

    @Test
    fun onUserGetUserTest() = runTest {
        val user = getUserUser()
        val model = getUserModel()

        val getUserDomainResult = GetUserDomainResult(interlocutor = user)

        val expectedUserPresentation = user.toUserPresentation()

        model.uiOperationFlow.test {
            getUserResultFlow().emit(getUserDomainResult)

            val operations = awaitAllItems<UiOperation>()

            assertUserGetUserOperations(expectedUserPresentation, operations)
        }
    }

    @Test
    fun onUserUpdateUserTest() = runTest {
        val user = getUserUser()
        val model = getUserModel()

        val updateUserDomainResult = UserUpdatedDomainResult(user = user)

        val expectedUserPresentation = user.toUserPresentation()

        model.uiOperationFlow.test {
            getUserResultFlow().emit(updateUserDomainResult)

            val operations = awaitAllItems<UiOperation>()

            assertUserUpdateUserOperations(expectedUserPresentation, operations)
        }
    }

    fun getUserUser(): User {
        return DEFAULT_USER
    }

    fun assertUserGetUserOperations(
        userPresentation: UserPresentation,
        uiOperations: List<UiOperation>
    ) {

    }
    fun assertUserUpdateUserOperations(
        userPresentation: UserPresentation,
        uiOperations: List<UiOperation>
    ) {

    }

    fun getUserResultFlow(): MutableSharedFlow<DomainResult>
    fun getUserModel(): ViewModelType
}