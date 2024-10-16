package com.techglock.health.app.records_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentDocumentTypeBinding
import com.techglock.health.app.records_tracker.adapter.DocumentTypeAdapter
import com.techglock.health.app.records_tracker.common.DataHandler
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocumentTypeFragment : BaseFragment() {

    private val viewModel: HealthRecordsViewModel by lazy {
        ViewModelProvider(this)[HealthRecordsViewModel::class.java]
    }
    private lateinit var binding: FragmentDocumentTypeBinding

    private var docTypeCode = ""
    private var from = ""
    private var documentTypeAdapter: DocumentTypeAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM, "")!!
            docTypeCode = it.getString(Constants.CODE, "ALL")!!
            Utilities.printLogError("from,DocTypeCode----->$from,$docTypeCode")
        }

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackBtnClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocumentTypeBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        documentTypeAdapter = DocumentTypeAdapter(
            this, requireContext(),
            DataHandler(requireContext()).getDocumentTypeList()
        )
        binding.rvDocType.layoutManager = GridLayoutManager(context, 3)
        binding.rvDocType.adapter = documentTypeAdapter

        binding.btnNextDocType.isEnabled = false
    }

    private fun setClickable() {

        binding.btnNextDocType.setOnClickListener {
            if (docTypeCode.equals("ALL", ignoreCase = true)) {
                Utilities.toastMessageShort(context, "Please Select Document Type.")
            } else {
                performNextBtnClick(it)
            }
        }

    }

    private fun performNextBtnClick(view: View) {
        val bundle = Bundle()
        bundle.putString(Constants.CODE, docTypeCode)
        view.findNavController()
            .navigate(R.id.action_documentTypeFragment_to_uploadRecordFragment, bundle)
        docTypeCode = ""
    }

    fun performBackBtnClick() {
        viewModel.deleteRecordsInSessionTable()
        Utilities.printLogError("DeletedRecordsInSession.....")
        when (from) {

            Constants.VIEW -> {
                val bundle = Bundle()
                bundle.putString(Constants.CODE, docTypeCode)
                findNavController().navigate(R.id.action_documentTypeFragment_to_viewRecordsFragment)
            }

            Constants.MEDICATION, Constants.TRACK_PARAMETER -> {
                requireActivity().finish()
            }

            else -> {
                findNavController().navigate(R.id.action_documentTypeFragment_to_viewRecordsFragment)
            }
        }
    }

    fun setDocTypeCode(code: String) {
        docTypeCode = code
        //Utilities.printLog("Code=>"+DocTypeCode);
        binding.btnNextDocType.isEnabled = true
    }


}