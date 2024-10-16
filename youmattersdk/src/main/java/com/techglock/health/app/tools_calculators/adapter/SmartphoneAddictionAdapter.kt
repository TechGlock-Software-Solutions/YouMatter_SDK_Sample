package com.techglock.health.app.tools_calculators.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemSmartphoneAddictionBinding
import com.techglock.health.app.model.toolscalculators.SmartPhoneSaveResponceModel
import com.techglock.health.app.tools_calculators.common.DataHandler

class SmartphoneAddictionAdapter(
    private val scaleAssetsList: List<SmartPhoneSaveResponceModel.Section>,
    private val context: Context
) :
    RecyclerView.Adapter<SmartphoneAddictionAdapter.SmartphoneAddictionViewHolder>() {

    private val dataHandler = DataHandler(context)

    override fun getItemCount(): Int = scaleAssetsList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SmartphoneAddictionViewHolder =
        SmartphoneAddictionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_smartphone_addiction, parent, false)
        )

    override fun onBindViewHolder(holder: SmartphoneAddictionViewHolder, position: Int) {
        val scaleAsset = scaleAssetsList[position]

        holder.imgSac.setImageResource(dataHandler.getSmartPhoneAddictionImage(scaleAsset.title!!))
        holder.titleSac.text = scaleAsset.title

        val list = scaleAsset.subSection

        holder.layoutSac.removeAllViews()
        var textView: TextView
        var subTextView: TextView
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val descParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val descParamSub = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (list!!.isNotEmpty()) {
            //holder.layoutSac.visibility = View.VISIBLE
            for (i in list.indices) {
                textView = li.inflate(R.layout.textview_bullete, null) as TextView
                textView.text = list[i].description
                textView.setTextAppearance(context, R.style.VivantDescription)
                descParam.setMargins(30, 10, 10, 10)
                textView.layoutParams = descParam
                holder.layoutSac.addView(textView)

                if (list[i].subDescription!!.isNotEmpty()) {
                    for (j in list[i].subDescription!!) {
                        subTextView = li.inflate(R.layout.textview_sub_bullete, null) as TextView
                        subTextView.text = j
                        subTextView.setTextAppearance(context, R.style.VivantDescription)
                        descParamSub.setMargins(80, 10, 10, 10)
                        subTextView.layoutParams = descParamSub
                        holder.layoutSac.addView(subTextView)
                    }
                }
            }
        } else {
            //holder.layoutSac.visibility = View.GONE
        }

    }

    inner class SmartphoneAddictionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemSmartphoneAddictionBinding.bind(view)

        val imgSac = binding.imgSac
        val titleSac = binding.titleSac
        val layoutSac = binding.layoutSac
    }

}