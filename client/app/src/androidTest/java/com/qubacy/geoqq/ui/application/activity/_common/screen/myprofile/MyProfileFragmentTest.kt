package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.factory._test.mock.MyProfileViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._di.module.MyProfileViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.MyProfileUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.context.util.getUriFromResId
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.ui._common._test.view.util.action.wait.WaitViewAction
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizationFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.popup.PopupFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common._test.context.MyProfileTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.MyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.MyProfileViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.module.FakeMyProfileViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.operation.MyProfileDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.operation.MyProfileUpdatedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.operation.profile.get.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.operation.profile.update.UpdateMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.input.MyProfileInputData
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyProfileFragmentTest : BusinessFragmentTest<
    FragmentMyProfileBinding,
    MyProfileUiState,
    MyProfileViewModel,
    MyProfileViewModelMockContext,
    MyProfileFragment
>(), PopupFragmentTest<MyProfileFragment>, AuthorizationFragmentTest {
    companion object {
        val DEFAULT_AVATAR_RES_ID = R.drawable.test
    }

    private lateinit var mAvatarImagePresentation: ImagePresentation
    private lateinit var mDefaultMyProfilePresentation: MyProfilePresentation

    override fun setup() {
        super.setup()

        val imageUri = InstrumentationRegistry.getInstrumentation()
            .targetContext.getUriFromResId(DEFAULT_AVATAR_RES_ID)

        mAvatarImagePresentation = ImagePresentation(0, imageUri)
        mDefaultMyProfilePresentation = MyProfileTestContext
            .generateMyProfilePresentation(mAvatarImagePresentation)
    }

    override fun createDefaultViewModelMockContext(): MyProfileViewModelMockContext {
        return MyProfileViewModelMockContext(MyProfileUiState())
    }

    override fun attachViewModelMockContext() {
        FakeMyProfileViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<MyProfileFragment> {
        return MyProfileFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.myProfileFragment
    }

    override fun getPermissionsToGrant(): Array<String> {
        return super.getPermissionsToGrant().plus(arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
    }

    override fun assertAdjustUiWithFalseLoadingState() {
        Espresso.onView(withId(R.id.fragment_my_profile_button_avatar))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.fragment_my_profile_input_about_me))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.fragment_my_profile_input_password))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password_again))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.fragment_my_profile_switch_hit_me_up))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    override fun assertAdjustUiWithTrueLoadingState() {
        Espresso.onView(withId(R.id.fragment_my_profile_button_avatar))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
        Espresso.onView(withId(R.id.fragment_my_profile_input_about_me))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
        Espresso.onView(withId(R.id.fragment_my_profile_input_password))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password_again))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
        Espresso.onView(withId(R.id.fragment_my_profile_switch_hit_me_up))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
    }

    @Test
    fun getMyProfileOnPermissionsGrantedTest() {
        defaultInit()

        Assert.assertTrue(mViewModelMockContext.getMyProfileCallFlag)
    }

    @Test
    fun onMyProfileFragmentGetMyProfileTest() = runTest {
        val viewModelMockContext = MyProfileViewModelMockContext(MyProfileUiState())

        val myProfilePresentation = mDefaultMyProfilePresentation
        val getMyProfileOperation = GetMyProfileUiOperation(myProfilePresentation)

        val expectedAvatarUri = myProfilePresentation.avatarUri
        val expectedUsername = myProfilePresentation.username
        val expectedAboutMe = myProfilePresentation.aboutMe

        initWithModelContext(viewModelMockContext)

        mViewModelMockContext.uiOperationFlow.emit(getMyProfileOperation)

//        Espresso.onView(withId(R.id.fragment_my_profile_avatar))
//            .check(ViewAssertions.matches(CommonImageViewMatcher(expectedAvatarUri)))
        Espresso.onView(withId(R.id.fragment_my_profile_text_login))
            .check(ViewAssertions.matches(withText(expectedUsername)))
        Espresso.onView(withId(R.id.fragment_my_profile_input_about_me))
            .check(ViewAssertions.matches(withText(expectedAboutMe)))
        Espresso.onView(withId(R.id.fragment_my_profile_switch_hit_me_up))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun recoveringDataFromUiStateOnStartTest() {
        val initMyProfile = mDefaultMyProfilePresentation
        val initMyProfileInputData = MyProfileTestContext
            .generateMyProfileInputData(initMyProfile.avatarUri)
        val initUiState = MyProfileUiState(
            myProfilePresentation = initMyProfile,
            myProfileInputData = initMyProfileInputData
        )
        val viewModelMockContext = MyProfileViewModelMockContext(uiState = initUiState)

        initWithModelContext(viewModelMockContext)

//        Espresso.onView(withId(R.id.fragment_my_profile_avatar))
//            .check(ViewAssertions.matches(
//                CommonImageViewMatcher(initMyProfileInputData.avatarUri!!)
//            ))
        Espresso.onView(withId(R.id.fragment_my_profile_input_about_me))
            .check(ViewAssertions.matches(withText(initMyProfileInputData.aboutMe)))
        Espresso.onView(withId(R.id.fragment_my_profile_input_password))
            .check(ViewAssertions.matches(withText(initMyProfileInputData.password)))
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password))
            .check(ViewAssertions.matches(withText(initMyProfileInputData.newPassword)))
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password_again))
            .check(ViewAssertions.matches(withText(initMyProfileInputData.newPasswordAgain)))
        Espresso.onView(withId(R.id.fragment_my_profile_switch_hit_me_up))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun preservingInputDataTest() = runTest {
        val expectedMyProfileInputData = MyProfileTestContext.generateMyProfileInputData(
            username = "test",
            aboutMe = "test",
            password = "testtest",
            newPassword = "testtest2",
            newPasswordAgain = "testtest2",
            hitMeUpType = HitMeUpType.EVERYBODY
        )

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(SetLoadingStateUiOperation(false))

        Espresso.onView(withId(R.id.fragment_my_profile_input_username))
            .perform(
                ViewActions.typeText(expectedMyProfileInputData.username),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_input_about_me))
            .perform(
                ViewActions.typeText(expectedMyProfileInputData.aboutMe),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_input_password))
            .perform(
                ViewActions.typeText(expectedMyProfileInputData.password),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password))
            .perform(
                ViewActions.typeText(expectedMyProfileInputData.newPassword),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password_again))
            .perform(
                ViewActions.typeText(expectedMyProfileInputData.newPasswordAgain),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_switch_hit_me_up))
            .perform(ViewActions.click())

        mActivityScenario.moveToState(Lifecycle.State.DESTROYED)

        Assert.assertTrue(mViewModelMockContext.preserveInputDataCallFlag)
    }

    @Test
    fun validateInputsTest() {
        class TestCase(
            val inputData: MyProfileInputData,
            val expectedInvalidInputSet: HashSet<Int>
        )

        val testCases = listOf(
            TestCase(
                MyProfileInputData(
                    username = ""
                ),
                hashSetOf(R.id.fragment_my_profile_input_username)
            ),
            TestCase(
                MyProfileInputData(
                    username = " "
                ),
                hashSetOf(R.id.fragment_my_profile_input_username)
            ),
            TestCase(
                MyProfileInputData(
                    username = " f"
                ),
                hashSetOf(R.id.fragment_my_profile_input_username)
            ),
            TestCase(
                MyProfileInputData(
                    username = "qqqq "
                ),
                hashSetOf(R.id.fragment_my_profile_input_username)
            ),
            TestCase(
                MyProfileInputData(
                    username = "qqqqqq"
                ),
                hashSetOf()
            ),
            TestCase(
                MyProfileInputData(
                    aboutMe = ""
                ),
                hashSetOf()
            ),
            TestCase(
                MyProfileInputData(
                    aboutMe = " "
                ),
                hashSetOf()
            ),
            TestCase(
                MyProfileInputData(
                    aboutMe = " t"
                ),
                hashSetOf()
            ),
            TestCase(
                MyProfileInputData(
                    password = ""
                ),
                hashSetOf(R.id.fragment_my_profile_input_password)
            ),
            TestCase(
                MyProfileInputData(
                    password = " "
                ),
                hashSetOf(R.id.fragment_my_profile_input_password)
            ),
            TestCase(
                MyProfileInputData(
                    password = "testtes"
                ),
                hashSetOf(R.id.fragment_my_profile_input_password)
            ),
            TestCase(
                MyProfileInputData(
                    password = "testtes "
                ),
                hashSetOf(R.id.fragment_my_profile_input_password)
            ),
            TestCase(
                MyProfileInputData(
                    password = "testtest"
                ),
                hashSetOf()
            ),
            TestCase(
                MyProfileInputData(
                    newPassword = ""
                ),
                hashSetOf(R.id.fragment_my_profile_input_new_password)
            ),
            TestCase(
                MyProfileInputData(
                    newPassword = " "
                ),
                hashSetOf(R.id.fragment_my_profile_input_new_password)
            ),
            TestCase(
                MyProfileInputData(
                    newPassword = "testtes"
                ),
                hashSetOf(R.id.fragment_my_profile_input_new_password)
            ),
            TestCase(
                MyProfileInputData(
                    newPassword = "testtes "
                ),
                hashSetOf(R.id.fragment_my_profile_input_new_password)
            ),
            TestCase(
                MyProfileInputData(
                    newPassword = "testtest"
                ),
                hashSetOf()
            ),
            TestCase(
                MyProfileInputData(
                    newPasswordAgain = ""
                ),
                hashSetOf(R.id.fragment_my_profile_input_new_password_again)
            ),
            TestCase(
                MyProfileInputData(
                    newPasswordAgain = " "
                ),
                hashSetOf(R.id.fragment_my_profile_input_new_password_again)
            ),
            TestCase(
                MyProfileInputData(
                    newPasswordAgain = "testtes"
                ),
                hashSetOf(R.id.fragment_my_profile_input_new_password_again)
            ),
            TestCase(
                MyProfileInputData(
                    newPasswordAgain = "testtes "
                ),
                hashSetOf(R.id.fragment_my_profile_input_new_password_again)
            ),
            TestCase(
                MyProfileInputData(
                    newPasswordAgain = "testtest"
                ),
                hashSetOf()
            )
        )

        defaultInit()

        for (testCase in testCases) {
            val gottenInvalidInputSet = mFragment.validateInputs(testCase.inputData)

            Assert.assertEquals(testCase.expectedInvalidInputSet, gottenInvalidInputSet)
        }
    }

    @Test
    fun errorsAppearOnClickingUpdateWithInvalidInputsAndDisappearOnChangingTheirContentTest() = runTest {
        val initMyProfilePresentation = mDefaultMyProfilePresentation
        val initUiState = MyProfileUiState(
            myProfilePresentation = initMyProfilePresentation
        )

        val invalidUsername = " "
        val invalidPassword = " "
        val invalidNewPassword = " "
        val invalidNewPasswordAgain = " "

        initWithModelContext(MyProfileViewModelMockContext(initUiState))

        mViewModelMockContext.uiOperationFlow.emit(SetLoadingStateUiOperation(false))

        Espresso.onView(withId(R.id.fragment_my_profile_input_username))
            .perform(ViewActions.clearText(), ViewActions.typeText(invalidUsername), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_my_profile_input_password))
            .perform(ViewActions.typeText(invalidPassword), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password))
            .perform(ViewActions.typeText(invalidNewPassword), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password_again))
            .perform(ViewActions.typeText(invalidNewPasswordAgain), ViewActions.closeSoftKeyboard())

        Espresso.onView(withId(R.id.fragment_my_profile_button_update))
            .perform(ViewActions.click())

        Espresso.onView(isRoot()).perform(WaitViewAction(3000))

        Espresso.onView(withText(R.string.fragment_my_profile_input_error_username))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.fragment_my_profile_input_wrapper_password)),
            withText(R.string.fragment_input_error_password),
        )).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.fragment_my_profile_input_wrapper_new_password)),
            withText(R.string.fragment_input_error_password),
        )).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.fragment_my_profile_input_wrapper_new_password_again)),
            withText(R.string.fragment_input_error_password),
        )).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun updateMyProfileTest() = runTest {
        val initMyProfilePresentation = mDefaultMyProfilePresentation
        val initUiState = MyProfileUiState(
            myProfilePresentation = initMyProfilePresentation
        )

        val updatedMyProfileInputData = MyProfileTestContext.generateMyProfileInputData(
            username = "updated username",
            aboutMe = "updated about me",
            password = "testtest",
            newPassword = "testtest2",
            newPasswordAgain = "testtest2",
            hitMeUpType = HitMeUpType.EVERYBODY
        )
        val isUpdateDataValid = true

        initWithModelContext(MyProfileViewModelMockContext(
            initUiState, isUpdateDataValid = isUpdateDataValid
        ))

        mViewModelMockContext.uiOperationFlow.emit(SetLoadingStateUiOperation(false))

        Espresso.onView(withId(R.id.fragment_my_profile_input_username))
            .perform(
                ViewActions.clearText(),
                ViewActions.typeText(updatedMyProfileInputData.username),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_input_about_me))
            .perform(
                ViewActions.clearText(),
                ViewActions.typeText(updatedMyProfileInputData.aboutMe),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_input_password))
            .perform(
                ViewActions.typeText(updatedMyProfileInputData.password),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password))
            .perform(
                ViewActions.typeText(updatedMyProfileInputData.newPassword),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_input_new_password_again))
            .perform(
                ViewActions.typeText(updatedMyProfileInputData.newPasswordAgain),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.fragment_my_profile_switch_hit_me_up))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.fragment_my_profile_button_update))
            .perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.updateMyProfileCallFlag)
    }

    @Test
    fun logoutMenuOptionTest() {
        defaultInit()

        Espresso.onView(withId(R.id.my_profile_top_bar_menu)).perform(ViewActions.click())
        Espresso.onView(withText(
            R.string.component_my_profile_top_bar_popup_option_logout_title
        )).perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.logoutMyProfileCallFlag)
    }

    @Test
    fun deleteMyProfileMenuOptionTest() {
        defaultInit()

        Espresso.onView(withId(R.id.my_profile_top_bar_menu)).perform(ViewActions.click())
        Espresso.onView(withText(
            R.string.component_my_profile_top_bar_popup_option_delete_profile_title
        )).perform(ViewActions.click())

        Espresso.onView(withText(R.string.fragment_my_profile_dialog_request_message_delete_account))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(R.string.component_request_dialog_button_positive_caption))
            .perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.deleteMyProfileCallFlag)
    }


    @Test
    fun onMyProfileFragmentUpdateMyProfile() = runTest {
        val viewModelMockContext = MyProfileViewModelMockContext(MyProfileUiState())

        val initMyProfilePresentation = mDefaultMyProfilePresentation
        val updatedMyProfilePresentation = mDefaultMyProfilePresentation
            .copy(
                username = "updated username",
                aboutMe = "updated about me",
                hitMeUp = HitMeUpType.NOBODY
            )

        val getMyProfileOperation = GetMyProfileUiOperation(initMyProfilePresentation)
        val updateMyProfileOperation = UpdateMyProfileUiOperation(updatedMyProfilePresentation)

        val expectedInitAvatarUri = initMyProfilePresentation.avatarUri
        val expectedInitUsername = initMyProfilePresentation.username
        val expectedInitAboutMe = initMyProfilePresentation.aboutMe
        val expectedInitHitMeUp = initMyProfilePresentation.hitMeUp == HitMeUpType.EVERYBODY

        val expectedUsername = updatedMyProfilePresentation.username
        val expectedAboutMe = updatedMyProfilePresentation.aboutMe
        val expectedHitMeUp = updatedMyProfilePresentation.hitMeUp == HitMeUpType.EVERYBODY

        initWithModelContext(viewModelMockContext)

        mViewModelMockContext.uiOperationFlow.emit(getMyProfileOperation)

//        Espresso.onView(withId(R.id.fragment_my_profile_avatar))
//            .check(ViewAssertions.matches(CommonImageViewMatcher(expectedInitAvatarUri)))
        Espresso.onView(withId(R.id.fragment_my_profile_input_username))
            .check(ViewAssertions.matches(withText(expectedInitUsername)))
        Espresso.onView(withId(R.id.fragment_my_profile_input_about_me))
            .check(ViewAssertions.matches(withText(expectedInitAboutMe)))
        Espresso.onView(withId(R.id.fragment_my_profile_switch_hit_me_up))
            .check(ViewAssertions.matches(
                if (expectedInitHitMeUp) ViewMatchers.isChecked() else ViewMatchers.isNotChecked()))

        mViewModelMockContext.uiOperationFlow.emit(updateMyProfileOperation)

        Espresso.onView(withId(R.id.fragment_my_profile_input_username))
            .check(ViewAssertions.matches(withText(expectedUsername)))
        Espresso.onView(withId(R.id.fragment_my_profile_input_about_me))
            .check(ViewAssertions.matches(withText(expectedAboutMe)))
        Espresso.onView(withId(R.id.fragment_my_profile_switch_hit_me_up))
            .check(ViewAssertions.matches(
                if (expectedHitMeUp) ViewMatchers.isChecked() else ViewMatchers.isNotChecked()))
    }

    @Test
    fun onMyProfileFragmentMyProfileUpdatedTest() = runTest {
        val updateMyProfileOperation = MyProfileUpdatedUiOperation()

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(updateMyProfileOperation)

        Espresso.onView(withText(R.string.fragment_my_profile_snackbar_message_profile_updated))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Deprecated("Poorly synchronized so can fail.")
    @Test
    fun processDeleteMyProfileOperationTest() = runTest {
        val deleteOperation = MyProfileDeletedUiOperation()

        val expectedDestination = R.id.loginFragment

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(deleteOperation)

        val gottenDestination = mNavController.currentDestination!!.id

        Assert.assertEquals(expectedDestination, gottenDestination)
    }

    @Deprecated("Poorly synchronized so can fail.")
    @Test
    fun processLogoutOperationTest() = runTest {
        val logoutOperation = LogoutUiOperation()

        val expectedDestination = R.id.loginFragment

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(logoutOperation)

        val gottenDestination = mNavController.currentDestination!!.id

        Assert.assertEquals(expectedDestination, gottenDestination)
    }

    override fun beforePopupMessageOccurredTest() {
        defaultInit()
    }

    override fun getPopupActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun getPopupFragment(): MyProfileFragment {
        return mFragment
    }

    override fun beforeNavigateToLoginTest() {
        defaultInit()
    }

    override fun getAuthorizationFragmentNavController(): NavController {
        return mNavController
    }

    override fun getAuthorizationFragmentLoginAction(): Int {
        return R.id.action_myProfileFragment_to_loginFragment
    }

    override fun getAuthorizationFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }
}