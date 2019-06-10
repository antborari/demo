package com.vozdelpueblo.coac.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vozdelpueblo.coac.BaseApplication
import com.vozdelpueblo.coac.R
import com.vozdelpueblo.coac.data.response.PerformanceDTO
import com.vozdelpueblo.coac.data.utils.PATTERN_DECIMAL
import com.vozdelpueblo.coac.data.utils.VOTACION_ACTIVA
import com.vozdelpueblo.coac.ui.dashboard.interfaces.OnClickItemDashboard
import kotlinx.android.synthetic.main.item_dashboard_coac.view.*
import java.text.DecimalFormat

class DashboardAdapter(private val myDataset: List<PerformanceDTO>,
                       private val onClickItem: OnClickItemDashboard) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DashboardAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dashboard_coac, parent, false)
        return ViewHolder(view, onClickItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(myDataset.get(position), position)
    }

    override fun getItemCount() = myDataset.size

    class ViewHolder(val view: View,
                     val onClickItem: OnClickItemDashboard) : RecyclerView.ViewHolder(view) {

        val tvTitle = view.tvItemTitle
        val tvAutor = view.tvItemAutor
        val tvLocalidad = view.tvItemLocalidad
        val checkLike = view.checkLikeItem
        val tvCount = view.itemCountDashboard
        val tvType = view.tvItemTitleType

        fun bind(performance: PerformanceDTO, position: Int) {
            tvTitle.text = performance.groupName
            tvAutor.text = itemView.resources.getString(R.string.autor, performance.author)
            tvLocalidad.text = itemView.resources.getString(R.string.localidad, performance.locate)
            tvCount.text = DecimalFormat(PATTERN_DECIMAL).format(performance.votes)
            tvType.text = getType(performance)
            checkLike.isChecked = getDataKeys().contains(performance.groupId)
            itemView.setOnClickListener {
                if (performance.active == VOTACION_ACTIVA) {
                    if (!getDataKeys().contains(performance.groupId)) {
                        onClickItem.onClickItemDashboardEnable(performance, position)
                    }
                } else {
                    onClickItem.onClickItemDashboardDisable(performance)
                }
            }
        }

        fun getDataKeys(): MutableSet<String> {
            return BaseApplication.instance.getKeyListActuaciones()
        }

        fun getType(performance: PerformanceDTO): String {
            if (performance.type == 0) return BaseApplication.instance.getString(R.string.chirigota)
            else if (performance.type == 1) return BaseApplication.instance.getString(R.string.comparsa)
            else if (performance.type == 2) return BaseApplication.instance.getString(R.string.coro)
            else return BaseApplication.instance.getString(R.string.cuarteto)
        }

    }
}