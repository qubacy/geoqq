package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePadding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentBottomSheetUserBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

class UserBottomSheetView(
    context: Context,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(
    context,
    attributeSet,
    com.google.android.material.R.attr.bottomSheetStyle,
    com.google.android.material.R.style.Widget_Material3_BottomSheet
) {
    private var mSidePadding: Int = 0

    private lateinit var mBinding: ComponentBottomSheetUserBinding
    private lateinit var mBehavior: BottomSheetBehavior<ConstraintLayout>

    init {
        initVariables(context)
        inflate(context)
        initAttrs()
    }

    private fun initVariables(context: Context) {
        mSidePadding = context.resources.getDimension(R.dimen.medium_gap_between_components)
            .toInt()
    }

    private fun inflate(context: Context) {
        val layoutInflater = LayoutInflater.from(context)

        mBinding = ComponentBottomSheetUserBinding.inflate(layoutInflater, this)
    }

    private fun initAttrs() {
        initLayoutAttrs()
        initStyleAttrs()
    }

    private fun initLayoutAttrs() {
        initBehavior()

        layoutParams = CoordinatorLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            gravity = Gravity.BOTTOM
            behavior = mBehavior

            updatePadding(left = mSidePadding, right = mSidePadding)
        }
    }

    private fun initBehavior() {
        mBehavior = BottomSheetBehavior<ConstraintLayout>(context, null).apply {
            addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    changeAboutMeLinesByState(newState)
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) { }
            })

            isHideable = true
        }
    }

    private fun changeAboutMeLinesByState(state: Int) {
        val lineCount = when (state) {
            BottomSheetBehavior.STATE_EXPANDED -> Int.MAX_VALUE
            else -> 5
        }

        mBinding.componentBottomSheetUserTextAboutMe.setLines(lineCount)
    }

    private fun initStyleAttrs() {

    }

    fun setUserData(userPresentation: UserPresentation) {
        mBinding.componentBottomSheetUserImageAvatar.setImageURI(userPresentation.avatar.uri)
        mBinding.componentBottomSheetUserTextUsername.text = userPresentation.username
        mBinding.componentBottomSheetUserTextAboutMe.text = userPresentation.description

        // todo: button appearance..


    }

    fun open() {
        mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}