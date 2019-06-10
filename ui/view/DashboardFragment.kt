package com.vozdelpueblo.coac.ui.dashboard.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.vozdelpueblo.coac.BaseApplication
import com.vozdelpueblo.coac.R
import com.vozdelpueblo.coac.data.response.PerformanceDTO
import com.vozdelpueblo.coac.ui.dashboard.adapters.DashboardAdapter
import com.vozdelpueblo.coac.ui.dashboard.adapters.FilterDatesPagerAdapter
import com.vozdelpueblo.coac.ui.dashboard.interfaces.OnClickItemDashboard
import com.vozdelpueblo.coac.ui.dashboard.interfaces.OnCompleteLikeItem
import com.vozdelpueblo.coac.ui.dashboard.interfaces.OnSnapPositionChangeListener
import com.vozdelpueblo.coac.ui.dashboard.util.OffsetItemDecoration
import com.vozdelpueblo.coac.ui.dashboard.util.SnapOnScrollListener
import com.vozdelpueblo.coac.ui.dashboard.util.attachSnapHelperWithListener
import com.vozdelpueblo.coac.ui.dashboard.viewmodel.DashboardViewModel
import com.vozdelpueblo.coac.ui.widget.AlertDialogCustom
import kotlinx.android.synthetic.main.dahsboard.*

class DashboardFragment : Fragment(),
        OnSnapPositionChangeListener,
        OnClickItemDashboard{

    private var mCurrentDate: String? = null
    private lateinit var mDashboardViewModel: DashboardViewModel
    private lateinit var adapterDashboard : DashboardAdapter
    private var recyclerViewState : Parcelable? = null

    companion object {
        fun newInstance(): DashboardFragment {
            val bundle = Bundle()
            val fragment = DashboardFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDashboardViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dahsboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewDates()
    }

    private fun initDashboardViewModel() {
        mDashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        val datesObserver = Observer<List<String>> { dates ->
            initAdapterFechas(dates)
        }
        mDashboardViewModel.getDates().observe(this, datesObserver)
        val actuacionesObserver = Observer<List<PerformanceDTO>> { actuaciones ->
            initAdapterActuaciones()
        }
        mDashboardViewModel.getPerformancesByFirebase().observe(this, actuacionesObserver)
    }

    private fun initAdapterActuaciones() {
        recyclerViewDashboard.apply {
            setHasFixedSize(true)
            adapter = DashboardAdapter(filterActuaciones(), this@DashboardFragment )
            adapterDashboard = adapter as DashboardAdapter
            recyclerViewDashboard.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }

        recyclerViewDashboard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
            }
        })
    }

    private fun initAdapterFechas(dates: List<String>) {
        if (mCurrentDate == null) {
            mCurrentDate = dates.get(0)
        }

        recyclerviewDate.apply {
            adapter = FilterDatesPagerAdapter(dates)
        }
    }

    private fun initRecyclerViewDates() {
        recyclerviewDate.apply {
            setHasFixedSize(true)
            addItemDecoration(OffsetItemDecoration(activity?.baseContext!!))
        }
        recyclerviewDate.attachSnapHelperWithListener(LinearSnapHelper(),
                SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
                this)
    }

    fun setLoading(active: Boolean) {
        if (active) {
            loadingDashboard?.setVisibility(View.VISIBLE)
        } else {
            loadingDashboard?.setVisibility(View.GONE)
        }
    }

    override fun onSnapPositionChange(position: Int) {
        mDashboardViewModel.getDates().value?.let {
            mCurrentDate = it.get(position)
            recyclerViewState = null
            initAdapterActuaciones()
        }
    }

    private fun filterActuaciones(): List<PerformanceDTO> {
        var actuaciones = listOf<PerformanceDTO>()
        mDashboardViewModel.getPerformancesByFirebase().value?.let {
            actuaciones = it.filter { actuacion -> actuacion.date.equals(mCurrentDate) }
        }
        return actuaciones
    }

    override fun onClickItemDashboardEnable(performance: PerformanceDTO, position: Int) {
        mDashboardViewModel.incrementLike(performance, object : OnCompleteLikeItem {
            override fun OnCompleteLikeItem(idGrupo: String) {
                BaseApplication.instance.writeSharedPreferences(idGrupo)
                adapterDashboard.notifyItemChanged(position)
            }
        })
    }

    override fun onClickItemDashboardDisable(performance: PerformanceDTO) {
        AlertDialogCustom.newInstance(performance).show(fragmentManager!!, AlertDialogCustom::class.java.simpleName)
    }
}