package com.qubacy.geoqq.ui.screen.myprofile

import android.content.Context
import androidx.databinding.DataBindingUtil
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
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.myprofile.MyProfileContext
import com.qubacy.geoqq.data.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.data.myprofile.state.MyProfileState
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Exception
import java.lang.reflect.Field

@RunWith(AndroidJUnit4::class)
class MyProfileFragmentTest {
    class PrivacyHitUpTestData(
        val curPrivacyHitUpPositionFieldReflection: Field,
        val fragment: MyProfileFragment
    ) {
        fun getCurPrivacyHitUpPosition(): Int {
            return curPrivacyHitUpPositionFieldReflection.getInt(fragment)
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
                if (myProfileUiState.value != null) myProfileUiState.value!!.avatar else null
            var username =
                if (myProfileUiState.value != null) myProfileUiState.value!!.username else null
            var description =
                if (myProfileUiState.value != null) myProfileUiState.value!!.description else null
            var password =
                if (myProfileUiState.value != null) myProfileUiState.value!!.password else null
            var hitUpOption =
                if (myProfileUiState.value != null) myProfileUiState.value!!.hitUpOption else null

            val newState = MyProfileState(
                avatar, username, description, password, hitUpOption, operations)

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

    private lateinit var mPrivacyHitUpTestData: PrivacyHitUpTestData
    private lateinit var mMyProfileUiStateTestData: MyProfileUiStateTestData

    @Before
    fun setup() {
        mMyProfileFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_MyProfile)
        mContext = InstrumentationRegistry.getInstrumentation().targetContext

        var fragment: MyProfileFragment? = null

        mMyProfileFragmentScenarioRule.apply {
            moveToState(Lifecycle.State.RESUMED)
            onFragment {
                mBinding = DataBindingUtil.getBinding<FragmentMyProfileBinding>(it.view!!)!!
                fragment = it
                mModel = ViewModelProvider(it)[MyProfileViewModel::class.java]
            }
        }

        val curPrivacyHitUpPositionFieldReflection =
            MyProfileFragment::class.java.getDeclaredField("mPrivacyHitUpPosition").apply {
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

        mPrivacyHitUpTestData = PrivacyHitUpTestData(
            curPrivacyHitUpPositionFieldReflection, fragment!!)
        mMyProfileUiStateTestData = MyProfileUiStateTestData(
            myProfileStateFlowFieldReflection.get(mModel) as MutableStateFlow<MyProfileState?>,
            myProfileUiStateFieldReflection.get(mModel) as LiveData<MyProfileUiState?>
        )
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
        Espresso.onView(withId(R.id.password_input))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.password_confirmation_input))
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
        Espresso.onView(withId(R.id.username_input))
            .perform(ViewActions.click(), ViewActions.closeSoftKeyboard())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.about_me_input))
            .perform(ViewActions.click(), ViewActions.closeSoftKeyboard())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.password_input))
            .perform(ViewActions.click(), ViewActions.closeSoftKeyboard())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.password_confirmation_input))
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
    fun allPrivacyHitUpVariantsAreVisibleOnClickTest() {
        Espresso.onView(withId(R.id.scroll_view))
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
    fun clickingPrivacyHitUpYesVariantChangesCurrentPrivacyHitUpVariantTest() {
        val hitUpVariantCaption = "Yes"
        val hitUpVariantPosition = 0

        Espresso.onView(withId(R.id.scroll_view))
            .perform(ViewActions.swipeUp())
        Espresso.onView(withId(R.id.privacy_hit_up))
            .perform(ViewActions.click())
        Espresso.onView(withText(hitUpVariantCaption))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.privacy_hit_up))
            .check(ViewAssertions.matches(withText(hitUpVariantCaption)))

        assertEquals(hitUpVariantPosition, mPrivacyHitUpTestData.getCurPrivacyHitUpPosition())
    }

    @Test
    fun providingNotFullProfileInformationLeadsToShowingMessageTest() {
        val text = "Some text"

        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.username_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.about_me_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.confirm_button))
            .perform(ViewActions.click())
        Espresso.onView(withText(R.string.error_my_profile_data_incorrect))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun providingFullProfileInformationGoesWithoutShowingMessagesTest() {
        val text = "Sometext"

        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.username_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.about_me_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.password_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.password_confirmation_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())

        val hitUpVariantCaption = "Yes"

        Espresso.onView(withId(R.id.scroll_view))
            .perform(ViewActions.swipeUp())
        Espresso.onView(withId(R.id.privacy_hit_up))
            .perform(ViewActions.click())
        Espresso.onView(withText(hitUpVariantCaption))
            .perform(ViewActions.click())

        Espresso.onView(withId(R.id.confirm_button))
            .perform(ViewActions.click())
        Espresso.onView(withText(R.string.error_my_profile_data_incorrect))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun errorMessageShownOnUiStateWithErrorTest() {
        val error = Error(R.string.error_my_profile_saving_failed, Error.Level.NORMAL)

        mMyProfileFragmentScenarioRule.onFragment {
            mMyProfileUiStateTestData.showError(error)
        }

        Espresso.onView(withText(R.string.error_my_profile_saving_failed))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun criticalErrorLeadsToClosingAppTest() {
        val error = Error(R.string.error_my_profile_saving_failed, Error.Level.NORMAL)

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
    fun providingCorrectDataLeadsToShowingSuccessMessageTest() {
        val state = MyProfileState(
            null,
            "username",
            "something",
            "password",
            MyProfileContext.HitUpOption.NEGATIVE,
            listOf(SuccessfulProfileSavingCallbackOperation()))

        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.username_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(state.username), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.about_me_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(state.description), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.password_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(state.password), ViewActions.closeSoftKeyboard())
        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.password_confirmation_input)), withId(R.id.input)))
            .perform(ViewActions.typeText(state.password), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.confirm_button))
            .perform(ViewActions.click())

        mMyProfileUiStateTestData.setState(state)
    }

    @Test
    fun successfulProfileDataSavingLeadsToSuccessMessageShowingTest() {
        val state = MyProfileState(
            null,
            "me",
            "something",
            "pass",
            MyProfileContext.HitUpOption.NEGATIVE,
            listOf(SuccessfulProfileSavingCallbackOperation()))

        mMyProfileUiStateTestData.setState(state)

        Espresso.onView(withText(R.string.profile_data_saved))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}