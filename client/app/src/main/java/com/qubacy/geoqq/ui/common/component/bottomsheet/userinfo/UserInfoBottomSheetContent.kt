package com.qubacy.geoqq.ui.common.component.bottomsheet.userinfo

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
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.ui.common.component.bottomsheet.BottomSheetContent
import com.qubacy.geoqq.ui.common.component.bottomsheet.BottomSheetContentCallback

class UserInfoBottomSheetContent(
    context: Context,
    attributeSet: AttributeSet
) : BottomSheetContent<User>,
    MaterialCardView(context, attributeSet),
    TransitionListener
{
    companion object {
        const val COLLAPSED_BOTTOM_PADDING_IN_DP = 20f
    }

    private lateinit var mBinding: ComponentUserProfileBottomSheetBinding
    private lateinit var mBehavior: BottomSheetBehavior<UserInfoBottomSheetContent>

    private var mCallback: UserInfoBottomSheetContentCallback? = null

    private var mLastProgressValue = 0f

    private val mBottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // 0 - 1 = collapsed -> expanded

            if (slideOffset < 0 || slideOffset == 1.0f || slideOffset == 0.0f) return

            mBinding.bottomSheetContent.progress = slideOffset
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        mBinding = ComponentUserProfileBottomSheetBinding.bind(this)
        mBehavior = BottomSheetBehavior.from(this)

        mBehavior.peekHeight = calculatePeekHeight()
        mBehavior.addBottomSheetCallback(mBottomSheetCallback)
        mBinding.bottomSheetContent.setTransitionListener(this)
    }

    private fun calculatePeekHeight(): Int {
        // measuring the current view to get it's size later:
        measure(0, 0)

        // getting the lowest element's relative Y:
        val lowestY = mBinding.addFriendButton.y + mBinding.addFriendButton.measuredHeight

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
            if (progress > mLastProgressValue && mBinding.description.ellipsize == TruncateAt.END) {
                mBinding.description.ellipsize = null
                mBinding.description.maxLines = Int.MAX_VALUE

            } else if (progress < mLastProgressValue && mBinding.description.ellipsize == null) {
                mBinding.description.ellipsize = TruncateAt.END
                mBinding.description.maxLines = 1
            }
        }

        mLastProgressValue = progress
    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) { }

    override fun onTransitionTrigger(
        motionLayout: MotionLayout?,
        triggerId: Int,
        positive: Boolean,
        progress: Float
    ) { }

    override fun setData(data: User) {
        mBinding.apply {
            avatar.setImageURI(data.avatarUri)
            username.text = data.username
            description.text = data.description

            addFriendButton.apply {
                isEnabled = !data.isMate

                setOnClickListener {
                    mCallback!!.addToMates(data)

                    close()
                }
            }
        }
    }

    override fun setCallback(callback: BottomSheetContentCallback) {
        mCallback = callback as UserInfoBottomSheetContentCallback
    }

    override fun showPreview() {
        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun close() {
        mBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}