package com.qubacy.geoqq.data.myprofile.repository

import android.net.Uri
import com.qubacy.geoqq.common.Base64MockContext
import com.qubacy.geoqq.common.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.myprofile.model.avatar.linked.DataMyProfileWithLinkedAvatar
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileWithSharedPreferencesResult
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileEntity
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.toDataMyProfile
import com.qubacy.geoqq.data.myprofile.repository.source.network.NetworkMyProfileDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicReference

class MyProfileDataRepositoryTest(

) {
    companion object {
        init {
            UriMockContext.mockUri()
            Base64MockContext.mockBase64()
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

        val dataMyProfile = DataMyProfileWithLinkedAvatar(
            username, description, hitUpOption, UriMockContext.mockedUri)

//        Mockito.`when`(spiedMyProfileEntity.toDataMyProfile()).thenReturn(dataMyProfile)
        Mockito.doReturn(dataMyProfile).`when`(spiedMyProfileEntity.toDataMyProfile())

        return spiedMyProfileEntity
    }

    private fun initMyProfileDataRepository(
        myProfileEntity: MyProfileEntity =
            getSpiedMyProfileEntity(String(), String(), String(), 0),
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
                val newList = mutableListOf<Result>().apply { addAll(curList) }

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

            val operation = mResultListAtomicRef.get().first()

            Assert.assertEquals(GetMyProfileWithSharedPreferencesResult::class, operation::class)

            val operationCast = operation as GetMyProfileWithSharedPreferencesResult

            Assert.assertEquals(myProfileEntity.toDataMyProfile(), operationCast.myProfileData)
        }
    }

    @Test
    fun getNetworkMyProfileWithoutLocalDataTest() {

    }

    @Test
    fun getLoadedLocalMyProfileDataThenGetNewMyProfileDataWithNetworkTest() {

    }

    @Test
    fun uploadMyProfileDataTest() {

    }
}