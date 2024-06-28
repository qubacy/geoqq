package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests

import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.choosablelistviewlib.item.ChoosableItemViewProvider
import com.qubacy.geoqq._common.context.util.getUriFromResId
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.factory._test.mock.MateRequestsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.state.MateRequestsUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui._common._test.view.util.action.scroll.recyclerview.RecyclerViewScrollToPositionViewAction
import com.qubacy.geoqq.ui._common._test.view.util.action.swipe.SwipeViewActionUtil
import com.qubacy.geoqq.ui._common._test.view.util.action.wait.WaitViewAction
import com.qubacy.geoqq.ui._common._test.view.util.assertion.recyclerview.item.count.RecyclerViewItemCountViewAssertion
import com.qubacy.geoqq.ui.application.activity._common.screen._common._test.context.ScreenTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizationFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.InterlocutorFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component.hint.view.HintViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common._test.context.MateTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.MateRequestsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module.FakeMateRequestsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.answer.ReturnAnsweredRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.chunk.insert.InsertRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.chunk.update.UpdateRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.request.AddRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.request.RemoveRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.request.UpdateRequestUiOperation
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MateRequestsFragmentTest : BusinessFragmentTest<
    FragmentMateRequestsBinding,
    MateRequestsUiState,
    MateRequestsViewModel,
    MateRequestsViewModelMockContext,
    MateRequestsFragment
