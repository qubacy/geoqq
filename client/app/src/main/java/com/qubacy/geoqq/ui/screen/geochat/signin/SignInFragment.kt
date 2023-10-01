package com.qubacy.geoqq.ui.screen.geochat.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
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

    private fun onSignInButtonClicked() {
        if (areInputsEmpty()) {
            Snackbar.make(
                requireContext(),
                requireView(),
                getString(R.string.error_sign_in_data_not_full),
                Snackbar.LENGTH_LONG).show()

            return
        }

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