package com.qubacy.geoqq.ui.screen.myprofile

import android.net.Uri
import app.cash.turbine.test
import com.qubacy.geoqq.common.AnyUtility
import com.qubacy.geoqq.common.UriMockContext
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.operation.ProfileDataSavedUiOperation
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MyProfileViewModelTest : ViewModelTest() {
    companion object {
        init {
            UriMockContext.mockUri()
        }
    }

    private lateinit var mModel: MyProfileViewModel
    private lateinit var mMyProfileStateFlow: MutableStateFlow<MyProfileState?>

    private lateinit var mMyProfileUiStateFlow: Flow<MyProfileUiState?>

    private fun setNewUiState(newState: MyProfileState?) = runTest {
        if (newState == null) return@runTest

        mMyProfileStateFlow.emit(newState)
    }

    private fun initMyProfileViewModel(
        newState: MyProfileState? = null
    ) {
        val myProfileUseCastMock = Mockito.mock(MyProfileUseCase::class.java)

        Mockito.`when`(myProfileUseCastMock.getMyProfile())
            .thenAnswer { setNewUiState(newState) }
        Mockito.`when`(myProfileUseCastMock.updateMyProfile(
            AnyUtility.any(Uri::class.java),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            AnyUtility.any(MyProfileDataModelContext.HitUpOption::class.java)
        )).thenAnswer { setNewUiState(newState) }

        mMyProfileStateFlow = MutableStateFlow<MyProfileState?>(null)

        Mockito.`when`(myProfileUseCastMock.stateFlow).thenAnswer {
            mMyProfileStateFlow
        }

        val mMateRequestsUiStateFlowFieldReflection = MyProfileViewModel::class.java
            .getDeclaredField("mMyProfileUiState")
            .apply { isAccessible = true }

        mModel = MyProfileViewModel(myProfileUseCastMock)
        mMyProfileUiStateFlow = mMateRequestsUiStateFlowFieldReflection.get(mModel)
                as Flow<MyProfileUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initMyProfileViewModel()
    }

    @Test
    fun getMyProfileTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = MyProfileState(
            mockUri,
            String(),
            String(),
            MyProfileDataModelContext.HitUpOption.NEGATIVE,
            listOf()
        )

        initMyProfileViewModel(newState)

        mMyProfileUiStateFlow.test {
            awaitItem()
            mModel.getProfileData()

            val gottenState = awaitItem()!!

            Assert.assertEquals(newState.avatar, gottenState.avatar)
            Assert.assertEquals(newState.username, gottenState.username)
            Assert.assertEquals(newState.description, gottenState.description)
            Assert.assertEquals(newState.hitUpOption, gottenState.hitUpOption)
        }
    }

    @Test
    fun updateMyProfileTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = MyProfileState(
            mockUri,
            String(),
            String(),
            MyProfileDataModelContext.HitUpOption.NEGATIVE,
            listOf(
                SuccessfulProfileSavingCallbackOperation()
            )
        )

        initMyProfileViewModel(newState)

        mMyProfileUiStateFlow.test {
            awaitItem()
            mModel.getProfileData()

            val gottenState = awaitItem()!!

            Assert.assertEquals(newState.avatar, gottenState.avatar)
            Assert.assertEquals(newState.username, gottenState.username)
            Assert.assertEquals(newState.description, gottenState.description)
            Assert.assertEquals(newState.hitUpOption, gottenState.hitUpOption)

            Assert.assertEquals(
                ProfileDataSavedUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }
}