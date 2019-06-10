package com.vozdelpueblo.coac.ui.dashboard.view

import androidx.fragment.app.Fragment
import com.vozdelpueblo.coac.ui.base.BaseActivity

class DashboardActivity : BaseActivity() {

    override fun initialize(): Fragment {
        return DashboardFragment.newInstance()
    }

}