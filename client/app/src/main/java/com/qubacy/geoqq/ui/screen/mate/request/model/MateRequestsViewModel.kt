package com.qubacy.geoqq.ui.screen.mate.request.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.mates.request.entity.MateRequest
import com.qubacy.geoqq.data.mates.request.state.MateRequestsState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.state.MateRequestsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// todo: provide a repository as a param..
class MateRequestsViewModel(

) : WaitingViewModel() {
    // todo: assign to the repository's flow:
    private val mMateRequestsStateFlow = MutableStateFlow<MateRequestsState?>(null)

    private val mMateRequestsUiStateFlow = mMateRequestsStateFlow.map { stateToUiState(it) }
    val mateRequestFlow: LiveData<MateRequestsUiState?> = mMateRequestsUiStateFlow.asLiveData()

    // todo: delete:
    init {
        mMateRequestsStateFlow.tryEmit(
            MateRequestsState(
            listOf(MateRequest(0), MateRequest(1), MateRequest(2)),
            listOf(User(0, "user 1"), User(1, "user 2"), User(2,"user 3")),
            listOf()
        ))
    }

    fun acceptMateRequest(mateRequest: MateRequest) {
        isWaiting.value = true // todo: uncomment;

        viewModelScope.launch {
            // todo: calling an appropriate method..

            // todo: delete:
            val requests = mateRequestFlow.value!!.mateRequests.filter { it.userId != mateRequest.userId }
            val users = mateRequestFlow.value!!.users.filter { it.userId != mateRequest.userId }

            mMateRequestsStateFlow.tryEmit(MateRequestsState(
                requests,
                users,
                listOf()
            ))

            isWaiting.value = false
        }
    }

    fun declineMateRequest(mateRequest: MateRequest) {
        isWaiting.value = true

        viewModelScope.launch {
            // todo: calling an appropriate method..

            // todo: delete:
            val requests = mateRequestFlow.value!!.mateRequests.filter { it.userId != mateRequest.userId }
            val users = mateRequestFlow.value!!.users.filter { it.userId != mateRequest.userId }

            mMateRequestsStateFlow.tryEmit(MateRequestsState(
                requests,
                users,
                listOf()
            ))

            isWaiting.value = false
        }
    }

    private fun stateToUiState(state: MateRequestsState?): MateRequestsUiState? {
        if (isWaiting.value == true) isWaiting.value = false // todo: should it be this way?
        if (state == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in state.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return MateRequestsUiState(state.mateRequests, state.users, uiOperations)
    }

    // todo: do i need any operations here?
    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                return ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }
}

class MateRequestsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateRequestsViewModel::class.java))
            throw IllegalArgumentException()

        return MateRequestsViewModel() as T
    }
}