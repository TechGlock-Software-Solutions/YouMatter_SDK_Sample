package com.techglock.health.app.security.ui_dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.databinding.DialogEmployeeBinding
import com.techglock.health.app.security.SecurityHelper
import com.techglock.health.app.security.adapter.EmployerAdapter
import com.techglock.health.app.security.viewmodel.StartupViewModel

class DialogEmployee(
    private val mContext: Context,
    private val listener: EmployerAdapter.EmployerSelectionListener,
    private val viewModel: StartupViewModel
) : Dialog(mContext) {

    private lateinit var binding: DialogEmployeeBinding

    private var employerAdapter: EmployerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        initialise()
    }

    private fun initialise() {
        employerAdapter = EmployerAdapter(mContext, listener)
        binding.rvBankList.adapter = employerAdapter
        employerAdapter!!.updateList(SecurityHelper.getEmployerList(mContext))
    }

}