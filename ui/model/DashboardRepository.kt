package com.vozdelpueblo.coac.ui.dashboard.model

import com.vozdelpueblo.coac.data.response.PerformanceDTO
import com.vozdelpueblo.coac.retrofit.RetrofitAdapter
import com.vozdelpueblo.coac.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardRepository {

    fun getAllPerformances(callback : Callback<List<PerformanceDTO>>) {
        val retrofit = RetrofitAdapter().adapter
        val service = retrofit.create(RetrofitService::class.java)
        val call = service.actuaciones
        call.enqueue(object : Callback<List<PerformanceDTO>> {
            override fun onResponse(call: Call<List<PerformanceDTO>>, response: Response<List<PerformanceDTO>>) {
                callback.onResponse(call, response)
            }

            override fun onFailure(call: Call<List<PerformanceDTO>>, t: Throwable) {
                callback.onFailure(call, t)
            }
        })
    }
}