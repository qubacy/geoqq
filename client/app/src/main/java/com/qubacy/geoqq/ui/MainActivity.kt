package com.qubacy.geoqq.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.util.Function
import com.qubacy.geoqq.databinding.ActivityMainBinding
import com.qubacy.geoqq.ui.common.activity.StyleableActivity

class MainActivity : AppCompatActivity(), StyleableActivity {
    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mPickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mPickImageCallback: Function<Uri?, Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mPickImageLauncher = getPickImageLauncher()
    }

    private fun getPickImageLauncher(): ActivityResultLauncher<PickVisualMediaRequest> {
        val contract = ActivityResultContracts.PickVisualMedia()

        return registerForActivityResult(contract) {
            mPickImageCallback.apply(it)
        }
    }

    override fun changeStatusBarColor(@ColorInt color: Int) {
        window.statusBarColor = color
    }

    fun pickImage(onImagePicked: Function<Uri?, Unit>) {
        mPickImageCallback = onImagePicked
        mPickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}