package com.qubacy.geoqq.data._common.util.token

import com.auth0.android.jwt.Claim
import com.qubacy.geoqq._common._test.util.mock.Base64MockUtil
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository.token.repository._common.util.TokenUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Date

class TokenUtilsTest {
    companion object {
        const val VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjIwMDAwMDAwMDB9" +
                ".ChDOscTIe0wMVSdQk4Bmb4et2iy6GRhaFxvK7lXgrDY"

        val VALID_TOKEN_PAYLOAD_CLAIM = object : Claim {
            override fun asBoolean(): Boolean? = null
            override fun asInt(): Int? = null
            override fun asLong(): Long = 2000000000
            override fun asDouble(): Double? = null
            override fun asString(): String? = null
            override fun asDate(): Date? = null
            override fun <T : Any?> asArray(tClazz: Class<T>?): Array<T> = asArray(tClazz)
            override fun <T : Any?> asList(tClazz: Class<T>?): MutableList<T> = mutableListOf()
            override fun <T : Any?> asObject(tClazz: Class<T>?): T? = null
        }
        const val PAYLOAD_TOKEN_NAME = "exp"
        val VALID_TOKEN_PAYLOAD = mapOf(
            PAYLOAD_TOKEN_NAME to VALID_TOKEN_PAYLOAD_CLAIM
        )
    }

    private lateinit var mLocalErrorDataSourceMockContainer: ErrorDataSourceMockContainer

    @Before
    fun setup() {
        Base64MockUtil.mockBase64()

        mLocalErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
    }

    @Test
    fun getTokenPayloadTest() {
        val token = VALID_TOKEN

        val expectedExpPayload = VALID_TOKEN_PAYLOAD[PAYLOAD_TOKEN_NAME]!!.asLong()
        val gottenExpPayload = TokenUtils.getTokenPayload(
            token,
            mLocalErrorDataSourceMockContainer.errorDataSourceMock
        )[PAYLOAD_TOKEN_NAME]!!.asLong()

        Assert.assertEquals(expectedExpPayload, gottenExpPayload)
    }

    @Test
    fun checkTokenForValidityTest() {
        class TestCase(
            val token: String,
            val expectedIsValid: Boolean
        )

        val testCases = listOf(
            TestCase("", false),
            TestCase(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJleHAiOjF9.5o2DmuhVcwInco2_YNzqMKLk-NGH44HoSQqX0CSzoaA",
                false
            ),
            TestCase(VALID_TOKEN, true)
        )

        for (testCase in testCases) {
            val gottenIsValid = TokenUtils.checkTokenForValidity(testCase.token)

            Assert.assertEquals(testCase.expectedIsValid, gottenIsValid)
        }
    }
}