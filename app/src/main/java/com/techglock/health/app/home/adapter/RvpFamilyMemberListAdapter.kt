package com.techglock.health.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.UserSingleton
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemFamilyMemberProfileBinding
import com.techglock.health.app.home.viewmodel.ProfileFamilyMemberViewModel
import com.techglock.health.app.model.entity.UserRelatives
import java.util.Locale

class RvpFamilyMemberListAdapter(
    val listener: OnItemClickListener,
    val context: Context,
    val viewModelProfile: ProfileFamilyMemberViewModel
) : RecyclerView.Adapter<RvpFamilyMemberListAdapter.FamilyMembersViewHolder>() {

    private val familyMembersList: MutableList<UserRelatives> = mutableListOf()
    private var relationshipCode = Constants.SELF_RELATIONSHIP_CODE

    override fun getItemCount(): Int = familyMembersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyMembersViewHolder =
        FamilyMembersViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_family_member_profile, parent, false)
        )

    override fun onBindViewHolder(holder: FamilyMembersViewHolder, position: Int) {
        val data = familyMembersList[position]
        holder.bindTo(data)

        if (relationshipCode == data.relationshipCode) {
            holder.binding.imgSelect.visibility = View.VISIBLE
        } else {
            holder.binding.imgSelect.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(position, familyMembersList[position])
//            val bundle = Bundle()
//            bundle.putString(Constants.RELATIVE_ID, familyMembersList[position].relativeID)
//            bundle.putString(Constants.RELATION_SHIP_ID, familyMembersList[position].relationShipID)
//            bundle.putString(Constants.RELATION_CODE, familyMembersList[position].relationshipCode)
//            bundle.putString(Constants.RELATION, familyMembersList[position].relationship)
//            it.findNavController().navigate(R.id.action_familyMembersListFragment2_to_editFamilyMemberDetailsFragment2, bundle)
        }
    }

    fun updateFamilyMembersList(items: List<UserRelatives>) {
        Utilities.printLog("InsideAdapter=> $items")
        familyMembersList.clear()
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        if (viewModelProfile.adminPersonId == viewModelProfile.personId) {
            familyMembersList.add(
                0,
                UserRelatives(
                    firstName = localResource.getString(R.string.TITLE_ADD_FAMILY_MEMBER),
                    relationshipCode = "ADD"
                )
            )
        }
        familyMembersList.addAll(items)
        notifyDataSetChanged()
    }

    fun updateRelationshipCode(relCode: String) {
        relationshipCode = relCode
        notifyDataSetChanged()
    }

    fun removeRelative(position: Int) {
        familyMembersList.removeAt(position)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: UserRelatives)
    }

    inner class FamilyMembersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemFamilyMemberProfileBinding.bind(view)

        fun bindTo(user: UserRelatives) {
            val relativeImgId =
                Utilities.getRelativeImgIdWithGender(user.relationshipCode, user.gender)
            binding.relativeImage.setImageResource(relativeImgId)
            val profPicBitmap = UserSingleton.getInstance()!!.profPicBitmap
            if (user.relationshipCode == Constants.SELF_RELATIONSHIP_CODE && profPicBitmap != null) {
                binding.relativeImage.setImageBitmap(profPicBitmap)
            }

            binding.txtPersonName.text = user.firstName
            binding.txtRelation.text =
                Utilities.getRelationshipByRelationshipCode(user.relationshipCode, context)
            if (user.relationshipCode.equals(Constants.SELF_RELATIONSHIP_CODE, true)) {
                binding.txtRelation.text = context.resources.getString(R.string.SELF)
            }

            if (user.relationship.isNullOrEmpty()) {
                binding.layoutFamilyMember.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_purple_light)
                binding.relativeImage.visibility = View.INVISIBLE
                binding.imgAdd.visibility = View.VISIBLE
                binding.txtPersonName.setTextColor(context.resources.getColor(R.color.colorPrimary))
                binding.txtRelation.visibility = View.INVISIBLE
            } else {
                binding.layoutFamilyMember.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_border_disabled)
                binding.relativeImage.visibility = View.VISIBLE
                binding.imgAdd.visibility = View.INVISIBLE
                binding.txtPersonName.setTextColor(context.resources.getColor(R.color.textViewColor))
                binding.txtRelation.visibility = View.VISIBLE
                //binding.txtRelation.text = user.relationship
            }

        }


    }
}