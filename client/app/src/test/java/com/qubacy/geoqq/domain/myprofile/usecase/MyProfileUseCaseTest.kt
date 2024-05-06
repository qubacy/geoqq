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
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.myprofile.model._common.DataPrivacy
import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._test.mock.AuthDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.usecase.UseCaseTest
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase._test.mock.LogoutUseCaseMockContainer
import com.qubacy.geoqq.domain.myprofile.model.profile.toMyProfile
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.profile.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.logout.usecase.result.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.profile.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.MyProfileUpdatedDomainResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MyProfileUseCaseTest : UseCaseTest<MyProfileUseCase>() {
    companion object {
        val DEFAULT_AVATAR = DataImage(0L, UriMockUtil.getMockedUri())
        val DEFAULT_DATA_PRIVACY = DataPrivacy(HitMeUpType.EVERYBODY)
        val DEFAULT_DATA_MY_PROFILE = DataMyProfile(
            "test", "test", "test", DEFAULT_AVATAR, DEFAULT_DATA_PRIVACY
        )
    }

    @get:Rule
    override val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mLogoutUseCaseMockContainer: LogoutUseCaseMockContainer
    private lateinit var mAuthDataRepositoryMockContainer: AuthDataRepositoryMockContainer

    private var mGetMyProfileResults: List<GetMyProfileDataResult>? = null

    private var mGetMyProfileCallFlag = false
    private var mUpdateMyProfileCallFlag = false
    private var mDeleteMyProfileCallFlag = false

    override fun clear() {
        super.clear()

        mGetMyProfileResults = null

        mGetMyProfileCallFlag = false
        mUpdateMyProfileCallFlag = false
        mDeleteMyProfileCallFlag = false
    }

    override fun initDependencies(): List<Any> {
        val superDependencies = super.initDependencies()

        mLogoutUseCaseMockContainer = LogoutUseCaseMockContainer()
        mAuthDataRepositoryMockContainer = AuthDataRepositoryMockContainer()

        val myProfileDataRepositoryMock = mockMyProfileDataRepository()

        return superDependencies.plus(
            listOf(
                mLogoutUseCaseMockContainer.logoutUseCaseMock,
                myProfileDataRepositoryMock,
                mAuthDataRepositoryMockContainer.authDataRepositoryMock
            )
        )
    }

    private fun mockMyProfileDataRepository(): MyProfileDataRepository {
        val myProfileDataRepositoryMock = Mockito.mock(MyProfileDataRepository::class.java)

        runTest {
            Mockito.`when`(myProfileDataRepositoryMock.getMyProfile()).thenAnswer {
                mGetMyProfileCallFlag = true

                if (mErrorDataSourceMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataSourceMockContainer.getError!!)

                val resultLiveData = MutableLiveData<GetMyProfileDataResult>()

                CoroutineScope(Dispatchers.Unconfined).launch {
                    for (result in mGetMyProfileResults!!)
                        resultLiveData.postValue(result)
                }

                resultLiveData
            }
            Mockito.`when`(myProfileDataRepositoryMock.updateMyProfile(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mUpdateMyProfileCallFlag = true

                if (mErrorDataSourceMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataSourceMockContainer.getError!!)

                Unit
            }
            Mockito.`when`(myProfileDataRepositoryMock.deleteMyProfile()).thenAnswer {
                mDeleteMyProfileCallFlag = true

                if (mErrorDataSourceMockContainer.getError != null)
                    throw ErrorAppException(mErrorDataSourceMockContainer.getError!!)

                Unit
            }
        }

        return myProfileDataRepositoryMock
    }

    override fun initUseCase(dependencies: List<Any>) {
        mUseCase = MyProfileUseCase(
            dependencies[0] as LocalErrorDataSource,
            dependencies[1] as LogoutUseCase,
            dependencies[2] as MyProfileDataRepository,
            dependencies[3] as AuthDataRepository
        )
    }

    @Test
    fun getMyProfileFailedTest() = runTest {
        val expectedError = TestError.normal

        mErrorDataSourceMockContainer.getError = expectedError

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
        val localDataMyProfile = DEFAULT_DATA_MY_PROFILE
        val remoteDataMyProfile = DEFAULT_DATA_MY_PROFILE.copy(login = "updated test")

        val expectedLocalMyProfile = localDataMyProfile.toMyProfile()
        val expectedRemoteMyProfile = remoteDataMyProfile.toMyProfile()

        mGetMyProfileResults = listOf(
            GetMyProfileDataResult(false, localDataMyProfile),
            GetMyProfileDataResult(true, remoteDataMyProfile)
        )

        mUseCase.resultFlow.test {
            mUseCase.getMyProfile()

            val localResult = awaitItem()

            Assert.assertTrue(mGetMyProfileCallFlag)
            Assert.assertTrue(localResult.isSuccessful())
            Assert.assertEquals(GetMyProfileDomainResult::class, localResult::class)

            val gottenLocalMyProfile = (localResult as GetMyProfileDomainResult).myProfile

            Assert.assertEquals(expectedLocalMyProfile, gottenLocalMyProfile)

            val remoteResult = awaitItem()

            Assert.assertTrue(remoteResult.isSuccessful())
            Assert.assertEquals(UpdateMyProfileDomainResult::class, remoteResult::class)

            val gottenRemoteMyProfile = (remoteResult as UpdateMyProfileDomainResult).myProfile

            Assert.assertEquals(expectedRemoteMyProfile, gottenRemoteMyProfile)
        }
    }

    @Test
    fun updateMyProfileFailedTest() = runTest {
        val myProfileUpdateData = MyProfileUpdateData()

        val expectedError = TestError.normal

        mErrorDataSourceMockContainer.getError = expectedError

        mUseCase.resultFlow.test {
            mUseCase.updateMyProfile(myProfileUpdateData)

            val result = awaitItem()

            Assert.assertTrue(mUpdateMyProfileCallFlag)
            Assert.assertFalse(result.isSuccessful())
            Assert.assertEquals(MyProfileUpdatedDomainResult::class, result::class)

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
            Assert.assertEquals(MyProfileUpdatedDomainResult::class, result::class)

            result as MyProfileUpdatedDomainResult

            val gottenIsSuccessful = result.isSuccessful()

            Assert.assertEquals(expectedIsSuccessful, gottenIsSuccessful)
        }
    }

    @Test
    fun deleteMyProfileFailedTest() = runTest {
        val expectedError = TestError.normal

        mErrorDataSourceMockContainer.getError = expectedError

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
            Assert.assertTrue(mAuthDataRepositoryMockContainer.logoutCallFlag)
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

            Assert.assertTrue(mAuthDataRepositoryMockContainer.logoutCallFlag)
            Assert.assertTrue(result.isSuccessful())
            Assert.assertEquals(LogoutDomainResult::class, result::class)
        }
    }
}