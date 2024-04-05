package com.qubacy.geoqq.data.myprofile.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.util.http.executor._test.mock.OkHttpClientMockContainer
import com.qubacy.geoqq.data.error.repository._test.mock.ErrorDataRepositoryMockContainer
import com.qubacy.geoqq.data.image.repository._test.mock.ImageDataRepositoryMockContainer
import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile
import com.qubacy.geoqq.data.myprofile.model._common.DataPrivacy
import com.qubacy.geoqq.data.myprofile.model.profile.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.model.update.DataMyProfileUpdateData
import com.qubacy.geoqq.data.myprofile.model.update.DataSecurity
import com.qubacy.geoqq.data.myprofile.model.update.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.myprofile.repository.source.http.HttpMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.http._common.MyProfilePrivacy
import com.qubacy.geoqq.data.myprofile.repository.source.http.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileDataStoreModel
import com.qubacy.geoqq.data.token.repository._test.mock.TokenDataRepositoryMockContainer
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Response

class MyProfileDataRepositoryTest(

) : DataRepositoryTest<MyProfileDataRepository>() {
    companion object {
        val DEFAULT_AVATAR = ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE
        val DEFAULT_PASSWORD = "test"


        val DEFAULT_DATA_MY_PROFILE = DataMyProfile(
            "test", "test", DEFAULT_AVATAR, DataPrivacy(HitMeUpType.EVERYBODY)
        )
        val DEFAULT_DATA_MY_PROFILE_UPDATE_DATA = DataMyProfileUpdateData(
            DEFAULT_DATA_MY_PROFILE.aboutMe,
            DEFAULT_DATA_MY_PROFILE.avatar.uri,
            DataSecurity(DEFAULT_PASSWORD, DEFAULT_PASSWORD),
            DEFAULT_DATA_MY_PROFILE.privacy
        )
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataRepositoryMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mImageDataRepositoryMockContainer: ImageDataRepositoryMockContainer
    private lateinit var mOkHttpClientMockContainer: OkHttpClientMockContainer

    private var mLocalSourceGetMyProfile: MyProfileDataStoreModel? = null

    private var mLocalSourceGetMyProfileCallFlag = false
    private var mLocalSourceSaveMyProfileCallFlag = false
    private var mLocalSourceResetMyProfileCallFlag = false

    private var mHttpSourceGetMyProfile: GetMyProfileResponse? = null

    private var mHttpSourceGetMyProfileResponseCallFlag = false
    private var mHttpSourceUpdateMyProfileResponseCallFlag = false
    private var mHttpSourceDeleteMyProfileResponseCallFlag = false
    private var mHttpSourceGetMyProfileCallFlag = false
    private var mHttpSourceUpdateMyProfileCallFlag = false
    private var mHttpSourceDeleteMyProfileCallFlag = false

    @Before
    fun setup() {
        initMyProfileDataRepository()
    }

    @After
    fun clear() {
        mLocalSourceGetMyProfile = null

        mLocalSourceGetMyProfileCallFlag = false
        mLocalSourceSaveMyProfileCallFlag = false
        mLocalSourceResetMyProfileCallFlag = false

        mHttpSourceGetMyProfile = null

        mHttpSourceGetMyProfileResponseCallFlag = false
        mHttpSourceUpdateMyProfileCallFlag = false
        mHttpSourceDeleteMyProfileCallFlag = false
        mHttpSourceGetMyProfileCallFlag = false
        mHttpSourceUpdateMyProfileCallFlag = false
        mHttpSourceDeleteMyProfileCallFlag = false
    }

    private fun initMyProfileDataRepository() {
        mErrorDataRepositoryMockContainer = ErrorDataRepositoryMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mImageDataRepositoryMockContainer = ImageDataRepositoryMockContainer()
        mOkHttpClientMockContainer = OkHttpClientMockContainer()

        val localMyProfileDataSourceMock = mockLocalMyProfileDataSource()
        val httpMyProfileDataSourceMock = mockHttpMyProfileDataSource()

        mDataRepository = MyProfileDataRepository(
            mErrorDataRepository = mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
            mTokenDataRepository = mTokenDataRepositoryMockContainer.tokenDataRepositoryMock,
            mImageDataRepository = mImageDataRepositoryMockContainer.imageDataRepositoryMock,
            mLocalMyProfileDataSource = localMyProfileDataSourceMock,
            mHttpMyProfileDataSource = httpMyProfileDataSourceMock,
            mHttpClient = mOkHttpClientMockContainer.httpClient
        )
    }

    private fun mockLocalMyProfileDataSource(): LocalMyProfileDataSource {
        val localMyProfileDataSourceMock = Mockito.mock(LocalMyProfileDataSource::class.java)

        runTest {
            Mockito.`when`(localMyProfileDataSourceMock.getMyProfile()).thenAnswer {
                mLocalSourceGetMyProfileCallFlag = true
                mLocalSourceGetMyProfile
            }
            Mockito.`when`(localMyProfileDataSourceMock.saveMyProfile(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mLocalSourceSaveMyProfileCallFlag = true

                Unit
            }
            Mockito.`when`(localMyProfileDataSourceMock.resetMyProfile()).thenAnswer {
                mLocalSourceResetMyProfileCallFlag = true

                Unit
            }
        }

        return localMyProfileDataSourceMock
    }

    private fun mockHttpMyProfileDataSource(): HttpMyProfileDataSource {
        val getMyProfileResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(getMyProfileResponseMock.body()).thenAnswer {
            mHttpSourceGetMyProfileResponseCallFlag = true
            mHttpSourceGetMyProfile
        }

        val updateMyProfileResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(updateMyProfileResponseMock.body()).thenAnswer {
            mHttpSourceUpdateMyProfileResponseCallFlag = true

            Unit
        }

        val deleteMyProfileResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(deleteMyProfileResponseMock.body()).thenAnswer {
            mHttpSourceDeleteMyProfileResponseCallFlag = true

            Unit
        }

        val getMyProfileCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(getMyProfileCallMock.execute()).thenAnswer {
            getMyProfileResponseMock
        }

        val updateMyProfileCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(updateMyProfileCallMock.execute()).thenAnswer {
            updateMyProfileResponseMock
        }

        val deleteMyProfileCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(deleteMyProfileCallMock.execute()).thenAnswer {
            deleteMyProfileResponseMock
        }

        val httpMyProfileDataSourceMock = Mockito.mock(HttpMyProfileDataSource::class.java)

        Mockito.`when`(httpMyProfileDataSourceMock.getMyProfile(Mockito.anyString())).thenAnswer {
            mHttpSourceGetMyProfileCallFlag = true
            getMyProfileCallMock
        }
        Mockito.`when`(httpMyProfileDataSourceMock.updateMyProfile(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mHttpSourceUpdateMyProfileCallFlag = true
            updateMyProfileCallMock
        }
        Mockito.`when`(httpMyProfileDataSourceMock.deleteMyProfile(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mHttpSourceDeleteMyProfileCallFlag = true
            deleteMyProfileCallMock
        }

        return httpMyProfileDataSourceMock
    }

    @Test
    fun getMyProfileWithNoLocalDataTest() = runTest {
        val dataMyProfile = DEFAULT_DATA_MY_PROFILE

        val getMyProfileResponse = GetMyProfileResponse(
            dataMyProfile.username, dataMyProfile.aboutMe,
            dataMyProfile.avatar.id, MyProfilePrivacy(dataMyProfile.privacy.hitMeUp.id)
        )
        val expectedDataMyProfile = dataMyProfile

        mImageDataRepositoryMockContainer.getImageById = dataMyProfile.avatar
        mHttpSourceGetMyProfile = getMyProfileResponse

        val gottenDataMyProfile = mDataRepository.getMyProfile().await().myProfile

        Assert.assertTrue(mLocalSourceGetMyProfileCallFlag)
        Assert.assertTrue(mHttpSourceGetMyProfileCallFlag)
        Assert.assertTrue(mHttpSourceGetMyProfileResponseCallFlag)
        Assert.assertTrue(mLocalSourceSaveMyProfileCallFlag)
        Assert.assertEquals(expectedDataMyProfile, gottenDataMyProfile)
    }

    @Test
    fun getMyProfileWithLocalDataTest() = runTest {
        val dataMyProfile = DEFAULT_DATA_MY_PROFILE

        val getMyProfileDataStoreModel = dataMyProfile.toMyProfileDataStoreModel()
        val getMyProfileResponse = GetMyProfileResponse(
            dataMyProfile.username, dataMyProfile.aboutMe,
            dataMyProfile.avatar.id, MyProfilePrivacy(dataMyProfile.privacy.hitMeUp.id)
        )
        val expectedDataMyProfile = dataMyProfile
        val expectedGetMyProfileResult = GetMyProfileDataResult(dataMyProfile)

        mLocalSourceGetMyProfile = getMyProfileDataStoreModel
        mImageDataRepositoryMockContainer.getImageById = dataMyProfile.avatar
        mHttpSourceGetMyProfile = getMyProfileResponse

        mDataRepository.resultFlow.test {
            val gottenDataMyProfile = mDataRepository.getMyProfile().await().myProfile

            Assert.assertTrue(mLocalSourceGetMyProfileCallFlag)
            Assert.assertEquals(expectedDataMyProfile, gottenDataMyProfile)

            val gottenResult = awaitItem()

            Assert.assertEquals(GetMyProfileDataResult::class, gottenResult::class)

            gottenResult as GetMyProfileDataResult

            Assert.assertTrue(mHttpSourceGetMyProfileCallFlag)
            Assert.assertTrue(mHttpSourceGetMyProfileResponseCallFlag)
            Assert.assertTrue(mLocalSourceSaveMyProfileCallFlag)
            Assert.assertEquals(expectedGetMyProfileResult, gottenResult)
        }
    }

    @Test
    fun updateMyProfileWithoutAvatarTest() = runTest {
        val myProfileUpdateData = DEFAULT_DATA_MY_PROFILE_UPDATE_DATA
            .copy(avatarUri = null)

        mLocalSourceGetMyProfile = myProfileUpdateData
            .toMyProfileDataStoreModel(DEFAULT_DATA_MY_PROFILE.toMyProfileDataStoreModel())

        mDataRepository.updateMyProfile(myProfileUpdateData)

        Assert.assertTrue(mHttpSourceUpdateMyProfileCallFlag)
        Assert.assertTrue(mHttpSourceUpdateMyProfileResponseCallFlag)
        Assert.assertTrue(mLocalSourceSaveMyProfileCallFlag)
    }

    @Test
    fun updateMyProfileWithAvatarTest() = runTest {
        val avatar = DEFAULT_AVATAR
        val myProfileUpdateData = DEFAULT_DATA_MY_PROFILE_UPDATE_DATA.copy(avatarUri = avatar.uri)

        mImageDataRepositoryMockContainer.saveImage = DEFAULT_AVATAR
        mLocalSourceGetMyProfile = myProfileUpdateData
            .toMyProfileDataStoreModel(DEFAULT_DATA_MY_PROFILE.toMyProfileDataStoreModel(), avatar)

        mDataRepository.updateMyProfile(myProfileUpdateData)

        Assert.assertTrue(mImageDataRepositoryMockContainer.saveImageCallFlag)
        Assert.assertTrue(mHttpSourceUpdateMyProfileCallFlag)
        Assert.assertTrue(mHttpSourceUpdateMyProfileResponseCallFlag)
        Assert.assertTrue(mLocalSourceSaveMyProfileCallFlag)
    }

    @Test
    fun deleteMyProfileTest() = runTest {
        mDataRepository.deleteMyProfile()

        Assert.assertTrue(mHttpSourceDeleteMyProfileCallFlag)
        Assert.assertTrue(mHttpSourceDeleteMyProfileResponseCallFlag)
        Assert.assertTrue(mLocalSourceResetMyProfileCallFlag)
    }
}