package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq._common.context.util.getUriFromResId
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.factory._test.mock.MateRequestsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module.MateRequestsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module.FakeMateRequestsViewModelModule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(MateRequestsViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class MateRequestsFragmentTest : BusinessFragmentTest<
    FragmentMateRequestsBinding,
    MateRequestsUiState,
    MateRequestsViewModel,
    MateRequestsViewModelMockContext,
    MateRequestsFragment
>() {
    private lateinit var mImagePresentation: ImagePresentation

    override fun getPermissionsToGrant(): Array<String> {
        return super.getPermissionsToGrant().plus(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    override fun setup() {
        super.setup()

        val imageUri = InstrumentationRegistry.getInstrumentation()
            .targetContext.getUriFromResId(R.drawable.ic_launcher_background)

        mImagePresentation = ImagePresentation(0, imageUri)
    }

    override fun createDefaultViewModelMockContext(): MateRequestsViewModelMockContext {
        return MateRequestsViewModelMockContext(MateRequestsUiState())
    }

    override fun attachViewModelMockContext() {
        FakeMateRequestsViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<MateRequestsFragment> {
        return MateRequestsFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.mateRequestsFragment
    }

    @Test
    fun test() {

    }
}