package com.qubacy.geoqq.ui.component.bottomsheet

import android.content.Context
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.card.MaterialCardView
import com.qubacy.geoqq.databinding.ComponentUserProfileBottomSheetBinding

class BottomSheetContent(context: Context, attributeSet: AttributeSet)
    : MaterialCardView(context, attributeSet), TransitionListener
{
    companion object {
        const val COLLAPSED_BOTTOM_PADDING_IN_DP = 20f
    }

    private lateinit var binding: ComponentUserProfileBottomSheetBinding
    private lateinit var behavior: BottomSheetBehavior<BottomSheetContent>

    private var lastProgressValue = 0f

    private val bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // 0 - 1 = collapsed -> expanded

            if (slideOffset < 0 || slideOffset == 1.0f || slideOffset == 0.0f) return

            binding.bottomSheetContent.progress = slideOffset
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        binding = ComponentUserProfileBottomSheetBinding.bind(this)
        behavior = BottomSheetBehavior.from(this)

        behavior.peekHeight = calculatePeekHeight()
        behavior.addBottomSheetCallback(bottomSheetCallback)
        binding.bottomSheetContent.setTransitionListener(this)
    }

    private fun calculatePeekHeight(): Int {
        // measuring the current view to get it's size later:
        measure(0, 0)

        // getting the lowest element's relative Y:
        val lowestY = binding.addFriendButton.y + binding.addFriendButton.measuredHeight

        return lowestY.toInt() + dpToPx(COLLAPSED_BOTTOM_PADDING_IN_DP).toInt()
    }

    private fun dpToPx(dp: Float) : Float {
        val displayMetrics = resources.displayMetrics

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
    }

    private fun pxToDp(pixels: Float) : Float {
        val displayMetrics = resources.displayMetrics

        return pixels / displayMetrics.density
    }

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) { }

    override fun onTransitionChange(
        motionLayout: MotionLayout?,
        startId: Int,
        endId: Int,
        progress: Float
    ) {
        if (progress in 0.70..0.90) {
            if (progress > lastProgressValue && binding.description.ellipsize == TruncateAt.END) {
                binding.description.ellipsize = null
                binding.description.maxLines = Int.MAX_VALUE

            } else if (progress < lastProgressValue && binding.description.ellipsize == null) {
                binding.description.ellipsize = TruncateAt.END
                binding.description.maxLines = 1
            }
        }

        lastProgressValue = progress
    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) { }

    override fun onTransitionTrigger(
        motionLayout: MotionLayout?,
        triggerId: Int,
        positive: Boolean,
        progress: Float
    ) { }

}