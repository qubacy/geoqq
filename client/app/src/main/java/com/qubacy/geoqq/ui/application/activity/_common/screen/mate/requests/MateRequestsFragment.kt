package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment

class MateRequestsFragment : BaseFragment<FragmentMateRequestsBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMateRequestsBinding {
        return FragmentMateRequestsBinding.inflate(inflater, container, false)
    }

}