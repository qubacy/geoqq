package com.qubacy.geoqq.data.myprofile.repository

import android.graphics.Bitmap
import android.net.Uri
import com.qubacy.geoqq.common.AnyUtility
import com.qubacy.geoqq.common.Base64MockContext
import com.qubacy.geoqq.common.BitmapMockContext
import com.qubacy.geoqq.common.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.myprofile.model.avatar.linked.DataMyProfileWithLinkedAvatar
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileResult
import com.qubacy.geoqq.data.myprofile.repository.result.UpdateMyProfileResult
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileEntity
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.toDataMyProfile
import com.qubacy.geoqq.data.myprofile.repository.source.network.NetworkMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.common.Privacy
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.response.GetMyProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicReference

class MyProfileDataRepositoryTest(

) {
    companion object {
        init {
            UriMockContext.mockUri()
            Base64MockContext.mockBase64()
            BitmapMockContext.mockBitmapFactory()
        }
    }

    private lateinit var mMyProfileDataRepository: MyProfileDataRepository
    private lateinit var mResultListAtomicRef: AtomicReference<List<Result>>

    private fun getSpiedMyProfileEntity(
        avatarUri: String,
        username: String,
        description: String,
        hitUpOptionIndex: Int
    ): MyProfileEntity {
        val myProfileEntity = MyProfileEntity(avatarUri, username, description, hitUpOptionIndex)
        val spiedMyProfileEntity = Mockito.spy(myProfileEntity)
        val hitUpOption = MyProfileDataModelContext.HitUpOption.entries
            .find { it.index == hitUpOptionIndex }!!

        val uri = Uri.parse(String())
        val dataMyProfileWithAvatar =
            DataMyProfileWithLinkedAvatar(username, description, hitUpOption, uri)

        Mockito.doAnswer { dataMyProfileWithAvatar }.`when`(spiedMyProfileEntity).toDataMyProfile()

        return myProfileEntity
    }

    private fun initMyProfileDataRepository(
        myProfileEntity: MyProfileEntity? = null,
        code: Int = 200,
        responseString: String = String()
    ) {
        val localMyProfileDataSource = Mockito.mock(LocalMyProfileDataSource::class.java)

        Mockito.`when`(localMyProfileDataSource.loadMyProfileData()).thenReturn(myProfileEntity)

        val networkMyProfileDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, responseString)
        ).create(NetworkMyProfileDataSource::class.java)

        mMyProfileDataRepository = MyProfileDataRepository(
            localMyProfileDataSource, networkMyProfileDataSource)

        mResultListAtomicRef = AtomicReference(listOf())

        GlobalScope.launch(Dispatchers.IO) {
            mMyProfileDataRepository.resultFlow.collect {
                val curList = mResultListAtomicRef.get()
                val newList = mutableListOf<Result>().apply {
                    addAll(curList)
                    add(it)
                }

                mResultListAtomicRef.set(newList)
            }
        }
    }

    @Before
    fun setup() {
        initMyProfileDataRepository()
    }

    @Test
    fun getLocalMyProfileWithoutNetworkConnectionTest() {
        val myProfileEntity = getSpiedMyProfileEntity(
            String(), "test", "test", 0
        )

        initMyProfileDataRepository(myProfileEntity, 400)

        runBlocking {
            mMyProfileDataRepository.getMyProfile(String())

            while (mResultListAtomicRef.get().isEmpty()) { }

            val result = mResultListAtomicRef.get().first()

            Assert.assertEquals(GetMyProfileResult::class, result::class)

            val resultCast = result as GetMyProfileResult

            Assert.assertEquals(myProfileEntity.username, resultCast.myProfileData.username)
        }
    }

    @Test
    fun getNetworkMyProfileWithoutLocalDataTest() {
        val myProfileResponse = GetMyProfileResponse(
            "test", "test", 0, Privacy(0)
        )
        val responseString = "{\"username\":\"${myProfileResponse.username}\"," +
                "\"description\":\"${myProfileResponse.description}\"," +
                "\"avatar-id\":${myProfileResponse.avatarId}," +
                "\"privacy\":{\"hit-me-up\":${myProfileResponse.privacy.hitMeUp}}}"

        initMyProfileDataRepository(code = 200, responseString = responseString)

        runBlocking {
            mMyProfileDataRepository.getMyProfile(String())

            while (mResultListAtomicRef.get().isEmpty()) { }

            val result = mResultListAtomicRef.get().first()

            Assert.assertEquals(GetMyProfileResult::class, result::class)

            val resultCast = result as GetMyProfileResult

            Assert.assertEquals(myProfileResponse.username, resultCast.myProfileData.username)
        }
    }

    @Test
    fun getLoadedLocalMyProfileDataThenGetNewMyProfileDataWithNetworkTest() {
        val myProfileEntity = getSpiedMyProfileEntity(
            String(), "local", "test", 0
        )
        val myProfileResponse = GetMyProfileResponse(
            "network", "test", 0, Privacy(0)
        )
        val responseString = "{\"username\":\"${myProfileResponse.username}\"," +
                "\"description\":\"${myProfileResponse.description}\"," +
                "\"avatar-id\":${myProfileResponse.avatarId}," +
                "\"privacy\":{\"hit-me-up\":${myProfileResponse.privacy.hitMeUp}}}"

        initMyProfileDataRepository(
            myProfileEntity = myProfileEntity, code = 200, responseString = responseString)

        runBlocking {
            mMyProfileDataRepository.getMyProfile(String())

            while (mResultListAtomicRef.get().size < 2) { }

            val resultList = mResultListAtomicRef.get()
            val localResult = resultList[0]
            val networkResult = resultList[1]

            Assert.assertEquals(GetMyProfileResult::class, localResult::class)
            Assert.assertEquals(GetMyProfileResult::class, networkResult::class)

            val localResultCast = localResult as GetMyProfileResult
            val networkResultCast = networkResult as GetMyProfileResult

            Assert.assertEquals(myProfileEntity.username, localResultCast.myProfileData.username)
            Assert.assertEquals(myProfileResponse.username, networkResultCast.myProfileData.username)
        }
    }

    @Test
    fun uploadMyProfileDataTest() {
        val avatarBitmapMock = Mockito.mock(Bitmap::class.java)

        Mockito.doAnswer { true }.`when`(avatarBitmapMock)
            .copyPixelsToBuffer(AnyUtility.any(ByteBuffer::class.java))

        initMyProfileDataRepository(code = 200, responseString = "{}")

        runBlocking {
            val result = mMyProfileDataRepository.updateMyProfile(
                String(), avatarBitmapMock, String(),
                String(), String(), MyProfileDataModelContext.HitUpOption.NEGATIVE
            )

            Assert.assertEquals(UpdateMyProfileResult::class, result::class)
        }
    }
}