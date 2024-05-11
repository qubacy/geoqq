package com.qubacy.geoqq.ui.application.activity._common.screen.login.model

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.domain.login.usecase.impl.LoginUseCaseImpl
import com.qubacy.geoqq.domain.login.usecase._common.result.SignedInDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.operation.SignInUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class LoginViewModelTest(

) : BusinessViewModelTest<LoginUiState, LoginUseCaseImpl, LoginViewModel>(
    LoginUseCaseImpl::class.java
) {
    private var mUseCaseSignInWithTokenCallFlag = false
    private var mUseCaseSignInWithLoginDataCallFlag = false
    private var mUseCaseSignUpCallFlag = false

    override fun clear() {
        super.clear()

        mUseCaseSignInWithTokenCallFlag = false
        mUseCaseSignInWithLoginDataCallFlag = false
        mUseCaseSignUpCallFlag = false
    }

    override fun initUseCase(): LoginUseCaseImpl {
        val loginUseCaseMock = super.initUseCase()

        Mockito.`when`(loginUseCaseMock.signIn()).thenAnswer {
            mUseCaseSignInWithTokenCallFlag = true

            Unit
        }
        Mockito.`when`(loginUseCaseMock.signIn(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mUseCaseSignInWithLoginDataCallFlag = true

            Unit
        }
        Mockito.`when`(loginUseCaseMock.signUp(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mUseCaseSignUpCallFlag = true

            Unit
        }

        return loginUseCaseMock
    }

    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataSource: LocalErrorDatabaseDataSourceImpl
    ): LoginViewModel {
        return LoginViewModel(savedStateHandle, errorDataSource, mUseCase)
    }

    @Test
    fun setLoginModeTest() {
        val expectedInitLoginMode = LoginUiState.LoginMode.SIGN_IN
        val expectedFinalLoginMode = LoginUiState.LoginMode.SIGN_UP

        mModel.setLoginMode(expectedInitLoginMode)

        Assert.assertEquals(expectedInitLoginMode, mModel.uiState.loginMode)

        mModel.setLoginMode(expectedFinalLoginMode)

        Assert.assertEquals(expectedFinalLoginMode, mModel.uiState.loginMode)
    }

    @Test
    fun isSignInDataValidTest() {
        class TestCase(
            val login: String,
            val password: String,
            val isValid: Boolean
        )

        val testCases = listOf(
            TestCase("testtest", "testtest", true)
        )

        for (testCase in testCases) {
            val gottenIsValid = mModel.isSignInDataValid(testCase.login, testCase.password)

            Assert.assertEquals(testCase.isValid, gottenIsValid)
        }
    }

    @Test
    fun isSignUpDataValidTest() {
        class TestCase(
            val login: String,
            val password: String,
            val passwordAgain: String,
            val isValid: Boolean
        )

        val testCases = listOf(
            TestCase("testtest", "testtest", "testtest2", false),
            TestCase("testtest", "testtest", "testtest", true),
        )

        for (testCase in testCases) {
            val gottenIsValid = mModel.isSignUpDataValid(
                testCase.login, testCase.password, testCase.passwordAgain)

            Assert.assertEquals(testCase.isValid, gottenIsValid)
        }
    }

    @Test
    fun signInWithTokenTest() = runTest {
        val expectedIsLoading = true

        mModel.uiOperationFlow.test {
            mModel.signIn()

            val operation = awaitItem()

            Assert.assertTrue(mUseCaseSignInWithTokenCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val isLoadingOperation = operation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedIsLoading, isLoadingOperation.isLoading)
        }
    }

    @Test
    fun signInWithLoginDataTest() = runTest {
        val login = "login"
        val password = "password"
        val expectedIsLoading = true

        mModel.uiOperationFlow.test {
            mModel.signIn(login, password)

            val operation = awaitItem()

            Assert.assertTrue(mUseCaseSignInWithLoginDataCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val isLoadingOperation = operation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedIsLoading, isLoadingOperation.isLoading)
        }
    }

    @Test
    fun signUpTest() = runTest {
        val login = "login"
        val password = "password"
        val expectedIsLoading = true

        mModel.uiOperationFlow.test {
            mModel.signUp(login, password)

            val operation = awaitItem()

            Assert.assertTrue(mUseCaseSignUpCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val isLoadingOperation = operation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedIsLoading, isLoadingOperation.isLoading)
        }
    }

    @Test
    fun processSignedInDomainResultWithErrorTest() = runTest {
        val expectedError = TestError.normal
        val initIsLoading = true
        val expectedIsLoading = false
        val initAutoSignInAllowedState = true
        val expectedAutoSignInAllowedState = false
        val errorDomainResult = SignedInDomainResult(expectedError)

        setUiState(LoginUiState(
            autoSignInAllowed = initAutoSignInAllowedState,
            isLoading = initIsLoading)
        )

        mModel.uiOperationFlow.test {
            mResultFlow.emit(errorDomainResult)

            val errorOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedAutoSignInAllowedState, mModel.uiState.autoSignInAllowed)
            Assert.assertEquals(expectedError, mModel.uiState.error)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            val errorOperationCast = errorOperation as ErrorUiOperation
            val setLoadingOperationCast = setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedError, errorOperationCast.error)
            Assert.assertEquals(expectedIsLoading, setLoadingOperationCast.isLoading)
        }
    }

    @Test
    fun processSignedInDomainResultTest() = runTest {
        val initIsLoading = true
        val expectedIsLoading = false
        val signedInDomainResult = SignedInDomainResult()
        val initAutoSignInAllowedState = true
        val expectedAutoSignInAllowedState = false

        setUiState(LoginUiState(
            autoSignInAllowed = initAutoSignInAllowedState,
            isLoading = initIsLoading)
        )

        mModel.uiOperationFlow.test {
            mResultFlow.emit(signedInDomainResult)

            val signInOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(SignInUiOperation::class, signInOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedAutoSignInAllowedState, mModel.uiState.autoSignInAllowed)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            val setLoadingOperationCast = setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedIsLoading, setLoadingOperationCast.isLoading)
        }
    }
}