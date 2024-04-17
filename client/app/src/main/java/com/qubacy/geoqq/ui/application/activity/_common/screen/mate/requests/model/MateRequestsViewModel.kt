package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.mate.requests.usecase.MateRequestsUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.MateableViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
class MateRequestsViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mMateRequestsUseCase: MateRequestsUseCase
) : BusinessViewModel<MateRequestsUiState, MateRequestsUseCase>(
    mSavedStateHandle, mErrorDataRepository, mMateRequestsUseCase
),
    MateableViewModel
{
    override fun isInterlocutorMateableOrDeletable(interlocutor: UserPresentation): Boolean {
        TODO("Not yet implemented")
    }

    override fun generateDefaultUiState(): MateRequestsUiState {
        return MateRequestsUiState()
    }
}

@Qualifier
annotation class MateRequestsViewModelFactoryQualifier

class MateRequestsViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mMateRequestsUseCase: MateRequestsUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateRequestsViewModel::class.java))
            throw IllegalArgumentException()

        return MateRequestsViewModel(handle, mErrorDataRepository, mMateRequestsUseCase) as T
    }
}