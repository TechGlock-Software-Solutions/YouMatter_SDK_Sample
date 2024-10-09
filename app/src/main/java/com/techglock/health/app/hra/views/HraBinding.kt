package com.techglock.health.app.hra.views


import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.ViewUtils
import com.techglock.health.app.common.view.FlowLayout
import com.techglock.health.app.hra.adapter.FamilyMembersAdapter
import com.techglock.health.app.hra.ui.HraQuesSingleSelectionFragment
import com.techglock.health.app.hra.viewmodel.HraViewModel
import com.techglock.health.app.model.entity.HRAQuestions
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.hra.Option
import java.util.*

@SuppressLint("StaticFieldLeak")
object HraBinding {

    lateinit var viewModel: HraViewModel

    @BindingAdapter("app:familyMembersList")
    @JvmStatic
    fun RecyclerView.setFamilyMembersList(list: MutableList<UserRelatives>?) {
        with(this.adapter as FamilyMembersAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateRelativeList(it) }
        }
    }

    @SuppressLint("ResourceAsColor")
    @BindingAdapter("app:hraRelativesList")
    @JvmStatic
    fun setHraRelativesList(radioGroup: RadioGroup, list: List<UserRelatives>?) {
        try {
            val localResource = LocaleHelper.getLocalizedResources(
                radioGroup.context,
                Locale(LocaleHelper.getLanguage(radioGroup.context)!!)
            )!!
            radioGroup.removeAllViews()
            val par = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            par.setMargins(10, 10, 10, 10)
            if (list != null) {
                for (i in list.indices) {
                    var name = list[i].firstName.split(" ")[0]
                    if (list[i].relativeID == viewModel.adminPersonId) {
                        name = localResource.getString(R.string.MYSELF)
                    }
                    val radioButton = RadioButton(radioGroup.context)
                    radioButton.apply {
                        id = i
                        tag = list[i].relativeID
                        text = name
                        layoutParams = par
                        gravity = Gravity.CENTER
                        buttonDrawable = null
                        setPadding(40, 25, 40, 25)
                        background = ViewUtils.getArcRbSelectorBgHra(true)
                        setTextColor(ViewUtils.getArcRbSelectorTxtColorHra(true))

                        //background = ContextCompat.getDrawable(context, R.drawable.hra_option_selector_bg)
                        //setTextColor(ContextCompat.getColorStateList(context, R.color.hra_option_selector_color))
                    }
                    radioGroup.addView(radioButton)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:rgPreviousList")
    @JvmStatic
    fun setRgPreviousList(radioGroup: RadioGroup, list: List<Option>?) {
        try {
            radioGroup.removeAllViews()
            val par = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            par.setMargins(10, 10, 10, 10)
            if (list != null) {
                for (i in list.indices) {
                    val radioButton = RadioButton(radioGroup.context)
                    radioButton.apply {
                        id = i
                        tag = list[i].answerCode
                        text = list[i].description
                        layoutParams = par
                        gravity = Gravity.CENTER
                        buttonDrawable = null
                        setPadding(40, 25, 40, 25)
                        background = ViewUtils.getArcRbSelectorBgHra(false)
                        setTextColor(ViewUtils.getArcRbSelectorTxtColorHra(false))

                        //background = ContextCompat.getDrawable(context, R.drawable.btn_round_disabled_hra)
                        //setTextColor(ContextCompat.getColorStateList(context, R.color.vivant_greyish))
                    }
                    radioGroup.addView(radioButton)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:rgChecked")
    @JvmStatic
    fun setRgChecked(radioGroup: RadioGroup, selectedTag: String?) {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                if (radioGroup.childCount > 0 && selectedTag != null) {
                    Utilities.printLogError("SelectedTag----->$selectedTag")
                    ViewUtils.setRadioButtonCheckByTag(radioGroup, selectedTag)
                    for (i in 0 until radioGroup.childCount) {
                        val rb = radioGroup.getChildAt(i) as RadioButton
                        if (rb.tag.toString() == selectedTag) {
                            Utilities.printLogError("Tag----->${rb.tag}")
                            rb.isChecked = true
                        }
                    }
                }
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    @BindingAdapter("app:rgChecked")
        @JvmStatic
        fun setRgChecked(radioGroup: RadioGroup, selectedTag: String?) {
            try {
                if (radioGroup.childCount > 0 && selectedTag != null) {
                    Utilities.printLogError("SelectedTag----->$selectedTag")
                    ViewUtils.setRadioButtonCheckByTag(radioGroup, selectedTag)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    @BindingAdapter("app:flChecked")
    @JvmStatic
    fun setFlChecked(flowLayout: FlowLayout, list: List<HRAQuestions>?) {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                if (list != null) {
                    if (list.isNotEmpty()) {
                        //Utilities.printLogError("SelectedOptionList----->$list")
                        for (i in 0 until flowLayout.childCount) {
                            val chk = flowLayout.getChildAt(i) as CheckBox
                            for (option in list) {
                                //if ( option.AnswerCode.equals(chk.tag.toString().trim(), ignoreCase = true)) {
                                //Utilities.printLogError("chk.tag----->${chk.tag}")
                                if (chk.tag.toString()
                                        .contains(option.AnswerCode, ignoreCase = true)
                                ) {
                                    Utilities.printLogError("isMatched----->${chk.tag} : ${option.AnswerCode}")
                                    chk.isChecked = true
                                }
                            }
                        }
                    }
                }
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("android:loadImage")
    @JvmStatic
    fun setImageView(imageView: AppCompatImageView, resource: Int?) {
        try {
            imageView.setImageResource(resource!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("android:htmlTextFromId")
    @JvmStatic
    fun setHTMLTextViewFromId(htmlTextView: HTMLTextView, text: Int?) {
        try {
            if (!Utilities.isNullOrEmptyOrZero(text.toString())) {
                htmlTextView.apply {
                    setHtmlTextFromId(text)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter(
        value = ["qCode", "singleSelectionFragment", "singleSelectionList"],
        requireAll = false
    )
    @JvmStatic
    fun RadioGroup.setSingleSelectionRgList(
        qCode: String,
        singleSelectionFragment: HraQuesSingleSelectionFragment,
        singleSelectionList: List<Option>?
    ) {
        try {
            var toProceed = true
            removeAllViews()
            if (singleSelectionList != null) {
                if (toProceed) {
                    val par = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    par.setMargins(10, 10, 10, 10)
                    for (i in singleSelectionList.indices) {
                        val radioButton = RadioButton(context)
                        val option = singleSelectionList[i]
                        radioButton.apply {
                            id = i
                            tag = option.answerCode
                            text = option.description
                            layoutParams = par
                            gravity = Gravity.CENTER
                            background = ContextCompat.getDrawable(
                                context,
                                R.drawable.hra_option_selector_bg
                            )
                            buttonDrawable = null
                            setTextColor(
                                ContextCompat.getColorStateList(
                                    context,
                                    R.color.hra_option_selector_color
                                )
                            )
                            setPadding(40, 25, 40, 25)
                            //Utilities.printLogError("Option($i + $1)--->$text")
                            Utilities.printLogError("Option" + (i + 1) + "--->" + text)
                        }
                        radioButton.setOnClickListener(singleSelectionFragment.listener)
                        addView(radioButton)
                    }
                    //viewModel.getResponse(qCode)
                    toProceed = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    @BindingAdapter(value = ["url", "defaultImage", "placeholder", "circleCrop"], requireAll = false)
    fun ImageView.setImageUrl(url: String, defaultResId: Int? = null, placeHolderResId: Int? = null, circleCrop: Boolean = false) {
        val glideRequest = if (defaultResId != null) {
            GlideApp.with(context).loadOrDefault(url, defaultResId)
        }
        else {
            GlideApp.with(context).load(url)
        }
        if (placeHolderResId != null) {
            glideRequest.placeholder(placeHolderResId)
        }
        if (circleCrop) {
            glideRequest.circleCrop()
        }
        glideRequest.into(this)
    }*/

    /*    @BindingAdapter("app:rgPreviousList")
        @JvmStatic fun setRgPreviousList(radioGroup: RadioGroup, list: List<Option>?) {
            radioGroup.removeAllViews()
            val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            //val padding = radioGroup.context.resources.getDimension(R.dimen._10sdp)
            par.setMargins(10, 10, 10, 10)
            try {
                if ( list != null ) {
                    for( i in list.indices ) {
                        val radioButton = RadioButton(radioGroup.context)
                        radioButton.apply {
                            id = i
                            tag = list[i].answerCode
                            text = list[i].description
                            layoutParams = par
                            gravity = Gravity.CENTER
                            background = ContextCompat.getDrawable(context, R.drawable.hra_previous_option_selector_bg)
                            buttonDrawable = null
                            setTextColor(ContextCompat.getColorStateList(context, R.color.hra_previous_option_selector_color))
                            setPadding(40, 25, 40, 25)
                        }
                        radioGroup.addView(radioButton)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    /*    @BindingAdapter("app:optionItems")
    @JvmStatic fun setOptions(recyclerView: RecyclerView, list: List<OptionList>?) {
        with(recyclerView.adapter as HraOptionsListAdapter) {
            recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 2)
            list?.let { updateOptionsList(it) }
        }
    }*/

}