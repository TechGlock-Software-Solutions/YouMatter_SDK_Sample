package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.UserSingleton
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemDialogSwitchProfileBinding
import com.techglock.health.app.home.viewmodel.ProfileFamilyMemberViewModel
import com.techglock.health.app.model.entity.UserRelatives

class SwitchProfileAdapter(
    pos: Int,
    val listener: OnItemClickListener,
    val context: Context,
    val viewModelProfile: ProfileFamilyMemberViewModel
) : RecyclerView.Adapter<SwitchProfileAdapter.FamilyMembersViewHolder>() {

    private val familyMembersList: MutableList<UserRelatives> = mutableListOf()
    private var selectedPos = pos

    override fun getItemCount(): Int = familyMembersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyMembersViewHolder =
        FamilyMembersViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dialog_switch_profile, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: FamilyMembersViewHolder, position: Int) {
        val relative = familyMembersList[position]
        holder.bindTo(relative)

        if (selectedPos == position) {
            listener.onItemClick(relative)
            holder.layoutRelative.background =
                ContextCompat.getDrawable(context, R.drawable.border_button_selected)
            holder.imgSelect.visibility = View.VISIBLE
            //holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
        } else {
            holder.layoutRelative.background =
                ContextCompat.getDrawable(context, R.drawable.btn_border_disabled)
            holder.imgSelect.visibility = View.GONE
            //holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
        }

        holder.itemView.setOnClickListener {
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyDataSetChanged()
        }
    }

    fun updateFamilyMembersList(items: List<UserRelatives>) {
        familyMembersList.clear()
        val filteredList =
            items.filter { DateHelper.isDateAbove18Years(it.dateOfBirth) }.toMutableList()
        familyMembersList.addAll(filteredList)
        notifyDataSetChanged()
    }

    inner class FamilyMembersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemDialogSwitchProfileBinding.bind(view)
        var layoutRelative = binding.layoutRelative
        var imgSelect = binding.imgSelect

        fun bindTo(user: UserRelatives) {
            binding.txtName.text = user.firstName
            binding.txtRelation.text =
                Utilities.getRelationshipByRelationshipCode(user.relationshipCode, context)
            Utilities.printLog("Adapter=> " + user.relationshipCode + " :: " + user.gender)

            val relativeImgId =
                Utilities.getRelativeImgIdWithGender(user.relationshipCode, user.gender)
            binding.imgRelation.setImageResource(relativeImgId)
            val profPicBitmap = UserSingleton.getInstance()!!.profPicBitmap
            if (adapterPosition == 0 && profPicBitmap != null) {
                binding.imgRelation.setImageBitmap(profPicBitmap)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(user: UserRelatives)
    }
}