package com.qubacy.geoqq.ui.screen.mate.request.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.carousel3dlib.general.Carousel3DContext
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.operation.MateRequestAnswerProcessedOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestCountOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestsOperation
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.MateRequestAnswerProcessedUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.SetMateRequestsUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.state.MateRequestsUiState
import kotlinx.coroutines.flow.map

open class MateRequestsViewModel(
    val mateRequestsUseCase: MateRequestsUseCase
) : WaitingViewModel() {
    companion object {
        const val DEFAULT_REQUEST_CHUNK_SIZE = 20
    }

    private val mMateRequestsStateFlow = mateRequestsUseCase.stateFlow

    private val mMateRequestsUiStateFlow = mMateRequestsStateFlow.map { stateToUiState(it) }
    val mateRequestFlow: LiveData<MateRequestsUiState?> = mMateRequestsUiStateFlow.asLiveData()

    private var mIsGettingRequests: Boolean = false
    val isGettingRequests get() = mIsGettingRequests

    private var mTotalRequestCount = 0

    private var mCurrentTopRequestOffset = 0
    private var mCurrentBottomRequestOffset = 0

    private var mRequestChunkToLoad = 0
    private var mRequestChunkLoaded = 0

    init {
        mateRequestsUseCase.setCoroutineScope(viewModelScope)
    }

    fun acceptMateRequest(mateRequest: MateRequest) {
        mIsWaiting.value = true

        mateRequestsUseCase.answerMateRequest(mateRequest.id, true)
    }

    fun declineMateRequest(mateRequest: MateRequest) {
        mIsWaiting.value = true

        mateRequestsUseCase.answerMateRequest(mateRequest.id, false)
    }

    fun getMateRequests() {
        mIsWaiting.value = true

        mateRequestsUseCase.getMateRequestCount()
    }

    fun mateRequestsListRolled(
        edgePosition: Int,
        direction: Carousel3DContext.RollingDirection
    ) {
        val curState = mateRequestFlow.value

        if (curState == null || mIsGettingRequests
        || mCurrentBottomRequestOffset - mCurrentTopRequestOffset <= DEFAULT_REQUEST_CHUNK_SIZE
        || (edgePosition != mCurrentBottomRequestOffset
            && edgePosition != mCurrentTopRequestOffset + DEFAULT_REQUEST_CHUNK_SIZE)
        ) {
            return
        }

        mIsGettingRequests = true
        mRequestChunkToLoad++

        if (direction == Carousel3DContext.RollingDirection.UP) {
            mCurrentTopRequestOffset += DEFAULT_REQUEST_CHUNK_SIZE

            mateRequestsUseCase.getMateRequests(
                DEFAULT_REQUEST_CHUNK_SIZE, mCurrentTopRequestOffset, false)

        } else {
            mCurrentBottomRequestOffset -= DEFAULT_REQUEST_CHUNK_SIZE

            mateRequestsUseCase.getMateRequests(
                DEFAULT_REQUEST_CHUNK_SIZE, mCurrentBottomRequestOffset, false)
        }
    }

    private fun stateToUiState(state: MateRequestsState?): MateRequestsUiState? {
        if (mIsWaiting.value == true) mIsWaiting.value = false // todo: should it be this way?
        if (state == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in state.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        val orderedMateRequests = generateOrderedMateRequestListFromChunks(state.mateRequestChunks)

        return MateRequestsUiState(orderedMateRequests, state.users, uiOperations)
    }

    private fun generateOrderedMateRequestListFromChunks(
        mateRequestChunks: HashMap<Long, List<MateRequest>>
    ): List<MateRequest> {
        val sortedMateRequestIds = mateRequestChunks.keys.sorted()

        return sortedMateRequestIds.map { mateRequestChunks[it]!! }.flatten()
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            SetMateRequestCountOperation::class -> {
                val setMateRequestCountOperation = operation as SetMateRequestCountOperation

                processSetMateRequestCountOperation(setMateRequestCountOperation)

                null
            }
            SetMateRequestsOperation::class -> {
                val setMateRequestsOperation = operation as SetMateRequestsOperation

                processSetMateRequestOperation(setMateRequestsOperation)
            }
            MateRequestAnswerProcessedOperation::class -> {
                val mateRequestAnswerProcessedOperation =
                    operation as MateRequestAnswerProcessedOperation

                MateRequestAnswerProcessedUiOperation()
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                return ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    private fun processSetMateRequestOperation(
        setMateRequestsOperation: SetMateRequestsOperation
    ): UiOperation? {
        mRequestChunkToLoad--

        if (mRequestChunkToLoad > 0) return null

        mIsGettingRequests = false

        return SetMateRequestsUiOperation(setMateRequestsOperation.isInit)
    }

    private fun processSetMateRequestCountOperation(
        setMateRequestCountOperation: SetMateRequestCountOperation
    ) {
        mTotalRequestCount = setMateRequestCountOperation.count

        getInitMateRequests()
    }

    private fun getInitMateRequests() {
        mIsGettingRequests = true
        mRequestChunkToLoad = 1
        mRequestChunkLoaded = 0

        mateRequestsUseCase.getMateRequests(DEFAULT_REQUEST_CHUNK_SIZE, 0, true)

        if (mTotalRequestCount > DEFAULT_REQUEST_CHUNK_SIZE) {
            val minChunkCount = (mTotalRequestCount / DEFAULT_REQUEST_CHUNK_SIZE)
            mCurrentBottomRequestOffset = minChunkCount * DEFAULT_REQUEST_CHUNK_SIZE
            val lastChunkSize = mTotalRequestCount - mCurrentBottomRequestOffset

            mateRequestsUseCase.getMateRequests(lastChunkSize, mCurrentBottomRequestOffset, true)

            ++mRequestChunkToLoad
        }
    }

    override fun retrieveError(errorId: Long) {
        TODO("Not yet implemented")
    }
}

open class MateRequestsViewModelFactory(
    val mateRequestsUseCase: MateRequestsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateRequestsViewModel::class.java))
            throw IllegalArgumentException()

        return MateRequestsViewModel(mateRequestsUseCase) as T
    }
}