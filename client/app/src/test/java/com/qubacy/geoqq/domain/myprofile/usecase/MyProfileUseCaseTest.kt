package com.qubacy.geoqq.domain.myprofile.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common._test.util.mock.UriMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.myprofile.model._common.DataPrivacy
import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.myprofile.model.profile.toMyProfile
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.logout.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.UpdateMyProfileDomainResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MyProfileUseCaseTest : UseCaseTest<MyProfileUseCase>() {
    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer

    private var mGetMyProfile: GetMyProfileDataResult? = null

    private var mGetMyProfileCallFlag = false
    private var mUpdateMyProfileCallFlag = false
    private var mDeleteMyProfileCallFlag = false

    override fun clear() {
        super.clear()

        mGetMyProfile = null

        mGetMyProfileCallFlag = false
        mUpdateMyProfileCallFlag = false
        mDeleteMyProfileCallFlag = false
    }

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        val myProfileDataRepositoryMock = Mockito.mock(MyProfileDataRepository::class.java)

        runTest {
            Mockito.`when`(myProfileDataRepositoryMock.getMyProfile()).thenAnswer {
                mGetMyProfileCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)

                MutableLiveData(mGetMyProfile!!)
            }
            Mockito.`when`(myProfileDataRepositoryMock.updateMyProfile(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mUpdateMyProfileCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)

                Unit
            }
            Mockito.`when`(myProfileDataRepositoryMock.deleteMyProfile()).thenAnswer {
                mDeleteMyProfileCallFlag = true

                if (mErrorDataRepositoryMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataRepositoryMockContainer.getError!!)

                Unit
            }
        }

        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()

        return superDependencies.plus(
            listOf(
                myProfileDataRepositoryMock,
                mTokenDataRepositoryMockContainer.tokenDataRepositoryMock
            )
        )
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = MyProfileUseCase(
            dependencies[0] as ErrorDataRepository,
            dependencies[1] as MyProfileDataRepository,
            dependencies[2] as TokenDataRepository
        )
    }

    @Test
    fun getMyProfileFailedTest() = runTest {
        val expectedError = TestError.normal

        mErrorDataRepositoryMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.getMyProfile()

            val result = awaitItem()

            Assert.assertTrue(mGetMyProfileCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(GetMyProfileDomainResult::class, result::class)

            val gottenError = result.error!!

            Assert.assertEquals(expectedError, gottenError)
        }
    }

    @Test
    fun getMyProfileSucceededTest() = runTest {
        val dataAvatar = DataImage(0L, UriMockUtil.getMockedUri())
        val dataPrivacy = DataPrivacy(HitMeUpType.EVERYBODY)
        val dataMyProfile = DataMyProfile("test", "test", dataAvatar, dataPrivacy)

        val expectedMyProfile = dataMyProfile.toMyProfile()

        mGetMyProfile = GetMyProfileDataResult(dataMyProfile)

        mUseCase.resultFlow.test {
            mUseCase.getMyProfile()

            val result = awaitItem()

            Assert.assertTrue(mGetMyProfileCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(GetMyProfileDomainResult::class, result::class)

            result as GetMyProfileDomainResult

            val gottenMyProfile = result.myProfile

            Assert.assertEquals(expectedMyProfile, gottenMyProfile)
        }
    }

    @Test
    fun updateMyProfileFailedTest() = runTest {
        val myProfileUpdateData = MyProfileUpdateData()

        val expectedError = TestError.normal

        mErrorDataRepositoryMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.updateMyProfile(myProfileUpdateData)

            val result = awaitItem()

            Assert.assertTrue(mUpdateMyProfileCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(UpdateMyProfileDomainResult::class, result::class)

            val gottenError = result.error!!

            Assert.assertEquals(expectedError, gottenError)
        }
    }

    @Test
    fun updateMyProfileSucceededTest() = runTest {
        val myProfileUpdateData = MyProfileUpdateData()

        val expectedIsSuccessful = true

        mUseCase.resultFlow.test {
            mUseCase.updateMyProfile(myProfileUpdateData)

            val result = awaitItem()

            Assert.assertTrue(mUpdateMyProfileCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(UpdateMyProfileDomainResult::class, result::class)

            result as UpdateMyProfileDomainResult

            val gottenIsSuccessful = result.isSuccessful()

            Assert.assertEquals(expectedIsSuccessful, gottenIsSuccessful)
        }
    }

    @Test
    fun deleteMyProfileFailedTest() = runTest {
        val expectedError = TestError.normal

        mErrorDataRepositoryMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.deleteMyProfile()

            val result = awaitItem()

            Assert.assertTrue(mDeleteMyProfileCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(DeleteMyProfileDomainResult::class, result::class)

            val gottenError = result.error!!

            Assert.assertEquals(expectedError, gottenError)
        }
    }

    @Test
    fun deleteMyProfileSucceededTest() = runTest {
        val expectedIsSuccessful = true

        mUseCase.resultFlow.test {
            mUseCase.deleteMyProfile()

            val result = awaitItem()

            Assert.assertTrue(mDeleteMyProfileCallFlag)
            Assert.assertTrue(mTokenDataRepositoryMockContainer.clearTokensCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(DeleteMyProfileDomainResult::class, result::class)

            result as DeleteMyProfileDomainResult

            val gottenIsSuccessful = result.isSuccessful()

            Assert.assertEquals(expectedIsSuccessful, gottenIsSuccessful)
        }
    }

    @Test
    fun logoutTest() = runTest {
        mUseCase.resultFlow.test {
            mUseCase.logout()

            val result = awaitItem()

            Assert.assertTrue(mTokenDataRepositoryMockContainer.clearTokensCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(LogoutDomainResult::class, result::class)
        }
    }
}