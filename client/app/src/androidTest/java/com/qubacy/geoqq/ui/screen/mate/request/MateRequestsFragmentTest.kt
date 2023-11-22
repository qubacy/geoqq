package com.qubacy.geoqq.ui.screen.mate.request

import android.net.Uri
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.carousel3dlib.layoutmanager.Carousel3DLayoutManager
import com.google.android.material.card.MaterialCardView
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.state.MateRequestsUiState
import com.qubacy.geoqq.ui.util.IsChildWithIndexViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MateRequestsFragmentTest {
    class MateRequestsUiStateTestData(
        private val mMateRequestsStateFlow: MutableStateFlow<MateRequestsState?>,
        private val mateRequestFlow: LiveData<MateRequestsUiState?>
    ) {
        fun setMateRequests(mateRequests: List<MateRequest>, users: List<User>) {
            val operations = listOf<Operation>()

            runBlocking {
                mMateRequestsStateFlow.emit(MateRequestsState(mateRequests, users, operations))
            }
        }

        fun removeMateRequest(mateRequest: MateRequest) {
            val prevUsers = if (mateRequestFlow.value == null) listOf() else mateRequestFlow.value!!.users
            val prevRequests = if (mateRequestFlow.value == null) listOf() else mateRequestFlow.value!!.mateRequests

            val newUsers = prevUsers.filter { it.id != mateRequest.userId }
            val newRequests = prevRequests.filter { it.userId != mateRequest.userId }

            val operations = listOf<Operation>()

            runBlocking {
                mMateRequestsStateFlow.emit(MateRequestsState(newRequests, newUsers, operations))
            }
        }

        fun showError(error: Error) {
            val users = if (mateRequestFlow.value == null) listOf() else mateRequestFlow.value!!.users
            val requests = if (mateRequestFlow.value == null) listOf() else mateRequestFlow.value!!.mateRequests

            val operations = listOf(
                HandleErrorOperation(error)
            )

            val mateRequestsState = MateRequestsState(requests, users, operations)

            runBlocking {
                mMateRequestsStateFlow.emit(mateRequestsState)
            }
        }
    }

    companion object {
        val TEST_URI: Uri = Uri.parse(String())
        val TEST_USERS = listOf(
            User(1, "iii", "test", TEST_URI, false),
            User(2, "aaa", "test", TEST_URI, false),
            User(3, "ggg", "test", TEST_URI, false),
        )
        val TEST_MATE_REQUESTS = listOf(
            MateRequest(0, 1),
            MateRequest(1, 2),
            MateRequest(2, 3),
        )
    }

    private lateinit var mMateRequestsFragmentScenarioRule: FragmentScenario<MateRequestsFragment>
    private lateinit var mModel: MateRequestsViewModel

    private lateinit var mMateRequestsUiStateTestData: MateRequestsUiStateTestData

    @Before
    fun setup() {
        mMateRequestsFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_Mates)
        mMateRequestsFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        mMateRequestsFragmentScenarioRule.onFragment {
            mModel = ViewModelProvider(it)[MateRequestsViewModel::class.java]
        }

        val mateRequestsStateFlowFieldReflection = MateRequestsViewModel::class.java
            .getDeclaredField("mMateRequestsStateFlow")
            .apply {
                isAccessible = true
            }

        mMateRequestsUiStateTestData = MateRequestsUiStateTestData(
            mateRequestsStateFlowFieldReflection.get(mModel) as MutableStateFlow<MateRequestsState?>,
            mModel.mateRequestFlow
        )
    }

    @Test
    fun allElementsInPlaceTest() {
        Espresso.onView(withId(R.id.requests_recycler_view))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsEnabledTest() {
        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun settingThreeMateRequestsLeadsToShowingThreeMateRequestCardsTest() {
        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(TEST_MATE_REQUESTS, TEST_USERS)
        }

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(
                TEST_MATE_REQUESTS.size + Carousel3DLayoutManager.EDGE_INVISIBLE_ITEMS_COUNT)))
    }

    @Test
    fun mateRequestsListRollOnVerticalSwipeTest() {
        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(TEST_MATE_REQUESTS, TEST_USERS)
        }

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeDown(), WaitingViewAction(500))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isAssignableFrom(MaterialCardView::class.java),
            ViewMatchers.hasDescendant(withText(TEST_USERS[1].username)),
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )).check(IsChildWithIndexViewAssertion(3))

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeUp(), WaitingViewAction(500))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isAssignableFrom(MaterialCardView::class.java),
            ViewMatchers.hasDescendant(withText(TEST_USERS[0].username)),
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )).check(IsChildWithIndexViewAssertion(3))
    }

    @Test
    fun horizontalSwipeLeadsToMateRequestDisappearanceTest() {
        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(TEST_MATE_REQUESTS, TEST_USERS)
        }

        // todo: this works shitty.. advance it once the DATA layer will be ready to handle things of
        // this sort

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeLeft(), WaitingViewAction(500) {
                mMateRequestsUiStateTestData.removeMateRequest(TEST_MATE_REQUESTS[0])
            }, WaitingViewAction(1000))
            .check(ViewAssertions.matches(hasChildCount(
                TEST_USERS.size - 1 + Carousel3DLayoutManager.EDGE_INVISIBLE_ITEMS_COUNT)))
        Espresso.onView(withText(TEST_USERS[0].username))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingNormalErrorOperationLeadsToShowingDialogTest() {
        val error = Error(0, "Test", false)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.showError(error)
        }

        Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingCriticalErrorOperationLeadsToAppClosingTest() {
        val error = Error(0, "Test", true)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.showError(error)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Exception) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }
}