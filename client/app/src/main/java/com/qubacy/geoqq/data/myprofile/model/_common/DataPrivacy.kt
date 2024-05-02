package com.qubacy.geoqq.data.myprofile.model._common

import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.myprofile.repository.source.http.api._common.MyProfilePrivacy

data class DataPrivacy(
    val hitMeUp: HitMeUpType
) {

}

fun MyProfilePrivacy.toDataPrivacy(): DataPrivacy {
    val hitMeUpType = HitMeUpType.getHitMeUpTypeById(hitMeUpId)

    return DataPrivacy(hitMeUpType)
}

fun DataPrivacy.toMyProfilePrivacy(): MyProfilePrivacy {
    return MyProfilePrivacy(hitMeUp.id)
}