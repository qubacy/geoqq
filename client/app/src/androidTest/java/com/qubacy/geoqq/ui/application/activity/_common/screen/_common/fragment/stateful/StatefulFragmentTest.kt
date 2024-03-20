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
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.BaseUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Field

abstract class StatefulFragmentTest<
    ViewBindingType : ViewBinding,
    UiStateType : BaseUiState,
    ViewModelType : StatefulViewModel<UiStateType>,
    ViewModelMockContextType: ViewModelMockContext<UiStateType>,
    FragmentType : StatefulFragment<ViewBindingType, UiStateType, ViewModelType>
>() : BaseFragmentTest<ViewBindingType, FragmentType>() {
    protected lateinit var mModel: ViewModelType
    protected lateinit var mViewModelMockContext: ViewModelMockContextType

    @Before
    override fun setup() {
        super.setup()
    }

    @After
    open fun clear() {
        // todo: there's no need to call .reset(). the context is recreated on every launch.
        //mViewModelMockContext.reset()
    }

    override fun defaultInit() {
        setupViewModelContext()
        init()
    }

    /**
     * Meant to be called BEFORE any manipulations on mViewModelMockContext.
     */
    protected fun setupViewModelContext() {
        initViewModelContext()
        attachViewModelMockContext()
    }

    private fun initViewModelContext() {
        mViewModelMockContext = createViewModelMockContext()
    }

    protected abstract fun createViewModelMockContext(): ViewModelMockContextType

    /**
     * This method is meant to be used for attaching mViewModelMockContext to
     * your Fake..ViewModelModule;
     */
    protected abstract fun attachViewModelMockContext()

    override fun initMockedVars() {
        super.initMockedVars()
    }

    private fun retrieveModelFieldReflection(): Field {
        return getFragmentClass()
            .getDeclaredField("mModel\$delegate")
            .apply { isAccessible = true }
    }

    override fun initFragmentOnActivity(fragment: Fragment) {
        super.initFragmentOnActivity(fragment)

        initViewModelMock()
    }

    private fun initViewModelMock() {
        val mModelFieldReflection = retrieveModelFieldReflection()
        val viewModel = (mModelFieldReflection.get(mFragment) as ViewModelLazy<ViewModelType>).value

        mModel = viewModel
    }

    private suspend fun postUiOperation(uiOperation: UiOperation) {
        mViewModelMockContext.uiOperationFlow.emit(uiOperation)
    }

    @Test
    open fun handleNormalErrorTest() = runTest {
        defaultInit()

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
    open fun handleCriticalErrorTest() = runTest {
        defaultInit()

        val errorOperation = ErrorUiOperation(TestError.critical)

        postUiOperation(errorOperation)

        Espresso.onView(ViewMatchers.withText(TestError.critical.message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.component_error_dialog_button_neutral_caption))
            .perform(ViewActions.click())

        try { Espresso.onView(ViewMatchers.isRoot()).check(ViewAssertions.doesNotExist()) }
        catch (_: NoActivityResumedException) { }
    }

    // todo: doesn't work:
//    @Test
//    fun operationFlowCollectedOnceAfterFragmentRestartTest() {
//        val expectedCollectorCount = 1
//
//        mActivityScenario.moveToState(Lifecycle.State.CREATED)
//        mActivityScenario.moveToState(Lifecycle.State.STARTED)
//
//        val gottenCollectorCount = Class
//            .forName("kotlinx.coroutines.flow.internal.AbstractSharedFlow")
//            .getDeclaredField("nCollectors")
//            .apply { isAccessible = true }.get(mViewModelMockContext.uiOperationFlow)
//
//        Assert.assertEquals(expectedCollectorCount, gottenCollectorCount)
//    }
}
