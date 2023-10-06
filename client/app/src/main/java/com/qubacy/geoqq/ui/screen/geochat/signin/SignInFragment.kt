package com.qubacy.geoqq.ui.screen.geochat.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.databinding.FragmentSignInBinding
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.geochat.signin.model.SignInViewModel
import com.qubacy.geoqq.ui.screen.geochat.signin.model.SignInViewModelFactory

class SignInFragment : WaitingFragment() {
    override val mModel: SignInViewModel by viewModels {
        SignInViewModelFactory()
    }

    private lateinit var mBinding: FragmentSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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

        setStartAnimation() // todo: think of this! it isn't working OK all the time.
    }

    override fun handleError(error: Error) {
        TODO("Not yet implemented")


    }

    private fun onSignInButtonClicked() {
        if (areInputsEmpty()) {
            showMessage(R.string.error_sign_in_data_not_full)

            return
        }

//        handleWaitingStart() // there's no reason to do it manually. the model should change isWaiting
                               // value that has to lead to calling the method;

        // todo: passing the data to the model..
    }

    private fun areInputsEmpty(): Boolean {
        val login = mBinding.loginInput.input.text.toString()
        val password = mBinding.passwordInput.input.text.toString()

        return (login.isEmpty() || password.isEmpty())
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
}