package com.qubacy.geoqq.screen.geochat.auth.signup

import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SignUpViewModelTest {
    private lateinit var mModel: SignUpViewModel

    @Before
    fun setup() {
        mModel = SignUpViewModel()
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
}