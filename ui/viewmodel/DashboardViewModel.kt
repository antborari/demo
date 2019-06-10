package com.vozdelpueblo.coac.ui.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vozdelpueblo.coac.data.response.PerformanceDTO
import com.vozdelpueblo.coac.ui.dashboard.model.DashboardRepository
import com.vozdelpueblo.coac.ui.dashboard.view.DashboardFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.database.*
import com.vozdelpueblo.coac.ui.dashboard.interfaces.OnCompleteLikeItem

class DashboardViewModel : ViewModel() {

    var repository: DashboardRepository = DashboardRepository()
    val actuaciones: MutableLiveData<List<PerformanceDTO>> by lazy {
        MutableLiveData<List<PerformanceDTO>>()
    }
    val dates: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    fun getPerformancesByRetrofit(view: DashboardFragment) {
        view.setLoading(true)
        repository.getAllActuaciones(object : Callback<List<PerformanceDTO>> {
            override fun onResponse(call: Call<List<PerformanceDTO>>, response: Response<List<PerformanceDTO>>) {
                if (response.body() != null) {
                    actuaciones.value = response.body()
                }
                view.setLoading(false)
            }

            override fun onFailure(call: Call<List<PerformanceDTO>>, t: Throwable) {
                actuaciones.value = arrayListOf()
                view.setLoading(false)
            }
        })
    }

    fun getPerformancesByFirebase(): LiveData<List<PerformanceDTO>> {
        if (actuaciones.value == null) {
            FirebaseDatabase.getInstance()
                    .getReference().child("actuaciones")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                actuaciones.postValue(toPerformances(dataSnapshot))
                            }
                        }
                    })
        }
        return actuaciones
    }

    fun getDates(): LiveData<List<String>> {
        if (dates.value == null) {
            FirebaseDatabase.getInstance()
                    .getReference().child("fechas")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                dates.postValue(toDates(dataSnapshot))
                            }
                        }
                    })
        }
        return dates
    }

    fun incrementLike(performance: PerformanceDTO, listenerComplete: OnCompleteLikeItem) {
        val postRef: DatabaseReference = FirebaseDatabase.getInstance()
                .getReference().child("actuaciones/" + performance.groupId + "/votes")
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val votes = mutableData.getValue(Int::class.java)
                if (votes == null) {
                    mutableData.value = 1
                } else {
                    mutableData.value = votes + 1
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, success: Boolean, dataSnapshot: DataSnapshot?) {
                if (success) {
                    listenerComplete.OnCompleteLikeItem(performance.groupId)
                }
            }
        })
    }

    private fun toPerformances(dataSnapshot: DataSnapshot): List<PerformanceDTO> {
        return dataSnapshot.children.map { children ->
            children.getValue(PerformanceDTO::class.java)!!.setIdGrupo(children.key!!)
        }
    }

    private fun toDates(dataSnapshot: DataSnapshot): List<String> {
        return dataSnapshot.children.map { children -> children.getValue(String::class.java)!! }
    }
}