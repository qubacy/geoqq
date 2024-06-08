package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content

import kotlin.reflect.full.isSubclassOf

class ErrorResponseContent(
    val id: Long
) {
    companion object {
        const val ID_PROP_NAME = "id"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || !other::class.isSubclassOf(ErrorResponseContent::class))
            return false

        other as ErrorResponseContent

        return (id == other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}