package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.myprofile.usecase.MyProfileUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.MyProfileInputData
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class MyProfileViewModelTest(

) : BusinessViewModelTest<MyProfileUiState, MyProfileUseCase, MyProfileViewModel>(
    MyProfileUseCase::class.java
) {
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
        val myProfileUseCaseMock = Mockito.mock(MyProfileUseCase::class.java)

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
    fun updateMyProfileTest() {

    }

    @Test
    fun deleteMyProfileTest() {

    }

    @Test
    fun logoutTest() {

    }

    @Test
    fun processGetMyProfileDomainResultTest() {

    }

    @Test
    fun processUpdateMyProfileDomainResultTest() {

    }

    @Test
    fun processDeleteMyProfileDomainResultTest() {

    }

    @Test
    fun processLogoutDomainResultTest() {

    }
}