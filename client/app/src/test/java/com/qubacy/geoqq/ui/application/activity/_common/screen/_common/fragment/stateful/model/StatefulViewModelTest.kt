package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.error.type.TestErrorType
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.BaseUiState
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
        val errorDataRepositoryMock = Mockito.mock(ErrorDataRepository::class.java)

        Mockito.`when`(errorDataRepositoryMock.getError(Mockito.anyLong()))
            .thenAnswer { mGetErrorResult }

        mModel = createViewModel(savedStateHandleMock, errorDataRepositoryMock)
    }

    protected abstract fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataRepository: ErrorDataRepository
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