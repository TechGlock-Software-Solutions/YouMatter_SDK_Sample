package com.techglock.health.app.home.views

import android.text.Html
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.home.adapter.*
import com.techglock.health.app.home.common.CalculatorModel
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.common.DataHandler.*
import com.techglock.health.app.model.blogs.BlogItem
import com.techglock.health.app.model.entity.HRASummary
import de.hdodenhof.circleimageview.CircleImageView

object HomeBinding {

    @BindingAdapter("app:calculatorList")
    @JvmStatic
    fun RecyclerView.setTrackersList(list: MutableList<CalculatorModel>?) {
        with(this.adapter as CalculatorsAdapter) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            list?.let { updateTrackersList(it) }
        }
    }

    @BindingAdapter("android:loadImage")
    @JvmStatic
    fun AppCompatImageView.setImageView(resource: Int) {
        //Picasso.get().load(resource).into(this)
        setImageResource(resource)
    }

    @BindingAdapter("android:loadCircularImage")
    @JvmStatic
    fun CircleImageView.setCircularImageView(resource: Int) {
        setImageResource(resource)
    }

    @BindingAdapter("android:hraObsColor")
    @JvmStatic
    fun LinearLayout.setHraObsColor(hraSummary: HRASummary?) {
        try {
            Utilities.printLog("setHraScore--->$hraSummary")
            if (hraSummary != null) {
                var wellnessScore = 0
                var hraCutOff = ""
                var currentHRAHistoryID = ""
                wellnessScore = hraSummary.scorePercentile.toInt()
                hraCutOff = hraSummary.hraCutOff
                currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                if (wellnessScore <= 0) {
                    wellnessScore = 0
                }

                if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && !currentHRAHistoryID.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    when {
                        hraCutOff.equals("0", ignoreCase = true) -> {
                            background.setTint(
                                ContextCompat.getColor(
                                    context,
                                    R.color.colorPrimary
                                )
                            )
                        }

                        wellnessScore in 0..15 -> {
                            background.setTint(ContextCompat.getColor(context, R.color.high_risk))
                        }

                        wellnessScore in 16..45 -> {
                            background.setTint(
                                ContextCompat.getColor(
                                    context,
                                    R.color.moderate_risk
                                )
                            )
                        }

                        wellnessScore in 46..84 -> {
                            background.setTint(
                                ContextCompat.getColor(
                                    context,
                                    R.color.healthy_risk
                                )
                            )
                        }

                        wellnessScore > 85 -> {
                            background.setTint(
                                ContextCompat.getColor(
                                    context,
                                    R.color.optimum_risk
                                )
                            )
                        }
                    }
                } else {
                    background.setTint(ContextCompat.getColor(context, R.color.colorPrimary))
                }
            } else {
                background.setTint(ContextCompat.getColor(context, R.color.colorPrimary))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:showHraScore")
    @JvmStatic
    fun AppCompatTextView.setHraScore(hraSummary: HRASummary?) {
        try {
            if (hraSummary != null) {
                var wellnessScore = 0
                var hraCutOff = ""
                var currentHRAHistoryID = ""
                wellnessScore = hraSummary.scorePercentile.toInt()
                hraCutOff = hraSummary.hraCutOff
                currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                if (wellnessScore <= 0) {
                    wellnessScore = 0
                }
                if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && !currentHRAHistoryID.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    if (hraCutOff.equals("0", ignoreCase = true)) {
                        wellnessScore = 0
                    }
                } else {
                    wellnessScore = 0
                }
                text = wellnessScore.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:showHraObservation")
    @JvmStatic
    fun AppCompatTextView.setHraObservation(hraSummary: HRASummary?) {
        try {
            if (hraSummary != null) {
                var wellnessScore = 0
                var hraCutOff = ""
                var currentHRAHistoryID = ""
                wellnessScore = hraSummary.scorePercentile.toInt()
                hraCutOff = hraSummary.hraCutOff
                currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                if (wellnessScore <= 0) {
                    wellnessScore = 0
                }
                if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && !currentHRAHistoryID.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    when {
                        hraCutOff.equals("0", ignoreCase = true) -> {
                            text = context.resources.getString(R.string.COMPLETE_YOUR_HRA)
                        }

                        wellnessScore in 0..15 -> {
                            text = context.resources.getString(R.string.HIGH_RISK)
                        }

                        wellnessScore in 16..45 -> {
                            text = context.resources.getString(R.string.NEEDS_IMPROVEMENT)
                        }

                        wellnessScore in 46..85 -> {
                            text = context.resources.getString(R.string.HEALTHY)
                        }

                        wellnessScore > 85 -> {
                            text = context.resources.getString(R.string.OPTIMUM)
                        }
                    }
                } else {
                    text = context.resources.getString(R.string.TAKE_YOUR_HRA)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:drawerItems")
    @JvmStatic
    fun RecyclerView.setDrawerItems(list: List<NavDrawerOption>?) {
        with(this.adapter as NavigationDrawerListAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateNavDrawerList(it) }
        }
    }

    @BindingAdapter("app:settingsItems")
    @JvmStatic
    fun RecyclerView.setSettingsItems(list: List<DataHandler.Option>?) {
        with(this.adapter as OptionSettingsAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateDashboardOptionsList(it) }
        }
    }

    @BindingAdapter("app:relationListItems")
    @JvmStatic
    fun RecyclerView.setRelationListItems(list: List<FamilyRelationOption>?) {
        with(this.adapter as FamilyRelationshipAdapter) {
            layoutManager = GridLayoutManager(context, 3)
            list?.let { updateRelationList(it) }
        }
    }

    @BindingAdapter("app:htmlTxt")
    @JvmStatic
    fun AppCompatTextView.setHtmlTxt(html: String) {
        try {
            text =
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:blogSuggestionItems")
    @JvmStatic
    fun setBlogSuggestionItem(recyclerView: RecyclerView, list: List<BlogItem>?) {
        Utilities.printLog("BlogItemList=====> " + list)
        with(recyclerView.adapter as BlogSuggestionAdapter) {
            list?.let { updateData(it) }
        }
    }

}