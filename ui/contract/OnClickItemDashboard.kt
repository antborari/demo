package com.vozdelpueblo.coac.ui.dashboard.interfaces

import com.vozdelpueblo.coac.data.response.PerformanceDTO

interface OnClickItemDashboard {

    fun onClickItemDashboardEnable(performance: PerformanceDTO, position: Int)

    fun onClickItemDashboardDisable(performance: PerformanceDTO)

}