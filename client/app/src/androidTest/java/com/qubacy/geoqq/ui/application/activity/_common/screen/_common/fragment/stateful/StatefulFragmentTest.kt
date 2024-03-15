package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelLazy
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.BaseUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.TestUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field

abstract class StatefulFragmentTest<
    ViewBindingType : ViewBinding,
    UiStateType : BaseUiState,
    TestUiStateType : TestUiState,
    ViewModelType : StatefulViewModel<UiStateType>,
    FragmentType : StatefulFragment<ViewBindingType, UiStateType, ViewModelType>
>() : BaseFragmentTest<ViewBindingType, FragmentType>() {
    protected lateinit var mModel: ViewModelType
    protected lateinit var mUiOperationFlow: MutableSharedFlow<UiOperation>

    @Before
    override fun setup() {
        super.setup()

        initOperationFlow()
    }

    private fun retrieveModelFieldReflection(): Field {
        return getFragmentClass()
            .getDeclaredField("mModel\$delegate")
            .apply { isAccessible = true }
    }

    override fun initFragmentOnActivity(fragment: Fragment) {
        super.initFragmentOnActivity(fragment)

        val mModelFieldReflection = retrieveModelFieldReflection()

        mModel = (mModelFieldReflection.get(mFragment) as ViewModelLazy<ViewModelType>).value
    }

    private fun initOperationFlow() {
        mUiOperationFlow = mModel.uiOperationFlow as MutableSharedFlow
    }

    private suspend fun postUiOperation(uiOperation: UiOperation) {
        mUiOperationFlow.emit(uiOperation)
    }

    protected fun getTestUiState(): TestUiStateType {
        return mModel.uiState as TestUiStateType
    }

    @Test
    fun handleNormalErrorTest() = runTest {
        val errorOperation = ErrorUiOperation(TestError.normal)

        postUiOperation(errorOperation)

        Espresso.onView(ViewMatchers.withText(TestError.normal.message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.component_error_dialog_button_neutral_caption))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(TestError.normal.message))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handleCriticalErrorTest() = runTest {
        val errorOperation = ErrorUiOperation(TestError.critical)

        postUiOperation(errorOperation)

        Espresso.onView(ViewMatchers.withText(TestError.critical.message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.component_error_dialog_button_neutral_caption))
            .perform(ViewActions.click())

        try { Espresso.onView(ViewMatchers.isRoot()).check(ViewAssertions.doesNotExist()) }
        catch (_: NoActivityResumedException) { }
    }

    @Test
    fun operationFlowCollectedOnceAfterFragmentRestartTest() {
        val expectedCollectorCount = 1

        mActivityScenario.moveToState(Lifecycle.State.CREATED)
        mActivityScenario.moveToState(Lifecycle.State.STARTED)

        val gottenCollectorCount = Class
            .forName("kotlinx.coroutines.flow.internal.AbstractSharedFlow")
            .getDeclaredField("nCollectors")
            .apply { isAccessible = true }.get(mUiOperationFlow)

        Assert.assertEquals(expectedCollectorCount, gottenCollectorCount)
    }
}
