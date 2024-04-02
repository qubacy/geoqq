package com.qubacy.geoqq.domain.myprofile.model._common

import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.myprofile.model._common.DataPrivacy

data class Privacy(
    val hitMeUp: HitMeUpType
) {

}

fun DataPrivacy.toPrivacy(): Privacy {
    return Privacy(hitMeUp)
}

fun Privacy.toDataPrivacy(): DataPrivacy {
    return DataPrivacy(hitMeUp)
}