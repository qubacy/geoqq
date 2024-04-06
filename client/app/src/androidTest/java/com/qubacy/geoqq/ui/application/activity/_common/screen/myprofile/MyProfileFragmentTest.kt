package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.factory._test.mock.MyProfileViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.module.MyProfileViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.module.FakeMyProfileViewModelModule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(MyProfileViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class MyProfileFragmentTest : BusinessFragmentTest<
    FragmentMyProfileBinding,
    MyProfileUiState,
    MyProfileViewModel,
    MyProfileViewModelMockContext,
    MyProfileFragment
>() {
    override fun createDefaultViewModelMockContext(): MyProfileViewModelMockContext {
        return MyProfileViewModelMockContext(MyProfileUiState())
    }

    override fun attachViewModelMockContext() {
        FakeMyProfileViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<MyProfileFragment> {
        return MyProfileFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.myProfileFragment
    }

    override fun getPermissionsToGrant(): Array<String> {
        return super.getPermissionsToGrant().plus(arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
    }

    @Test
    fun test() {

    }
}