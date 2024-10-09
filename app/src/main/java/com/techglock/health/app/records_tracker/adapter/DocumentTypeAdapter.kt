package com.techglock.health.app.records_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemDocTypeBinding
import com.techglock.health.app.records_tracker.model.DocumentType
import com.techglock.health.app.records_tracker.ui.DocumentTypeFragment

class DocumentTypeAdapter(
    val fragment: DocumentTypeFragment,
    val context: Context,
    private val documentTypeList: List<DocumentType>
) : RecyclerView.Adapter<DocumentTypeAdapter.DocumentTypeViewHolder>() {

    private var selectedPos = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DocumentTypeAdapter.DocumentTypeViewHolder =
        DocumentTypeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_doc_type, parent, false)
        )

    override fun getItemCount(): Int = documentTypeList.size

    @SuppressLint("ClickableViewAccessibility,RecyclerView")
    override fun onBindViewHolder(holder: DocumentTypeViewHolder, position: Int) {
        val docType = documentTypeList[position]

        holder.imgOption.setImageResource(docType.imageId)
        holder.txtOption.text = docType.title

        if (selectedPos == -1) {
            holder.layoutOption.background =
                ContextCompat.getDrawable(context, R.drawable.bg_transparant)
        } else {
            if (selectedPos == position) {
                holder.layoutOption.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_selected)
                holder.imgSelect.visibility = View.VISIBLE
                //holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
            } else {
                holder.layoutOption.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_transparant)
                holder.imgSelect.visibility = View.INVISIBLE
                //holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
            }
        }

        holder.itemView.setOnClickListener {
            fragment.setDocTypeCode(docType.code)
            notifyItemChanged(selectedPos)
            selectedPos = position
            //notifyItemChanged(selectedPos)
            notifyDataSetChanged()
        }

    }

    inner class DocumentTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemDocTypeBinding.bind(view)
        val layoutOption = binding.layoutOption
        val imgSelect = binding.imgSelect
        val imgOption = binding.imgOption
        val txtOption = binding.txtOption
    }
}