package com.qubacy.geoqq.ui.screen.geochat.auth.signup

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentSignUpBinding
import com.qubacy.geoqq.ui.screen.geochat.auth.common.AuthFragment
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.state.SignUpUiState
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModelFactory

class SignUpFragment : AuthFragment() {
    override val mModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory()
    }

    private lateinit var mBinding: FragmentSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionWindowBackgroundColorResId(R.color.green_dark)

        enterTransition = Slide(Gravity.END).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
        returnTransition = Slide(Gravity.END).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()

            addListener(getExitTransitionListener())
        }
    }

    private fun getExitTransitionListener(): Transition.TransitionListener {
        return object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) {
                super.onTransitionStart(transition)

                mBinding.signUpContainer.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSignUpBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.signUpButton.setOnClickListener { onSignUpButtonClicked() }

        mModel.signUpUiState.observe(viewLifecycleOwner) {
            onSignUpUiStateChanged(it)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun onSignUpUiStateChanged(signUpUiState: SignUpUiState) {
        if (checkUiStateForErrors(signUpUiState)) return

        if (signUpUiState.isSignedUp) {
            // todo: moving to the MainMenu fragment..

        }
    }

    private fun onSignUpButtonClicked() {
        val login = mBinding.loginInput.input.text.toString()
        val password = mBinding.passwordInput.input.text.toString()
        val repeatedPassword = mBinding.passwordConfirmationInput.input.text.toString()

        if (!mModel.isSignUpDataCorrect(login, password, repeatedPassword)) {
            showMessage(R.string.error_sign_up_data_incorrect)

            return
        }

        mModel.signUp(login, password, repeatedPassword)
    }

    override fun handleWaitingAbort() {
        super.handleWaitingAbort()

        mModel.interruptSignUp()
    }
}