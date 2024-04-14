package com.qubacy.geoqq.data.user.repository._test.mock

import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data.image.repository._test.mock.ImageDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
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

        val DEFAULT_GET_USERS_BY_IDS = GetUsersByIdsDataResult(listOf(DEFAULT_DATA_USER))
        val DEFAULT_RESOLVE_USERS = DEFAULT_GET_USERS_BY_IDS.users.associateBy { it.id }
        val DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER = DEFAULT_RESOLVE_USERS
    }

    val userDataRepository: UserDataRepository

    var getUsersByIds: GetUsersByIdsDataResult = DEFAULT_GET_USERS_BY_IDS
    var resolveUsers: Map<Long, DataUser> = DEFAULT_RESOLVE_USERS
    var resolveUsersWithLocalUser: Map<Long, DataUser> = DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER

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
                MutableLiveData(getUsersByIds)
            }
            Mockito.`when`(userDataRepositoryMock.resolveUsers(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mResolveUsersCallFlag = true
                resolveUsers
            }
            Mockito.`when`(userDataRepositoryMock.resolveUsersWithLocalUser(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mResolveUsersWithLocalUserCallFlag = true
                resolveUsersWithLocalUser
            }
        }

        return userDataRepositoryMock
    }

    fun reset() {
        getUsersByIds = DEFAULT_GET_USERS_BY_IDS
        resolveUsers = DEFAULT_RESOLVE_USERS

        mGetUsersByIdsCallFlag = false
        mResolveUsersCallFlag = false
    }
}