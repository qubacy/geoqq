package com.qubacy.geoqq.ui.screen.geochat.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private lateinit var mBinding: FragmentSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private fun onSignUpButtonClicked() {
        if (areInputsEmpty()) {
            Snackbar.make(
                requireContext(),
                requireView(),
                getString(R.string.error_sign_up_data_not_full),
                Snackbar.LENGTH_LONG).show()

            return
        }

        // todo: conveying data to the model..
    }

    private fun areInputsEmpty(): Boolean {
        val login = mBinding.loginInput.input.text.toString()
        val password = mBinding.passwordInput.input.text.toString()
        val repeatedPassword = mBinding.passwordConfirmationInput.input.text.toString()

        return (login.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty())
    }
}