package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.geo.chat.usecase.GeoChatUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.GeoChatViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.GeoChatViewModelFactoryQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object GeoChatViewModelModule {
    @Provides
    @GeoChatViewModelFactoryQualifier
    fun provideGeoChatViewModelFactory(
        errorDataRepository: ErrorDataRepository,
        geoChatUseCase: GeoChatUseCase
    ): ViewModelProvider.Factory {
        return GeoChatViewModelFactory(errorDataRepository, geoChatUseCase)
    }
}