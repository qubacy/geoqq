package com.qubacy.geoqq.ui.screen.mate.request

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.carousel3dlib.layoutmanager.Carousel3DLayoutManager
import com.google.android.material.card.MaterialCardView
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.mates.request.entity.MateRequest
import com.qubacy.geoqq.data.mates.request.state.MateRequestsState
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

            val newUsers = prevUsers.filter { it.userId != mateRequest.userId }
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
        val users = listOf(
            User(0, "me"),
            User(1, "aaa"),
            User(2, "ggg"),
        )
        val mateRequests = listOf(
            MateRequest(0),
            MateRequest(1),
            MateRequest(2),
        )

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(mateRequests, users)
        }

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(
                mateRequests.size + Carousel3DLayoutManager.EDGE_INVISIBLE_ITEMS_COUNT)))
    }

    @Test
    fun mateRequestsListRollOnVerticalSwipeTest() {
        val users = listOf(
            User(0, "me"),
            User(1, "aaa"),
            User(2, "ggg"),
        )
        val mateRequests = listOf(
            MateRequest(0),
            MateRequest(1),
            MateRequest(2),
        )

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(mateRequests, users)
        }

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeDown(), WaitingViewAction(500))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isAssignableFrom(MaterialCardView::class.java),
            ViewMatchers.hasDescendant(withText(users[1].username)),
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )).check(IsChildWithIndexViewAssertion(3))

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeUp(), WaitingViewAction(500))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isAssignableFrom(MaterialCardView::class.java),
            ViewMatchers.hasDescendant(withText(users[0].username)),
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )).check(IsChildWithIndexViewAssertion(3))
    }

    @Test
    fun horizontalSwipeLeadsToMateRequestDisappearanceTest() {
        val users = listOf(
            User(0, "me"),
            User(1, "aaa"),
            User(2, "ggg"),
        )
        val mateRequests = listOf(
            MateRequest(0),
            MateRequest(1),
            MateRequest(2),
        )

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(mateRequests, users)
        }

        // todo: this works shitty.. advance it once the DATA layer will be ready to handle things of
        // this sort

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeLeft(), WaitingViewAction(500) {
                mMateRequestsUiStateTestData.removeMateRequest(mateRequests[0])
            }, WaitingViewAction(1000))
            .check(ViewAssertions.matches(hasChildCount(
                users.size - 1 + Carousel3DLayoutManager.EDGE_INVISIBLE_ITEMS_COUNT)))
        Espresso.onView(withText(users[0].username))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingNormalErrorOperationLeadsToShowingDialogTest() {
        val error = Error(R.string.error_chat_message_sending_failed, Error.Level.NORMAL)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.showError(error)
        }

        Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingCriticalErrorOperationLeadsToAppClosingTest() {
        val error = Error(R.string.error_chat_message_sending_failed, Error.Level.CRITICAL)

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