package com.qubacy.geoqq.ui.screen.geochat.settings

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.Slide
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentRadiusSettingOptionBinding
import com.qubacy.geoqq.databinding.FragmentGeoChatSettingsBinding
import com.qubacy.geoqq.ui.common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModelFactory
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.MapLoadStatistics
import com.yandex.mapkit.map.MapLoadedListener
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject

class GeoChatSettingsFragment() : LocationFragment(), MapLoadedListener {
    companion object {
        const val TAG = "SETTINGS_FRAGMENT"

        const val LOCATION_CIRCLE_STROKE_WIDTH = 2f
        const val CAMERA_MOVING_ANIMATION_DURATION = 1f
        const val VIEW_CIRCLE_COEFFICIENT = 1.2f
    }

    private lateinit var mBinding: FragmentGeoChatSettingsBinding

    private var mCurLocationCircle: CircleMapObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(requireContext())

        enterTransition = Slide(Gravity.END).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
        returnTransition = Slide(Gravity.END).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }

        // todo: decide what to do with this abrupt animation:
//        exitTransition = Fade().apply {
//            mode = Fade.MODE_OUT
//            interpolator = AccelerateDecelerateInterpolator()
//            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
//        }
        reenterTransition = Fade().apply {
            mode = Fade.MODE_IN
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }

        mModel = GeoChatSettingsViewModelFactory().create(GeoChatSettingsViewModel::class.java)
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

        MapKitFactory.getInstance().onStart()

        (mModel as GeoChatSettingsViewModel).onMapLoadingStarted()

        mBinding.map.apply {
            onStart()

            mapWindow.map.setMapLoadedListener(this@GeoChatSettingsFragment)
        }
    }

    override fun onStop() {
        (mModel as GeoChatSettingsViewModel).onMapLoadingStopped()
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
            .apply {
                lifecycleOwner = this@GeoChatSettingsFragment
            }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTransitionWindowBackgroundColorResId(R.color.green_dark_soft)

        mBinding.map.apply {
            setNoninteractive(true)
        }

        mBinding.radiusSetting1.apply {
            radiusSettingOptionText = (mModel as GeoChatSettingsViewModel).getLabelForRadiusOption(0)

            radioButton.setOnClickListener {
                (mModel as GeoChatSettingsViewModel).changeCurRadiusOptionIndex(0)
            }
        }
        mBinding.radiusSetting2.apply {
            radiusSettingOptionText = (mModel as GeoChatSettingsViewModel).getLabelForRadiusOption(1)

            radioButton.setOnClickListener {
                (mModel as GeoChatSettingsViewModel).changeCurRadiusOptionIndex(1)
            }
        }
        mBinding.radiusSetting3.apply {
            radiusSettingOptionText = (mModel as GeoChatSettingsViewModel).getLabelForRadiusOption(2)

            radioButton.setOnClickListener {
                (mModel as GeoChatSettingsViewModel).changeCurRadiusOptionIndex(2)
            }
        }
        mBinding.radiusSetting4.apply {
            radiusSettingOptionText = (mModel as GeoChatSettingsViewModel).getLabelForRadiusOption(3)

            radioButton.setOnClickListener {
                (mModel as GeoChatSettingsViewModel).changeCurRadiusOptionIndex(3)
            }
        }
        mBinding.radiusSetting5.apply {
            radiusSettingOptionText = (mModel as GeoChatSettingsViewModel).getLabelForRadiusOption(4)

            radioButton.setOnClickListener {
                (mModel as GeoChatSettingsViewModel).changeCurRadiusOptionIndex(4)
            }
        }

        mBinding.goButton.apply {
            isEnabled = false
            setOnClickListener { onGoClicked() }
        }

        (mModel as GeoChatSettingsViewModel).curRadiusOptionIndex.observe(viewLifecycleOwner) {
            Log.d(TAG, "curRadiusOptionIndex.observe")

            changeRadiusChoice(it)

            drawCurLocationCircle((mModel as GeoChatSettingsViewModel).lastLocationPoint.value)
            setCameraPositionForCurCircle()
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onLocationPointChanged(newLocationPoint: Point) {
        drawCurLocationCircle(newLocationPoint)
        setCameraPositionForCurCircle(true)
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
                curRadiusVariantBinding.radiusSettingOptionIsChecked = true
            }
        }
    }

    private fun onGoClicked() {
        val directions = GeoChatSettingsFragmentDirections
            .actionGeoChatSettingsFragmentToGeoChatFragment(
                (mModel as GeoChatSettingsViewModel).getCurRadiusOptionMeters())

        findNavController().navigate(directions)
    }

    private fun drawCurLocationCircle(locationPoint: Point?) {
        Log.d(TAG, "drawCurLocationCircle(): locationPoint: ${locationPoint?.latitude}:${locationPoint?.longitude}")

        if (locationPoint == null) return

        val locationCircle = Circle(
            locationPoint, (mModel as GeoChatSettingsViewModel).getCurRadiusOptionMeters())

        if (mCurLocationCircle != null) removeCircleFromMap(mCurLocationCircle!!)

        mCurLocationCircle = mBinding.map.mapWindow.map.mapObjects.addCircle(locationCircle)
            .apply {
                strokeColor = ContextCompat.getColor(
                    requireContext(), com.qubacy.geoqq.R.color.red_primary)
                strokeWidth = LOCATION_CIRCLE_STROKE_WIDTH
                fillColor = ContextCompat.getColor(
                    requireContext(), com.qubacy.geoqq.R.color.red_primary_alpha)
            }
    }

    private fun removeCircleFromMap(circle: CircleMapObject) {
        var isOnMap = false

        mBinding.map.mapWindow.map.mapObjects.traverse(object : MapObjectVisitor {
            override fun onPlacemarkVisited(p0: PlacemarkMapObject) {}
            override fun onPolylineVisited(p0: PolylineMapObject) {}
            override fun onPolygonVisited(p0: PolygonMapObject) {}
            override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean { return true }
            override fun onCollectionVisitEnd(p0: MapObjectCollection) {}
            override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean { return false }
            override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {}

            override fun onCircleVisited(p0: CircleMapObject) {
                if (p0 == circle) isOnMap = true
            }
        })

        if (!isOnMap) return

        mBinding.map.mapWindow.map.mapObjects.remove(mCurLocationCircle!!)
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

    override fun onMapLoaded(p0: MapLoadStatistics) {
        (mModel as GeoChatSettingsViewModel).onMapLoadingStopped()

        mBinding.goButton.isEnabled = true
    }
}