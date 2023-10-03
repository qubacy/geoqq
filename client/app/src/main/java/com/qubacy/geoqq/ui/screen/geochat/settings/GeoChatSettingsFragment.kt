package com.qubacy.geoqq.ui.screen.geochat.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.qubacy.geoqq.databinding.ComponentRadiusSettingOptionBinding
import com.qubacy.geoqq.databinding.FragmentGeoChatSettingsBinding
import com.qubacy.geoqq.ui.common.fragment.WaitingFragment
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModelFactory
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CircleMapObject

class GeoChatSettingsFragment : WaitingFragment() {
    companion object {
        const val TAG = "SETTINGS_FRAGMENT"

        const val LOCATION_CIRCLE_STROKE_WIDTH = 2f
        const val CAMERA_MOVING_ANIMATION_DURATION = 1f
        const val VIEW_CIRCLE_COEFFICIENT = 1.2f
    }

    override val mModel: GeoChatSettingsViewModel by viewModels {
        GeoChatSettingsViewModelFactory()
    }

    private lateinit var mBinding: FragmentGeoChatSettingsBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var mCurLocationPoint: Point = Point(0.0, 0.0)
    private var mCurLocationCircle: CircleMapObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(requireContext())
    }

    override fun onStart() {
        super.onStart()

        MapKitFactory.getInstance().onStart()
        mBinding.map.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        mBinding.map.onStop()

        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentGeoChatSettingsBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.curRadiusOptionIndex.observe(viewLifecycleOwner) {
            changeRadiusChoice(it)
            drawCurLocationCircle()
            setCameraPositionForCurCircle()
        }

        mBinding.radiusSetting250m.radioButton.setOnClickListener {
            mModel.changeCurRadiusOptionIndex(0)
        }
        mBinding.radiusSetting500m.radioButton.setOnClickListener {
            mModel.changeCurRadiusOptionIndex(1)
        }
        mBinding.radiusSetting1km.radioButton.setOnClickListener {
            mModel.changeCurRadiusOptionIndex(2)
        }
        mBinding.radiusSetting3km.radioButton.setOnClickListener {
            mModel.changeCurRadiusOptionIndex(3)
        }
        mBinding.radiusSetting10km.radioButton.setOnClickListener {
            mModel.changeCurRadiusOptionIndex(4)
        }
        mBinding.goButton.setOnClickListener { onGoClicked() }
    }

    private fun changeRadiusChoice(radiusOptionIndex: Int) {
        for (curRadiusOptionIndex in 0 until mBinding.radiusSettings.childCount) {
            val curRadiusVariantView = mBinding.radiusSettings.getChildAt(curRadiusOptionIndex)

            val curRadiusVariantBinding =
                DataBindingUtil.getBinding<ComponentRadiusSettingOptionBinding>(curRadiusVariantView)

            if (curRadiusVariantBinding == null) return

            if (curRadiusVariantBinding.radiusSettingOptionIsChecked == true) {
                if (radiusOptionIndex == curRadiusOptionIndex) return
                else curRadiusVariantBinding.radiusSettingOptionIsChecked = false

            } else if (radiusOptionIndex == curRadiusOptionIndex) {
                curRadiusVariantBinding.radiusSettingOptionIsChecked =  true
            }
        }
    }

    private fun onGoClicked() {
//        handleWaitingStart() // there's no reason to do it manually. the model should change isWaiting
                               // value that has to lead to calling the method;

        // todo: conveying a signal to the model..


    }

    override fun getPermissionsToRequest(): Array<String>? {
        return arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestedPermissionsGranted() {
        super.onRequestedPermissionsGranted()

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        mFusedLocationClient.getCurrentLocation(
            CurrentLocationRequest.Builder().build(),
            null
        ).addOnCompleteListener {
            mCurLocationPoint = Point(it.result.latitude, it.result.longitude)

            drawCurLocationCircle()
            setCameraPositionForCurCircle(false)
        }
    }

    override fun onRequestedPermissionsDenied(deniedPermissions: List<String>) {
        super.onRequestedPermissionsDenied(deniedPermissions)

        // todo: handling a denying case..

        Log.d(TAG, "Denied permissions: ${deniedPermissions.joinToString()}")
    }

    private fun drawCurLocationCircle() {
        val locationCircle = Circle(mCurLocationPoint, mModel.getCurRadiusOptionMeters())

        if (mCurLocationCircle != null)
            mBinding.map.mapWindow.map.mapObjects.remove(mCurLocationCircle!!)

        mCurLocationCircle = mBinding.map.mapWindow.map.mapObjects.addCircle(locationCircle)
            .apply {
                strokeColor = ContextCompat.getColor(
                    requireContext(), com.qubacy.geoqq.R.color.red_primary)
                strokeWidth = LOCATION_CIRCLE_STROKE_WIDTH
                fillColor = ContextCompat.getColor(
                    requireContext(), com.qubacy.geoqq.R.color.red_primary_alpha)
            }
    }

    private fun setCameraPositionForCurCircle(
        isAnimated: Boolean = true
    ) {
        val viewCircle = Circle(
            mCurLocationCircle!!.geometry.center,
            mCurLocationCircle!!.geometry.radius * VIEW_CIRCLE_COEFFICIENT
        )
        val newCameraPosition = mBinding.map.mapWindow.map.cameraPosition(
            Geometry.fromCircle(viewCircle))

        if (isAnimated)
            mBinding.map.mapWindow.map.move(
                newCameraPosition,
                Animation(Animation.Type.SMOOTH, CAMERA_MOVING_ANIMATION_DURATION),
                null
            )
        else
            mBinding.map.mapWindow.map.move(newCameraPosition)
    }
}