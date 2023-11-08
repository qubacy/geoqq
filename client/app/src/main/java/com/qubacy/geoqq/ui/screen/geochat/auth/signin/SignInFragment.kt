package com.qubacy.geoqq.ui.screen.geochat.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.qubacy.geoqq.R
import com.qubacy.geoqq.applicaion.Application
import com.qubacy.geoqq.applicaion.container.signin.SignInContainer
import com.qubacy.geoqq.databinding.FragmentSignInBinding
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.operation.PassSignInUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.state.SignInUiState

class SignInFragment : WaitingFragment() {
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

        val application = (requireActivity().application as Application)

        application.appContainer.initSignInContainer(
            application.appContainer.errorDataRepository,
            application.appContainer.tokenDataRepository,
            application.appContainer.signInDataRepository,
        )

        mModel = application.appContainer.signInContainer!!
            .signInViewModelFactory.create(SignInViewModel::class.java)
    }

    override fun onStop() {
        (requireActivity().application as Application).appContainer.clearSignInContainer()

        super.onStop()
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

        (mModel as SignInViewModel).signInUiStateFlow.value?.let {
            onUiStateGotten(it)
        }
        (mModel as SignInViewModel).signInUiStateFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onUiStateGotten(it)
        }

        view.doOnPreDraw {
            setStartAnimation() // todo: think of this! it isn't working OK all the time.

            (mModel as SignInViewModel).signIn()
        }
    }

    private fun onUiStateGotten(uiState: SignInUiState) {
        while (true) {
            val uiOperation = uiState.takeUiOperation() ?: break

            processUiOperation(uiOperation)
        }
    }

    private fun processUiOperation(uiOperation: UiOperation) {
        when (uiOperation::class.java) {
            PassSignInUiOperation::class.java -> {
                val passSignInUiOperation = uiOperation as PassSignInUiOperation

                processPassSignInUiOperation(passSignInUiOperation)
            }
            ShowErrorUiOperation::class.java -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    private fun processPassSignInUiOperation(passSignInUiOperation: PassSignInUiOperation) {
        moveToMainMenu()
    }

    private fun moveToMainMenu() {
        findNavController().navigate(R.id.action_signInFragment_to_mainMenuFragment)
    }

    private fun onSignUpButtonClicked() {
        findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
    }

    private fun onSignInButtonClicked() {
        val login = mBinding.loginInput.input.text.toString()
        val password = mBinding.passwordInput.input.text.toString()

        closeSoftKeyboard()

        if (!(mModel as SignInViewModel).isSignInDataCorrect(login, password)) {
            showMessage(R.string.error_sign_in_data_incorrect)

            return
        }

        (mModel as SignInViewModel).signIn(login, password)
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

        (mModel as SignInViewModel).interruptSignIn()
    }
}