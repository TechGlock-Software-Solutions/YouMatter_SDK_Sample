package com.techglock.health.app.home.adapter


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.RowitemFamilyprofileBinding
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.model.entity.UserRelatives

class FamilyProfileAdapter(
    val viewModel: DashboardViewModel,
    private val relativeList: MutableList<UserRelatives>,
    private val sp_familyprofile: Spinner, val context: Context
) : BaseAdapter() {
//class FamilyProfileAdapter(val viewModel:DashboardViewModel,val sp_familyprofile: Spinner,val context: Context) : BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val mRowItemSize = intArrayOf(15, 30, 30, 30)
    private val userRelativesList: MutableList<UserRelatives> = mutableListOf()

    override fun getCount(): Int = relativeList.size + 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItem(position: Int): Any {
        return relativeList[position]
    }

    private fun getRelative(position: Int): UserRelatives {
        return relativeList[position]
    }

    fun updateRelativesList(items: List<UserRelatives>) {
        userRelativesList.clear()
        userRelativesList.addAll(items)
        //Utilities.printLog("userRelativesList"+userRelativesList)
    }

    /*     override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
             return super.getDropDownView(position, recycledView, parent)
         }*/

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View? {
        if (position == relativeList.lastIndex + 1) {

            if (viewModel.isSelfUser()) {
                return addFamilyMemberButton()
            } else {
                val view = TextView(context)
                view.visibility = View.GONE
                return view
            }
        } else {
            return super.getDropDownView(position, recycledView, parent)
        }
    }

    /*     override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
             var mConvertView = convertView
             val viewHolder: FamilyProfileViewHolder
             val relative = getRelative(position)
             mConvertView = mInflater.inflate(R.layout.rowitem_familyprofile, parent, false)
             viewHolder = FamilyProfileViewHolder(mConvertView)
             if (relative != null) {
                 val relativeImgId = Utilities.getRelationImgIdWithGender(relative.relationshipCode , relative.gender)
                 viewHolder.relativeImage.setImageResource(relativeImgId)
                 viewHolder.relativeName.text = relative.firstName
                 viewHolder.relation.text = relative.relationship
             }
             return mConvertView
         }*/

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: FamilyProfileViewHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.rowitem_familyprofile, parent, false)
            vh = FamilyProfileViewHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as FamilyProfileViewHolder
        }
        val relative = getRelative(position)
        val relativeImgId =
            Utilities.getRelativeImgIdWithGender(relative.relationshipCode, relative.gender)
        vh.relativeImage.setImageResource(relativeImgId)
        vh.relativeName.text = relative.firstName
        vh.relation.text = relative.relationship
        return view
    }

    inner class FamilyProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RowitemFamilyprofileBinding.bind(view)
        var relativeImage = binding.imgRelative
        var relativeName = binding.txtRelativeName
        var relation = binding.txtRelation

        /*         fun bindTo( user:UserRelatives,viewModel: DashboardViewModel) {
                     binding.userRelative = user
                     binding.viewModel = viewModel
                     //app:familyMembers="@{viewModel.userRelativesList}"
                 }*/
    }

    /*     private fun addFamilyMemberButton(): View {
             val view = TextView(context)
             view.text = "    "+context.resources.getString(R.string.ADD_NEW_MEMBER)
             view.textSize = 18f
             view.setTypeface(Typeface.DEFAULT_BOLD, Typeface.NORMAL)
             view.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_plus), null, null, null)
             view.setCompoundDrawableTintList(ColorStateList.valueOf(Color.WHITE))
             view.setPadding(mRowItemSize[0], mRowItemSize[1], mRowItemSize[2], mRowItemSize[3])
             view.gravity = Gravity.CENTER
             view.setTextColor(Color.WHITE)
             view.setBackgroundColor(context.getResources().getColor(R.color.green))
             view.setOnClickListener {
                 Utilities.detachSpinnerWindow(sp_familyprofile)
                 val intent = Intent()
                 intent.setComponent(ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_PROFILE))
                 context.startActivity(intent)
             }
             return view
         }*/

    private fun addFamilyMemberButton(): View? {
        val convertView: View
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (inflater != null) {
            convertView = inflater.inflate(R.layout.btn_add_new_family_member, null)
            convertView.setOnClickListener {
                Utilities.detachSpinnerWindow(sp_familyprofile)
                val intent = Intent()
                intent.component =
                    ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_PROFILE)
                context.startActivity(intent)
            }
            return convertView
        }
        return null
    }

    private fun addTransparentView(): View {
        val view = View(context)
        view.setPadding(mRowItemSize[0], mRowItemSize[1], mRowItemSize[2], mRowItemSize[3])
        view.setBackgroundColor(context.resources.getColor(R.color.transparent))
        return view
    }

}