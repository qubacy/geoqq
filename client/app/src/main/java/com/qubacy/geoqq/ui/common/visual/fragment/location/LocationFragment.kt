package com.qubacy.geoqq.ui.common.visual.fragment.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.ui.common.visual.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.common.visual.fragment.location.model.LocationViewModel
import com.yandex.mapkit.geometry.Point

abstract class LocationFragment() : WaitingFragment() {
    companion object {
        const val TAG = "LOCATION_FRAGMENT"

        const val DEFAULT_LOCATION_UPDATING_INTERVAL = 10000L
    }

    private var mLocationUpdatingInterval: Long = DEFAULT_LOCATION_UPDATING_INTERVAL

    private var mAreLocationServicesEnabled = false
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mAreLocationServicesEnabled = checkLocationServiceAvailability()

        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)

        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

        setLocationServiceStatus()
    }

    private fun checkLocationServiceAvailability(): Boolean {
        val locationManager = requireContext()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsProviderEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkProviderEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        Log.d(
            TAG, "checkLocationServiceAvailability(): " +
                "isGpsProviderEnabled = $isGpsProviderEnabled;" +
                " isNetworkProviderEnabled = $isNetworkProviderEnabled")

        return (isGpsProviderEnabled || isNetworkProviderEnabled)
    }

    override fun getPermissionsToRequest(): Array<String>? {
        return arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (mModel as LocationViewModel).lastLocationPoint.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            Log.d(TAG, "lastLocationPoint.observe(): ${it.latitude};${it.longitude}")

            onLocationPointChanged(it)
        }
    }

    abstract fun onLocationPointChanged(newLocationPoint: Point)

    @SuppressLint("MissingPermission")
    override fun onRequestedPermissionsGranted() {
        super.onRequestedPermissionsGranted()

        Log.d(TAG, "onRequestedPermissionsGranted() Geolocation permissions have been granted!")

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        mLocationCallback = getOnLocationChangedCallback()

        mFusedLocationClient!!.requestLocationUpdates(
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                mLocationUpdatingInterval).build(),
            mLocationCallback!!,
            Looper.getMainLooper()
        ).addOnFailureListener {
            onRequestingLocationUpdatesFailed(it)
        }
    }

    override fun onRequestedPermissionsDenied(deniedPermissions: List<String>) {
        super.onRequestedPermissionsDenied(deniedPermissions)

        Log.d(TAG, "Denied permissions: ${deniedPermissions.joinToString()}")

        onLocationPermissionsDenied()
    }

    open fun onLocationPermissionsDenied() {
        Log.d(TAG, "onLocationPermissionsDenied()")

        (mModel as LocationViewModel)
            .retrieveError(ErrorContext.Location.LOCATION_PERMISSIONS_DENIED.id)
    }

    open fun onLocationServicesNotEnabled() {
        Log.d(TAG, "onLocationServicesNotEnabled()")

        (mModel as LocationViewModel)
            .retrieveError(ErrorContext.Location.LOCATION_SERVICES_NOT_ENABLED.id)
    }

    open fun onRequestingLocationUpdatesFailed(exception: Exception) {
        Log.d(TAG, "onRequestingLocationUpdatesFailed(): ${exception.message}")

        (mModel as LocationViewModel)
            .retrieveError(ErrorContext.Location.GMS_API_NOT_AVAILABLE.id)
    }

    open fun onNewLocationGotten(newLocation: Location?) {
        if (newLocation == null) {
            // todo: is it necessary to handle?

            return
        }

        (mModel as LocationViewModel).changeLastLocation(newLocation)
    }

    open fun getOnLocationChangedCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                Log.d(TAG, "onLocationResult(): ${p0.lastLocation.toString()}")

                onNewLocationGotten(p0.lastLocation)
            }

            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)

                Log.d(TAG, "onLocationAvailability(): ${p0.isLocationAvailable}")

                setLocationServiceStatus()
            }
        }
    }

    private fun setLocationServiceStatus() {
        mAreLocationServicesEnabled = checkLocationServiceAvailability()

        if (!mAreLocationServicesEnabled) {
            onLocationServicesNotEnabled()
        }
    }
}