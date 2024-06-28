package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat

import android.Manifest
import android.os.Bundle
import android.view.KeyEvent
import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.databinding.FragmentGeoChatBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.factory._test.mock.GeoChatViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.module.FakeGeoChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state.GeoChatUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.context.util.getUriFromResId
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.ui._common._test.view.util.action.wait.WaitViewAction
import com.qubacy.geoqq.ui._common._test.view.util.assertion.recyclerview.item.count.RecyclerViewItemCountViewAssertion
import com.qubacy.geoqq.ui.application.activity._common.screen._common._test.context.ScreenTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizationFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.ChatFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.InterlocutorFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.LocationFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.popup.PopupFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.GeoChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo._common._test.context.GeoTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.add.AddGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.sending.ChangeMessageSendingAllowedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.update.UpdateGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.GeoMessagePresentation
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeoChatFragmentTest(

) : BusinessFragmentTest<
    FragmentGeoChatBinding,
    GeoChatUiState,
    GeoChatViewModel,
    GeoChatViewModelMockContext,
    GeoChatFragment
>(),
    PopupFragmentTest<GeoChatFragment>,
    LocationFragmentTest,
    InterlocutorFragmentTest<GeoChatFragment>,
    AuthorizationFragmentTest,
    ChatFragmentTest<GeoChatFragment>
{
    companion object {
        const val DEFAULT_NAV_ARG_RADIUS = 1000
        const val DEFAULT_NAV_ARG_LATITUDE = 0f
        const val DEFAULT_NAV_ARG_LONGITUDE = 0f

        const val DEFAULT_MESSAGE_ANIMATION_DURATION = 500L

        val DEFAULT_AVATAR_RES_ID = R.drawable.test
    }

    private lateinit var mImagePresentation: ImagePresentation
    private lateinit var mUserPresentation: UserPresentation

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

    override fun getPermissionsToGrant(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun getFragmentArgs(): Bundle {
        return GeoChatFragmentArgs(
            DEFAULT_NAV_ARG_RADIUS, DEFAULT_NAV_ARG_LATITUDE, DEFAULT_NAV_ARG_LONGITUDE
        ).toBundle()
    }

    override fun createDefaultViewModelMockContext(): GeoChatViewModelMockContext {
        return GeoChatViewModelMockContext(GeoChatUiState())
    }

    override fun attachViewModelMockContext() {
        FakeGeoChatViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<GeoChatFragment> {
        return GeoChatFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.geoChatFragment
    }

    /**
     * Poorly synchronized;
     */
    @Test
    fun clickingOtherMessageLeadsToRequestingShowingUserProfileTest() = runTest {
        val initMessages = generateGeoMessagePresentations(1)
        val initAddGeoMessagesUiOperation = AddGeoMessagesUiOperation(initMessages)

        val message = initMessages.first()
        val user = message.user
        val localUserId = user.id + 1

        initWithModelContext(GeoChatViewModelMockContext(
            GeoChatUiState(), getLocalUserId = localUserId))

        mViewModelMockContext.uiOperationFlow.emit(initAddGeoMessagesUiOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(500))

        Espresso.onView(withText(message.text)).perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.getUserProfileByMessagePositionCallFlag)
    }

    @Test
    fun tryingSendingInvalidMessageLeadsToShowingErrorTest() = runTest {
        val error = TestError.normal

        val invalidText = "  "

        initWithModelContext(GeoChatViewModelMockContext(
            GeoChatUiState(isMessageSendingAllowed = true), retrieveErrorResult = error))

        Espresso.onView(withId(R.id.fragment_geo_chat_input_message))
            .perform(
                ViewActions.typeText(invalidText),
                ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
            )
        Espresso.onView(withText(error.message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickingMessageInputHidesUserDetailsSheetTest() = runTest {
        val initInterlocutor = mUserPresentation
        val initShowInterlocutorUiOperation = ShowInterlocutorDetailsUiOperation(initInterlocutor)

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(initShowInterlocutorUiOperation)

        Espresso.onView(withId(R.id.fragment_geo_chat_input_message))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isCompletelyDisplayed())))
    }

    /**
     * Poorly synchronized;
     */
    @Test
    fun onGeoChatFragmentAddGeoMessagesTest() = runTest {
        val messages = generateGeoMessagePresentations(1)
        val addGeoMessagesUiOperation = AddGeoMessagesUiOperation(messages)

        val message = messages.first()

        val expectedInitItemCount = 0

        val expectedItemCount = messages.size
        val expectedUsername = message.user.username
        val expectedMessageText = message.text
        val expectedMessageTimestamp = message.timestamp

        defaultInit()

        Espresso.onView(withId(R.id.fragment_geo_chat_list))
            .check(RecyclerViewItemCountViewAssertion(expectedInitItemCount))

        mViewModelMockContext.uiOperationFlow.emit(addGeoMessagesUiOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(500))

        Espresso.onView(withId(R.id.fragment_geo_chat_list))
            .check(RecyclerViewItemCountViewAssertion(expectedItemCount))
        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedMessageText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedMessageTimestamp))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onGeoChatFragmentUpdateGeoMessagesTest() = runTest {
        val initMessages = generateGeoMessagePresentations(1)
        val initAddGeoMessagesUiOperation = AddGeoMessagesUiOperation(initMessages)
        val initMessage = initMessages.first()

        val positions = initMessages.indices.toList()
        val updatedMessages = initMessages.map { it.copy(text = "updated text") }
        val updateGeoMessagesUiOperation = UpdateGeoMessagesUiOperation(positions, updatedMessages)
        val updatedMessage = updatedMessages.first()

        val expectedInitUsername = initMessage.user.username
        val expectedInitMessageText = initMessage.text
        val expectedInitMessageTimestamp = initMessage.timestamp

        val expectedUsername = updatedMessage.user.username
        val expectedMessageText = updatedMessage.text
        val expectedMessageTimestamp = updatedMessage.timestamp

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(initAddGeoMessagesUiOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(DEFAULT_MESSAGE_ANIMATION_DURATION))

        Espresso.onView(withText(expectedInitUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedInitMessageText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedInitMessageTimestamp))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        mViewModelMockContext.uiOperationFlow.emit(updateGeoMessagesUiOperation)

        Espresso.onView(isRoot()).perform(WaitViewAction(DEFAULT_MESSAGE_ANIMATION_DURATION))

        Espresso.onView(withText(expectedUsername))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedMessageText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(expectedMessageTimestamp))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onGeoChatFragmentChangeMessageSendingTest() = runTest {
        val initIsMessageSendingAllowed = false
        val initUiState = GeoChatUiState(isMessageSendingAllowed = initIsMessageSendingAllowed)

        val expectedIsMessageSendingAllowed = true

        val changeMessageSendingAllowedUiOperation =
            ChangeMessageSendingAllowedUiOperation(expectedIsMessageSendingAllowed)

        initWithModelContext(GeoChatViewModelMockContext(uiState = initUiState))

        Espresso.onView(withId(R.id.fragment_geo_chat_input_message))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))

        mViewModelMockContext.uiOperationFlow.emit(changeMessageSendingAllowedUiOperation)

        Espresso.onView(withId(R.id.fragment_geo_chat_input_message))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    override fun assertAdjustUiWithFalseLoadingState() {
        Espresso.onView(withId(R.id.fragment_geo_chat_progress_bar))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        Espresso.onView(withId(R.id.fragment_geo_chat_input_message))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    override fun assertAdjustUiWithTrueLoadingState() {
        Espresso.onView(withId(R.id.fragment_geo_chat_progress_bar))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.fragment_geo_chat_input_message))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    override fun beforeNavigateToLoginTest() {
        defaultInit()
    }

    override fun getAuthorizationFragmentNavController(): NavController {
        return mNavController
    }

    override fun getAuthorizationFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun getAuthorizationFragmentLoginAction(): Int {
        return R.id.action_geoChatFragment_to_loginFragment
    }

    override fun onChatFragmentMessageSentTest() {
        // todo: nothing to do for now..


    }

    override fun beforeOnChatFragmentMateRequestSentTest() {
        defaultInit()
    }

    override fun getChatFragmentFragment(): GeoChatFragment {
        return mFragment
    }

    override fun getChatFragmentInterlocutorFragmentTest(

    ): InterlocutorFragmentTest<GeoChatFragment> {
        return this
    }

    override fun beforeAdjustInterlocutorFragmentUiWithInterlocutorTest() {
        defaultInit()
    }

    override fun beforeOpenInterlocutorDetailsSheetTest() {
        defaultInit()
    }

    override fun getInterlocutorFragmentFragment(): GeoChatFragment {
        return mFragment
    }

    override fun getInterlocutorFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun getInterlocutorFragmentAvatar(): ImagePresentation {
        return mImagePresentation
    }

    override fun adjustUiWithLocationPointTest() {
        // todo: nothing to do for now..


    }

    override fun beforePopupMessageOccurredTest() {
        defaultInit()
    }

    override fun getPopupActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun getPopupFragment(): GeoChatFragment {
        return mFragment
    }

    private fun generateGeoMessagePresentations(
        count: Int
    ): List<GeoMessagePresentation> {
        return IntRange(0, count - 1).reversed().map {
            val id = it.toLong()

            GeoTestContext.generateGeoMessagePresentation(mUserPresentation.copy(id = id))
        }
    }
}