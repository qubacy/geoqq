package com.qubacy.geoqq.ui.util

import android.util.Log
import android.view.View
import androidx.core.view.marginTop
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class DragBottomSheetViewAction(
    private val mDirection: Direction = Direction.UP
) : ViewAction {
    companion object {
        const val TAG = "DraggingViewAction"

        const val TOP_OFFSET = 20f
        const val STEP_COUNT = 100

        enum class Direction {
            UP, DOWN
        }
    }

    override fun getDescription(): String {
        return "An action to perform dragging of a bottom sheet."
    }

    override fun getConstraints(): Matcher<View> {
        return Matchers.allOf(
            ViewMatchers.isDisplayed(),
            ViewMatchers.isClickable(),
            ViewMatchers.isFocusable()
        )
    }

    override fun perform(uiController: UiController?, view: View?) {
        if (uiController == null || view == null)
            throw IllegalArgumentException()

        val highTopPosition = floatArrayOf(view.right / 2f, view.marginTop.toFloat())
        val lowTopPosition = floatArrayOf(view.right / 2f, view.top + TOP_OFFSET)

        val viewEndTopPosition = if (mDirection == Direction.DOWN) lowTopPosition else highTopPosition
        val viewStartTopPosition =  if (mDirection == Direction.DOWN) highTopPosition else lowTopPosition

        val viewDraggingTrackParticle =
            (viewStartTopPosition[1] - viewEndTopPosition[1]) / STEP_COUNT

        val fingerPrecision = Press.FINGER.describePrecision()
        val downActionResult = MotionEvents.sendDown(
            uiController, viewStartTopPosition, fingerPrecision)

        for (step in 0..STEP_COUNT) {
            val curY = (viewStartTopPosition[1] - viewDraggingTrackParticle * step)
            val viewDraggingTopPosition = floatArrayOf(view.right / 2f, curY)

            val moveActionResult = MotionEvents.sendMovement(
                uiController, downActionResult.down, viewDraggingTopPosition)

            Log.d(TAG, "step = $step; curY = $curY; moveActionResult = $moveActionResult")

            uiController.loopMainThreadForAtLeast(10)
        }

        MotionEvents.sendUp(uiController, downActionResult.down)
    }
}