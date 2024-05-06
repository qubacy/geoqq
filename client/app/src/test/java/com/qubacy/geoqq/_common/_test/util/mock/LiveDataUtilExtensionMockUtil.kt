package com.qubacy.geoqq._common._test.util.mock

import androidx.lifecycle.LiveData
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

object LiveDataUtilExtensionMockUtil {
    const val PACKAGE_NAME = "com.qubacy.geoqq._common.util.livedata.extension.LiveDataUtilExtension"
    const val DEFAULT_VERSION = -1

    private var mValues: List<Any>? = null
    private var mVersion: Int = DEFAULT_VERSION

    fun <T>mock(values: List<Any>) {
        mockkStatic(PACKAGE_NAME)

        mValues = values

        every {
            runTest {
                any<LiveData<T>>().awaitUntilVersion(any())
            }
        } answers {
            ++mVersion

            mValues?.get(mVersion)!!
        }
    }

    fun reset() {
        unmockkAll()

        mValues = null
        mVersion = DEFAULT_VERSION
    }
}