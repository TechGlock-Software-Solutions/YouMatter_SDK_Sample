package com.techglock.health.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemDashboardPolicyBinding
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel.KYP

class SudPolicyListAdapter(
    val context: Context,
    private val listener: OnPolicyClickListener
) : RecyclerView.Adapter<SudPolicyListAdapter.SudPolicyListViewHolder>() {

    //private val sudPolicyList: MutableList<PolicyDetails> = mutableListOf()
    private val sudPolicyList: MutableList<KYP> = mutableListOf()

    override fun getItemCount(): Int = sudPolicyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SudPolicyListViewHolder =
        SudPolicyListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dashboard_policy, parent, false)
        )

    override fun onBindViewHolder(holder: SudPolicyListViewHolder, position: Int) {
        val policy = sudPolicyList[position]
        try {
            holder.txtPolicyHolderName.text =
                Utilities.convertStringToPascalCase(policy.dear!!).replace(".", "")
            holder.txtPolicyName.text = policy.contractTypeDesc
            holder.txtPolicyNumber.text = policy.policyNumber
            //holder.txtNextRenewal.text = policy.nextRenewalDue
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.layoutPolicy.setOnClickListener {
            listener.onPolicyClick(policy)
        }
    }

    //fun updateList(items: List<PolicyDetails>) {
    fun updateList(items: List<KYP>) {
        sudPolicyList.clear()
        sudPolicyList.addAll(items)
        notifyDataSetChanged()
    }

    fun addPolicy(item: KYP) {
        sudPolicyList.add(item)
        //notifyItemInserted(sudPolicyList.size)
    }

    inner class SudPolicyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemDashboardPolicyBinding.bind(view)
        val layoutPolicy = binding.layoutPolicy
        val txtPolicyHolderName = binding.txtPolicyHolderName
        val txtPolicyName = binding.txtPolicyName
        val txtPolicyNumber = binding.txtPolicyNumber
        val txtNextRenewal = binding.txtNextRenewal
    }

    interface OnPolicyClickListener {
        //fun onPolicyClick(policyDetails:PolicyDetails)
        fun onPolicyClick(kypDetails: KYP)
    }

}