package com.qubacy.geoqq.ui.screen.mate.request.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.carousel3dlib.general.Carousel3DContext
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.operation.MateRequestAnswerProcessedOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestCountOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestsOperation
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.MateRequestAnswerProcessedUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.SetMateRequestsUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.state.MateRequestsUiState
import com.qubacy.geoqq.ui.screen.mate.request.model.util.IndexOffsetMap
import kotlinx.coroutines.flow.map

open class MateRequestsViewModel(
    private val mMateRequestsUseCase: MateRequestsUseCase
) : WaitingViewModel() {
    companion object {
        const val TAG = "MATE_REQ_VIEW_MODEL"

        const val DEFAULT_REQUEST_CHUNK_SIZE = 20
    }

    private val mMateRequestsStateFlow = mMateRequestsUseCase.stateFlow

    private val mMateRequestsUiStateFlow = mMateRequestsStateFlow.map { stateToUiState(it) }
    val mateRequestFlow: LiveData<MateRequestsUiState?> = mMateRequestsUiStateFlow.asLiveData()

    private var mIsGettingRequests: Boolean = false
    val isGettingRequests get() = mIsGettingRequests

    private var mTotalRequestCount = 0

    private var mCurrentTopRequestOffset = 0
    private var mCurrentBottomRequestOffset = 0

    private var mRequestChunkToLoad = 0
    private var mRequestChunkLoaded = 0

    private val mMateRequestIndexOffsetMap = IndexOffsetMap()

    init {
        mMateRequestsUseCase.setCoroutineScope(viewModelScope)
    }

    fun acceptMateRequest(position: Int, mateRequest: MateRequest) {
        mIsWaiting.value = true

        mMateRequestIndexOffsetMap.addIndex(position)
        mMateRequestsUseCase.answerMateRequest(mateRequest.id, true)
    }

    fun declineMateRequest(position: Int, mateRequest: MateRequest) {
        mIsWaiting.value = true

        mMateRequestIndexOffsetMap.addIndex(position)
        mMateRequestsUseCase.answerMateRequest(mateRequest.id, false)
    }

    fun getMateRequests() {
        mIsWaiting.value = true

        mMateRequestsUseCase.getMateRequestCount()
    }

    fun mateRequestsListRolled(
        edgePosition: Int,
        direction: Carousel3DContext.RollingDirection
    ) {
        val curState = mateRequestFlow.value
        val originalPosition = mMateRequestIndexOffsetMap.getIndexWithOffset(edgePosition)

        if (curState == null || mIsGettingRequests
        || mCurrentBottomRequestOffset - mCurrentTopRequestOffset <= DEFAULT_REQUEST_CHUNK_SIZE
        || (originalPosition != mCurrentBottomRequestOffset
            && originalPosition != mCurrentTopRequestOffset + DEFAULT_REQUEST_CHUNK_SIZE)
        ) {
            return
        }

        Log.d(TAG, "mateRequestsListRolled(): getting new chunk")

        mIsGettingRequests = true
        mRequestChunkToLoad++

        if (direction == Carousel3DContext.RollingDirection.UP) {
            mCurrentTopRequestOffset += DEFAULT_REQUEST_CHUNK_SIZE

            mMateRequestsUseCase.getMateRequests(
                DEFAULT_REQUEST_CHUNK_SIZE, mCurrentTopRequestOffset, false)

        } else {
            mCurrentBottomRequestOffset -= DEFAULT_REQUEST_CHUNK_SIZE

            mMateRequestsUseCase.getMateRequests(
                DEFAULT_REQUEST_CHUNK_SIZE, mCurrentBottomRequestOffset, false)
        }
    }

    private fun stateToUiState(state: MateRequestsState?): MateRequestsUiState? {
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

                mIsWaiting.value = false

                MateRequestAnswerProcessedUiOperation()
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                mIsWaiting.value = false

                ShowErrorUiOperation(handleErrorOperation.error)
            }
            InterruptionResult::class -> {
                val interruptionOperation = operation as InterruptionResult

                null
            }
            else -> {
                Log.d(TAG, "processOperation(): unknown operation ${operation::class.simpleName}")

                throw IllegalStateException()
            }
        }
    }

    private fun processSetMateRequestOperation(
        setMateRequestsOperation: SetMateRequestsOperation
    ): UiOperation? {
        mRequestChunkToLoad--

        if (mRequestChunkToLoad > 0) return null

        mIsWaiting.value = false
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

        mMateRequestsUseCase.getMateRequests(DEFAULT_REQUEST_CHUNK_SIZE, 0, true)

        if (mTotalRequestCount > DEFAULT_REQUEST_CHUNK_SIZE) {
            val minChunkCount = (mTotalRequestCount / DEFAULT_REQUEST_CHUNK_SIZE)
            mCurrentBottomRequestOffset = minChunkCount * DEFAULT_REQUEST_CHUNK_SIZE
            val lastChunkSize = mTotalRequestCount - mCurrentBottomRequestOffset

            mMateRequestsUseCase.getMateRequests(lastChunkSize, mCurrentBottomRequestOffset, true)

            ++mRequestChunkToLoad
        }
    }

    override fun retrieveError(errorId: Long) {
        mMateRequestsUseCase.getError(errorId)
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