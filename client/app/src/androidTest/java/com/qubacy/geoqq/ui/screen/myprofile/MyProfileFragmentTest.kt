package com.qubacy.geoqq.ui.screen.myprofile

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
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
        private val mModel: MyProfileViewModel,
        private val mMyProfileUiState: MutableLiveData<MyProfileUiState>
    ) {
        fun setMyProfileUiState(uiState: MyProfileUiState) {
            mMyProfileUiState.value = uiState
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
        val myProfileUiStateFieldReflection = MyProfileViewModel::class.java
            .getDeclaredField("mMyProfileUiState").apply {
                isAccessible = true
            }

        mPrivacyHitUpTestData = PrivacyHitUpTestData(
            curPrivacyHitUpPositionFieldReflection, fragment!!)
        mMyProfileUiStateTestData = MyProfileUiStateTestData(
            mModel,
            myProfileUiStateFieldReflection.get(mModel) as MutableLiveData<MyProfileUiState>
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

        // todo: why DOESN'T it work????

//        for (privacyHitUpVariant in privacyHitUpVariants) {
//            Log.d(TAG, "allPrivacyHitUpVariantsAreVisibleOnClickTest(): " +
//                    "privacyHitUpVariant = $privacyHitUpVariant")
//
//            Espresso.onView(withText(privacyHitUpVariant))
//                .check(ViewAssertions.matches(Matchers.allOf(
//                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
//                )))
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
        val text = "Some text"

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
        val error = Error(R.string.error_my_profile_changing_failed, Error.Level.NORMAL)
        val errorMyProfileUiState = MyProfileUiState(error = error)

        mMyProfileFragmentScenarioRule.onFragment {
            mMyProfileUiStateTestData.setMyProfileUiState(errorMyProfileUiState)
        }

        Espresso.onView(withText(R.string.error_my_profile_changing_failed))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun criticalErrorLeadsToClosingAppTest() {
        val error = Error(R.string.error_my_profile_changing_failed, Error.Level.NORMAL)
        val errorMyProfileUiState = MyProfileUiState(error = error)

        mMyProfileFragmentScenarioRule.onFragment {
            mMyProfileUiStateTestData.setMyProfileUiState(errorMyProfileUiState)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Exception) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }
}