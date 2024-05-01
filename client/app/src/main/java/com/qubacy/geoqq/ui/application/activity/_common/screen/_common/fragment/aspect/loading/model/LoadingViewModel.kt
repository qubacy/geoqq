package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model

interface LoadingViewModel {
    fun preserveLoadingState(isLoading: Boolean)
    fun changeLoadingState(isLoading: Boolean)
}