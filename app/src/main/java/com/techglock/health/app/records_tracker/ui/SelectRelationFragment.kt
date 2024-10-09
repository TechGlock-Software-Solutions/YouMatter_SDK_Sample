package com.techglock.health.app.records_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.extension.preventDoubleClick
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSelectRelationBinding
import com.techglock.health.app.model.entity.RecordInSession
import com.techglock.health.app.records_tracker.adapter.SelectRelationshipAdapter
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectRelationFragment : BaseFragment() {

    private val viewModel: HealthRecordsViewModel by lazy {
        ViewModelProvider(this)[HealthRecordsViewModel::class.java]
    }
    private lateinit var binding: FragmentSelectRelationBinding

    private var code = ""
    private var notes = ""
    private var relativeId = ""
    private var personRel = ""
    private var personName = ""
    private var selectRelationshipAdapter: SelectRelationshipAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString(Constants.CODE)!!
            Utilities.printLogError("code----->$code")
        }

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectRelationBinding.inflate(inflater, container, false)
//        binding.viewmodel = viewModel
//        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        registerObservers()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        viewModel.getUserRelatives()

        binding.rvRelativesList.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(context, 3)
        selectRelationshipAdapter = SelectRelationshipAdapter(this, viewModel, requireContext())
        binding.rvRelativesList.adapter = selectRelationshipAdapter

        binding.btnSaveSelectRelation.isEnabled = false
    }

    private fun registerObservers() {
        viewModel.saveDocument.observe(viewLifecycleOwner) {

        }
        viewModel.userRelativesList.observe(viewLifecycleOwner) {
            selectRelationshipAdapter!!.updateRelativesList(it)
        }
    }

    private fun setClickable() {

        binding.btnSaveSelectRelation.setOnClickListener { view ->
            binding.btnSaveSelectRelation.preventDoubleClick()
            if (relativeId.isNotEmpty()) {
                notes = binding.edtNotes.text.toString()
                viewModel.getRecordToUploadList(code, notes, relativeId, personRel, personName)
                viewModel.recordToUploadRequestList.observe(viewLifecycleOwner) {
                    if (it != null) {
                        val recordToUploadRequestList = it
                        if (recordToUploadRequestList.isNotEmpty()) {
                            viewModel.callSaveDocumentApi(
                                "Save", code, recordToUploadRequestList, personName, personRel, view
                            )

                        }
                    }
                }
            } else {
                Utilities.toastMessageShort(
                    context,
                    resources.getString(R.string.ERROR_SELECT_MEMBER_TO_PROCEED)
                )
            }
        }

    }

    fun setNoDataView(toShow: Boolean) {
        if (toShow) {
            //binding.rvSelectedRecords.visibility = View.GONE
            //binding.txtNoDocuments.visibility = View.VISIBLE
        } else {
            //binding.rvSelectedRecords.visibility = View.VISIBLE
            //binding.txtNoDocuments.visibility = View.GONE
        }
    }

    fun deleteRecordInSession(record: RecordInSession) {
        viewModel.deleteRecordInSession(record)
    }

    fun setRelativeID(value: String) {
        relativeId = value
        Utilities.printLog("RelativeId----->$relativeId")
        binding.btnSaveSelectRelation.isEnabled = true
    }

    fun setPersonRel(value: String) {
        personRel = value
        Utilities.printLog("PersonRelation----->$personRel")
    }

    fun setPersonName(value: String) {
        personName = value
        Utilities.printLog("PersonName----->$personName")
    }

    fun performBackClick() {
        val bundle = Bundle()
        bundle.putString(Constants.CODE, code)
        findNavController().navigate(
            R.id.action_selectRelationFragment_to_uploadRecordFragment,
            bundle
        )
    }

}