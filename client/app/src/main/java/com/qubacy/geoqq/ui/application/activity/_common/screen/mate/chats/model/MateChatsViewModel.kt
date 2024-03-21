package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.mate.chats.usecase.MateChatsUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
class MateChatsViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mUseCase: MateChatsUseCase
) : BusinessViewModel<MateChatsUiState, MateChatsUseCase>(
    mSavedStateHandle, mErrorDataRepository, mUseCase
) {
    override fun generateDefaultUiState(): MateChatsUiState {
        return MateChatsUiState()
    }

}

@Qualifier
annotation class MateChatsViewModelFactoryQualifier

class MateChatsViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mMateChatsUseCase: MateChatsUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateChatsViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatsViewModel(handle, mErrorDataRepository, mMateChatsUseCase) as T
    }
}