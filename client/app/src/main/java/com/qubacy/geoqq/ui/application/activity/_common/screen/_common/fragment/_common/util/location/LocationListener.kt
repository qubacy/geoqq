package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationListener(
    context: Context,
    callback: LocationListenerCallback,
    updatingInterval: Long = DEFAULT_LOCATION_UPDATING_INTERVAL
) {
    companion object {
        const val TAG = "LocationListener"

        const val DEFAULT_LOCATION_UPDATING_INTERVAL = 10000L
    }

    private val mContext: Context = context
    private val mCallback: LocationListenerCallback = callback

    private val mLocationUpdatingInterval: Long = updatingInterval

    private var mAreLocationServicesEnabled: Boolean

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null

    init {
        mAreLocationServicesEnabled = checkLocationServiceAvailability()
    }

    @SuppressLint("MissingPermission")
    fun startLocationListening(activity: Activity) {
        Log.d(TAG, "startLocationListening(): entering..")

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        mLocationCallback = getOnLocationChangedCallback()

        mFusedLocationClient!!.requestLocationUpdates(
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                mLocationUpdatingInterval).build(),
            mLocationCallback!!,
            Looper.getMainLooper()
        ).addOnFailureListener {
            mCallback.onRequestingLocationUpdatesFailed(it)
        }
    }

    fun reset() {
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
    }

    private fun checkLocationServiceAvailability(): Boolean {
        val locationManager = mContext
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsProviderEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkProviderEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        Log.d(
            TAG,
            "checkLocationServiceAvailability(): " +
            "isGpsProviderEnabled = $isGpsProviderEnabled;" +
            " isNetworkProviderEnabled = $isNetworkProviderEnabled"
        )

        return (isGpsProviderEnabled || isNetworkProviderEnabled)
    }

    private fun getOnLocationChangedCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                Log.d(TAG, "onLocationResult(): ${p0.lastLocation.toString()}")

                mCallback.onNewLocationGotten(p0.lastLocation)
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

        if (!mAreLocationServicesEnabled) mCallback.onLocationServicesNotEnabled()
    }
}