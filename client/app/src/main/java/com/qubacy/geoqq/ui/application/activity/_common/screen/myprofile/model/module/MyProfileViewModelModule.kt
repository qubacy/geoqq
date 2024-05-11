package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.myprofile.usecase._common.MyProfileUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.MyProfileViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.MyProfileViewModelFactoryQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object MyProfileViewModelModule {
    @Provides
    @MyProfileViewModelFactoryQualifier
    fun provideMyProfileViewModel(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        myProfileUseCase: MyProfileUseCase
    ): ViewModelProvider.Factory {
        return MyProfileViewModelFactory(localErrorDataSource, myProfileUseCase)
    }
}