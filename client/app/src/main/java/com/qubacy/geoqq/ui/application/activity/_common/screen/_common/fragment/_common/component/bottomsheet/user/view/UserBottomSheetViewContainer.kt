package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentBottomSheetUserBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

class UserBottomSheetViewContainer(
    context: Context,
    parent: CoordinatorLayout,
    val expandedHeight: Int,
    val collapsedHeight: Int = expandedHeight / 2,
    private val mCallback: UserBottomSheetViewContainerCallback
) {
    companion object {
        const val TAG = "UserBottomSheet"
    }

    private lateinit var mBinding: ComponentBottomSheetUserBinding
    private lateinit var mBehavior: BottomSheetBehavior<MotionLayout>

    private var mIsMateButtonEnabled: Boolean = true

    init {
        inflate(context, parent)
        initLayout()
    }

    private fun inflate(context: Context, parent: CoordinatorLayout) {
        val layoutInflater = LayoutInflater.from(context)

        mBinding = ComponentBottomSheetUserBinding.inflate(layoutInflater, parent, false)
    }

    private fun initLayout() {
        mBinding.root.updateLayoutParams {
            height = expandedHeight
        }

        initBehavior()

        mBinding.root.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        v.viewTreeObserver.removeOnPreDrawListener(this)

                        return true
                    }
                })
            }

            override fun onViewDetachedFromWindow(v: View) {}
        })
    }

    private fun initBehavior() {
        mBehavior = BottomSheetBehavior.from(mBinding.root).apply {
            addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset !in 0.001f..0.999f) return // todo: is it ok??

                    mBinding.root.setTransition(
                        R.id.component_bottom_sheet_user_scene_transition_collapsed_expanded)
                    mBinding.root.progress = slideOffset
                }
            })

            peekHeight = collapsedHeight
        }
    }

    fun adjustToInsets(insetsRes: WindowInsetsCompat) {
        val bottomInset = getBottomInsetFromInsetsRes(insetsRes)

        mBinding.root.getConstraintSet(R.id.component_bottom_sheet_user_scene_collapsed).apply {
            setMargin(
                R.id.component_bottom_sheet_user_button_mate,
                ConstraintLayout.LayoutParams.BOTTOM,
                bottomInset
            )
        }
        mBinding.root.getConstraintSet(R.id.component_bottom_sheet_user_scene_expanded).apply {
            setMargin(
                R.id.component_bottom_sheet_user_button_mate,
                ConstraintLayout.LayoutParams.BOTTOM,
                bottomInset
            )
        }

        mBinding.root.requestLayout() // todo: mb this is useless;
    }

    private fun getBottomInsetFromInsetsRes(insetsRes: WindowInsetsCompat): Int {
        return insetsRes.getInsets(
            WindowInsetsCompat.Type.navigationBars()
        ).bottom
    }

    fun setUserData(userPresentation: UserPresentation) {
        mBinding.componentBottomSheetUserImageAvatar.setImageURI(userPresentation.avatar.uri)
        mBinding.componentBottomSheetUserTextUsername.text = userPresentation.username
        mBinding.componentBottomSheetUserTextAboutMe.text = userPresentation.description

        setupMateButtonByUserData(userPresentation)
    }

    private fun setupMateButtonByUserData(userPresentation: UserPresentation) {
        mBinding.componentBottomSheetUserButtonMate.apply {
            isEnabled = !userPresentation.isDeleted && mIsMateButtonEnabled

            setText(
                if (!userPresentation.isMate) R.string.component_bottom_sheet_user_button_mate_caption_add
                else R.string.component_bottom_sheet_user_button_mate_caption_remove
            )
            setOnClickListener { mCallback.onMateButtonClicked() }
        }
    }

    fun open() {
        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun close() {
        mBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun getView(): View {
        return mBinding.root
    }

    fun setMateButtonEnabled(isEnabled: Boolean) {
        mIsMateButtonEnabled = isEnabled
        mBinding.componentBottomSheetUserButtonMate.isEnabled = isEnabled
    }
}