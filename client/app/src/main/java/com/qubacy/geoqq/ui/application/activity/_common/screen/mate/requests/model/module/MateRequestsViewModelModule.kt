package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
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
        localErrorDataSource: LocalErrorDataSource,
        mateRequestsUseCase: MateRequestsUseCase
    ): ViewModelProvider.Factory {
        return MateRequestsViewModelFactory(localErrorDataSource, mateRequestsUseCase)
    }
}