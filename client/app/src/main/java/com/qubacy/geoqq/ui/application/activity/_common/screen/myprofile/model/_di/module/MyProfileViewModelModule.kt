package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._di.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.impl.MyProfileViewModelImplFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.impl.MyProfileViewModelFactoryQualifier
import dagger.Binds
import dagger.Module

@Module
abstract class MyProfileViewModelModule {
    @Binds
    @MyProfileViewModelFactoryQualifier
    abstract fun bindMyProfileViewModel(
        myProfileViewModelFactory: MyProfileViewModelImplFactory
    ): ViewModelProvider.Factory
}