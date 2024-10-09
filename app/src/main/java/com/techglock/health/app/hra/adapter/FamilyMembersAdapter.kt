package com.techglock.health.app.hra.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemSelectMemberBinding
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.entity.UserRelatives

class FamilyMembersAdapter(
    val viewModel: HraViewModel, val context: Context,
    val listener: OnRelativeClickListener
) : RecyclerView.Adapter<FamilyMembersAdapter.FamilyMembersViewsHolder>() {

    private var selectedPos = -1
    private var familyMembersList: MutableList<UserRelatives> = mutableListOf()

    override fun getItemCount(): Int = familyMembersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyMembersViewsHolder =
        FamilyMembersViewsHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_member, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: FamilyMembersViewsHolder, position: Int) {
        val relative = familyMembersList[position]
        var name = relative.firstName.split(" ")[0]
        if (relative.relativeID == viewModel.adminPersonId) {
            name = context.resources.getString(R.string.MYSELF)
        }
        holder.txtRelative.text = name

        if (selectedPos == -1) {
            holder.layoutRelative.background =
                ContextCompat.getDrawable(context, R.drawable.btn_border_disabled)
        } else {
            if (selectedPos == position) {
                holder.layoutRelative.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_selected)
                holder.imgSelect.setImageResource(R.drawable.img_tick_green)
            } else {
                holder.layoutRelative.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_border_disabled)
                holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
            }
        }

        holder.itemView.setOnClickListener {
            listener.onRelativeSelection(position, relative)
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyDataSetChanged()
        }

    }

    fun updateRelativeList(items: MutableList<UserRelatives>) {
        familyMembersList.clear()
        familyMembersList.addAll(items)
        this.notifyDataSetChanged()
    }

    inner class FamilyMembersViewsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSelectMemberBinding.bind(view)
        val layoutRelative = binding.layoutRelative
        val imgSelect = binding.imgSelect
        val txtRelative = binding.txtRelative
    }

    interface OnRelativeClickListener {
        fun onRelativeSelection(position: Int, relative: UserRelatives)
    }

}