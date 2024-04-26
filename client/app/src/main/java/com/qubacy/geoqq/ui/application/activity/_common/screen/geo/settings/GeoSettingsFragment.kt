package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentGeoSettingsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.hint.view.HintViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.MateRequestsFragment

class GeoSettingsFragment : BaseFragment<FragmentGeoSettingsBinding>() {

    private lateinit var mHintViewProvider: HintViewProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHintViewProvider()
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

            setHintText("TEST")
        }

        scheduleHintTextViewAppearanceAnimation()
    }

    private fun scheduleHintTextViewAppearanceAnimation() {
        mBinding.root.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                mHintViewProvider.scheduleAppearanceAnimation(
                    true, MateRequestsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT
                )

                return true
            }
        })
    }
}