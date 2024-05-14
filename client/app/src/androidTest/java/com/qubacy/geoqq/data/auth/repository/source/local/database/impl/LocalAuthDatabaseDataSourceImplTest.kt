package com.qubacy.geoqq.data.auth.repository.source.local.database.impl

import android.content.ContentValues
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common._test.insertable.LocalInsertableDatabaseDataSourceTest
import com.qubacy.geoqq.data.auth.repository._common.source.local.database.impl.LocalAuthDatabaseDataSourceImpl
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import org.junit.Assert
import org.junit.Test

class LocalAuthDatabaseDataSourceImplTest(

) : LocalDatabaseDataSourceTest(), LocalInsertableDatabaseDataSourceTest<UserEntity> {
    private lateinit var mLocalAuthDatabaseDataSource: LocalAuthDatabaseDataSourceImpl

    override fun setup() {
        super.setup()

        mLocalAuthDatabaseDataSource = mDatabase.authDao()
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

        val expectedUserRowCount = 0

        insertItems(mDatabase, UserEntity.TABLE_NAME, initUserEntities)

        mLocalAuthDatabaseDataSource.dropDataTables()

        val gottenUserRowCount = getUserRowCount()

        Assert.assertEquals(expectedUserRowCount, gottenUserRowCount)
    }

    private fun getUserRowCount(): Int {
        val selectUsersCursor = mDatabase.query("SELECT * FROM ${UserEntity.TABLE_NAME};", null)

        return selectUsersCursor.count
    }
}