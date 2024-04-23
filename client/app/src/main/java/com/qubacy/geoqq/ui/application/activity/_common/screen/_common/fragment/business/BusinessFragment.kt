package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragment

abstract class BusinessFragment<
    ViewBindingType : ViewBinding,
    UiStateType : BusinessUiState,
    ViewModelType : BusinessViewModel<UiStateType, *>
>() : StatefulFragment<ViewBindingType, UiStateType, ViewModelType>() {
    companion object {
        const val TAG = "BusinessFragment"
    }

    override val mStartTransitionOnPreDraw: Boolean = false

    override fun onStart() {
        super.onStart()

        initOnDestinationChangedListener(requireView())
    }

    private fun initOnDestinationChangedListener(view: View) {
        Navigation.findNavController(view).addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    Log.d(TAG, "onDestinationChanged(): destination = $destination;")

                    this@BusinessFragment.adjustUiWithLoadingState(true)
                }
            })
    }

    protected override fun runInitWithUiState(uiState: UiStateType) {
        super.runInitWithUiState(uiState)

        if (uiState.isLoading) adjustUiWithLoadingState(true)
    }

    protected override fun processUiOperation(uiOperation: UiOperation): Boolean {
        startPostponedEnterTransition() // todo: is it ok?

        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            SetLoadingStateUiOperation::class ->
                processSetLoadingOperation(uiOperation as SetLoadingStateUiOperation)
            else -> return false
        }

        return true
    }

    protected open fun processSetLoadingOperation(loadingOperation: SetLoadingStateUiOperation) {
        adjustUiWithLoadingState(loadingOperation.isLoading)
    }

    protected open fun adjustUiWithLoadingState(isLoading: Boolean) {}
}