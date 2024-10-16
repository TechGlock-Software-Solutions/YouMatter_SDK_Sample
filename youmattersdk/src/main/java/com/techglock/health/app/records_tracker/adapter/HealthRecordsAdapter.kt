package com.techglock.health.app.records_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemDocumentBinding
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.records_tracker.common.DataHandler
import com.techglock.health.app.records_tracker.common.RecordSingleton
import com.techglock.health.app.records_tracker.ui.OptionsBottomSheet
import com.techglock.health.app.records_tracker.ui.ViewRecordsFragment
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel
import java.io.File

class HealthRecordsAdapter(
    val context: Context,
    val viewModel: HealthRecordsViewModel,
    private val fragment: ViewRecordsFragment,
    val from: String
) : RecyclerView.Adapter<HealthRecordsAdapter.HealthRecordsAdapterViewHolder>() {

    private var recordList: MutableList<HealthDocument> = mutableListOf()
    var categoryCode = "ALL"
    var color = intArrayOf(
        R.color.vivant_bright_sky_blue,
        R.color.vivant_watermelon,
        R.color.vivant_orange_yellow,
        R.color.vivant_bright_blue,
        R.color.vivant_soft_pink,
        R.color.vivant_nasty_green,
        R.color.vivant_dusky_blue
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HealthRecordsAdapterViewHolder =
        HealthRecordsAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        )

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: HealthRecordsAdapterViewHolder, position: Int) {
        val record = recordList[position]

        try {
            val code = record.Code!!
            if (!Utilities.isNullOrEmpty(code)) {
                holder.txtDocCategory.text = DataHandler(context).getCategoryByCode(code)
                //holder.imgCategory.setImageResource(DataHandler(context).getCategoryImageByCode(code))
                holder.imgCategory.setImageResource(Utilities.getCategoryImageByCode(code))

                /*                if ( code.equals("LAB",ignoreCase = true) ) {
                                    holder.txtDigitize.visibility = View.VISIBLE
                                } else {
                                    holder.txtDigitize.visibility = View.GONE
                                }*/
            }
            holder.txtDocName.text = record.Title
            holder.txtPersonName.text = record.PersonName
            if (!record.Comment.isNullOrEmpty()) {
                holder.txtNote.visibility = View.VISIBLE
                holder.txtNote.text =
                    context.resources.getString(R.string.NOTE) + " : " + record.Comment
            } else {
                holder.txtNote.visibility = View.GONE
            }
            if (!Utilities.isNullOrEmpty(record.RecordDate)) {
                holder.txtDocDate.text = DateHelper.convertDateTimeValue(
                    record.RecordDate,
                    DateHelper.SERVER_DATE_YYYYMMDD,
                    DateHelper.DATEFORMAT_DDMMMYYYY_NEW
                )
            } else {
                holder.txtDocDate.text = " -- "
            }

            holder.itemView.setOnClickListener {
                fragment.performAction(Constants.VIEW, record)
            }

            holder.imgMore.setOnClickListener {
                val bottomSheet = OptionsBottomSheet(fragment, record)
                bottomSheet.show(fragment.childFragmentManager, OptionsBottomSheet.TAG)
            }

            holder.txtDigitize.setOnClickListener {
                digitizeRecord(record)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateList(list: MutableList<HealthDocument>, code: String) {
        recordList.clear()
        recordList.addAll(list)
        categoryCode = code
        notifyDataSetChanged()
    }

    fun toggleList() {
        recordList.reverse()
        notifyDataSetChanged()
    }

    private fun digitizeRecord(record: HealthDocument) {
        val file = File(record.Path, record.Name!!)
        if (file.exists()) {
            viewModel.callDigitizeDocumentApi(from, categoryCode, record)
        } else {
            RecordSingleton.getInstance()!!.setHealthRecord(record)
            viewModel.callDownloadRecordApi(Constants.DIGITIZE, record)
        }
    }

    class HealthRecordsAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemDocumentBinding.bind(view)
        val imgMore = binding.imgMore
        val imgCategory = binding.imgCategory
        val txtDocCategory = binding.txtDocCategory
        val txtNote = binding.txtNote
        val txtDocName = binding.txtDocName
        val txtPersonName = binding.txtPersonName
        val txtDocDate = binding.txtDocDate
        val txtDigitize = binding.txtDigitize
    }

}