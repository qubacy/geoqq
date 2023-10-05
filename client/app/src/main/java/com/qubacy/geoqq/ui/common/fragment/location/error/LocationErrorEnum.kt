package com.qubacy.geoqq.ui.common.fragment.location.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error

enum class LocationErrorEnum(val error: Error) {
    LOCATION_PERMISSIONS_DENIED(
        Error(R.string.error_location_lacking_location_permissions, Error.Level.CRITICAL)),
    LOCATION_SERVICES_NOT_ENABLED(
        Error(R.string.error_location_lacking_location_permissions, Error.Level.CRITICAL)),
    GMS_API_NOT_AVAILABLE(
        Error(R.string.error_gms_api_not_available, Error.Level.CRITICAL)),
    ;
}