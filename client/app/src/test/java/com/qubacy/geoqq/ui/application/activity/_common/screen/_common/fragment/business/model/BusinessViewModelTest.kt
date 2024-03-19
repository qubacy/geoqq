package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model

import app.cash.turbine.test
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

abstract class BusinessViewModelTest<
    UiStateType : BusinessUiState,
    UseCaseType: UseCase,
    ViewModelType : BusinessViewModel<UiStateType>
>(
    private val mUseCaseClass: Class<UseCaseType>
) : StatefulViewModelTest<UiStateType, ViewModelType>() {
    protected lateinit var mUseCase: UseCaseType
    protected lateinit var mResultFlow: MutableSharedFlow<DomainResult>

    override fun preInit() {
        mUseCase = initUseCase()
    }

    protected open fun initUseCase(): UseCaseType {
        val useCase = createUseCaseMock()

        mResultFlow = MutableSharedFlow()

        Mockito.`when`(useCase.resultFlow).thenReturn(mResultFlow)

        return useCase
    }

    private fun createUseCaseMock(): UseCaseType {
        return Mockito.mock(mUseCaseClass)
    }

    @Test
    fun changeLoadingStateTest() = runTest {
        val expectedInitIsLoading = false
        val expectedFinalIsLoading = true

        mModel.uiOperationFlow.test {
            mModel.changeLoadingState(expectedInitIsLoading)

            val initOperation = awaitItem()

            Assert.assertEquals(SetLoadingStateUiOperation::class, initOperation::class)
            Assert.assertEquals(expectedInitIsLoading, mModel.uiState.isLoading)

            val initSetLoadingOperation = initOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedInitIsLoading, initSetLoadingOperation.isLoading)

            mModel.changeLoadingState(expectedFinalIsLoading)

            val finalOperation = awaitItem()

            Assert.assertEquals(SetLoadingStateUiOperation::class, finalOperation::class)
            Assert.assertEquals(expectedFinalIsLoading, mModel.uiState.isLoading)

            val finalSetLoadingOperation = finalOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedFinalIsLoading, finalSetLoadingOperation.isLoading)
        }
    }

    /**
     * There is no opportunity to test it like this for now. We need a shared DomainResult in order
     * to accomplish it;
     */
//    @Test
//    fun processErrorDomainResultTest() = runTest {
//        val expectedError = TestError.normal
//        val errorDomainResult = TestDomainResult(expectedError)
//
//        mModel.uiOperationFlow.test {
//            mResultFlow.emit(errorDomainResult)
//
//            val operation = awaitItem()
//
//            Assert.assertEquals(ErrorUiOperation::class, operation::class)
//            Assert.assertEquals(expectedError, mModel.uiState.error)
//
//            val errorOperation = operation as ErrorUiOperation
//
//            Assert.assertEquals(expectedError, errorOperation.error)
//        }
//    }
}