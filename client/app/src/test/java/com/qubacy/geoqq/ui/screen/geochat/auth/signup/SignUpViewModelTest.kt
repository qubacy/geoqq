package com.qubacy.geoqq.ui.screen.geochat.auth.signup

import app.cash.turbine.test
import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase
import com.qubacy.geoqq.domain.geochat.signup.operation.ApproveSignUpOperation
import com.qubacy.geoqq.domain.geochat.signup.state.SignUpState
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.operation.PassSignUpUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.state.SignUpUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

class SignUpViewModelTest : ViewModelTest() {
    private lateinit var mModel: SignUpViewModel
    private lateinit var mSignUpStateFlow: MutableStateFlow<SignUpState?>

    private lateinit var mSignUpUiStateFlow: Flow<SignUpUiState?>

    private fun setNewUiState(newState: SignUpState?) = runTest {
        if (newState == null) return@runTest

        mSignUpStateFlow.emit(newState)
    }

    private fun initSignUpViewModel(
        newState: SignUpState? = null
    ) {
        val signUpUseCaseMock = mock(SignUpUseCase::class.java)

        Mockito.`when`(signUpUseCaseMock.signUp(anyString(), anyString()))
            .thenAnswer { setNewUiState(newState) }

        mSignUpStateFlow = MutableStateFlow<SignUpState?>(null)

        Mockito.`when`(signUpUseCaseMock.stateFlow).thenAnswer {
            mSignUpStateFlow
        }

        val mSignInUiStateFlowFieldReflection = SignUpViewModel::class.java
            .getDeclaredField("mSignUpUiStateFlow")
            .apply { isAccessible = true }

        mModel = SignUpViewModel(signUpUseCaseMock)
        mSignUpUiStateFlow = mSignInUiStateFlowFieldReflection.get(mModel) as Flow<SignUpUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initSignUpViewModel()
    }

    data class IsSignUpDataCorrectTestCase(
        val username: String,
        val password: String,
        val confirmationPassword: String,
        val expectedResult: Boolean
    )

    @Test
    fun isSignUpDataCorrectTest() {
        val testCases = listOf(
            IsSignUpDataCorrectTestCase("", "", "", false),
            IsSignUpDataCorrectTestCase("login", "", "", false),
            IsSignUpDataCorrectTestCase("", "pass", "", false),
            IsSignUpDataCorrectTestCase("", "", "pass", false),
            IsSignUpDataCorrectTestCase("login", "pass", "", false),
            IsSignUpDataCorrectTestCase("login", "", "pass", false),
            IsSignUpDataCorrectTestCase("", "pass", "pass", false),
            IsSignUpDataCorrectTestCase("login", "pass", "pass", false),
            IsSignUpDataCorrectTestCase("loginnnn", "pass", "pass", false),
            IsSignUpDataCorrectTestCase("login", "password", "password", false),
            IsSignUpDataCorrectTestCase("login nnn", "password", "password", false),
            IsSignUpDataCorrectTestCase("loginnnn", "pass word", "pass word", false),
            IsSignUpDataCorrectTestCase("897533665538655919920744202246802", "password", "password", false),
            IsSignUpDataCorrectTestCase("loginnnn", "897533665538655919920744202246802", "897533665538655919920744202246802", false),
            IsSignUpDataCorrectTestCase("loginnnn", "password", "password", true),
        )

        for (testCase in testCases) {
            val result = mModel.isSignUpDataCorrect(
                testCase.username, testCase.password, testCase.confirmationPassword)

            Assert.assertEquals(testCase.expectedResult, result)
        }
    }

    @Test
    fun signUpTest() = runTest {
        val newState = SignUpState(
            listOf(ApproveSignUpOperation())
        )

        initSignUpViewModel(newState)

        mSignUpUiStateFlow.test {
            awaitItem()
            mModel.signUp(String(), String(), String())

            val gottenState = awaitItem()

            Assert.assertEquals(PassSignUpUiOperation::class, gottenState!!.takeUiOperation()!!::class)
        }
    }
}