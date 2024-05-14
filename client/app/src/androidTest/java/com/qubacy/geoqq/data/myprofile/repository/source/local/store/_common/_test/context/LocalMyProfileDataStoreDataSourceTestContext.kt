package com.qubacy.geoqq.data.myprofile.repository.source.local.store._common._test.context

import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.model.MyProfileDataStoreModel

object LocalMyProfileDataStoreDataSourceTestContext {
    val DEFAULT_MY_PROFILE_DATA_STORE_MODEL = MyProfileDataStoreModel(
        0, "test", "test", "test", HitMeUpType.EVERYBODY.id
    )
}