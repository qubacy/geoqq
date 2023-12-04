package com.qubacy.geoqq.ui.screen.myprofile

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.myprofile.model.MyProfileModelContext
import com.qubacy.geoqq.domain.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.common.ApplicationTestBase
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Exception
import java.lang.reflect.Method

@RunWith(AndroidJUnit4::class)
class MyProfileFragmentTest : ApplicationTestBase() {
    class UserAvatarTestData(
        val setNewUserAvatarWithUriMethodReflection: Method,
        val fragment: MyProfileFragment
    ) {
        fun setNewUserAvatarWithUri(uri: Uri?) {
            setNewUserAvatarWithUriMethodReflection.invoke(fragment, uri)
        }
    }
    class PrivacyHitUpTestData(
        val changedInputHash: HashMap<String, Any>,
        val onPrivacyHitUpItemSelectedMethodReflection: Method,
        val fragment: MyProfileFragment
    ) {
        fun getCurPrivacyHitUpPosition(): Int {
            return changedInputHash[MyProfileModelContext.PRIVACY_HIT_UP_POSITION_KEY] as Int
        }

        fun changeCurPrivacyHitUpPosition(position: Int) {
            onPrivacyHitUpItemSelectedMethodReflection.invoke(fragment, position)
        }
    }

    class MyProfileUiStateTestData(
        val myProfileStateFlow: MutableStateFlow<MyProfileState?>,
        val myProfileUiState: LiveData<MyProfileUiState?>
    ) {
        fun setState(state: MyProfileState) {
            runBlocking {
                myProfileStateFlow.emit(state)
            }
        }

        fun showError(error: Error) {
            val operations = listOf(
                HandleErrorOperation(error)
            )

            var avatar =
                if (myProfileUiState.value != null) myProfileUiState.value!!.avatar else Uri.EMPTY
            var username =
                if (myProfileUiState.value != null) myProfileUiState.value!!.username else String()
            var description =
                if (myProfileUiState.value != null) myProfileUiState.value!!.description else String()
            var hitUpOption =
                if (myProfileUiState.value != null) myProfileUiState.value!!.hitUpOption
                else DataMyProfile.HitUpOption.POSITIVE

            val newState = MyProfileState(
                avatar, username, description, hitUpOption, operations)

            runBlocking {
                myProfileStateFlow.emit(newState)
            }
        }
    }

    companion object {
        const val TAG = "MY_PROFILE_FRAGMENT"
    }

    private lateinit var mMyProfileFragmentScenarioRule: FragmentScenario<MyProfileFragment>
    private lateinit var mBinding: FragmentMyProfileBinding
    private lateinit var mModel: MyProfileViewModel
    private lateinit var mContext: Context

    private lateinit var mResources: Resources
    private lateinit var mTestAvatarUri: Uri

    private lateinit var mUserAvatarTestData: UserAvatarTestData
    private lateinit var mPrivacyHitUpTestData: PrivacyHitUpTestData
    private lateinit var mMyProfileUiStateTestData: MyProfileUiStateTestData

    private fun generateMyProfileUiTestState(avatarUri: Uri): MyProfileState {
        return MyProfileState(
            avatarUri,
            "test",
            "test",
            DataMyProfile.HitUpOption.NEGATIVE,
            listOf()
        )
    }

