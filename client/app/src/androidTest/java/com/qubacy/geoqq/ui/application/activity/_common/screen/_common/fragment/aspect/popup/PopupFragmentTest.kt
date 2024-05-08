package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.popup

import android.widget.Toast
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.BaseFragment
import kotlinx.coroutines.test.runTest
import org.junit.Test

interface PopupFragmentTest<FragmentType>
    where FragmentType : BaseFragment<*>, FragmentType : PopupFragment
{
    companion object {
        const val TEST_MESSAGE = "test message"
    }

    fun beforePopupMessageOccurredTest() { }
    @Test
    fun onPopupMessageOccurredTest() = runTest {
        beforePopupMessageOccurredTest()

        getPopupActivityScenario().onActivity {
            getPopupFragment().onPopupMessageOccurred(TEST_MESSAGE, Toast.LENGTH_SHORT)
        }

        Espresso.onView(withText(TEST_MESSAGE))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }
    fun getPopupActivityScenario(): ActivityScenario<*>
    fun getPopupFragment(): FragmentType
}