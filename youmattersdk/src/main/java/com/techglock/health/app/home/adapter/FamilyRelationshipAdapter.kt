package com.techglock.health.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.databinding.ItemRelationshipBinding
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.ui.ProfileAndFamilyMember.SelectRelationshipFragment
import com.techglock.health.app.home.viewmodel.ProfileFamilyMemberViewModel

class FamilyRelationshipAdapter(
    val fragment: SelectRelationshipFragment,
    val viewModel: ProfileFamilyMemberViewModel,
    val context: Context
) : RecyclerView.Adapter<FamilyRelationshipAdapter.FamilyRelationshipViewHolder>() {

    private val familyRelationList: MutableList<DataHandler.FamilyRelationOption> = mutableListOf()
    private var selectedPos = -1
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = familyRelationList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FamilyRelationshipViewHolder =
        FamilyRelationshipViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_relationship, parent, false)
        )

    override fun onBindViewHolder(holder: FamilyRelationshipViewHolder, position: Int) {

        val relationOption = familyRelationList[position]
        holder.imgSelect.setImageResource(relationOption.relationImgId)
        holder.relation.text = relationOption.relation

//        holder.layoutRelation.tag = position
        /*        holder.relation.setTextColor(context.getResources().getColor(R.color.vivant_track_param_textcolor))
                holder.relationIcon.setBackgroundColor(context.resources.getColor(R.color.white))
                ImageViewCompat.setImageTintList(holder.relationIcon,
                    ColorStateList.valueOf(context.resources.getColor(R.color.vivant_track_param_textcolor)))*/

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

    fun updateRelationList(items: List<DataHandler.FamilyRelationOption>) {
        familyRelationList.clear()
        familyRelationList.addAll(items)
    }

    inner class FamilyRelationshipViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemRelationshipBinding.bind(view)
        var relationIcon = binding.imgRelation
        var relation = binding.txtRelation
        var layoutRelative = binding.layoutRelative
        var imgSelect = binding.imgSelect

        init {
            binding.layoutRelative.setOnClickListener {

                val memberDetail = familyRelationList[adapterPosition]
                fragment.setRelationShipCode(memberDetail.relationshipCode)
                fragment.setRelation(memberDetail.relation)
                fragment.setGender(memberDetail.gender)

                notifyItemChanged(selectedPos)
                selectedPos = layoutPosition
                notifyItemChanged(selectedPos)
            }
        }

    }
}