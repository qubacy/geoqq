package com.qubacy.geoqq.data.image._common.extension

import android.graphics.Bitmap.CompressFormat

enum class ImageExtension(
    val format: CompressFormat,
    val strings: Array<String>
) {
    PNG_EXTENSION(CompressFormat.PNG, arrayOf("png")),
    JPEG_EXTENSION(CompressFormat.JPEG, arrayOf("jpg", "jpeg"));

    companion object {
        fun getFormatByString(string: String): CompressFormat {
            return entries.find { it.strings.contains(string) }!!.format
        }

        fun getStringByFormat(format: CompressFormat): String {
            return entries.find { it.format == format }!!.strings.first()
        }
    }
}