package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.InterlocutorFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.InterlocutorFragmentTest
import org.junit.Test

interface ChatFragmentTest<FragmentType>
    where FragmentType : ChatFragment,
          FragmentType : InterlocutorFragment // todo: is it right?
{
    @Test
    fun onChatFragmentMessageSentTest()

    @Test
    fun onChatFragmentMateRequestSentTest() {
        beforeOnChatFragmentMateRequestSentTest()

        val interlocutorTest = getChatFragmentInterlocutorFragmentTest()
        val fragment = getChatFragmentFragment()

        val userPresentation = interlocutorTest.getInterlocutorFragmentUserPresentation()

        interlocutorTest.getInterlocutorFragmentActivityScenario().onActivity {
            fragment.openInterlocutorDetailsSheet(userPresentation)
            fragment.onChatFragmentMateRequestSent()
        }

        Espresso.onView(withText(R.string.fragment_chat_snackbar_message_mate_request_sent))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun beforeOnChatFragmentMateRequestSentTest()

    fun getChatFragmentFragment(): FragmentType

    fun getChatFragmentInterlocutorFragmentTest(): InterlocutorFragmentTest<FragmentType>
}