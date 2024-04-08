package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
class MateChatViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mMateChatUseCase: MateChatUseCase
) : BusinessViewModel<MateChatUiState, MateChatUseCase>(
    mSavedStateHandle, mErrorDataRepository, mMateChatUseCase
) {
    override fun generateDefaultUiState(): MateChatUiState {
        return MateChatUiState()
    }

    // todo: is it ok?:
    fun setChatContext(chat: MateChatPresentation) {
        mUiState.interlocutor = chat.user
    }


}

@Qualifier
annotation class MateChatViewModelFactoryQualifier

class MateChatViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mMateChatUseCase: MateChatUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateChatViewModel::class.java))
            throw IllegalArgumentException()

        return MateChatViewModel(handle, mErrorDataRepository, mMateChatUseCase) as T
    }
}