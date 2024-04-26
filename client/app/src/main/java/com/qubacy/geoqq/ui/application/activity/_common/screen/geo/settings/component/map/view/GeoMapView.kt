package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.component.map.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.yandex.mapkit.mapview.MapView

class GeoMapView(
    context: Context,
    attributeSet: AttributeSet? = null
) : MapView(context, attributeSet), ScaleGestureDetector.OnScaleGestureListener {
    companion object {
        const val TAG = "GeoMapView"
    }

    private var mCallback: GeoMapViewCallback? = null

    private val mScaleGestureDetector: ScaleGestureDetector

    private var mScale: Float = 1f

    init {
        mScaleGestureDetector = ScaleGestureDetector(context, this)
    }

    fun setCallback(callback: GeoMapViewCallback) {
        mCallback = callback
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || event.pointerCount != 2) return true

        mScaleGestureDetector.onTouchEvent(event)

        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        mScale *= detector.scaleFactor // todo: mb it's useless;

        Log.d(TAG, "onScale(): mScale = $mScale;")

        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        Log.d(TAG, "onScaleEnd(): mScale = $mScale;")

        mCallback?.onPinchZoom(mScale)

        mScale = 1f
    }
}