package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemFundDetailsBinding
import com.techglock.health.app.model.sudLifePolicy.SudFundDetailsModel

class SudFundDetailsAdapter(val context: Context) :
    RecyclerView.Adapter<SudFundDetailsAdapter.SudFundDetailsViewHolder>() {

    private val sudFundDetailList: MutableList<SudFundDetailsModel.FundDetail> = mutableListOf()

    override fun getItemCount(): Int = sudFundDetailList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SudFundDetailsViewHolder =
        SudFundDetailsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_fund_details, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SudFundDetailsViewHolder, position: Int) {
        val fundDetail = sudFundDetailList[position]
        try {
            holder.txtFundName.text = fundDetail.fundName
            holder.txtTotalFundValue.text =
                "${context.resources.getString(R.string.INDIAN_RUPEE)} ${
                    Utilities.formatNumberDecimalWithComma(
                        Utilities.roundOffPrecisionToTwoDecimalPlaces(
                            fundDetail.fundValue!!
                        )
                    )
                }"
            holder.txtUnitsAllocated.text = Utilities.formatNumberDecimalWithComma(
                Utilities.roundOffPrecisionToTwoDecimalPlaces(fundDetail.unitsAllocated!!)
            )
            holder.txtNav.text =
                Utilities.roundOffPrecision(fundDetail.nav!!.toDouble(), 2).toString()
            holder.txtNavDate.text =
                DateHelper.formatDateValueInReadableFormat(fundDetail.navDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*        if ( sudFundDetailList.size > 0 && position == sudFundDetailList.size-1 ) {
                    holder.view.visibility = View.GONE
                } else {
                    holder.view.visibility = View.VISIBLE
                }*/
    }

    fun updateList(items: List<SudFundDetailsModel.FundDetail>) {
        sudFundDetailList.clear()
        sudFundDetailList.addAll(items)
        notifyDataSetChanged()
    }

    inner class SudFundDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemFundDetailsBinding.bind(view)
        val txtFundName = binding.txtFundName
        val txtTotalFundValue = binding.txtTotalFundValue
        val txtUnitsAllocated = binding.txtUnitsAllocated
        val txtNav = binding.txtNav
        val txtNavDate = binding.txtNavDate
        //val view = binding.view2
    }
}