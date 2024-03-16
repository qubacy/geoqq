package com.qubacy.geoqq.ui.application.activity._common.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.graphics.Insets
import androidx.core.view.updatePadding
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentLoginBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private lateinit var mHeaderDrawable: AnimatedVectorDrawableCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mHeaderDrawable = AnimatedVectorDrawableCompat.create(
            requireContext(), R.drawable.ic_login_header_animated
        )!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderAnimation()
    }

    private fun setupHeaderAnimation() {
        mBinding.fragmentLoginImageHeader.apply {
            setImageDrawable(mHeaderDrawable)
        }
        mBinding.root.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    mBinding.root.viewTreeObserver.removeOnPreDrawListener(this)
                    mHeaderDrawable.start()

                    return true
                }
            }
        )
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun adjustViewToInsets(insets: Insets) {
        super.adjustViewToInsets(insets)

        mBinding.fragmentLoginChangeLoginTypeWrapper.apply {
            updatePadding(bottom = insets.bottom)
        }
    }
}