package com.qubacy.geoqq.data._common.repository.source_common.local.database._common._test.util

import android.content.ContentValues
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database

fun Database.insert(tableName: String, contentValues: ContentValues) {
    openHelper.writableDatabase.insert(
        tableName,
        android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT,
        contentValues
    )
}