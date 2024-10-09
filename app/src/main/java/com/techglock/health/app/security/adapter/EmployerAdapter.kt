package com.techglock.health.app.security.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemBankNameBinding
import com.techglock.health.app.security.model.EmployerModel

class EmployerAdapter(
    val context: Context,
    val employerSelectionListener: EmployerSelectionListener
) : RecyclerView.Adapter<EmployerAdapter.BankViewHolder>() {

    private val bankList: MutableList<EmployerModel> = mutableListOf()

    override fun getItemCount(): Int = bankList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder =
        BankViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_bank_name, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        val bank = bankList[position]

        holder.txtBank.text = bank.employertitle
        holder.layoutBank.setOnClickListener {
            employerSelectionListener.onEmployerSelect(bank)
        }
    }

    fun updateList(items: List<EmployerModel>) {
        bankList.clear()
        bankList.addAll(items)
        notifyDataSetChanged()
    }

    interface EmployerSelectionListener {
        fun onEmployerSelect(employer: EmployerModel)
    }

    inner class BankViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBankNameBinding.bind(view)
        val layoutBank = binding.layoutBank
        val txtBank = binding.txtBank
    }

}