package com.qubacy.geoqq.ui.application.activity._common.screen.start

import android.view.LayoutInflater
import android.view.ViewGroup
import com.qubacy.geoqq.databinding.FragmentStartBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment

class StartFragment : BaseFragment<FragmentStartBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStartBinding {
        return FragmentStartBinding.inflate(inflater, container, false)
    }
}