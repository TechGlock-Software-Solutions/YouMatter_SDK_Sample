package com.techglock.health.app.records_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.UserSingleton
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemRelationshipRecordBinding
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.records_tracker.ui.SelectRelationFragment
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel

class SelectRelationshipAdapter(
    val fragment: SelectRelationFragment, val viewModel: HealthRecordsViewModel,
    val context: Context
) : RecyclerView.Adapter<SelectRelationshipAdapter.SelectRelationshipViewHolder>() {

    val relativesList: MutableList<UserRelatives> = mutableListOf()
    private var selectedPos = -1
    val utilities = Utilities

    init {
        //automaticallySelectedSlef()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectRelationshipViewHolder =
        SelectRelationshipViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_relationship_record, parent, false)
        )

    override fun getItemCount(): Int {
        return relativesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SelectRelationshipViewHolder, position: Int) {
        val relative = relativesList[position]

        holder.txtName.text = relative.firstName
        holder.txtRelation.text = "(" + utilities.getRelationshipByRelationshipCode(
            relative.relationshipCode,
            context
        ) + ")"

        val relativeImgId =
            Utilities.getRelativeImgIdWithGender(relative.relationshipCode, relative.gender)
        holder.imgRelation.setImageResource(relativeImgId)
        val profPicBitmap = UserSingleton.getInstance()!!.profPicBitmap
        if (relative.relationshipCode == Constants.SELF_RELATIONSHIP_CODE && profPicBitmap != null) {
            holder.imgRelation.setImageBitmap(profPicBitmap)
        }

        if (selectedPos == -1) {
            holder.layoutRelative.background =
                ContextCompat.getDrawable(context, R.drawable.btn_border_disabled)
        } else {
            if (selectedPos == position) {
                holder.layoutRelative.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_selected)
                holder.imgSelect.visibility = View.VISIBLE
                //holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
            } else {
                holder.layoutRelative.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_border_disabled)
                holder.imgSelect.visibility = View.INVISIBLE
                //holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
            }
        }

    }

    fun updateRelativesList(items: List<UserRelatives>) {
        relativesList.clear()
        relativesList.addAll(items)
        automaticallySelectedSlef()
        this.notifyDataSetChanged()
        //fragment.stopShimmer()
    }

    private fun automaticallySelectedSlef() {
        if (relativesList.size == 1) {
            val personDetail = relativesList[0]
            val personID = personDetail.relativeID
            val relation = personDetail.relationship
            val personName = personDetail.firstName
            fragment.setRelativeID(personID)
            fragment.setPersonRel(relation)
            fragment.setPersonName(personName)

            notifyItemChanged(0)
            selectedPos = 0
            notifyItemChanged(0)
        }
    }

    inner class SelectRelationshipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemRelationshipRecordBinding.bind(view)
        val layoutRelative = binding.layoutRelative
        val imgRelation = binding.imgRelation
        val imgSelect = binding.imgSelect
        val txtName = binding.txtName
        val txtRelation = binding.txtRelation

        init {
            layoutRelative.setOnClickListener {

                val personDetail = relativesList[adapterPosition]
                val personID = personDetail.relativeID
                val personRel = personDetail.relationship.trim { it <= ' ' }
                val personName = personDetail.firstName.trim { it <= ' ' }
                fragment.setRelativeID(personID)
                fragment.setPersonRel(personRel)
                fragment.setPersonName(personName)

                notifyItemChanged(selectedPos)
                selectedPos = layoutPosition
                notifyDataSetChanged()
            }
        }
    }
}