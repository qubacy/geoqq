package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui._common._test.view.util.matcher.image.common.CommonImageViewMatcher
import org.junit.Test

interface InterlocutorFragmentTest<FragmentType : InterlocutorFragment> {

    @Test
    fun openInterlocutorDetailsSheetTest() {
        beforeOpenInterlocutorDetailsSheetTest()

        val fragment = getInterlocutorFragmentFragment()
        val userPresentation = getInterlocutorFragmentUserPresentation()

//        val expectedAvatarUri = userPresentation.avatar.uri
        val expectedUsername = userPresentation.username
        val expectedAboutMe = userPresentation.description

        getInterlocutorFragmentActivityScenario().onActivity {
            fragment.openInterlocutorDetailsSheet(userPresentation)
        }

        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(withId(R.id.component_bottom_sheet_user_image_avatar))
//            .check(ViewAssertions.matches(CommonImageViewMatcher(expectedAvatarUri)))
        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        expectedAboutMe?.also {
            Espresso.onView(withText(it))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun adjustInterlocutorFragmentUiWithInterlocutorTest() {
        beforeAdjustInterlocutorFragmentUiWithInterlocutorTest()

        val fragment = getInterlocutorFragmentFragment()
        val initUserPresentation = getInterlocutorFragmentUserPresentation()

        val userPresentation = initUserPresentation
            .copy(username = "updated username", description = "updated about me")

//        val expectedAvatarUri = userPresentation.avatar.uri
        val expectedUsername = userPresentation.username
        val expectedAboutMe = userPresentation.description

        getInterlocutorFragmentActivityScenario().onActivity {
            fragment.openInterlocutorDetailsSheet(userPresentation)
            fragment.adjustInterlocutorFragmentUiWithInterlocutor(userPresentation)
        }

        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(withId(R.id.component_bottom_sheet_user_image_avatar))
//            .check(ViewAssertions.matches(CommonImageViewMatcher(expectedAvatarUri)))
        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        expectedAboutMe?.also {
            Espresso.onView(withText(it))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    fun beforeAdjustInterlocutorFragmentUiWithInterlocutorTest()

    fun beforeOpenInterlocutorDetailsSheetTest()

    fun getInterlocutorFragmentFragment(): FragmentType

    fun getInterlocutorFragmentActivityScenario(): ActivityScenario<*>

    fun getInterlocutorFragmentUserPresentation(): UserPresentation {
        return UserPresentation(
            0L,
            "test username",
            "test about me",
            getInterlocutorFragmentAvatar(),
            false,
            false
        )
    }

    fun getInterlocutorFragmentAvatar(): ImagePresentation
}