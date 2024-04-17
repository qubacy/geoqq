package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.mate.requests.usecase.MateRequestsUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModelFactoryQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object MateRequestsViewModelModule {
    @Provides
    @MateRequestsViewModelFactoryQualifier
    fun provideMateRequestsViewModel(
        errorDataRepository: ErrorDataRepository,
        mateRequestsUseCase: MateRequestsUseCase
    ): ViewModelProvider.Factory {
        return MateRequestsViewModelFactory(errorDataRepository, mateRequestsUseCase)
    }
}