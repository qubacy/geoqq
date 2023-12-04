package com.qubacy.geoqq.ui.screen.mate.request

import androidx.cardview.widget.CardView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.navigation.testing.TestNavHostController
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
import com.example.carousel3dlib.layoutmanager.Carousel3DLayoutManager
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.operation.MateRequestAnswerProcessedOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestsOperation
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState
import com.qubacy.geoqq.common.ApplicationTestBase
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import com.qubacy.geoqq.domain.generator.MateRequestGeneratorUtility
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
class MateRequestsFragmentTest : ApplicationTestBase() {
    class MateRequestsUiStateTestData(
        private val mMateRequestsStateFlow: MutableStateFlow<MateRequestsState?>,
        private val mateRequestFlow: LiveData<MateRequestsUiState?>
    ) {
        private fun mateRequestsToChunks(
            mateRequests: List<MateRequest>
        ): HashMap<Long, List<MateRequest>> {
            val mateRequestCount = mateRequests.size
            val chunkCount = mateRequestCount / MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE +
                if (mateRequestCount % MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE == 0) 0
                else 1

            val mateRequestChunks = hashMapOf<Long, List<MateRequest>>()

            for (i in 0 until chunkCount) {
                val mateRequestStartIndex = (i * MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE)
                val mateRequestEndIndex = (if (i != chunkCount - 1)
                    mateRequestStartIndex + MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE
                else mateRequestCount - mateRequestStartIndex).toInt()

                mateRequestChunks[mateRequestStartIndex.toLong()] = mateRequests.subList(
                    mateRequestStartIndex, mateRequestEndIndex)
            }

            return mateRequestChunks
        }

        fun setMateRequests(mateRequests: List<MateRequest>, users: List<User>, isInit: Boolean) {
            val mateRequestChunks = mateRequestsToChunks(mateRequests)

            setMateRequests(mateRequestChunks, users, isInit)
        }

        fun setMateRequests(
            mateRequestChunks: HashMap<Long, List<MateRequest>>, users: List<User>, isInit: Boolean
        ) {
            val operations = listOf<Operation>(
                SetMateRequestsOperation(isInit)
            )

            runBlocking {
                mMateRequestsStateFlow.emit(MateRequestsState(mateRequestChunks, users, operations))
            }
        }

        fun removeMateRequest(mateRequest: MateRequest) {
            val prevUsers = if (mateRequestFlow.value == null) listOf() else mateRequestFlow.value!!.users
            val prevRequests = if (mateRequestFlow.value == null) listOf() else mateRequestFlow.value!!.mateRequests

            val newUsers = prevUsers.filter { it.id != mateRequest.userId }
            val newRequests = prevRequests.filter { it.userId != mateRequest.userId }

            val operations = listOf<Operation>(
                MateRequestAnswerProcessedOperation()
            )

            val mateRequestChunks = mateRequestsToChunks(newRequests)

            runBlocking {
                mMateRequestsStateFlow.emit(MateRequestsState(mateRequestChunks, newUsers, operations))
            }
        }

        fun showError(error: Error) {
            val users = if (mateRequestFlow.value == null) listOf() else mateRequestFlow.value!!.users
            val requests = if (mateRequestFlow.value == null) listOf() else mateRequestFlow.value!!.mateRequests
            val mateRequestChunks = mateRequestsToChunks(requests)

            val operations = listOf(
                HandleErrorOperation(error)
            )

            val mateRequestsState = MateRequestsState(mateRequestChunks, users, operations)

            runBlocking {
                mMateRequestsStateFlow.emit(mateRequestsState)
            }
        }
    }

    private lateinit var mMateRequestsFragmentScenarioRule: FragmentScenario<MateRequestsFragment>
    private lateinit var mNavHostController: TestNavHostController
    private lateinit var mModel: MateRequestsViewModel

    private lateinit var mMateRequestsUiStateTestData: MateRequestsUiStateTestData

    @Before
    override fun setup() {
        super.setup()

        mMateRequestsFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_Mates)
        mMateRequestsFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        var fragment: MateRequestsFragment? = null

        mNavHostController = TestNavHostController(ApplicationProvider.getApplicationContext())

        mMateRequestsFragmentScenarioRule.onFragment {
            mNavHostController.setGraph(R.navigation.nav_graph)
            mNavHostController.setCurrentDestination(R.id.mateRequestsFragment)

            fragment = it
        }

        val mModelFieldReflection = MateRequestsFragment::class.java.superclass.superclass
            .getDeclaredField("mModel").apply { isAccessible = true }
        val mateRequestsStateFlowFieldReflection = MateRequestsViewModel::class.java
            .getDeclaredField("mMateRequestsStateFlow")
            .apply {
                isAccessible = true
            }

        mModel = mModelFieldReflection.get(fragment) as MateRequestsViewModel

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
        val initMateRequests = MateRequestGeneratorUtility.generateMateRequests(1)
        val initUsers = UserGeneratorUtility.generateUsers(1, 1)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(initMateRequests, initUsers, true)
        }

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun settingThreeMateRequestsLeadsToShowingThreeMateRequestCardsTest() {
        val initMateRequests = MateRequestGeneratorUtility.generateMateRequests(3)
        val initUsers = UserGeneratorUtility.generateUsers(3, 1)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(initMateRequests, initUsers, true)
        }

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(
                initUsers.size + Carousel3DLayoutManager.EDGE_INVISIBLE_ITEMS_COUNT)))
    }

    @Test
    fun mateRequestsListRollOnVerticalSwipeTest() {
        val initMateRequests = MateRequestGeneratorUtility.generateMateRequests(3)
        val initUsers = UserGeneratorUtility.generateUsers(3, 1)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(initMateRequests, initUsers, true)
        }

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeDown(), WaitingViewAction(500))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isAssignableFrom(CardView::class.java),
            ViewMatchers.hasDescendant(withText(initUsers[1].username)),
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )).check(IsChildWithIndexViewAssertion(3))

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeUp(), WaitingViewAction(500))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isAssignableFrom(CardView::class.java),
            ViewMatchers.hasDescendant(withText(initUsers[0].username)),
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )).check(IsChildWithIndexViewAssertion(3))
    }

    @Test
    fun horizontalSwipeLeadsToMateRequestDisappearanceTest() {
        val initMateRequests = MateRequestGeneratorUtility.generateMateRequests(3)
        val initUsers = UserGeneratorUtility.generateUsers(3, 1)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(initMateRequests, initUsers, true)
        }

        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeLeft(), WaitingViewAction(500) {
                mMateRequestsUiStateTestData.removeMateRequest(initMateRequests[0])
            }, WaitingViewAction(1000))
            .check(ViewAssertions.matches(hasChildCount(
                initUsers.size - 1 + Carousel3DLayoutManager.EDGE_INVISIBLE_ITEMS_COUNT)))
        Espresso.onView(Matchers.allOf(
            withText(initUsers[0].username),
            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )).check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingNormalErrorOperationLeadsToShowingDialogTest() {
        val initMateRequests = MateRequestGeneratorUtility.generateMateRequests(3)
        val initUsers = UserGeneratorUtility.generateUsers(3, 1)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(initMateRequests, initUsers, true)
        }

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
        val initMateRequests = MateRequestGeneratorUtility.generateMateRequests(3)
        val initUsers = UserGeneratorUtility.generateUsers(3, 1)

        mMateRequestsFragmentScenarioRule.onFragment {
            mMateRequestsUiStateTestData.setMateRequests(initMateRequests, initUsers, true)
        }

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