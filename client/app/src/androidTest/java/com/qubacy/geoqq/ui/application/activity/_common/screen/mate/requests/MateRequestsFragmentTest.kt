package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests

import androidx.test.espresso.Espresso
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
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.factory._test.mock.MateRequestsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module.MateRequestsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui._common._test.view.util.action.scroll.recyclerview.RecyclerViewScrollToPositionViewAction
import com.qubacy.geoqq.ui._common._test.view.util.action.wait.WaitViewAction
import com.qubacy.geoqq.ui._common._test.view.util.assertion.recyclerview.item.count.RecyclerViewItemCountViewAssertion
import com.qubacy.geoqq.ui._common._test.view.util.matcher.image.common.CommonImageViewMatcher
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.hint.view.HintViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user._test.util.UserPresentationGenerator
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module.FakeMateRequestsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.InsertRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.RemoveRequestUiOperation
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(MateRequestsViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class MateRequestsFragmentTest : BusinessFragmentTest<
    FragmentMateRequestsBinding,
    MateRequestsUiState,
    MateRequestsViewModel,
    MateRequestsViewModelMockContext,
    MateRequestsFragment
>() {
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

        val imageUri = InstrumentationRegistry.getInstrumentation()
            .targetContext.getUriFromResId(DEFAULT_AVATAR_RES_ID)

        mImagePresentation = ImagePresentation(0, imageUri)
        mUserPresentation = UserPresentation(
            0, "test", "test", mImagePresentation, false, false)
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
    fun processRemoveRequestUiOperationTest() = runTest {
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

    @Test
    fun processInsertRequestsUiOperationTest() = runTest {
        val requests = generateMateRequests(1)
        val insertRequestsOperation = InsertRequestsUiOperation(0, requests)

        val expectedRequestCount = requests.size

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(insertRequestsOperation)

        Espresso.onView(withId(R.id.fragment_mate_requests_list))
            .check(RecyclerViewItemCountViewAssertion(expectedRequestCount))
    }

    @Test
    fun processShowInterlocutorDetailsUiOperationTest() = runTest {
        val userPresentation = mUserPresentation
        val showInterlocutorDetailsOperation = ShowInterlocutorDetailsUiOperation(userPresentation)

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(showInterlocutorDetailsOperation)

        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(Matchers.allOf(
                ViewMatchers.hasDescendant(Matchers.allOf(
                    withId(R.id.component_bottom_sheet_user_image_avatar),
                    CommonImageViewMatcher(userPresentation.avatar.uri)
                )),
                ViewMatchers.hasDescendant(withText(userPresentation.username)),
                ViewMatchers.hasDescendant(withText(userPresentation.description)),
                ViewMatchers.hasDescendant(Matchers.allOf(
                    withId(R.id.component_bottom_sheet_user_button_mate),
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)
                ))
            )))
    }

    @Test
    fun processUpdateInterlocutorDetailsUiOperationTest() = runTest {
        val userPresentation = mUserPresentation
        val showInterlocutorDetailsOperation = ShowInterlocutorDetailsUiOperation(userPresentation)

        val updatedUserPresentation = userPresentation.copy(username = "updated one")
        val updateInterlocutorDetailsOperation =
            UpdateInterlocutorDetailsUiOperation(updatedUserPresentation)

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(showInterlocutorDetailsOperation)
        mViewModelMockContext.uiOperationFlow.emit(updateInterlocutorDetailsOperation)

        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(Matchers.allOf(
                ViewMatchers.hasDescendant(Matchers.allOf(
                    withId(R.id.component_bottom_sheet_user_image_avatar),
                    CommonImageViewMatcher(updatedUserPresentation.avatar.uri)
                )),
                ViewMatchers.hasDescendant(withText(updatedUserPresentation.username)),
                ViewMatchers.hasDescendant(withText(updatedUserPresentation.description)),
                ViewMatchers.hasDescendant(Matchers.allOf(
                    withId(R.id.component_bottom_sheet_user_button_mate),
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)
                ))
            )))
    }

    @Test
    fun processSetLoadingStateUiOperationTest() = runTest {
        val initLoadingState = false
        val initRequests = generateMateRequests(1)
        val initUiState = MateRequestsUiState(isLoading = initLoadingState)

        val initInsertRequestsUiOperation = InsertRequestsUiOperation(0, initRequests)

        val isLoading = true
        val setLoadingStateOperation = SetLoadingStateUiOperation(isLoading)
        val userPresentation = mUserPresentation

        initWithModelContext(MateRequestsViewModelMockContext(
            initUiState, getUserProfileWithMateRequestId = userPresentation))

        mViewModelMockContext.uiOperationFlow.emit(initInsertRequestsUiOperation)

        // todo: find a way to avoid this:
        Espresso.onView(isRoot()).perform(WaitViewAction(500))

        mViewModelMockContext.uiOperationFlow.emit(setLoadingStateOperation)

        Espresso.onView(isAssignableFrom(ChoosableItemViewProvider::class.java))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.doesNotExist())
    }

    private fun generateMateRequests(
        count: Int,
        offset: Int = 0
    ): MutableList<MateRequestPresentation> {
        return IntRange(offset, count + offset - 1).map { it ->
            val id = it.toLong()
            val user = UserPresentationGenerator.generateUserPresentation(id, mImagePresentation)

            MateRequestPresentation(id, user)
        }.toMutableList()
    }
}