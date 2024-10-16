package com.techglock.health.app.records_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemUploadRecordBinding
import com.techglock.health.app.model.entity.RecordInSession
import com.techglock.health.app.records_tracker.ui.UploadRecordFragment
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel
import com.techglock.health.app.records_tracker.views.ShrBinding.setRecordInSessionImg

class UploadRecordAdapter(
    val fragment: UploadRecordFragment, val context: Context,
    val viewModel: HealthRecordsViewModel
) : RecyclerView.Adapter<UploadRecordAdapter.UploadRecordViewHolder>() {

    internal var uploadRecordList: MutableList<RecordInSession> = mutableListOf()
    var listener: ShowNoDataListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadRecordViewHolder =
        UploadRecordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_upload_record, parent, false)
        )

    override fun getItemCount(): Int = uploadRecordList.size

    override fun onBindViewHolder(holder: UploadRecordViewHolder, position: Int) {
        val recordInSession = uploadRecordList[position]

        holder.imgCancelDoc.tag = position
        holder.imgSelectedDoc.setRecordInSessionImg(recordInSession)

        holder.imgCancelDoc.setOnClickListener {
            try {
                if (uploadRecordList.size != 0 && uploadRecordList.size >= position) {
                    removeItem(position)
                    viewModel.deleteRecordInSession(recordInSession)
                }
                if (uploadRecordList.size > 0) {
                    fragment.setListVisibility(true)
                } else {
                    fragment.setListVisibility(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateList(list: MutableList<RecordInSession>) {
        Utilities.printData("UploadDataList", list)
        this.uploadRecordList.clear()
        this.uploadRecordList.addAll(list)
        this.notifyDataSetChanged()
    }

    fun insertItem(item: RecordInSession, position: Int) {
        Utilities.printLogError("Inserted_Item_At--->$position")
        this.uploadRecordList.add(item)
        this.notifyItemInserted(position)
    }

    private fun removeItem(position: Int) {
        Utilities.printLogError("Removed_Item_At--->$position")
        this.uploadRecordList.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyDataSetChanged()
    }

    inner class UploadRecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemUploadRecordBinding.bind(view)
        val imgCancelDoc = binding.imgCancelDoc
        val imgSelectedDoc = binding.imgSelectedDoc
    }

    interface ShowNoDataListener {
        fun showNoData(show: Boolean)
    }
}