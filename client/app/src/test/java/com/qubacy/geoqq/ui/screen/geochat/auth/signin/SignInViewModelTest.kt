package com.qubacy.geoqq.ui.screen.geochat.auth.signin

import app.cash.turbine.test
import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.domain.geochat.signin.operation.ProcessSignInResultOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.operation.PassSignInUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.state.SignInUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

class SignInViewModelTest : ViewModelTest() {
    private lateinit var mModel: SignInViewModel
    private lateinit var mSignInStateFlow: MutableStateFlow<SignInState?>

    private lateinit var mSignInUiStateFlow: Flow<SignInUiState?>

    private fun setNewUiState(newState: SignInState?) = runTest {
        if (newState == null) return@runTest

        mSignInStateFlow.emit(newState)
    }

    private fun initSignInViewModel(
        newState: SignInState? = null
    ) {
        val signInUseCaseMock = mock(SignInUseCase::class.java)

        Mockito.`when`(signInUseCaseMock.signInWithLocalToken())
            .thenAnswer { setNewUiState(newState) }
        Mockito.`when`(signInUseCaseMock.signInWithLoginPassword(anyString(), anyString()))
            .thenAnswer { setNewUiState(newState) }

        mSignInStateFlow = MutableStateFlow<SignInState?>(null)

        Mockito.`when`(signInUseCaseMock.stateFlow).thenAnswer {
            mSignInStateFlow
        }

        val mSignInUiStateFlowFieldReflection = SignInViewModel::class.java
            .getDeclaredField("mSignInUiStateFlow")
            .apply { isAccessible = true }

        mModel = SignInViewModel(signInUseCaseMock)
        mSignInUiStateFlow = mSignInUiStateFlowFieldReflection.get(mModel) as Flow<SignInUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initSignInViewModel()
    }

    data class IsSignInDataCorrectTestCase(
        val username: String,
        val password: String,
        val expectedResult: Boolean
    )

    @Test
    fun isSignInDataCorrectTest() {
        val testCases = listOf(
            IsSignInDataCorrectTestCase("", "", false),
            IsSignInDataCorrectTestCase("login", "", false),
            IsSignInDataCorrectTestCase("", "pass",false),
            IsSignInDataCorrectTestCase("login", "pass",false),
            IsSignInDataCorrectTestCase("loginnnn", "pass", false),
            IsSignInDataCorrectTestCase("login", "password", false),
            IsSignInDataCorrectTestCase("login nnn", "password", false),
            IsSignInDataCorrectTestCase("loginnnn", "pass word", false),
            IsSignInDataCorrectTestCase("897533665538655919920744202246802", "password", false),
            IsSignInDataCorrectTestCase("loginnnn", "897533665538655919920744202246802", false),
            IsSignInDataCorrectTestCase("loginnnn", "password", true),
        )

        for (testCase in testCases) {
            val result = mModel.isSignInDataCorrect(testCase.username, testCase.password)

            Assert.assertEquals(testCase.expectedResult, result)
        }
    }

    @Test
    fun signInWithLocalTokenTest() = runTest {
        val newState = SignInState(
            listOf(ProcessSignInResultOperation(true))
        )

        initSignInViewModel(newState)

        mSignInUiStateFlow.test {
            awaitItem()
            mModel.signIn()

            val gottenUiState = awaitItem()

            Assert.assertEquals(PassSignInUiOperation::class, gottenUiState!!.takeUiOperation()!!::class)
        }
    }

    @Test
    fun signInWithLoginPasswordTest() = runTest {
        val newState = SignInState(
            listOf(ProcessSignInResultOperation(true))
        )

        initSignInViewModel(newState)

        mSignInUiStateFlow.test {
            awaitItem() // skipping a null state;
            mModel.signIn(String(), String())

            val gottenUiState = awaitItem()

            Assert.assertEquals(PassSignInUiOperation::class, gottenUiState!!.takeUiOperation()!!::class)
        }
    }
}