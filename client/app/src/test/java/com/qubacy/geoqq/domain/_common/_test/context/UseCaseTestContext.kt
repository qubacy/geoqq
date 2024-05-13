package com.qubacy.geoqq.domain._common._test.context

import com.qubacy.geoqq._common._test.util.mock.UriMockUtil
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.domain._common.model.image.Image
import com.qubacy.geoqq.domain._common.model.user.toUser

object UseCaseTestContext {
    val DEFAULT_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER.toUser()
    val DEFAULT_IMAGE = Image(0, UriMockUtil.getMockedUri())
}