    @Before
    override fun setup() {
        super.setup()

        mMyProfileFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_MyProfile)
        mMyProfileFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)
        mContext = InstrumentationRegistry.getInstrumentation().targetContext

        var fragment: MyProfileFragment? = null

        mMyProfileFragmentScenarioRule.apply {
            onFragment {
                mBinding = DataBindingUtil.getBinding<FragmentMyProfileBinding>(it.requireView())!!
                fragment = it
            }
        }

        val mModelFieldReflection = MyProfileFragment::class.java.superclass.superclass
            .getDeclaredField("mModel").apply { isAccessible = true }
        val changedInputHashFieldReflection =
            MyProfileFragment::class.java.getDeclaredField("mChangedInputHash").apply {
                isAccessible = true
            }
        val myProfileStateFlowFieldReflection = MyProfileViewModel::class.java
            .getDeclaredField("mMyProfileStateFlow").apply {
                isAccessible = true
            }
        val myProfileUiStateFieldReflection = MyProfileViewModel::class.java
            .getDeclaredField("myProfileUiState").apply {
                isAccessible = true
            }
        val onPrivacyHitUpItemSelectedMethodReflection = MyProfileFragment::class.java
            .getDeclaredMethod("onPrivacyHitUpItemSelected", Int::class.java)
            .apply {
                isAccessible = true
            }
        val setNewUserAvatarWithUriMethodReflection = MyProfileFragment::class.java
            .getDeclaredMethod("setNewUserAvatarWithUri", Uri::class.java)
            .apply {
                isAccessible = true
            }

        mModel = mModelFieldReflection.get(fragment) as MyProfileViewModel
        mUserAvatarTestData = UserAvatarTestData(
            setNewUserAvatarWithUriMethodReflection,
            fragment!!
        )
        mPrivacyHitUpTestData = PrivacyHitUpTestData(
            changedInputHashFieldReflection.get(fragment) as HashMap<String, Any>,
            onPrivacyHitUpItemSelectedMethodReflection,
            fragment!!)
        mMyProfileUiStateTestData = MyProfileUiStateTestData(
            myProfileStateFlowFieldReflection.get(mModel) as MutableStateFlow<MyProfileState?>,
            myProfileUiStateFieldReflection.get(mModel) as LiveData<MyProfileUiState?>
        )

        mResources = InstrumentationRegistry.getInstrumentation().targetContext.resources

        mTestAvatarUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + mResources.getResourcePackageName(R.drawable.ic_add_friend)
                    + '/' + mResources.getResourceTypeName(R.drawable.ic_add_friend)
                    + '/' + mResources.getResourceEntryName(R.drawable.ic_add_friend)
        )

        val testState = generateMyProfileUiTestState(mTestAvatarUri)

        mMyProfileUiStateTestData.setState(testState)
    }

    @Test
    fun allElementsInPlaceTest() {
        Espresso.onView(withId(R.id.plus_icon))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.username_input))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.about_me_input))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.current_password_input))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.new_password_input))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.new_password_confirmation_input))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.privacy_hit_up))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.confirm_button))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsEnabledTest() {
        Espresso.onView(withId(R.id.plus_icon))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.about_me_input))
            .perform(ViewActions.click(), ViewActions.closeSoftKeyboard())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.current_password_input))
            .perform(ViewActions.click(), ViewActions.closeSoftKeyboard())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.new_password_input))
            .perform(ViewActions.click(), ViewActions.closeSoftKeyboard())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.new_password_confirmation_input))
            .perform(ViewActions.click(), ViewActions.closeSoftKeyboard())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.privacy_hit_up))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.confirm_button))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun longTextFitsOneVisualLineForAllTextInputsTest() {
        val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur."

        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.username_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(longText), ViewActions.closeSoftKeyboard())
            .check(MaterialTextInputVisualLineCountViewAssertion(1))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.about_me_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(longText), ViewActions.closeSoftKeyboard())
            .check(MaterialTextInputVisualLineCountViewAssertion(1))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.password_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(longText), ViewActions.closeSoftKeyboard())
            .check(MaterialTextInputVisualLineCountViewAssertion(1))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.password_confirmation_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(longText), ViewActions.closeSoftKeyboard())
            .check(MaterialTextInputVisualLineCountViewAssertion(1))
    }

    @Test
    fun allPrivacyHitUpVariantsAreVisibleOnClickTest() { Espresso.onView(withId(R.id.scroll_view))
            .perform(ViewActions.swipeUp())
        Espresso.onView(withId(R.id.privacy_hit_up))
            .perform(ViewActions.click())

        val privacyHitUpVariants = mContext.resources.getStringArray(R.array.hit_up_variants)

        // todo: still doesnt work:

//        for (privacyHitUpVariant in privacyHitUpVariants) {
//            Espresso.onView(withText(privacyHitUpVariant))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        }

        assertEquals(privacyHitUpVariants.size, mBinding.privacyHitUp.adapter.count)

        for (itemIndex in 0 until mBinding.privacyHitUp.adapter.count) {
            assertEquals(
                privacyHitUpVariants[itemIndex],
                mBinding.privacyHitUp.adapter.getItem(itemIndex))
        }
    }

    @Test
    fun providingNoNewInfoLeadsToNothingTest() {
        Espresso.onView(withId(R.id.confirm_button)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.loading_screen)).check(ViewAssertions.doesNotExist())
    }

    @Test
    fun providingNewAvatarGoesWithoutShowingErrorMessageTest() {
        mMyProfileFragmentScenarioRule.onFragment {
            mUserAvatarTestData.setNewUserAvatarWithUri(mTestAvatarUri)
        }

        Espresso.onView(withId(R.id.confirm_button))
            .perform(WaitingViewAction(1000), ViewActions.click())
        Espresso.onView(withId(R.id.loading_screen)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun providingNewDescriptionGoesWithoutShowingErrorMessageTest() {
        val newDescription = "fqfq qwfq fq f q wf"

        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.about_me_input)), withId(R.id.input)))
            .perform(
                ViewActions.scrollTo(),
                ViewActions.typeText(newDescription),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(withId(R.id.confirm_button)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.loading_screen)).check(ViewAssertions.matches(isDisplayed()))
    }

    data class PasswordChangingTestCase(
        val curPassword: String = String(),
        val newPassword: String = String(),
        val repeatNewPassword: String = String(),
        val isIncorrect: Boolean
    )

    @Test
    fun passwordChangingTest() {
        val correctPassword = "password"
        val testCases = listOf(
            PasswordChangingTestCase(isIncorrect = true),
            PasswordChangingTestCase(curPassword = correctPassword, isIncorrect = true),
            PasswordChangingTestCase(newPassword = correctPassword, isIncorrect = true),
            PasswordChangingTestCase(repeatNewPassword = correctPassword, isIncorrect = true),
            PasswordChangingTestCase(curPassword = correctPassword, newPassword = correctPassword, isIncorrect = true),
            PasswordChangingTestCase(curPassword = correctPassword, repeatNewPassword = correctPassword, isIncorrect = true),
            PasswordChangingTestCase(correctPassword, correctPassword, correctPassword, false),
        )

        for (testCase in testCases) {
            if (testCase.curPassword.isNotEmpty()) {
                Espresso.onView(Matchers.allOf(
                    isDescendantOfA(withId(R.id.current_password_input)), withId(R.id.input)))
                    .perform(ViewActions.scrollTo(), ViewActions.typeText(testCase.curPassword), ViewActions.closeSoftKeyboard())
            }
            if (testCase.newPassword.isNotEmpty()) {
                Espresso.onView(Matchers.allOf(
                    isDescendantOfA(withId(R.id.new_password_input)), withId(R.id.input)))
                    .perform(ViewActions.scrollTo(), ViewActions.typeText(testCase.newPassword), ViewActions.closeSoftKeyboard())
            }
            if (testCase.repeatNewPassword.isNotEmpty()) {
                Espresso.onView(Matchers.allOf(
                    isDescendantOfA(withId(R.id.new_password_confirmation_input)), withId(R.id.input)))
                    .perform(ViewActions.scrollTo(), ViewActions.typeText(testCase.repeatNewPassword), ViewActions.closeSoftKeyboard())
            }

            Espresso.onView(withId(R.id.confirm_button)).perform(ViewActions.click())

            if (!testCase.isIncorrect) {
                Espresso.onView(withId(R.id.loading_screen)).check(ViewAssertions.matches(isDisplayed()))
            } else {
                Espresso.onView(withId(R.id.loading_screen)).check(ViewAssertions.doesNotExist())
            }

            clearPasswordFields()
        }
    }

    private fun clearPasswordFields() {
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.current_password_input)), withId(R.id.input)))
            .perform(ViewActions.scrollTo(), ViewActions.clearText(), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.new_password_input)), withId(R.id.input)))
            .perform(ViewActions.scrollTo(), ViewActions.clearText(), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.new_password_confirmation_input)), withId(R.id.input)))
            .perform(ViewActions.scrollTo(), ViewActions.clearText(), ViewActions.closeSoftKeyboard())
    }

    @Test
    fun providingNewHitUpOptionGoesWithoutShowingErrorMessageTest() {
        mMyProfileFragmentScenarioRule.onFragment {
            mMyProfileUiStateTestData.setState(
                MyProfileState(
                    avatar = mTestAvatarUri,
                    username = "fwqf",
                    description = "g3523 235 235",
                    hitUpOption = DataMyProfile.HitUpOption.POSITIVE
                )
            )
        }

        Espresso.onView(withId(R.id.privacy_hit_up)).perform(scrollTo())

        mMyProfileFragmentScenarioRule.onFragment {
            mPrivacyHitUpTestData.changeCurPrivacyHitUpPosition(DataMyProfile.HitUpOption.NEGATIVE.index)
        }

        Espresso.onView(withId(R.id.confirm_button)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.loading_screen)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun errorMessageShownOnUiStateWithErrorTest() {
        val error = Error(0, "Test", false)

        mMyProfileFragmentScenarioRule.onFragment {
            mMyProfileUiStateTestData.showError(error)
        }

        Espresso.onView(withText(error.message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun criticalErrorLeadsToClosingAppTest() {
        val error = Error(0, "Test", true)

        mMyProfileFragmentScenarioRule.onFragment {
            mMyProfileUiStateTestData.showError(error)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Exception) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }

    @Test
    fun successfulProfileDataSavingLeadsToSuccessMessageShowingTest() {
        val state = MyProfileState(
            mTestAvatarUri,
            "me",
            "something",
            DataMyProfile.HitUpOption.NEGATIVE,
            listOf(SuccessfulProfileSavingCallbackOperation()))

        mMyProfileUiStateTestData.setState(state)

        Espresso.onView(withText(R.string.profile_data_saved))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}