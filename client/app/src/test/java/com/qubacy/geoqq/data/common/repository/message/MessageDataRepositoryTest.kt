package com.qubacy.geoqq.data.common.repository.message

abstract class MessageDataRepositoryTest {
    protected fun generateNetworkResponseWithCount(count: Int): String {
        val responseStringBuilder = StringBuilder("{\"messages\":[")

        for (i in 0 until count)  {
            responseStringBuilder
                .append("{\"id\":$i, \"user-id\":$i, \"text\":\"test\", \"time\":100}")
            responseStringBuilder.append(if (i != count - 1) "," else "")
        }

        return responseStringBuilder.append("]}").toString()
    }
}