package com.qubacy.geoqq.data.user.repository._common._test.mock

import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository._common.result.ResolveUsersDataResult
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class UserDataRepositoryMockContainer {
    companion object {
        val DEFAULT_GET_USERS_BY_IDS = UserDataRepositoryTestContext.DEFAULT_GET_USERS_BY_IDS
        val DEFAULT_RESOLVE_USERS = UserDataRepositoryTestContext.DEFAULT_RESOLVE_USERS
        val DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER = UserDataRepositoryTestContext
            .DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER
    }

    val userDataRepository: UserDataRepository

    var error: Error? = null

    var getUsersByIdsResult: GetUsersByIdsDataResult = DEFAULT_GET_USERS_BY_IDS
    var resolveUsersResult: ResolveUsersDataResult = DEFAULT_RESOLVE_USERS
    var resolveUsersWithLocalUserResult: ResolveUsersDataResult =
        DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER

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

                MutableLiveData(getUsersByIdsResult)
            }
            Mockito.`when`(userDataRepositoryMock.resolveUsers(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mResolveUsersCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                MutableLiveData(resolveUsersResult)
            }
            Mockito.`when`(userDataRepositoryMock.resolveUsersWithLocalUser(
                AnyMockUtil.anyObject()
            )).thenAnswer {
                mResolveUsersWithLocalUserCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                MutableLiveData(resolveUsersWithLocalUserResult)
            }
        }

        return userDataRepositoryMock
    }

    fun reset() {
        error = null

        getUsersByIdsResult = DEFAULT_GET_USERS_BY_IDS
        resolveUsersResult = DEFAULT_RESOLVE_USERS
        resolveUsersWithLocalUserResult = DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER

        mGetUsersByIdsCallFlag = false
        mResolveUsersCallFlag = false
        mResolveUsersWithLocalUserCallFlag = false
    }
}