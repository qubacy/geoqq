package com.qubacy.geoqq.ui.screen.geochat.chat

import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GeoChatViewModelTest {
    private lateinit var mModel: GeoChatViewModel

    @Before
    fun setup() {
        mModel = GeoChatViewModel()
    }

    data class IsMessageCorrectTestCase(
        val messageText: String,
        val expectedResult: Boolean
    )

    @Test
    fun isMessageCorrectTest() {
        val testCases = listOf(
            IsMessageCorrectTestCase("", false),
            IsMessageCorrectTestCase(" ", true), // todo: it should be failed!
            IsMessageCorrectTestCase("f", true),
            IsMessageCorrectTestCase("fwefwef wef wfwe", true),
        )

        for (testCase in testCases) {
            val result = mModel.isMessageCorrect(testCase.messageText)

            assertEquals(testCase.expectedResult, result)
        }
    }
}