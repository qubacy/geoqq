package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model

import app.cash.turbine.test
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.SendMateRequestDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.operation.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

interface ChatViewModelTest<
    ViewModelType : BusinessViewModel<*, *>
> {
    @Test
    fun onChatSendMessageTest() = runTest {
        val viewModel = getChatViewModelViewModel()
        val sendMessageDomainResult = SendMessageDomainResult()

        val expectedLoadingState = false

        viewModel.uiOperationFlow.test {
            getChatViewModelResultFlow().emit(sendMessageDomainResult)

            val messageSentOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(MessageSentUiOperation::class, messageSentOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = viewModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun onChatSendMateRequestTest() = runTest {
        val viewModel = getChatViewModelViewModel()
        val sendMateRequestDomainResult = SendMateRequestDomainResult()

        val expectedLoadingState = false

        viewModel.uiOperationFlow.test {
            getChatViewModelResultFlow().emit(sendMateRequestDomainResult)

            val requestSentOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(MateRequestSentToInterlocutorUiOperation::class, requestSentOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val finalUiState = viewModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    fun getChatViewModelViewModel(): ViewModelType
    fun getChatViewModelResultFlow(): MutableSharedFlow<DomainResult>
}