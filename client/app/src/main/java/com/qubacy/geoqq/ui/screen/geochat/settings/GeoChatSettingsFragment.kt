package com.qubacy.geoqq.ui.screen.geochat.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.databinding.ComponentRadiusSettingOptionBinding
import com.qubacy.geoqq.databinding.FragmentGeoChatSettingsBinding
import com.qubacy.geoqq.ui.common.component.dialog.error.ErrorDialog
import com.qubacy.geoqq.ui.common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModelFactory
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CircleMapObject

class GeoChatSettingsFragment() : LocationFragment() {
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

    private var mCurLocationCircle: CircleMapObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(requireContext())
    }

    override fun onDestroy() {

        super.onDestroy()
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
        mBinding = FragmentGeoChatSettingsBinding.inflate(
            inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.curRadiusOptionIndex.observe(viewLifecycleOwner) {
            changeRadiusChoice(it)

            drawCurLocationCircle(mModel.lastLocationPoint.value)
            setCameraPositionForCurCircle()
        }

        mBinding.radiusSetting1.apply {
            radiusSettingOptionText = mModel.getLabelForRadiusOption(0)

            radioButton.setOnClickListener {
                mModel.changeCurRadiusOptionIndex(0)
            }
        }
        mBinding.radiusSetting2.apply {
            radiusSettingOptionText = mModel.getLabelForRadiusOption(1)

            radioButton.setOnClickListener {
                mModel.changeCurRadiusOptionIndex(1)
            }
        }
        mBinding.radiusSetting3.apply {
            radiusSettingOptionText = mModel.getLabelForRadiusOption(2)

            radioButton.setOnClickListener {
                mModel.changeCurRadiusOptionIndex(2)
            }
        }
        mBinding.radiusSetting4.apply {
            radiusSettingOptionText = mModel.getLabelForRadiusOption(3)

            radioButton.setOnClickListener {
                mModel.changeCurRadiusOptionIndex(3)
            }
        }
        mBinding.radiusSetting5.apply {
            radiusSettingOptionText = mModel.getLabelForRadiusOption(4)

            radioButton.setOnClickListener {
                mModel.changeCurRadiusOptionIndex(4)
            }
        }

        mBinding.goButton.setOnClickListener { onGoClicked() }
    }

    override fun onLocationPointChanged(newLocationPoint: Point) {
        drawCurLocationCircle(newLocationPoint)
        setCameraPositionForCurCircle(false)
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
        val directions = GeoChatSettingsFragmentDirections
            .actionGeoChatSettingsFragmentToGeoChatFragment(mModel.getCurRadiusOptionMeters())

        findNavController().navigate(directions)
    }

    private fun drawCurLocationCircle(locationPoint: Point?) {
        Log.d(TAG, "drawCurLocationCircle(): locationPoint: ${locationPoint?.latitude}:${locationPoint?.longitude}")

        if (locationPoint == null) return

        val locationCircle = Circle(locationPoint, mModel.getCurRadiusOptionMeters())

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
        if (mCurLocationCircle == null) return

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