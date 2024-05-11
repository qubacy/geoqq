package com.qubacy.geoqq.data.auth.repository.source.local

import android.content.ContentValues
import com.qubacy.geoqq.data._common.repository.source_common.local.database.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data._common.repository.source_common.local.database._common._test.insertable.LocalInsertableDatabaseDataSourceTest
import com.qubacy.geoqq.data.auth.repository._common.source.local.database.impl.LocalAuthDatabaseDataSourceImpl
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import org.junit.Assert
import org.junit.Test

class LocalAuthDatabaseDataSourceTest(

) : LocalDatabaseDataSourceTest(), LocalInsertableDatabaseDataSourceTest<UserEntity> {
    private lateinit var mLocalAuthDatabaseDataSource: LocalAuthDatabaseDataSourceImpl
    private lateinit var mLocalUserDataSource: LocalUserDatabaseDataSourceDao

    override fun setup() {
        super.setup()

        mLocalAuthDatabaseDataSource = mDatabase.authDao()
        mLocalUserDataSource = mDatabase.userDao()
    }

    override fun packEntityContent(itemEntity: UserEntity): ContentValues {
        val contentValues = ContentValues().apply {
            put(UserEntity.ID_PARAM_NAME, itemEntity.id)
            put(UserEntity.USERNAME_PARAM_NAME, itemEntity.username)
            put(UserEntity.DESCRIPTION_PARAM_NAME, itemEntity.description)
            put(UserEntity.AVATAR_ID_PARAM_NAME, itemEntity.avatarId)
            put(UserEntity.IS_MATE_PARAM_NAME, itemEntity.isMate)
            put(UserEntity.IS_DELETED_PARAM_NAME, itemEntity.isDeleted)
        }

        return contentValues
    }

    @Test
    fun dropDataTablesTest() {
        val initUserEntities = listOf(
            UserEntity(0L, "test 1", "test", 0L, 0, 0),
            UserEntity(1L, "test 2", "test", 0L, 0, 0)
        )

        val userIds = initUserEntities.map { it.id }

        insertItems(mDatabase, UserEntity.TABLE_NAME, initUserEntities)

        mLocalAuthDatabaseDataSource.dropDataTables()

        val gottenUserEntities = mLocalUserDataSource.getUsersByIds(userIds)

        Assert.assertNull(gottenUserEntities)
    }
}