package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.appbar.MaterialToolbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentGeoSettingsBinding
import com.qubacy.geoqq.ui._common.util.theme.extension.resolveColorAttr
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.hint.view.HintViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunner
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.LoadingFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.operation.LocationPointChangedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.util.listener.LocationListener
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.util.listener.LocationListenerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.component.map.view.GeoMapViewCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.GeoSettingsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.GeoSettingsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.operation.ChangeRadiusUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.state.GeoSettingsUiState
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.MapLoadStatistics
import com.yandex.mapkit.map.MapLoadedListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeoSettingsFragment(

) : StatefulFragment<
    FragmentGeoSettingsBinding, GeoSettingsUiState, GeoSettingsViewModel
>(), LoadingFragment, LocationFragment,
    PermissionRunnerCallback, LocationListenerCallback, GeoMapViewCallback,
    MapLoadedListener
{
    companion object {
        const val TAG = "GeoSettingsFragment"

        const val HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT = 1500L

        const val DEFAULT_VIEW_COEFFICIENT = 1.2f
        const val DEFAULT_CAMERA_MOVING_ANIMATION_DURATION = 0.3f

        const val DEFAULT_RADIUS_CIRCLE_STROKE_WIDTH = 2f
        const val DEFAULT_RADIUS_CIRCLE_FILL_ALPHA = 100
    }

    @Inject
    @GeoSettingsViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: GeoSettingsViewModel by viewModels(
        factoryProducer = { viewModelFactory })

    private lateinit var mPermissionRunner: PermissionRunner<GeoSettingsFragment>
    private lateinit var mLocationListener: LocationListener

    private lateinit var mHintViewProvider: HintViewProvider

    private var mCircleMapObject: CircleMapObject? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLocationListener()
        initPermissionRunner()

        initUiControls()

        initHintViewProvider()
    }

    override fun getFragmentDestinationId(): Int {
        return R.id.geoSettingsFragment
    }

    override fun onStart() {
        mCircleMapObject = null // todo: ??

        super.onStart()

        MapKitFactory.getInstance().onStart()
        mBinding.fragmentGeoSettingsMap.onStart()

        if (!mPermissionRunner.isRequestingPermissions)
            mLocationListener.startLocationListening(requireActivity())
    }

    override fun onStop() {
        mLocationListener.reset()
        mBinding.fragmentGeoSettingsMap.onStop()
        MapKitFactory.getInstance().onStop()

        super.onStop()
    }

    override fun afterDestinationChange() {
        super.afterDestinationChange()

        adjustUiWithLoadingState(true)
    }

    override fun retrieveToolbar(): MaterialToolbar {
        return mBinding.fragmentGeoSettingsTopBar
    }

    override fun getFragmentTitle(): String {
        return getString(R.string.fragment_geo_settings_top_bar_title_text)
    }

    override fun runInitWithUiState(uiState: GeoSettingsUiState) {
        super.runInitWithUiState(uiState)

        uiState.lastLocationPoint?.let { adjustMapWithLocationPoint(it) }

        adjustUiWithRadius(uiState.radius)
    }

    private fun adjustUiWithRadius(radius: Float) {
        mBinding.fragmentGeoSettingsTextRadius.text =
            getString(R.string.fragment_geo_settings_text_radius_text, radius.toInt().toString())
    }

    private fun initPermissionRunner() {
        mPermissionRunner = PermissionRunner(this).also {
            it.requestPermissions()
        }
    }

    private fun initUiControls() {
        initTopBarMenu()
        initMapView()
    }

    private fun initTopBarMenu() {
        inflateTopBarMenu()
        mBinding.fragmentGeoSettingsTopBar.setOnMenuItemClickListener {
            onMenuItemClicked(it)
        }
    }

    private fun inflateTopBarMenu() {
        mBinding.fragmentGeoSettingsTopBar.inflateMenu(R.menu.geo_settings_top_bar)
    }

    private fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.main_top_bar_option_my_profile -> navigateToMyProfile()
            R.id.geo_settings_top_bar_option_hint -> showHint()
            else -> return false
        }

        return true
    }

    private fun navigateToMyProfile() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_geoSettingsFragment_to_myProfileFragment)
    }

    private fun showHint() {
        mHintViewProvider.animateAppearance(true)
    }

    private fun initMapView() {
        mBinding.fragmentGeoSettingsMap.apply {
            setNoninteractive(true)
            mapWindow.map.setMapLoadedListener(this@GeoSettingsFragment)
            setCallback(this@GeoSettingsFragment)
        }
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
                    true, GeoSettingsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT
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
        mLocationListener.startLocationListening(requireActivity())
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
            SetLoadingStateUiOperation::class ->
                processSetLoadingOperation(uiOperation as SetLoadingStateUiOperation)
            LocationPointChangedUiOperation::class ->
                processLocationPointChangedUiOperation(
                    uiOperation as LocationPointChangedUiOperation)
            ChangeRadiusUiOperation::class ->
                processChangeRadiusUiOperation(uiOperation as ChangeRadiusUiOperation)
            else -> return false
        }

        return true
    }

    private fun processChangeRadiusUiOperation(changeRadiusUiOperation: ChangeRadiusUiOperation) {
        changeCircleRadius(changeRadiusUiOperation.radius)
        setCameraPositionForRadiusCircle(true)

        adjustUiWithRadius(changeRadiusUiOperation.radius)
    }

    override fun processLocationPointChangedUiOperation(
        locationPointChangedUiOperation: LocationPointChangedUiOperation
    ) {
        adjustMapWithLocationPoint(locationPointChangedUiOperation.locationPoint)
    }

    private fun adjustMapWithLocationPoint(locationPoint: Point) {
        setRadiusCircleWithLocationPoint(locationPoint)
    }

    private fun setRadiusCircleWithLocationPoint(locationPoint: Point) {
        Log.d(TAG, "setRadiusCircleWithLocationPoint(): entering..")

        drawRadiusCircleWithLocationPoint(locationPoint)
        setCameraPositionForRadiusCircle()
    }

    private fun drawRadiusCircleWithLocationPoint(locationPoint: Point) {
        val radius = mModel.uiState.radius
        val radiusCircle = Circle(locationPoint, radius)

        if (mCircleMapObject == null) initCircleMapObject(radiusCircle)
        else mCircleMapObject!!.geometry = radiusCircle
    }

    private fun setCameraPositionForRadiusCircle(
        isAnimated: Boolean = true
    ) {
        val radiusCircle = mCircleMapObject!!.geometry
        val viewCircle = Circle(radiusCircle.center,
            radiusCircle.radius * DEFAULT_VIEW_COEFFICIENT)
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

    private fun initCircleMapObject(circle: Circle) {
        Log.d(TAG, "initCircleMapObject(): entering..")

        val containerColor = requireContext().theme.resolveColorAttr(
            com.google.android.material.R.attr.colorErrorContainer)

        mCircleMapObject = mBinding.fragmentGeoSettingsMap.mapWindow.map.mapObjects
            .addCircle(circle).apply {
                strokeColor = requireContext().theme
                    .resolveColorAttr(androidx.appcompat.R.attr.colorError)
                strokeWidth = DEFAULT_RADIUS_CIRCLE_STROKE_WIDTH
                fillColor = ColorUtils.setAlphaComponent(
                    containerColor, DEFAULT_RADIUS_CIRCLE_FILL_ALPHA)
            }
    }

    override fun onMapLoaded(p0: MapLoadStatistics) {
        mModel.setMapLoadingStatus(true)


    }

    override fun onPinchZoom(coefficient: Float) {
        Log.d(TAG, "onPinchZoom(): coefficient = $coefficient;")

        scaleRadius(coefficient)
    }

    private fun scaleRadius(coefficient: Float) {
        mModel.applyScaleForRadius(coefficient)
    }

    private fun changeCircleRadius(radius: Float) {
        if (mCircleMapObject == null) return

        val prevRadiusCircle = mCircleMapObject!!.geometry

        mCircleMapObject!!.geometry = Circle(prevRadiusCircle.center, radius)
    }
}