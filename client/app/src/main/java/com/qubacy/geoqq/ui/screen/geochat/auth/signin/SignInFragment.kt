package com.qubacy.geoqq.ui.screen.geochat.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentSignInBinding
import com.qubacy.geoqq.ui.screen.geochat.auth.common.AuthFragment
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.state.SignInUiState
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModelFactory

class SignInFragment : AuthFragment() {
    override val mModel: SignInViewModel by viewModels {
        SignInViewModelFactory()
    }

    private lateinit var mBinding: FragmentSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionWindowBackgroundColorResId(R.color.green_dark)

        exitTransition = MaterialElevationScale(false).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSignInBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.signInButton.setOnClickListener { onSignInButtonClicked() }
        mBinding.signUpButton.setOnClickListener { onSignUpButtonClicked() }

        mModel.signInUiState.observe(viewLifecycleOwner) {
            onSignInUiStateChanged(it)
        }

        setStartAnimation() // todo: think of this! it isn't working OK all the time.
    }

    private fun onSignInUiStateChanged(signInUiState: SignInUiState) {
        if (checkUiStateForErrors(signInUiState)) return

        if (signInUiState.isSignedIn) {
            // todo: moving to the MainMenu fragment..

        }
    }

    private fun onSignUpButtonClicked() {
        findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
    }

    private fun onSignInButtonClicked() {
        val login = mBinding.loginInput.input.text.toString()
        val password = mBinding.passwordInput.input.text.toString()

        if (!mModel.isSignInDataCorrect(login, password)) {
            showMessage(R.string.error_sign_up_data_incorrect)

            return
        }

        mModel.signIn(login, password)
    }

    private fun setStartAnimation() {
        val formAnim = AnimationUtils.loadAnimation(
            context, R.anim.sign_in_background_start_animation)
        val formContentAnim = AnimationUtils.loadAnimation(
            context, R.anim.sign_in_form_content_start_animation)
        val signUpAnim = AnimationUtils.loadAnimation(
            context, R.anim.sign_in_sign_up_start_animation)

        mBinding.root.doOnPreDraw {
            mBinding.form.startAnimation(formAnim)
            mBinding.formContent.startAnimation(formContentAnim)
            mBinding.signUpButton.startAnimation(signUpAnim)
        }
    }

    override fun handleWaitingAbort() {
        super.handleWaitingAbort()

        mModel.interruptSignIn()
    }
}