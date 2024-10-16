package com.techglock.health.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemSmitFitBinding
import com.techglock.health.app.home.common.DataHandler

class SmitFitAdapter(
    private val mContext: Context,
    private val listener: OnSmitFitFeatureListener
) : RecyclerView.Adapter<SmitFitAdapter.SmitFitViewHolder>() {

    private var featuresList: MutableList<DataHandler.SmitFitModel> = mutableListOf()

    override fun getItemCount(): Int = featuresList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SmitFitAdapter.SmitFitViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_smit_fit, parent, false)
        return SmitFitViewHolder(v)
    }

    override fun onBindViewHolder(holder: SmitFitAdapter.SmitFitViewHolder, position: Int) {
        val feature = featuresList[position]
        holder.imgFeature.setImageResource(feature.imgId)
        holder.txtFeature.text = feature.featureTitle
        holder.txtFeature.setTextColor(ContextCompat.getColor(mContext, feature.color))

        holder.layoutSmitFit.setOnClickListener {
            listener.onSmitFitFeatureClick(feature)
        }
    }

    fun updateList(list: List<DataHandler.SmitFitModel>) {
        this.featuresList.clear()
        this.featuresList.addAll(list)
        this.notifyDataSetChanged()
    }

    interface OnSmitFitFeatureListener {
        fun onSmitFitFeatureClick(smitFitModel: DataHandler.SmitFitModel)
    }

    inner class SmitFitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSmitFitBinding.bind(view)
        var layoutSmitFit = binding.layoutSmitFit
        var imgFeature = binding.imgFeature
        var txtFeature = binding.txtFeature
    }

}