package com.techglock.health.app.records_tracker.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentViewRecordsBinding
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.records_tracker.adapter.HealthRecordsAdapter
import com.techglock.health.app.records_tracker.common.DataHandler
import com.techglock.health.app.records_tracker.common.RecordSingleton
import com.techglock.health.app.records_tracker.model.DocumentType
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel
import com.techglock.health.app.repository.utils.Resource
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ViewRecordsFragment : BaseFragment(), OptionsBottomSheet.OnOptionClickListener {

    private val viewModel: HealthRecordsViewModel by lazy {
        ViewModelProvider(this)[HealthRecordsViewModel::class.java]
    }
    private lateinit var binding: FragmentViewRecordsBinding

    //private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var code = "ALL"
    private var sync = false
    private var isDataChanged = false
    private var healthRecordsAdapter: HealthRecordsAdapter? = null
    private var recordsList: MutableList<HealthDocument> = mutableListOf()
    private var allCategoryList: MutableList<DocumentType> = mutableListOf()
    private var animation: LayoutAnimationController? = null
    val record: HealthDocument? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // Callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //(activity as HealthRecordsActivity).routeToHomeScreen()
                    requireActivity().finish()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewRecordsBinding.inflate(inflater, container, false)
        arguments?.let {
            if (it.containsKey(Constants.FROM)) {
                from = it.getString(Constants.FROM)!!
            }
            if (it.containsKey(Constants.CODE)) {
                //code = it.getString(Constants.CODE)!!
            }
            Utilities.printLogError("from,code----->$from,$code")
        }
        initialise()
        registerObservers()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.HEALTH_RECORDS_DASHBOARD_SCREEN
        )
        animation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom)
        allCategoryList = DataHandler(requireContext()).getDocumentTypeListAll()

        healthRecordsAdapter =
            HealthRecordsAdapter(requireContext(), viewModel, this, Constants.VIEW)
        binding.rvRecords.layoutAnimation = animation
        binding.rvRecords.adapter = healthRecordsAdapter

        if (from.isEmpty()) {
            startShimmer()
            viewModel.callListDocumentsApi(true)
        } else {
            sync = true
            viewModel.getHealthDocumentsWhereCode("ALL")
            //binding.tabLayout.scrollX = binding.tabLayout.width
            //binding.tabLayout.getTabAt(DataHandler(requireContext()).getRecordCategoryPositionByCode(code))!!.select()
        }

    }

    private fun registerObservers() {

        viewModel.listDocuments.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.status == Resource.Status.SUCCESS) {
                    try {
                        val data = HashMap<String,Any>()
                        var recordAvailable = false
                        val list = it.data!!.documents
                        var documentsList: MutableList<HealthDocument> = mutableListOf()
                        if (!list.isNullOrEmpty()) {
                            documentsList.clear()
                            documentsList.addAll(list)
                            documentsList = documentsList.filter {
                                it.Code != "PROFPIC"
                            }.toMutableList()
                            if (documentsList.size > 0) {
                                binding.rvRecords.visibility = View.VISIBLE
                                binding.layoutNoRecords.visibility = View.GONE
                                binding.rvRecords.adapter = healthRecordsAdapter
                                recordAvailable = true
                                healthRecordsAdapter!!.updateList(documentsList, code)
                            } else {
                                binding.rvRecords.visibility = View.GONE
                                binding.layoutNoRecords.visibility = View.VISIBLE
                            }
                            sync = true
                        } else {
                            binding.rvRecords.visibility = View.GONE
                            binding.layoutNoRecords.visibility = View.VISIBLE
                        }
                        stopShimmer()
                        if ( recordAvailable ) {
                            data[CleverTapConstants.DATA_AVAILABLE] = CleverTapConstants.YES
                        } else {
                            data[CleverTapConstants.DATA_AVAILABLE] = CleverTapConstants.NO
                        }
                        CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.HEALTH_RECORDS_DASHBOARD_SCREEN,data)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    binding.rvRecords.visibility = View.GONE
                    binding.layoutNoRecords.visibility = View.VISIBLE
                    stopShimmer()
                }
            }
        }

        viewModel.healthDocumentsList.observe(viewLifecycleOwner) {
            if (it != null) {
                val labRecords = it
                recordsList.clear()
                recordsList.addAll(labRecords)
                //recordsList.addAll(allRecords.filter { it.Code == "LAB" })
                /*                recordsList = recordsList.filter { it.PersonId.toString() == viewModel.personId }
                                    .toMutableList()*/
                recordsList = recordsList.filter {
                    it.Code != "PROFPIC"
                }.toMutableList()
                if (recordsList.size > 0) {
                    Utilities.printLogError("RecordCount--->${recordsList.size}")
                    binding.layoutRecords.visibility = View.VISIBLE
                    binding.layoutNoRecords.visibility = View.GONE
                    binding.rvRecords.layoutAnimation = animation
                    healthRecordsAdapter!!.updateList(recordsList, code)
                    binding.rvRecords.scheduleLayoutAnimation()
                } else {
                    binding.layoutRecords.visibility = View.GONE
                    binding.layoutNoRecords.visibility = View.VISIBLE
                }
            }
        }

        viewModel.postDownload.observe(viewLifecycleOwner) {
            if (it != null) {
                Utilities.printLogError("postDownload----->$it")
                val record = RecordSingleton.getInstance()!!.getHealthRecord()
                if (!Utilities.isNullOrEmptyOrZero(record.Id.toString())) {
                    when {
                        it.equals(Constants.VIEW, ignoreCase = true) -> {
                            DataHandler(requireContext()).viewDocument(record)
                        }

                        it.equals(Constants.SHARE, ignoreCase = true) -> {
                            DataHandler(requireContext()).shareDataWithAppSingle(record, viewModel)
                        }

                        it.equals(Constants.DIGITIZE, ignoreCase = true) -> {
                            viewModel.callDigitizeDocumentApi(Constants.VIEW, code, record)
                        }
                    }
                }
            }
        }

        viewModel.documentStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it == Resource.Status.SUCCESS) {
                    isDataChanged = true
                    viewModel.getHealthDocumentsWhereCode(code)
                }
            }
        }

        viewModel.downloadDoc.observe(viewLifecycleOwner) {}
        viewModel.deleteDocument.observe(viewLifecycleOwner) {}
        viewModel.ocrDigitizeDocument.observe(viewLifecycleOwner) {}
    }

    private fun updateData(position: Int) {
        val name = allCategoryList[position].title
        val categoryCodeValue = allCategoryList[position].code
        Utilities.printLogError("Selected Item :: $position,$name,$categoryCodeValue")
        code = categoryCodeValue
        if (sync) {
            viewModel.getHealthDocumentsWhereCode(categoryCodeValue)
        }
    }

    private fun setClickable() {

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                updateData(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.imgSort.setOnClickListener {
            binding.rvRecords.layoutAnimation = animation
            healthRecordsAdapter!!.toggleList()
            binding.rvRecords.scheduleLayoutAnimation()
        }

        binding.layoutAddRecords.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.CODE, code)
            bundle.putString(Constants.FROM, Constants.VIEW)
            findNavController().navigate(
                R.id.action_viewRecordsFragment_to_documentTypeFragment,
                bundle
            )
        }

    }

    override fun onOptionClick(action: String, record: HealthDocument) {
        when (action) {

            Constants.DOWNLOAD -> {
                RecordSingleton.getInstance()!!.setHealthRecord(record)
                viewModel.callDownloadRecordApi(action, record)
            }

            Constants.SHARE -> {
                performAction(Constants.SHARE, record)
            }

            Constants.DELETE -> {
                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = resources.getString(R.string.DELETE_RECORD)
                dialogData.message = resources.getString(R.string.MSG_DELETE_CONFIRMATION)
                dialogData.btnLeftName = resources.getString(R.string.NO)
                dialogData.btnRightName = resources.getString(R.string.YES)
                dialogData.hasErrorBtn = true
                val defaultNotificationDialog =
                    DefaultNotificationDialog(
                        activity,
                        object : DefaultNotificationDialog.OnDialogValueListener {

                            override fun onDialogClickListener(
                                isButtonLeft: Boolean,
                                isButtonRight: Boolean
                            ) {
                                if (isButtonRight) {
                                    val deleteRecordIds: ArrayList<String> = ArrayList()
                                    deleteRecordIds.add(record.Id.toString())
                                    viewModel.callDeleteRecordsApi(deleteRecordIds)
                                }
                            }
                        },
                        dialogData
                    )
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
            }

        }
    }

    fun performAction(action: String, record: HealthDocument) {
        val file = File(record.Path, record.Name!!)
        if (file.exists()) {
            when (action) {
                Constants.VIEW -> {
                    DataHandler(requireContext()).viewDocument(record)
                }

                Constants.SHARE -> {
                    DataHandler(requireContext()).shareDataWithAppSingle(record, viewModel)
                }
            }
        } else {
            RecordSingleton.getInstance()!!.setHealthRecord(record)
            viewModel.callDownloadRecordApi(action, record)
        }
    }

    private fun startShimmer() {
        binding.layoutRecordsShimmer.startShimmer()
        binding.layoutRecordsShimmer.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        binding.layoutRecordsShimmer.stopShimmer()
        binding.layoutRecordsShimmer.visibility = View.GONE
    }

}