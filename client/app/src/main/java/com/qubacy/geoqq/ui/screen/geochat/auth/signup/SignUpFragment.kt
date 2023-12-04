package com.qubacy.geoqq.ui.screen.geochat.auth.signup

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import com.google.android.material.transition.MaterialElevationScale
import com.qubacy.geoqq.R
import com.qubacy.geoqq.applicaion.common.Application
import com.qubacy.geoqq.databinding.FragmentSignUpBinding
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.operation.PassSignUpUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.state.SignUpUiState

class SignUpFragment(

) : WaitingFragment() {
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

        exitTransition = MaterialElevationScale(false).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
    }

    override fun initFlowContainerIfNull() {
        val application = (requireActivity().application as Application)

        if (application.appContainer.signUpContainer != null) return

        application.appContainer.initSignUpContainer(
            application.appContainer.errorDataRepository,
            application.appContainer.tokenDataRepository,
            application.appContainer.signUpDataRepository
        )

        mModel = application.appContainer.signUpContainer!!
            .signUpViewModelFactory.create(SignUpViewModel::class.java)
    }

    override fun clearFlowContainer() {
        (requireActivity().application as Application).appContainer.clearSignUpContainer()
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

        (mModel as SignUpViewModel).signUpUiStateFlow.value?.let {
            onUiStateGotten(it)
        }
        (mModel as SignUpViewModel).signUpUiStateFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onUiStateGotten(it)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun onUiStateGotten(uiState: SignUpUiState) {
        while (true) {
            val uiOperation = uiState.takeUiOperation() ?: break

            processUiOperation(uiOperation)
        }
    }

    private fun processUiOperation(uiOperation: UiOperation) {
        when (uiOperation::class) {
            PassSignUpUiOperation::class -> {
                val passSignUpUiOperation = uiOperation as PassSignUpUiOperation

                processPassSignUpUiOperation(passSignUpUiOperation)
            }
            ShowErrorUiOperation::class -> {
                val errorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(errorUiOperation.error)
            }
        }
    }

    private fun processPassSignUpUiOperation(passSignUpUiOperation: PassSignUpUiOperation) {
        moveToMainMenu()
    }

    private fun moveToMainMenu() {
        findNavController().navigate(R.id.action_signUpFragment_to_mainMenuFragment)
    }

    private fun onSignUpButtonClicked() {
        val login = mBinding.loginInput.input.text.toString()
        val password = mBinding.passwordInput.input.text.toString()
        val repeatedPassword = mBinding.passwordConfirmationInput.input.text.toString()

        closeSoftKeyboard()

        if (!(mModel as SignUpViewModel).isSignUpDataCorrect(login, password, repeatedPassword)) {
            showMessage(R.string.error_sign_up_data_incorrect)

            return
        }

        (mModel as SignUpViewModel).signUp(login, password, repeatedPassword)
    }

    override fun handleWaitingAbort() {
        super.handleWaitingAbort()

        (mModel as SignUpViewModel).interruptSignUp()
    }
}