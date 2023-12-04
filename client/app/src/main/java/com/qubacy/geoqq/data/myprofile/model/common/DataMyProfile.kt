package com.qubacy.geoqq.data.myprofile.model.common

abstract class DataMyProfile(
    val username: String,
    val description: String,
    val hitUpOption: HitUpOption
) {
    enum class HitUpOption(
        val index: Int
    ) {
        POSITIVE(0), NEGATIVE(1);
    }
}