package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common

import androidx.lifecycle.SavedStateHandle
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase._common.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.get.GetRequestChunkDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.update.UpdateRequestChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.InterlocutorViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.result.handler.InterlocutorDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.result.handler.MateRequestsDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.state.MateRequestsUiState

abstract class MateRequestsViewModel(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mMateRequestsUseCase: MateRequestsUseCase
) : BusinessViewModel<MateRequestsUiState, MateRequestsUseCase>(
    mSavedStateHandle, mErrorSource, mMateRequestsUseCase
), AuthorizedViewModel, InterlocutorViewModel {
    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(AuthorizedDomainResultHandler(this))
            .plus(InterlocutorDomainResultHandler(this))
            .plus(MateRequestsDomainResultHandler(this))
    }
    override fun generateDefaultUiState(): MateRequestsUiState {
        return MateRequestsUiState()
    }
    abstract fun getUserProfileWithMateRequestId(id: Long): UserPresentation
    abstract fun getNextRequestChunk()
    abstract fun answerRequest(position: Int, isAccepted: Boolean)
    abstract fun isNextRequestChunkGettingAllowed(): Boolean
    abstract fun onMateRequestsGetRequestChunk(
        getRequestChunkResult: GetRequestChunkDomainResult
    ): List<UiOperation>
    abstract fun onMateRequestsUpdateRequestChunk(
        updateRequestChunkResult: UpdateRequestChunkDomainResult
    ): List<UiOperation>
    abstract fun onMateRequestsAnswerMateRequest(
        answerRequestResult: AnswerMateRequestDomainResult
    ): List<UiOperation>
    open fun resetRequests() {
        mUiState.apply {
            requests.clear()

            newRequestCount = 0
            answeredRequestCount = 0
        }
    }
}