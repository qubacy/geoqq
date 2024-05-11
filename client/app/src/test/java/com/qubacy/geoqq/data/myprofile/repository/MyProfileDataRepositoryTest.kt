package com.qubacy.geoqq.data.myprofile.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data.image.repository._test.mock.ImageDataRepositoryMockContainer
import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile
import com.qubacy.geoqq.data.myprofile.model._common.DataPrivacy
import com.qubacy.geoqq.data.myprofile.model.profile.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.model.update.DataMyProfileUpdateData
import com.qubacy.geoqq.data.myprofile.model.update.DataSecurity
import com.qubacy.geoqq.data.myprofile.model.update.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api._common.MyProfilePrivacy
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store.impl.LocalMyProfileDataStoreDataSourceImpl
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.model.MyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.model.profile.toDataMyProfile
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest.impl.RemoteMyProfileHttpRestDataSourceImpl
import com.qubacy.geoqq.data.myprofile.repository.impl.MyProfileDataRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MyProfileDataRepositoryTest(

) : DataRepositoryTest<MyProfileDataRepositoryImpl>() {
    companion object {
        val DEFAULT_AVATAR = ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE
        val DEFAULT_PASSWORD = "test"

        val DEFAULT_DATA_MY_PROFILE = DataMyProfile(
            "login", "test", "test", DEFAULT_AVATAR, DataPrivacy(HitMeUpType.EVERYBODY)
        )
        val DEFAULT_DATA_MY_PROFILE_UPDATE_DATA = DataMyProfileUpdateData(
            DEFAULT_DATA_MY_PROFILE.username,
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

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mImageDataRepositoryMockContainer: ImageDataRepositoryMockContainer

    private var mLocalSourceGetMyProfile: MyProfileDataStoreModel? = null

    private var mLocalSourceGetMyProfileCallFlag = false
    private var mLocalSourceSaveMyProfileCallFlag = false
    private var mLocalSourceResetMyProfileCallFlag = false

    private var mHttpSourceGetMyProfileResponse: GetMyProfileResponse? = null

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

        mHttpSourceGetMyProfileResponse = null

        mHttpSourceUpdateMyProfileCallFlag = false
        mHttpSourceDeleteMyProfileCallFlag = false
        mHttpSourceGetMyProfileCallFlag = false
        mHttpSourceUpdateMyProfileCallFlag = false
        mHttpSourceDeleteMyProfileCallFlag = false
    }

    private fun initMyProfileDataRepository() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mImageDataRepositoryMockContainer = ImageDataRepositoryMockContainer()

        val localMyProfileDataSourceMock = mockLocalMyProfileDataSource()
        val httpMyProfileDataSourceMock = mockHttpMyProfileDataSource()

        mDataRepository = MyProfileDataRepositoryImpl(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mImageDataRepository = mImageDataRepositoryMockContainer.imageDataRepositoryMock,
            mLocalMyProfileDataStoreDataSource = localMyProfileDataSourceMock,
            mRemoteMyProfileHttpRestDataSource = httpMyProfileDataSourceMock
        )
    }

    private fun mockLocalMyProfileDataSource(): LocalMyProfileDataStoreDataSourceImpl {
        val localMyProfileDataSourceMock = Mockito.mock(LocalMyProfileDataStoreDataSourceImpl::class.java)

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

    private fun mockHttpMyProfileDataSource(): RemoteMyProfileHttpRestDataSourceImpl {
        val httpMyProfileDataSourceMock = Mockito.mock(RemoteMyProfileHttpRestDataSourceImpl::class.java)

        Mockito.`when`(httpMyProfileDataSourceMock.getMyProfile()).thenAnswer {
            mHttpSourceGetMyProfileCallFlag = true
            mHttpSourceGetMyProfileResponse
        }
        Mockito.`when`(httpMyProfileDataSourceMock.updateMyProfile(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mHttpSourceUpdateMyProfileCallFlag = true

            Unit
        }
        Mockito.`when`(httpMyProfileDataSourceMock.deleteMyProfile()).thenAnswer {
            mHttpSourceDeleteMyProfileCallFlag = true

            Unit
        }

        return httpMyProfileDataSourceMock
    }

    @Test
    fun getMyProfileWithNoLocalDataTest() = runTest {
        val dataMyProfile = DEFAULT_DATA_MY_PROFILE

        val getMyProfileResponse = GetMyProfileResponse(
            dataMyProfile.login, dataMyProfile.username, dataMyProfile.aboutMe,
            dataMyProfile.avatar.id, MyProfilePrivacy(dataMyProfile.privacy.hitMeUp.id)
        )
        val expectedDataMyProfile = dataMyProfile

        mImageDataRepositoryMockContainer.getImageById = dataMyProfile.avatar
        mHttpSourceGetMyProfileResponse = getMyProfileResponse

        val gottenDataMyProfile = mDataRepository.getMyProfile().await().myProfile

        Assert.assertTrue(mLocalSourceGetMyProfileCallFlag)
        Assert.assertTrue(mHttpSourceGetMyProfileCallFlag)
        Assert.assertTrue(mLocalSourceSaveMyProfileCallFlag)
        Assert.assertEquals(expectedDataMyProfile, gottenDataMyProfile)
    }

    @Test
    fun getMyProfileWithLocalDataTest() = runTest {
        val dataMyProfile = DEFAULT_DATA_MY_PROFILE

        val getMyProfileDataStoreModel = dataMyProfile.toMyProfileDataStoreModel()
        val getMyProfileResponse = GetMyProfileResponse(
            dataMyProfile.login, "remote user", dataMyProfile.aboutMe,
            dataMyProfile.avatar.id, MyProfilePrivacy(dataMyProfile.privacy.hitMeUp.id)
        )
        val expectedLocalDataMyProfile = dataMyProfile
        val expectedRemoteMyProfile = getMyProfileResponse.toDataMyProfile(dataMyProfile.avatar)

        mLocalSourceGetMyProfile = getMyProfileDataStoreModel
        mImageDataRepositoryMockContainer.getImageById = dataMyProfile.avatar
        mHttpSourceGetMyProfileResponse = getMyProfileResponse

        val getMyProfileResult = mDataRepository.getMyProfile()
        val gottenLocalDataMyProfile = getMyProfileResult.awaitUntilVersion(0)
            .myProfile

        Assert.assertTrue(mLocalSourceGetMyProfileCallFlag)
        Assert.assertEquals(expectedLocalDataMyProfile, gottenLocalDataMyProfile)

        val gottenRemoteDataMyProfile = getMyProfileResult.awaitUntilVersion(1)
            .myProfile

        Assert.assertTrue(mHttpSourceGetMyProfileCallFlag)
        Assert.assertTrue(mLocalSourceSaveMyProfileCallFlag)
        Assert.assertEquals(expectedRemoteMyProfile, gottenRemoteDataMyProfile)
    }

    @Test
    fun updateMyProfileWithoutAvatarTest() = runTest {
        val myProfileUpdateData = DEFAULT_DATA_MY_PROFILE_UPDATE_DATA
            .copy(avatarUri = null)

        mLocalSourceGetMyProfile = myProfileUpdateData
            .toMyProfileDataStoreModel(DEFAULT_DATA_MY_PROFILE.toMyProfileDataStoreModel())

        mDataRepository.updateMyProfile(myProfileUpdateData)

        Assert.assertTrue(mHttpSourceUpdateMyProfileCallFlag)
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
        Assert.assertTrue(mLocalSourceSaveMyProfileCallFlag)
    }

    @Test
    fun deleteMyProfileTest() = runTest {
        mDataRepository.deleteMyProfile()

        Assert.assertTrue(mHttpSourceDeleteMyProfileCallFlag)
        Assert.assertTrue(mLocalSourceResetMyProfileCallFlag)
    }
}