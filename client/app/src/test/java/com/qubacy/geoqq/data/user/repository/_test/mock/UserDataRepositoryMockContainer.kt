package com.qubacy.geoqq.data.user.repository._test.mock

import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data.image.repository._test.mock.ImageDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository.result.ResolveUsersDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class UserDataRepositoryMockContainer {
    companion object {
        val DEFAULT_DATA_USER = DataUser(
            0L,
            String(),
            String(),
            ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE,
            false, false
        )

        val DEFAULT_GET_USERS_BY_IDS = GetUsersByIdsDataResult(true, listOf(DEFAULT_DATA_USER))
        val DEFAULT_RESOLVE_USERS = ResolveUsersDataResult(
            true, DEFAULT_GET_USERS_BY_IDS.users.associateBy { it.id })
        val DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER = DEFAULT_RESOLVE_USERS
    }

    val userDataRepository: UserDataRepository

    var error: Error? = null

    var getUsersByIdsResults: List<GetUsersByIdsDataResult> = listOf(DEFAULT_GET_USERS_BY_IDS)
    var resolveUsersResults: List<ResolveUsersDataResult> = listOf(DEFAULT_RESOLVE_USERS)
    var resolveUsersWithLocalUserResults: List<ResolveUsersDataResult> =
        listOf(DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER)

    private var mGetUsersByIdsCallFlag = false
    val getUsersByIdsCallFlag get() = mGetUsersByIdsCallFlag
    private var mResolveUsersCallFlag = false
    val resolveUsersCallFlag get() = mResolveUsersCallFlag
    private var mResolveUsersWithLocalUserCallFlag = false
    val resolveUsersWithLocalUserCallFlag get() = mResolveUsersWithLocalUserCallFlag

    init {
        userDataRepository = mockUserDataRepository()
    }

    private fun mockUserDataRepository(): UserDataRepository {
        val userDataRepositoryMock = Mockito.mock(UserDataRepository::class.java)

        runTest {
            Mockito.`when`(userDataRepositoryMock.getUsersByIds(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mGetUsersByIdsCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                val resultLiveData = MutableLiveData<GetUsersByIdsDataResult>()

                CoroutineScope(coroutineContext).launch {
                    for (result in getUsersByIdsResults) resultLiveData.postValue(result)
                }

                resultLiveData
            }
            Mockito.`when`(userDataRepositoryMock.resolveUsers(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mResolveUsersCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                val resultLiveData = MutableLiveData<ResolveUsersDataResult>()

                CoroutineScope(coroutineContext).launch {
                    for (result in resolveUsersResults) resultLiveData.postValue(result)
                }

                resultLiveData
            }
            Mockito.`when`(userDataRepositoryMock.resolveUsersWithLocalUser(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mResolveUsersWithLocalUserCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                val resultLiveData = MutableLiveData<ResolveUsersDataResult>()

                CoroutineScope(coroutineContext).launch {
                    for (result in resolveUsersWithLocalUserResults)
                        resultLiveData.postValue(result)
                }

                resolveUsersWithLocalUserResults
            }
        }

        return userDataRepositoryMock
    }

    fun reset() {
        error = null

        getUsersByIdsResults = listOf(DEFAULT_GET_USERS_BY_IDS)
        resolveUsersResults = listOf(DEFAULT_RESOLVE_USERS)
        resolveUsersWithLocalUserResults = listOf(DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER)

        mGetUsersByIdsCallFlag = false
        mResolveUsersCallFlag = false
        mResolveUsersWithLocalUserCallFlag = false
    }
}