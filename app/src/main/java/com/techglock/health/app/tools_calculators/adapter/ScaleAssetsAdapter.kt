package com.techglock.health.app.tools_calculators.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemScaleAssetsBinding
import com.techglock.health.app.tools_calculators.model.ScaleAsset

class ScaleAssetsAdapter(
    private val scaleAssetsList: List<ScaleAsset>,
    private val context: Context
) :
    RecyclerView.Adapter<ScaleAssetsAdapter.ScaleAssetsViewHolder>() {

    override fun getItemCount(): Int = scaleAssetsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScaleAssetsViewHolder =
        ScaleAssetsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_scale_assets, parent, false)
        )

    override fun onBindViewHolder(holder: ScaleAssetsAdapter.ScaleAssetsViewHolder, position: Int) {
        val scaleAsset = scaleAssetsList[position]

        holder.imgScaleAsset.setImageResource(scaleAsset.imageId)
        holder.titleAssetPoints.text = scaleAsset.title

        when (position) {
            0 -> {
                holder.layoutTracker.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(context, R.color.bg_risk_normal),
                        BlendModeCompat.SRC_ATOP
                    )
            }

            1 -> {
                holder.layoutTracker.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(context, R.color.bg_risk_moderate),
                        BlendModeCompat.SRC_ATOP
                    )
            }

            2 -> {
                holder.layoutTracker.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(context, R.color.bg_risk_extremly_severe),
                        BlendModeCompat.SRC_ATOP
                    )
            }
        }

        val list = scaleAsset.list

        holder.layoutAssetPoints.removeAllViews()
        var textView: TextView
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val descParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        for (i in list) {
            textView = li.inflate(R.layout.textview_bullete, null) as TextView
            textView.text = i
            textView.setTextAppearance(context, R.style.VivantDescription)
            descParam.setMargins(30, 10, 10, 10)
            textView.layoutParams = descParam
            holder.layoutAssetPoints.addView(textView)
        }
    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawablesRelative) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    inner class ScaleAssetsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemScaleAssetsBinding.bind(view)

        val layoutTracker = binding.layoutTracker
        val imgScaleAsset = binding.imgScaleAsset
        val titleAssetPoints = binding.titleAssetPoints
        val layoutAssetPoints = binding.layoutAssetPoints
    }

}