package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.interceptor.lang

import android.os.Build
import android.os.LocaleList
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

class LanguageHeaderHttpInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request().newBuilder()
                .addHeader("Accept-Language", getLanguage())
                .build()
        )
    }

    private fun getLanguage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault().toLanguageTags();
        } else {
            Locale.getDefault().language;
        }
    }
}