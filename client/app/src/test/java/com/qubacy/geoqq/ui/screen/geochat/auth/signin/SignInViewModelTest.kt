package com.qubacy.geoqq.ui.screen.geochat.auth.signin

import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SignInViewModelTest {
    private lateinit var mModel: SignInViewModel

    @Before
    fun setup() {
        mModel = SignInViewModel()
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
}