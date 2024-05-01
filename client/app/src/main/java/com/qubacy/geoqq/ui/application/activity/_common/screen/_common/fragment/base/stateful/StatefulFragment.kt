package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful

import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.state.BaseUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler.error.ErrorUiOperationHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

abstract class StatefulFragment<
    ViewBindingType : ViewBinding,
    UiStateType : BaseUiState,
    ViewModelType : StatefulViewModel<UiStateType>
>() : BaseFragment<ViewBindingType>() {
    companion object {
        const val TAG = "StatefulFragment"
    }

    protected abstract val mModel: ViewModelType
    private var mOperationCollectionJob: Job? = null

    protected lateinit var mUiOperationHandlers: Array<UiOperationHandler<*>>

    private val mErrorMutex: Mutex = Mutex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mUiOperationHandlers = generateUiOperationHandlers()
    }

    @CallSuper
    protected open fun generateUiOperationHandlers(): Array<UiOperationHandler<*>> {
        return arrayOf(ErrorUiOperationHandler(this))
    }

    override fun onStop() {
        mOperationCollectionJob?.cancel()

        super.onStop()
    }

    override fun onStart() {
        super.onStart()

        startOperationCollection()
        initUiState(mModel.uiState)
    }

    private fun startOperationCollection() {
        mOperationCollectionJob = lifecycleScope.launch {
            mModel.uiOperationFlow.collect {
                mErrorMutex.lock() // todo: is it ok?

                processUiOperation(it)
            }
        }
    }

    private fun initUiState(uiState: UiStateType) {
        runInitWithUiState(uiState)
    }

    protected open fun runInitWithUiState(uiState: UiStateType) {
        if (uiState.error != null) onErrorOccurred(uiState.error!!)
    }

    @CallSuper
    protected open fun processUiOperation(uiOperation: UiOperation): Boolean {
        for (uiOperationHandler in mUiOperationHandlers) {
            Log.d(TAG, "processUiOperation(): uiOperation = $uiOperation; uiOperationHandler = $uiOperationHandler;")

            if (uiOperationHandler.handleUiOperation(uiOperation)) {
                if (uiOperationHandler !is ErrorUiOperationHandler) mErrorMutex.unlock()

                return true
            }
        }

        return false
    }

    private fun processErrorOperation(errorOperation: ErrorUiOperation) {
        onErrorOccurred(errorOperation.error)
    }

    override fun onErrorHandled(error: Error) {
        mModel.absorbCurrentError()
        mErrorMutex.unlock()
    }
}
