package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.model.image.Image
import com.qubacy.geoqq.domain.myprofile.model._common.Privacy
import com.qubacy.geoqq.domain.myprofile.model.profile.MyProfile
import com.qubacy.geoqq.domain.myprofile.usecase.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.logout.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.toMyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.DeleteMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.UpdateMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.MyProfileInputData
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.toUpdatedMyProfilePresentation
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class MyProfileViewModelTest(

) : BusinessViewModelTest<MyProfileUiState, MyProfileUseCase, MyProfileViewModel>(
    MyProfileUseCase::class.java
) {
    companion object {
        val DEFAULT_IMAGE = Image(0L, Mockito.mock(Uri::class.java))
        val DEFAULT_MY_PROFILE = MyProfile(
            "test", "test", DEFAULT_IMAGE, Privacy(HitMeUpType.NOBODY))
    }

    private var mUseCaseGetMyProfileCallFlag = false
    private var mUseCaseUpdateMyProfileCallFlag = false
    private var mUseCaseDeleteMyProfileCallFlag = false
    private var mUseCaseLogoutCallFlag = false

    override fun clear() {
        super.clear()

        mUseCaseGetMyProfileCallFlag = false
        mUseCaseUpdateMyProfileCallFlag = false
        mUseCaseDeleteMyProfileCallFlag = false
        mUseCaseLogoutCallFlag = false
    }

    override fun initUseCase(): MyProfileUseCase {
        val myProfileUseCaseMock = super.initUseCase()

        Mockito.`when`(myProfileUseCaseMock.getMyProfile()).thenAnswer {
            mUseCaseGetMyProfileCallFlag = true

            Unit
        }
        Mockito.`when`(myProfileUseCaseMock.updateMyProfile(AnyMockUtil.anyObject())).thenAnswer {
            mUseCaseUpdateMyProfileCallFlag = true

            Unit
        }
        Mockito.`when`(myProfileUseCaseMock.deleteMyProfile()).thenAnswer {
            mUseCaseDeleteMyProfileCallFlag = true

            Unit
        }
        Mockito.`when`(myProfileUseCaseMock.logout()).thenAnswer {
            mUseCaseLogoutCallFlag = true

            Unit
        }

        return myProfileUseCaseMock
    }

    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataRepository: ErrorDataRepository
    ): MyProfileViewModel {
        return MyProfileViewModel(savedStateHandle, errorDataRepository, mUseCase)
    }

    @Test
    fun isUpdateDataValidTest() {
        class TestCase(
            val inputData: MyProfileInputData,
            val expectedIsValid: Boolean
        )

        val testCases = listOf(
            TestCase(
                MyProfileInputData(),
                false
            ),
            TestCase(
                MyProfileInputData(aboutMe = "about me"),
                true
            ),
            TestCase(
                MyProfileInputData(
                    password = "testtest",
                    newPassword = "testtest2"
                ),
                false
            ),
            TestCase(
                MyProfileInputData(
                    password = "testtest",
                    newPassword = "testtest2",
                    newPasswordAgain = "testtest"
                ),
                false
            ),
            TestCase(
                MyProfileInputData(
                    newPassword = "testtest2",
                    newPasswordAgain = "testtest2"
                ),
                false
            ),
            TestCase(
                MyProfileInputData(
                    password = "testtest",
                    newPassword = "testtest2",
                    newPasswordAgain = "testtest2"
                ),
                true
            ),
            TestCase(
                MyProfileInputData(
                    hitMeUp = HitMeUpType.NOBODY
                ),
                true
            ),
            TestCase(
                MyProfileInputData(
                    hitMeUp = HitMeUpType.EVERYBODY
                ),
                true
            ),
        )

        for (testCase in testCases) {
            val gottenIsValid = mModel.isUpdateDataValid(testCase.inputData)

            Assert.assertEquals(testCase.expectedIsValid, gottenIsValid)
        }
    }

    @Test
    fun getMyProfileTest() = runTest {
        val initLoadingState = false

        val expectedLoadingState = true

        setUiState(MyProfileUiState(isLoading = initLoadingState))

        mModel.uiOperationFlow.test {
            mModel.getMyProfile()

            val result = awaitItem()

            Assert.assertTrue(mUseCaseGetMyProfileCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, result::class)

            val gottenLoadingState = (result as SetLoadingStateUiOperation).isLoading

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
        }
    }

    @Test
    fun updateMyProfileTest() = runTest {
        val initLoadingState = false

        val expectedUpdateMyProfileData = MyProfileInputData()
        val expectedLoadingState = true

        setUiState(MyProfileUiState(isLoading = initLoadingState))

        mModel.uiOperationFlow.test {
            mModel.updateMyProfile(expectedUpdateMyProfileData)

            val result = awaitItem()

            Assert.assertTrue(mUseCaseUpdateMyProfileCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, result::class)

            val gottenLoadingState = (result as SetLoadingStateUiOperation).isLoading
            val uiState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedUpdateMyProfileData, uiState.myProfileInputData)
        }
    }

    @Test
    fun deleteMyProfileTest() = runTest {
        val initLoadingState = false

        val expectedLoadingState = true

        setUiState(MyProfileUiState(isLoading = initLoadingState))

        mModel.uiOperationFlow.test {
            mModel.deleteMyProfile()

            val result = awaitItem()

            Assert.assertTrue(mUseCaseDeleteMyProfileCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, result::class)

            val gottenLoadingState = (result as SetLoadingStateUiOperation).isLoading

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
        }
    }

    @Test
    fun logoutTest() = runTest {
        val initLoadingState = false

        val expectedLoadingState = true

        setUiState(MyProfileUiState(isLoading = initLoadingState))

        mModel.uiOperationFlow.test {
            mModel.logout()

            val result = awaitItem()

            Assert.assertTrue(mUseCaseLogoutCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, result::class)

            val gottenLoadingState = (result as SetLoadingStateUiOperation).isLoading

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
        }
    }

    @Test
    fun processGetMyProfileDomainResultWithErrorTest() = runTest {
        val initIsLoading = true

        val expectedError = TestError.normal
        val expectedIsLoading = false
        val errorDomainResult = GetMyProfileDomainResult(expectedError)

        setUiState(MyProfileUiState(isLoading = initIsLoading))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(errorDomainResult)

            val errorOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedError, mModel.uiState.error)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            val errorOperationCast = errorOperation as ErrorUiOperation
            val setLoadingOperationCast = setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedError, errorOperationCast.error)
            Assert.assertEquals(expectedIsLoading, setLoadingOperationCast.isLoading)
        }
    }

    @Test
    fun processGetMyProfileDomainResultTest() = runTest {
        val initIsLoading = true
        val myProfile = DEFAULT_MY_PROFILE

        val expectedIsLoading = false
        val expectedMyProfilePresentation = myProfile.toMyProfilePresentation()
        val getMyProfileResult = GetMyProfileDomainResult(myProfile = myProfile)

        setUiState(MyProfileUiState(isLoading = initIsLoading))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getMyProfileResult)

            val getMyProfileOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(GetMyProfileUiOperation::class, getMyProfileOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedMyProfilePresentation, mModel.uiState.myProfilePresentation)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            getMyProfileOperation as GetMyProfileUiOperation
            setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedMyProfilePresentation, getMyProfileOperation.myProfile)
            Assert.assertEquals(expectedIsLoading, setLoadingOperation.isLoading)
        }
    }

    @Test
    fun processUpdateMyProfileDomainResultWithErrorTest() = runTest {
        val initIsLoading = true

        val expectedError = TestError.normal
        val expectedIsLoading = false
        val errorDomainResult = UpdateMyProfileDomainResult(expectedError)

        setUiState(MyProfileUiState(isLoading = initIsLoading))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(errorDomainResult)

            val errorOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedError, mModel.uiState.error)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            val errorOperationCast = errorOperation as ErrorUiOperation
            val setLoadingOperationCast = setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedError, errorOperationCast.error)
            Assert.assertEquals(expectedIsLoading, setLoadingOperationCast.isLoading)
        }
    }

    @Test
    fun processUpdateMyProfileDomainResultTest() = runTest {
        val initIsLoading = true
        val initMyProfileInputData = MyProfileInputData(aboutMe = "updated test")
        val initMyProfilePresentation = DEFAULT_MY_PROFILE.toMyProfilePresentation()

        val expectedIsLoading = false
        val expectedMyProfilePresentation = initMyProfileInputData
            .toUpdatedMyProfilePresentation(initMyProfilePresentation)
        val updateMyProfileResult = UpdateMyProfileDomainResult()

        setUiState(MyProfileUiState(
            isLoading = initIsLoading,
            myProfilePresentation = initMyProfilePresentation,
            myProfileInputData = initMyProfileInputData)
        )

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateMyProfileResult)

            val updateMyProfileOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(UpdateMyProfileUiOperation::class, updateMyProfileOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedMyProfilePresentation, mModel.uiState.myProfilePresentation)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedIsLoading, setLoadingOperation.isLoading)
        }
    }

    @Test
    fun processDeleteMyProfileDomainResultWithErrorTest() = runTest {
        val initIsLoading = true

        val expectedError = TestError.normal
        val expectedIsLoading = false
        val errorDomainResult = DeleteMyProfileDomainResult(expectedError)

        setUiState(MyProfileUiState(isLoading = initIsLoading))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(errorDomainResult)

            val errorOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedError, mModel.uiState.error)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            val errorOperationCast = errorOperation as ErrorUiOperation
            val setLoadingOperationCast = setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedError, errorOperationCast.error)
            Assert.assertEquals(expectedIsLoading, setLoadingOperationCast.isLoading)
        }
    }

    @Test
    fun processDeleteMyProfileDomainResultTest() = runTest {
        val initIsLoading = true

        val expectedIsLoading = false
        val deleteMyProfileResult = DeleteMyProfileDomainResult()

        setUiState(MyProfileUiState(isLoading = initIsLoading))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(deleteMyProfileResult)

            val deleteMyProfileUiOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(DeleteMyProfileUiOperation::class, deleteMyProfileUiOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedIsLoading, setLoadingOperation.isLoading)
        }
    }

    @Test
    fun processLogoutDomainResultWithErrorTest() = runTest {
        val initIsLoading = true

        val expectedError = TestError.normal
        val expectedIsLoading = false
        val errorDomainResult = LogoutDomainResult(expectedError)

        setUiState(MyProfileUiState(isLoading = initIsLoading))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(errorDomainResult)

            val errorOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(ErrorUiOperation::class, errorOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedError, mModel.uiState.error)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            val errorOperationCast = errorOperation as ErrorUiOperation
            val setLoadingOperationCast = setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedError, errorOperationCast.error)
            Assert.assertEquals(expectedIsLoading, setLoadingOperationCast.isLoading)
        }
    }

    @Test
    fun processLogoutDomainResultTest() = runTest {
        val initIsLoading = true

        val expectedIsLoading = false
        val logoutMyProfileResult = LogoutDomainResult()

        setUiState(MyProfileUiState(isLoading = initIsLoading))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(logoutMyProfileResult)

            val logoutUiOperation = awaitItem()
            val setLoadingOperation = awaitItem()

            Assert.assertEquals(LogoutUiOperation::class, logoutUiOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, setLoadingOperation::class)
            Assert.assertEquals(expectedIsLoading, mModel.uiState.isLoading)

            setLoadingOperation as SetLoadingStateUiOperation

            Assert.assertEquals(expectedIsLoading, setLoadingOperation.isLoading)
        }
    }
}