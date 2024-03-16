package com.qubacy.geoqq.ui.application.activity._common.screen.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.Insets
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentLoginBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment(

) : BusinessFragment<FragmentLoginBinding, LoginUiState, LoginViewModel>() {
    @Inject
    @LoginViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: LoginViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mHeaderDrawable: AnimatedVectorDrawableCompat
    private var mRepeatPasswordChangeVisibilityAnimationDuration: Long = 0L

    private var mRepeatPasswordViewHeight: Int = 0
    private var mControlComponentGap: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mHeaderDrawable = AnimatedVectorDrawableCompat.create(
            requireContext(), R.drawable.ic_login_header_animated
        )!!
        mRepeatPasswordChangeVisibilityAnimationDuration = requireContext().resources
            .getInteger(R.integer.fragment_login_repeat_password_visibility_change_animation_duration)
            .toLong()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewValues()

        mBinding.fragmentLoginButtonChangeLoginType.apply {
            setOnClickListener { onChangeLoginTypeClicked() }
        }

        setupHeaderAnimation()
    }

    private fun initViewValues() {
        mBinding.fragmentLoginTextInputPasswordWrapper.measure(0, 0)

        mRepeatPasswordViewHeight = mBinding.fragmentLoginTextInputPasswordWrapper.measuredHeight
        mControlComponentGap = mBinding.fragmentLoginTextInputPasswordWrapper.marginTop
    }

    private fun onChangeLoginTypeClicked() {
        changeLoginMode()
    }

    private fun changeLoginMode() {
        // todo: implement..

        val curLoginMode = mModel.uiState.loginMode
        val newLoginMode = LoginUiState.LoginMode.getNextLoginMode(curLoginMode)

        mModel.setLoginMode(newLoginMode)
        setControlsWithLoginMode(newLoginMode)
    }

    private fun setControlsWithLoginMode(loginMode: LoginUiState.LoginMode) {
        setRepeatPasswordWithLoginMode(loginMode)
        setLoginButtonWithLoginMode(loginMode)
        setChangeLoginTypeTextWithLoginMode(loginMode)
        setChangeLoginTypeButtonWithLoginMode(loginMode)
    }

    private fun setRepeatPasswordWithLoginMode(loginMode: LoginUiState.LoginMode) {
        changeRepeatPasswordVisibility(loginMode == LoginUiState.LoginMode.SIGN_UP)
    }

    private fun setLoginButtonWithLoginMode(loginMode: LoginUiState.LoginMode) {
        val loginButtonText =
            if (loginMode == LoginUiState.LoginMode.SIGN_IN)
                R.string.fragment_login_button_login_text_sign_in_mode
            else
                R.string.fragment_login_button_login_text_sign_up_mode

        mBinding.fragmentLoginButtonLogin.setText(loginButtonText)
    }

    private fun setChangeLoginTypeTextWithLoginMode(loginMode: LoginUiState.LoginMode) {
        val changeLoginTypeText =
            if (loginMode == LoginUiState.LoginMode.SIGN_IN)
                R.string.fragment_login_text_change_login_type_text_sign_in_mode
            else
                R.string.fragment_login_text_change_login_type_text_sign_up_mode

        mBinding.fragmentLoginTextChangeLoginType.setText(changeLoginTypeText)
    }

    private fun setChangeLoginTypeButtonWithLoginMode(loginMode: LoginUiState.LoginMode) {
        val changeLoginTypeText =
            if (loginMode == LoginUiState.LoginMode.SIGN_IN)
                R.string.fragment_login_button_change_login_type_text_sign_in_mode
            else
                R.string.fragment_login_button_change_login_type_text_sign_up_mode

        mBinding.fragmentLoginButtonChangeLoginType.setText(changeLoginTypeText)
    }

    private fun changeRepeatPasswordVisibility(toVisible: Boolean) {
        val repeatPasswordAnimator =
            getRepeatPasswordTextInputOnVisibilityChangeAnimation(toVisible)
        val loginButtonAnimator =
            getLoginButtonTranslationAnimationOnRepeatPasswordTextInputVisibilityChange(toVisible)

        repeatPasswordAnimator.apply {
            duration = mRepeatPasswordChangeVisibilityAnimationDuration
            interpolator = AccelerateDecelerateInterpolator()
        }
        loginButtonAnimator.apply {
            duration = mRepeatPasswordChangeVisibilityAnimationDuration
            interpolator = AccelerateDecelerateInterpolator()
        }

        repeatPasswordAnimator.start()
        loginButtonAnimator.start()
    }

    private fun getRepeatPasswordTextInputOnVisibilityChangeAnimation(
        toVisible: Boolean
    ): ViewPropertyAnimator {
        val repeatPasswordView = mBinding.fragmentLoginTextInputRepeatPasswordWrapper

        val animationStartAction = {
            repeatPasswordView.apply {
                if (toVisible) visibility = View.VISIBLE
            }
        }
        val animationEndAction = {
            repeatPasswordView.apply {
                if (!toVisible) {
                    visibility = View.GONE
                    scaleY = 0f
                    alpha = 0f
                } else {
                    scaleY = 1f
                    alpha = 1f
                }
            }
        }
        val animatorListener = object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) { animationStartAction() }
            override fun onAnimationEnd(animation: Animator) { animationEndAction() }
            override fun onAnimationCancel(animation: Animator) {
                animationStartAction()
                animationEndAction()
            }
        }

        return repeatPasswordView.animate().apply {
            alpha(if (toVisible) 1f else 0f)
            scaleY(if (toVisible) 1f else 0f)

        }.setListener(animatorListener)
    }

    private fun getLoginButtonTranslationAnimationOnRepeatPasswordTextInputVisibilityChange(
        repeatPasswordViewToVisible: Boolean
    ): ViewPropertyAnimator {
        val loginButtonView = mBinding.fragmentLoginButtonLogin
        val rawLoginButtonTranslationY = mRepeatPasswordViewHeight + mControlComponentGap
        val loginButtonTranslationY =
            if (repeatPasswordViewToVisible) 0
            else -rawLoginButtonTranslationY

        loginButtonView.apply {
            if (repeatPasswordViewToVisible)
                translationY = -(rawLoginButtonTranslationY.toFloat())
        }

        val animationEndAction = { loginButtonView.translationY = 0f }
        val animatorListener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) { animationEndAction() }
            override fun onAnimationCancel(animation: Animator) { animationEndAction() }
        }

        return loginButtonView.animate().apply {
            translationY(loginButtonTranslationY.toFloat())

        }.setListener(animatorListener)
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