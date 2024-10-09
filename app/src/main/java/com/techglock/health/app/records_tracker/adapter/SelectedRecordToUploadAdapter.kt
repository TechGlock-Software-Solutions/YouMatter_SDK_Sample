package com.techglock.health.app.records_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemSelectedRecordBinding
import com.techglock.health.app.model.entity.RecordInSession
import com.techglock.health.app.records_tracker.ui.SelectRelationFragment
import com.techglock.health.app.records_tracker.views.ShrBinding.setRecordInSessionImg

class SelectedRecordToUploadAdapter(val fragment: SelectRelationFragment, val context: Context) :
    RecyclerView.Adapter<SelectedRecordToUploadAdapter.SelectedRecordToUploadViewHolder>() {

    var documentList: MutableList<RecordInSession> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedRecordToUploadViewHolder =
        SelectedRecordToUploadViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_selected_record, parent, false)
        )

    override fun getItemCount(): Int = documentList.size

    override fun onBindViewHolder(holder: SelectedRecordToUploadViewHolder, position: Int) {

        val recordInSession = documentList[position]

        holder.imgCancel.tag = position
        holder.imgSelectedDoc.setRecordInSessionImg(recordInSession)

        holder.imgCancel.setOnClickListener {
            try {
                if (!documentList.isNullOrEmpty() && documentList.size >= position) {
                    fragment.deleteRecordInSession(recordInSession)
                    Utilities.printLogError("Removed_Item_At--->$position")
                    documentList.removeAt(position)
                    notifyDataSetChanged()
                }
                if (!documentList.isNullOrEmpty()) {
                    fragment.setNoDataView(false)
                } else {
                    fragment.setNoDataView(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateData(list: MutableList<RecordInSession>) {
        this.documentList.clear()
        this.documentList.addAll(list)
        Utilities.printData("RecordToUploadList", list)
        this.notifyDataSetChanged()
    }

    /*    private fun removeItem(position: Int) {
            Utilities.printLogError("Removed_Item_At--->$position")
            this.documentList.removeAt(position)
            this.notifyItemRemoved(position)
            this.notifyDataSetChanged()
        }*/

    inner class SelectedRecordToUploadViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemSelectedRecordBinding.bind(view)
        val imgCancel = binding.imgCancelDoc
        val imgSelectedDoc = binding.imgSelectedDoc
    }
}