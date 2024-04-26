package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentGeoSettingsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.hint.view.HintViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.LoadingFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.operation.LocationPointChangedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.util.listener.LocationListener
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.util.listener.LocationListenerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.GeoSettingsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.GeoSettingsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.state.GeoSettingsUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.MateRequestsFragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeoSettingsFragment(

) : StatefulFragment<
    FragmentGeoSettingsBinding, GeoSettingsUiState, GeoSettingsViewModel
>(), LoadingFragment, LocationFragment, PermissionRunnerCallback, LocationListenerCallback {
    companion object {
        const val DEFAULT_RADIUS_METERS = 1000
        const val DEFAULT_VIEW_COEFFICIENT = 1.2f
        const val DEFAULT_CAMERA_MOVING_ANIMATION_DURATION = 300f
    }

    @Inject
    @GeoSettingsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: GeoSettingsViewModel by viewModels(
        factoryProducer = { viewModelFactory })

    private lateinit var mLocationListener: LocationListener

    private lateinit var mHintViewProvider: HintViewProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runPermissionCheck<GeoSettingsFragment>()

        initLocationListener()
        initHintViewProvider()
    }

    override fun onStart() {
        super.onStart()

        MapKitFactory.getInstance().onStart()
        mBinding.fragmentGeoSettingsMap.onStart()
        mLocationListener.startLocationListening(requireActivity())
    }

    override fun onStop() {
        mLocationListener.reset()
        mBinding.fragmentGeoSettingsMap.onStop()
        MapKitFactory.getInstance().onStop()

        super.onStop()
    }

    private fun initLocationListener() {
        mLocationListener = LocationListener(requireContext(), this)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGeoSettingsBinding {
        return FragmentGeoSettingsBinding.inflate(inflater, container, false)
    }

    override fun adjustViewToInsets(insets: Insets, insetsRes: WindowInsetsCompat) {
        super.adjustViewToInsets(insets, insetsRes)

        mBinding.fragmentGeoSettingsTopBarWrapper.apply {
            updatePadding(top = insets.top)
        }
        mBinding.fragmentGeoSettingsButtonGo.apply {
            updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = insets.bottom
            }
        }
        mBinding.fragmentGeoSettingsTextRadius.apply {
            updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = insets.bottom
            }
        }
    }

    private fun initHintViewProvider() {
        mHintViewProvider = HintViewProvider(mBinding.root, false).apply {
            getView().updateLayoutParams<CoordinatorLayout.LayoutParams> {
                anchorId = mBinding.fragmentGeoSettingsTopBarWrapper.id
                anchorGravity = Gravity.BOTTOM
                gravity = Gravity.BOTTOM
            }

            mBinding.root.addView(this.getView(), 1)

            setHintText(getString(R.string.fragment_geo_settings_hint_text))
        }

        scheduleHintTextViewAppearanceAnimation()
    }

    private fun scheduleHintTextViewAppearanceAnimation() {
        mBinding.root.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                mHintViewProvider.scheduleAppearanceAnimation(
                    true, MateRequestsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT
                )

                return true
            }
        })
    }

    override fun getPermissionsToRequest(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestedPermissionsGranted(endAction: (() -> Unit)?) {
        // todo: starting listening for the location..


    }

    override fun adjustUiWithLoadingState(isLoading: Boolean) {
        mBinding.fragmentGeoSettingsProgressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onNewLocationGotten(location: Location?) {
        if (location == null) return

        Log.d(TAG, "onNewLocationGotten(): location = ${location.latitude}:${location.longitude};")

        mModel.changeLastLocation(location)
    }

    override fun onLocationServicesNotEnabled() {
        TODO("Not yet implemented")
    }

    override fun onRequestingLocationUpdatesFailed(exception: Exception) {
        TODO("Not yet implemented")
    }

    override fun processUiOperation(uiOperation: UiOperation): Boolean {
        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            LocationPointChangedUiOperation::class ->
                processLocationPointChangedUiOperation(
                    uiOperation as LocationPointChangedUiOperation)
            else -> return false
        }

        return true
    }

    override fun processLocationPointChangedUiOperation(
        locationPointChangedUiOperation: LocationPointChangedUiOperation
    ) {
        adjustMapWithLocationPoint(locationPointChangedUiOperation.locationPoint)
    }

    private fun adjustMapWithLocationPoint(locationPoint: Point) {
        setCameraPositionForViewCircle(locationPoint)
    }

    private fun setCameraPositionForViewCircle(
        locationPoint: Point,
        isAnimated: Boolean = true
    ) {
        val viewCircle = Circle(
            locationPoint,
            DEFAULT_RADIUS_METERS * DEFAULT_VIEW_COEFFICIENT
        )
        val newCameraPosition = mBinding.fragmentGeoSettingsMap.mapWindow.map
            .cameraPosition(Geometry.fromCircle(viewCircle))

        if (isAnimated)
            mBinding.fragmentGeoSettingsMap.mapWindow.map.move(
                newCameraPosition,
                Animation(Animation.Type.SMOOTH, DEFAULT_CAMERA_MOVING_ANIMATION_DURATION),
                null
            )
        else
            mBinding.fragmentGeoSettingsMap.mapWindow.map.move(newCameraPosition)
    }
}