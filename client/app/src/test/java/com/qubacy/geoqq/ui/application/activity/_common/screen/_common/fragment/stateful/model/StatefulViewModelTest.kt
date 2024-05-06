package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.error.type.TestErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.state.BaseUiState
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

abstract class StatefulViewModelTest<
    UiStateType : BaseUiState, ViewModelType : StatefulViewModel<UiStateType>
> {
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    protected lateinit var mModel: ViewModelType

    protected var mGetErrorResult: Error? = null

    protected open fun setUiState(uiState: UiStateType) {
        StatefulViewModel::class.java.getDeclaredField("mUiState")
            .apply { isAccessible = true }
            .set(mModel, uiState)
    }

    @Before
    open fun setup() {
        preInit()
        init()
    }

    @After
    open fun clear() {
        mGetErrorResult = null
    }

    protected open fun preInit() { }

    private fun init() {
        resetResults()
        initViewModel()
    }

    protected open fun resetResults() { }

    protected open fun initViewModel() {
        val savedStateHandleMock = Mockito.mock(SavedStateHandle::class.java)
        val errorDataSourceMock = Mockito.mock(LocalErrorDataSource::class.java)

        Mockito.`when`(errorDataSourceMock.getError(Mockito.anyLong()))
            .thenAnswer { mGetErrorResult }

        mModel = createViewModel(savedStateHandleMock, errorDataSourceMock)
    }

    protected abstract fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataSource: LocalErrorDataSource
    ): ViewModelType

    @Test
    fun retrieveErrorTest() = runTest {
        val expectedError = TestError.normal

        mGetErrorResult = expectedError

        mModel.uiOperationFlow.test {
            mModel.retrieveError(TestErrorType.TEST)

            val errorOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)

            val error = (errorOperation as ErrorUiOperation).error

            Assert.assertEquals(expectedError.id, error.id)
        }
    }
}