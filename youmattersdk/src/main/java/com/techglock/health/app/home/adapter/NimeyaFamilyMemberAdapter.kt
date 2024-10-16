package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemNimeyaFamilyMemberBinding
import com.techglock.health.app.model.nimeya.SaveProtectoMeterModel


class NimeyaFamilyMemberAdapter(
    val context: Context,
    private val deleteListener: OnDeleteClickListener,
    private val editListener: OnEditClickListener
) : RecyclerView.Adapter<NimeyaFamilyMemberAdapter.NimeyaFamilyMemberViewHolder>() {

    val familyMemberList: MutableList<SaveProtectoMeterModel.FamilyDetail> = mutableListOf()

    override fun getItemCount(): Int = familyMemberList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NimeyaFamilyMemberViewHolder =
        NimeyaFamilyMemberViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_nimeya_family_member, parent, false)
        )

    override fun onBindViewHolder(
        holder: NimeyaFamilyMemberViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val familyMember = familyMemberList[position]
        holder.txtName.text = familyMember.name
        holder.txtRelation.text = familyMember.relation + " , "
        holder.txtDob.text = DateHelper.convertDateSourceToDestination(
            familyMember.dob,
            DateHelper.DATEFORMAT_DDMMMMYYYY,
            DateHelper.DATEFORMAT_DDMMMYYYY_NEW
        )
        holder.txtIsDependent.text = "Is dependent : ${familyMember.isDependent}"
        familyMember.age = DateHelper.calculatePersonAge(
            DateHelper.convertDateSourceToDestination(
                familyMember.dob,
                DateHelper.DATEFORMAT_DDMMMMYYYY,
                DateHelper.DISPLAY_DATE_DDMMMYYYY
            ), context
        ).split(" ").toTypedArray()[0]

        holder.imgEdit.setOnClickListener {
            editListener.onEditClick(familyMember)
        }
        holder.imgDelete.setOnClickListener {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = "Remove Family Member"
            dialogData.message =
                "Are you sure that you want to Remove ${familyMember.name} from family members list?"
            dialogData.btnLeftName = "No"
            dialogData.btnRightName = "Yes"
            dialogData.hasErrorBtn = true
            val defaultNotificationDialog =
                DefaultNotificationDialog(
                    context,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(
                            isButtonLeft: Boolean,
                            isButtonRight: Boolean
                        ) {
                            if (isButtonRight) {
                                removeAt(position)
                            }
                        }
                    },
                    dialogData
                )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }

    }

    fun updateList(items: List<SaveProtectoMeterModel.FamilyDetail>) {
        familyMemberList.clear()
        familyMemberList.addAll(items)
        notifyDataSetChanged()
    }

    private fun removeAt(position: Int) {
        Utilities.printLogError("Removing_At--->$$position")
        notifyItemRemoved(position)
        refresh()
        familyMemberList.removeAt(position)
        deleteListener.onDeleteClick(familyMemberList)
    }

    fun refresh() {
        this.notifyDataSetChanged()
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(familyMemberList: MutableList<SaveProtectoMeterModel.FamilyDetail>)
    }

    interface OnEditClickListener {
        fun onEditClick(familyMember: SaveProtectoMeterModel.FamilyDetail)
    }

    inner class NimeyaFamilyMemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemNimeyaFamilyMemberBinding.bind(view)
        val txtName = binding.txtName
        val txtRelation = binding.txtRelation
        val txtDob = binding.txtDob
        val txtIsDependent = binding.txtIsDependent
        val imgEdit = binding.imgEdit
        val imgDelete = binding.imgDelete
    }

}