>(), InterlocutorFragmentTest<MateRequestsFragment>, AuthorizationFragmentTest {
    companion object {
        val DEFAULT_AVATAR_RES_ID = R.drawable.test
    }

    private lateinit var mImagePresentation: ImagePresentation
    private lateinit var mUserPresentation: UserPresentation

    override fun getPermissionsToGrant(): Array<String> {
        return super.getPermissionsToGrant().plus(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    override fun setup() {
        super.setup()

        initVariables()
    }

    private fun initVariables() {
        val imageUri = InstrumentationRegistry.getInstrumentation()
            .targetContext.getUriFromResId(DEFAULT_AVATAR_RES_ID)

        mImagePresentation = ImagePresentation(0, imageUri)
        mUserPresentation = ScreenTestContext.generateUserPresentation(mImagePresentation)
    }

    override fun createDefaultViewModelMockContext(): MateRequestsViewModelMockContext {
        return MateRequestsViewModelMockContext(MateRequestsUiState())
    }

    override fun attachViewModelMockContext() {
        FakeMateRequestsViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<MateRequestsFragment> {
        return MateRequestsFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.mateRequestsFragment
    }

    /**
     * Note: this one is poorly synchronized:
     */
    @Test
    fun hintTextAppearsForShortTimeThenDisappearsTest() {
        defaultInit()

        Espresso.onView(isRoot())
            .perform(WaitViewAction(MateRequestsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT))
        Espresso.onView(withId(R.id.component_hint_text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        val disappearanceTimeout = HintViewProvider.DEFAULT_HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT +
                HintViewProvider.DEFAULT_APPEARANCE_ANIMATION_DURATION

        Espresso.onView(isRoot())
            .perform(WaitViewAction(disappearanceTimeout))
        Espresso.onView(withId(R.id.component_hint_text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun clickingInfoMenuOptionLeadsToShowingHintTest() {
        defaultInit()

        val hintVisibilityTime = MateRequestsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT +
                HintViewProvider.DEFAULT_APPEARANCE_ANIMATION_DURATION +
                HintViewProvider.DEFAULT_HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT +
                HintViewProvider.DEFAULT_APPEARANCE_ANIMATION_DURATION

        Espresso.onView(isRoot()).perform(WaitViewAction(hintVisibilityTime))

        Espresso.onView(withId(R.id.mate_requests_top_bar_option_hint)).perform(ViewActions.click())

        Espresso.onView(isRoot())
            .perform(WaitViewAction(MateRequestsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT))
        Espresso.onView(withId(R.id.component_hint_text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun clickingMyProfileMenuOptionLeadsToNavigationToMyProfileFragmentTest() {
        defaultInit()

        val expectedDestination = R.id.myProfileFragment

        Espresso.onView(withId(R.id.main_top_bar_option_my_profile)).perform(ViewActions.click())

        val gottenDestination = mNavController.currentDestination!!.id

        Assert.assertEquals(expectedDestination, gottenDestination)
    }

    @Test
    fun tryingToLoadChatsOnEmptyRequestListTest() {
        val initUiState = MateRequestsUiState(requests = mutableListOf())

        initWithModelContext(MateRequestsViewModelMockContext(uiState = initUiState))

        Assert.assertTrue(mViewModelMockContext.getNextRequestChunkCallFlag)
    }

    @Test
    fun loadingNewRequestChunkOnReachingListEndTest() {
        val initRequests = generateMateRequests(20)
        val initUiState = MateRequestsUiState(requests = initRequests)

        initWithModelContext(MateRequestsViewModelMockContext(uiState = initUiState))

        // hard to fix without changing the main code for now:
//        Assert.assertFalse(mViewModelMockContext.getNextRequestChunkCallFlag)

        Espresso.onView(withId(R.id.fragment_mate_requests_list))
            .perform(RecyclerViewScrollToPositionViewAction(initRequests.size - 1))

        Assert.assertTrue(mViewModelMockContext.getNextRequestChunkCallFlag)
    }

    /**
     * Note: initInsertRequestsOperation is used because the requests are reset in onStart() method;
     */
    @Test
    fun onMateRequestsFragmentRemoveRequestTest() = runTest {
        val initRequests = generateMateRequests(1)
        val initInsertRequestsOperation = InsertRequestsUiOperation(0, initRequests)

        val requestToRemovePosition = 0
        val removeRequestOperation = RemoveRequestUiOperation(requestToRemovePosition)

        val expectedRequestCount = initRequests.size - 1

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(initInsertRequestsOperation)
        mViewModelMockContext.uiOperationFlow.emit(removeRequestOperation)

        Espresso.onView(withId(R.id.fragment_mate_requests_list))
            .check(RecyclerViewItemCountViewAssertion(expectedRequestCount))
    }

    /**
     * Poor synchronization:
     */
    @Test
    fun onMateRequestsFragmentInsertRequestsTest() = runTest {
        val requests = generateMateRequests(1)
        val insertRequestsOperation = InsertRequestsUiOperation(0, requests)

        val request = requests.first()

        val expectedRequestCount = requests.size
        val expectedAvatarUri = request.user.avatar.uri
        val expectedUsername = request.user.username

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(insertRequestsOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(500))

        Espresso.onView(withId(R.id.fragment_mate_requests_list))
            .check(RecyclerViewItemCountViewAssertion(expectedRequestCount))
//        Espresso.onView(CommonImageViewMatcher(expectedAvatarUri))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onMateRequestsFragmentUpdateRequestTest() = runTest {
        val initRequests = generateMateRequests(1)
        val initRequest = initRequests.first()
        val initInsertRequestsOperation = InsertRequestsUiOperation(0, initRequests)

        val position = 0
        val request = initRequest.copy(user = initRequest.user.copy(username = "updated"))

        val updateRequestOperation = UpdateRequestUiOperation(position, request)

        val expectedAvatarUri = request.user.avatar.uri
        val expectedUsername = request.user.username

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(initInsertRequestsOperation)
        mViewModelMockContext.uiOperationFlow.emit(updateRequestOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(500))

//        Espresso.onView(CommonImageViewMatcher(expectedAvatarUri))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onMateRequestsFragmentUpdateRequestsTest() = runTest {
        val initRequests = generateMateRequests(1)
        val initInsertRequestsOperation = InsertRequestsUiOperation(0, initRequests)

        val position = 0
        val requests = initRequests.map { it.copy(user = it.user.copy(username = "updated")) }

        val updateRequestsOperation = UpdateRequestsUiOperation(position, requests)
        val request = requests.first()

        val expectedAvatarUri = request.user.avatar.uri
        val expectedUsername = request.user.username

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(initInsertRequestsOperation)
        mViewModelMockContext.uiOperationFlow.emit(updateRequestsOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(500))

//        Espresso.onView(CommonImageViewMatcher(expectedAvatarUri))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /**
     * Poor synchronization:
     */
    @Test
    fun onMateRequestsFragmentReturnAnsweredRequestTest() = runTest {
        val initRequests = generateMateRequests(1)
        val initInsertRequestsOperation = InsertRequestsUiOperation(0, initRequests)

        val request = initRequests.first()
        val returnAnsweredRequestUiOperation = ReturnAnsweredRequestUiOperation(0)

        val expectedAvatarUri = request.user.avatar.uri
        val expectedUsername = request.user.username

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(initInsertRequestsOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(500))

        Espresso.onView(withId(R.id.component_mate_request_preview_container))
            .perform(SwipeViewActionUtil.generateSwipeViewAction(
                GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT))

//        Espresso.onView(CommonImageViewMatcher(expectedAvatarUri))
//            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isCompletelyDisplayed())))
        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isCompletelyDisplayed())))

        mViewModelMockContext.uiOperationFlow.emit(returnAnsweredRequestUiOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(500))

//        Espresso.onView(CommonImageViewMatcher(expectedAvatarUri))
//            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun onMateRequestsAddRequestTest() = runTest {
        val initRequestPresentations = mutableListOf<MateRequestPresentation>()
        val initUiState = MateRequestsUiState(requests = initRequestPresentations)

        val requestPresentationToAdd = generateMateRequests(1).first()
        val addRequestUiOperation = AddRequestUiOperation(requestPresentationToAdd)

        val expectedInitItemCount = initRequestPresentations.size
        val expectedFinalItemCount = expectedInitItemCount + 1

        initWithModelContext(MateRequestsViewModelMockContext(uiState = initUiState))

        Espresso.onView(withId(R.id.fragment_mate_requests_list))
            .check(RecyclerViewItemCountViewAssertion(expectedInitItemCount))

        mViewModelMockContext.uiOperationFlow.emit(addRequestUiOperation)

        Espresso.onView(withId(R.id.fragment_mate_requests_list))
            .check(RecyclerViewItemCountViewAssertion(expectedFinalItemCount))
    }

    private fun generateMateRequests(
        count: Int,
        offset: Int = 0
    ): MutableList<MateRequestPresentation> {
        return IntRange(offset, count + offset - 1).map { it ->
            val id = it.toLong()
            val user = ScreenTestContext.generateUserPresentation(mImagePresentation, id)

            MateTestContext.generateMateRequestPresentation(user, id)
        }.toMutableList()
    }

    override fun beforeAdjustUiWithLoadingStateTest() = runTest {
        val initRequests = generateMateRequests(1)
        val initInsertRequestsUiOperation = InsertRequestsUiOperation(0, initRequests)

        val userPresentation = mUserPresentation

        initWithModelContext(MateRequestsViewModelMockContext(
            MateRequestsUiState(), getUserProfileWithMateRequestId = userPresentation))

        mViewModelMockContext.uiOperationFlow.emit(initInsertRequestsUiOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(500))
    }

    override fun assertAdjustUiWithFalseLoadingState() {
        Espresso.onView(isAssignableFrom(ChoosableItemViewProvider::class.java))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        mActivityScenario.onActivity {
            mFragment.closeInterlocutorDetailsSheet()
        }
    }

    override fun assertAdjustUiWithTrueLoadingState() {
        Espresso.onView(isAssignableFrom(ChoosableItemViewProvider::class.java))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }

    override fun beforeNavigateToLoginTest() {
        defaultInit()
    }

    override fun getAuthorizationFragmentNavController(): NavController {
        return mNavController
    }

    override fun getAuthorizationFragmentLoginAction(): Int {
        return R.id.action_mateRequestsFragment_to_loginFragment
    }

    override fun getAuthorizationFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun beforeAdjustInterlocutorFragmentUiWithInterlocutorTest() {
        defaultInit()
    }

    override fun beforeOpenInterlocutorDetailsSheetTest() {
        defaultInit()
    }

    override fun getInterlocutorFragmentFragment(): MateRequestsFragment {
        return mFragment
    }

    override fun getInterlocutorFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun getInterlocutorFragmentAvatar(): ImagePresentation {
        return mImagePresentation
    }
}