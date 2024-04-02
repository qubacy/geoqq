package com.qubacy.geoqq._common.model.hitmeup

enum class HitMeUpType(val id: Int) {
    EVERYBODY(0), NOBODY(1);

    companion object {
        fun getHitMeUpTypeById(id: Int): HitMeUpType {
            return entries.find { it.id == id }!!
        }
    }
}