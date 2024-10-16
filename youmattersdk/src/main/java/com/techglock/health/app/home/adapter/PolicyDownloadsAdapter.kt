package com.techglock.health.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemPolicyDownloadsBinding
import com.techglock.health.app.home.common.DataHandler.SudPolicyDownloadModel

class PolicyDownloadsAdapter(
    val context: Context,
    private val listener: OnDownloadClickListener
) : RecyclerView.Adapter<PolicyDownloadsAdapter.PolicyDownloadsViewHolder>() {


    private val sudPolicyList: MutableList<SudPolicyDownloadModel> = mutableListOf()

    override fun getItemCount(): Int = sudPolicyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolicyDownloadsViewHolder =
        PolicyDownloadsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_policy_downloads, parent, false)
        )

    override fun onBindViewHolder(holder: PolicyDownloadsViewHolder, position: Int) {
        val policy = sudPolicyList[position]
        holder.txtPolicyDownloadTitle.text = policy.title
        holder.txtPolicyDownloadDesc.text = policy.desc

        holder.layoutPolicy.setOnClickListener {
            listener.onDownloadClick(policy)
        }
    }

    fun updateList(items: MutableList<SudPolicyDownloadModel>) {
        sudPolicyList.clear()
        sudPolicyList.addAll(items)
        notifyDataSetChanged()
    }

    inner class PolicyDownloadsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPolicyDownloadsBinding.bind(view)
        val layoutPolicy = binding.layoutPolicyDownloads
        val txtPolicyDownloadTitle = binding.txtPolicyDownloadTitle
        val txtPolicyDownloadDesc = binding.txtPolicyDownloadDesc
    }

    interface OnDownloadClickListener {
        fun onDownloadClick(policy: SudPolicyDownloadModel)
    }

}