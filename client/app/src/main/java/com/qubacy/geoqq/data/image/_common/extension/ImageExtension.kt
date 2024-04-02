package com.qubacy.geoqq.data.image._common.extension

import android.graphics.Bitmap.CompressFormat

enum class ImageExtension(
    val format: CompressFormat,
    val id: Int,
    val strings: Array<String>
) {
    PNG_EXTENSION(CompressFormat.PNG, 1, arrayOf("png")),
    JPEG_EXTENSION(CompressFormat.JPEG, 2, arrayOf("jpg", "jpeg"));

    companion object {
        fun getFormatByString(string: String): CompressFormat {
            return entries.find { it.strings.contains(string) }!!.format
        }

        fun getFormatById(id: Int): CompressFormat {
            return entries.find { it.id == id }!!.format
        }

        fun getStringByFormat(format: CompressFormat): String {
            return entries.find { it.format == format }!!.strings.first()
        }

        fun getIdByFormat(format: CompressFormat): Int {
            return entries.find { it.format == format }!!.id
        }
    }
}