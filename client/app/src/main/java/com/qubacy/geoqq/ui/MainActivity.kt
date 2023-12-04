package com.qubacy.geoqq.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.dhaval2404.imagepicker.ImagePicker
import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.databinding.ActivityMainBinding
import com.qubacy.geoqq.ui.common.visual.activity.StyleableActivity

class MainActivity : AppCompatActivity(),
    com.qubacy.geoqq.ui.common.visual.activity.StyleableActivity {
    companion object {
        const val TAG = "MAIN_ACTIVITY"
    }

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mPickImageCallback: PickImageCallback

    private val mStartForImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val resultCode = it.resultCode
        val data = it.data

        if (resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data!!

            mPickImageCallback.onImagePicked(imageUri)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            val errorText = ImagePicker.getError(data)

            Log.d(TAG, "StartActivityForResult: error = $errorText")

            mPickImageCallback.onImagePickingError(ErrorContext.Local.IMAGE_PICKING_ERROR.id)

        } else {
            // do we need to handle a cancellation?
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreenCompat = installSplashScreen()

        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
    }

    override fun changeStatusBarColor(@ColorInt color: Int) {
        window.statusBarColor = color
    }

    fun pickImage(
        maxImageWidth: Int,
        maxImageHeight: Int,
        pickImageCallback: PickImageCallback
    ) {
        mPickImageCallback = pickImageCallback

        ImagePicker.with(this)
            .maxResultSize(maxImageWidth, maxImageHeight)
            .galleryOnly()
            .createIntent {
                mStartForImageResult.launch(it)
            }
    }
}