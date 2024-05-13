package com.qubacy.geoqq.domain.myprofile.usecase._common._test.context

import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.domain._common._test.context.UseCaseTestContext
import com.qubacy.geoqq.domain.myprofile.model._common.Privacy
import com.qubacy.geoqq.domain.myprofile.model.profile.MyProfile

object MyProfileUseCaseTestContext {
    private val DEFAULT_IMAGE = UseCaseTestContext.DEFAULT_IMAGE

    val DEFAULT_MY_PROFILE = MyProfile(
        "test", "test", "test", DEFAULT_IMAGE, Privacy(HitMeUpType.NOBODY)
    )
}