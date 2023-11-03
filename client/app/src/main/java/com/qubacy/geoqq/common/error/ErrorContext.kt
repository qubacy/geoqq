package com.qubacy.geoqq.common.error

object ErrorContext {
    enum class Database(val id: Long) {
        UNKNOWN_DATABASE_ERROR(1),;
    }

    enum class Network(val id: Long) {
        UNKNOWN_NETWORK_RESPONSE_ERROR(200),
        UNKNOWN_NETWORK_FAILURE(201),;

    }

    enum class Token(val id: Long) {
        LOCAL_REFRESH_TOKEN_NOT_FOUND(400),
        LOCAL_REFRESH_TOKEN_INVALID(401),
        ;
    }

    enum class Image(val id: Long) {
        IMAGE_LOADING_FAILED(600),
        IMAGE_SAVING_FAILED(601),
        IMAGE_DECODING_FAILED(602),;
    }

    enum class Location(val id: Long) {
        LOCATION_PERMISSIONS_DENIED(800),
        LOCATION_SERVICES_NOT_ENABLED(801),
        GMS_API_NOT_AVAILABLE(802),;
    }

    enum class Local(val id: Long) {
        IMAGE_PICKING_ERROR(1000),;
    }
}