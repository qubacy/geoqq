package com.qubacy.geoqq.ui._common._test.view.util.action.pinch

import android.graphics.Point
import android.os.SystemClock
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.MotionEvent.PointerProperties
import android.view.View
import androidx.test.espresso.InjectEventSecurityException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class PinchZoomViewAction(
    val isPinchOut: Boolean = true
) : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return Matchers.allOf(
            ViewMatchers.isEnabled(),
            ViewMatchers.isDisplayed()
        )
    }

    override fun getDescription(): String {
        return "Pinch zoom view action."
    }

    override fun perform(uiController: UiController, view: View) {
        val middlePosition = getCenterPoint(view)
        val width = view.measuredWidth
        val delta = width / 4

        val startDelta = if (isPinchOut) delta else 0
        val endDelta = if (isPinchOut) 0 else delta

        val startPoint1 = Point(middlePosition.x - startDelta, middlePosition.y)
        val startPoint2 = Point(middlePosition.x + startDelta, middlePosition.y)
        val endPoint1 = Point(middlePosition.x - endDelta, middlePosition.y)
        val endPoint2 = Point(middlePosition.x + endDelta, middlePosition.y)

        performPinch(uiController, startPoint1, startPoint2, endPoint1, endPoint2)
    }
}

private fun getCenterPoint(view: View): Point {
    val locationOnScreen = IntArray(2)

    view.getLocationOnScreen(locationOnScreen)

    val viewHeight = view.height * view.scaleY
    val viewWidth = view.width * view.scaleX

    return Point(
        (locationOnScreen[0] + viewWidth / 2).toInt(),
        (locationOnScreen[1] + viewHeight / 2).toInt()
    )
}

private fun performPinch(
    uiController: UiController,
    startPoint1: Point,
    startPoint2: Point,
    endPoint1: Point,
    endPoint2: Point
) {
    val duration = 500
    val eventMinInterval: Long = 10

    val startTime = SystemClock.uptimeMillis()
    var eventTime = startTime

    var event: MotionEvent
    var eventX1 = startPoint1.x.toFloat()
    var eventY1 = startPoint1.y.toFloat()
    var eventX2 = startPoint2.x.toFloat()
    var eventY2 = startPoint2.y.toFloat()

    val properties = arrayOfNulls<PointerProperties>(2)
    val pp1 = PointerProperties().apply {
        id = 0
        toolType = MotionEvent.TOOL_TYPE_FINGER
    }
    val pp2 = PointerProperties().apply {
        id = 1
        toolType = MotionEvent.TOOL_TYPE_FINGER
    }

    properties.also {
        it[0] = pp1
        it[1] = pp2
    }

    val pointerCoords = arrayOfNulls<PointerCoords>(2)

    val pc1 = PointerCoords().apply {
        x = eventX1
        y = eventY1
        pressure = 1f
        size = 1f
    }

    val pc2 = PointerCoords().apply {
        x = eventX2
        y = eventY2
        pressure = 1f
        size = 1f
    }

    pointerCoords.also {
        it[0] = pc1
        it[1] = pc2
    }

    try {
        event = MotionEvent.obtain(
            startTime, eventTime,
            MotionEvent.ACTION_DOWN, 1, properties,
            pointerCoords, 0, 0,
            1f, 1f, 0, 0, 0, 0
        )
        injectMotionEventToUiController(uiController, event)

        event = MotionEvent.obtain(
            startTime,
            eventTime,
            MotionEvent.ACTION_POINTER_DOWN + (pp2.id shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
            2, properties, pointerCoords,
            0, 0, 1f, 1f,
            0, 0, 0, 0
        )
        injectMotionEventToUiController(uiController, event)

        val moveEventNumber = duration / eventMinInterval
        val stepX1 =  (endPoint1.x - startPoint1.x) / moveEventNumber
        val stepY1 = (endPoint1.y - startPoint1.y) / moveEventNumber
        val stepX2 = (endPoint2.x - startPoint2.x) / moveEventNumber
        val stepY2 = (endPoint2.y - startPoint2.y) / moveEventNumber

        for (i in 0 until moveEventNumber) {
            eventTime += eventMinInterval
            eventX1 += stepX1
            eventY1 += stepY1
            eventX2 += stepX2
            eventY2 += stepY2

            pc1.x = eventX1
            pc1.y = eventY1
            pc2.x = eventX2
            pc2.y = eventY2

            pointerCoords[0] = pc1
            pointerCoords[1] = pc2

            event = MotionEvent.obtain(
                startTime, eventTime,
                MotionEvent.ACTION_MOVE, 2, properties,
                pointerCoords, 0, 0, 1f, 1f,
                0, 0, 0, 0
            )
            injectMotionEventToUiController(uiController, event)
        }

        pc1.apply {
            x = endPoint1.x.toFloat()
            y = endPoint1.y.toFloat()
        }
        pc2.apply {
            x = endPoint2.x.toFloat()
            y = endPoint2.y.toFloat()
        }
        pointerCoords.also {
            it[0] = pc1
            it[1] = pc2
        }

        eventTime += eventMinInterval
        event = MotionEvent.obtain(
            startTime,
            eventTime,
            MotionEvent.ACTION_POINTER_UP + (pp2.id shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
            2, properties, pointerCoords, 0, 0,
            1f, 1f, 0, 0, 0, 0
        )
        injectMotionEventToUiController(uiController, event)

        eventTime += eventMinInterval
        event = MotionEvent.obtain(
            startTime, eventTime,
            MotionEvent.ACTION_UP, 1, properties,
            pointerCoords, 0, 0, 1f, 1f,
            0, 0, 0, 0
        )
        injectMotionEventToUiController(uiController, event)

    } catch (e: InjectEventSecurityException) {
        throw RuntimeException("Could not perform pinch", e)
    }
}

@Throws(InjectEventSecurityException::class)
private fun injectMotionEventToUiController(
    uiController: UiController,
    event: MotionEvent
) {
    val injectEventSucceeded = uiController.injectMotionEvent(event)

    check(injectEventSucceeded) { "Error performing event $event" }
}