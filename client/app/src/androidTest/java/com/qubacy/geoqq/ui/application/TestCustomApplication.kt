package com.qubacy.geoqq.ui.application

import com.qubacy.geoqq.ui._di.component.CustomApplicationComponent
import com.qubacy.geoqq.ui._di.component.DaggerTestCustomApplicationComponent

class TestCustomApplication : CustomApplication() {
    override fun initApplicationComponent(): CustomApplicationComponent {
        return DaggerTestCustomApplicationComponent.create()
    }
}