package com.techglock.health.app.records_tracker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.databinding.FragmentDigitizeRecordBinding
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.model.shr.HealthDataParameter
import com.techglock.health.app.records_tracker.adapter.DigitizeRecordParametersAdapter
import com.techglock.health.app.records_tracker.common.RecordSingleton
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileInputStream
import java.util.*

@AndroidEntryPoint
class DigitizeRecordFragment : BaseFragment(),
    com.github.barteksc.pdfviewer.listener.OnPageChangeListener,
    DatePickerDialog.OnDateSetListener {

    private val viewModel: HealthRecordsViewModel by lazy {
        ViewModelProvider(this)[HealthRecordsViewModel::class.java]
    }
    private lateinit var binding: FragmentDigitizeRecordBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var code = ""
    private var uri = ""
    private var recordPath = ""
    private var recordName = ""
    private var recordType = ""
    private var personId = ""
    private var digitizedParamList: List<HealthDataParameter> = ArrayList()

    private var pageNumber: Int = 0
    private val cal = Calendar.getInstance()
    private var digitizeRecordParametersAdapter: DigitizeRecordParametersAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val record = RecordSingleton.getInstance()!!.getHealthRecord()
            from = it.getString(Constants.FROM)!!
            code = it.getString(Constants.CODE)!!
            uri = it.getString(Constants.URI)!!
            recordPath = record.Path!!
            recordName = record.Name!!
            recordType = record.Type!!
            personId = record.PersonId!!.toString()
            digitizedParamList = RecordSingleton.getInstance()!!.getDigitizedParamList()
            //Utilities.printLogError("From,code,uri,Path,Name,Type,PersonId----->$from,$code,$uri,$recordPath,$recordName,$recordType,$personId")
            //Utilities.printLogError"DigitizedParamList----->$digitizedParamList")
        }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnCancelDigitize.performClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDigitizeRecordBinding.inflate(inflater, container, false)
        initialise()
        registerObservers()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        setTodayCheckupDate()
        loadDocument()
        digitizeRecordParametersAdapter =
            DigitizeRecordParametersAdapter(digitizedParamList, viewModel, requireContext(), this)
        binding.rvDigitizedRecords.setExpanded(true)
        binding.rvDigitizedRecords.adapter = digitizeRecordParametersAdapter
    }

    private fun setTodayCheckupDate() {
        binding.edtCheckupDate.setText(DateHelper.currentDateAsStringddMMMyyyyNew)
    }

    private fun registerObservers() {
        viewModel.ocrUnitExist.observe(viewLifecycleOwner) {
            if (it != null) {
                //Utilities.printLog("OcrUnitExist----->${it.isExist}")
                digitizeRecordParametersAdapter!!.itemViewRefresh(it.isExist)
            }
        }
        viewModel.ocrSaveDocument.observe(viewLifecycleOwner) {
            if (it != null) {
                //Utilities.printLog("OcrSaveDocument----->${it.isSaved}")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadDocument() {
        val webView = binding.docWebview
        webView.webViewClient = WebViewClient()
        val webSettings = binding.docWebview.settings
        webSettings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true

        when (recordType) {
            "PDF" -> {
                binding.pdfView.visibility = View.VISIBLE
                binding.docWebview.visibility = View.GONE
                displayPdf()
            }

            "IMAGE" -> {
                try {
                    binding.docWebview.visibility = View.VISIBLE
                    binding.pdfView.visibility = View.GONE
                    var html =
                        "<html><body><center><img src='{IMAGE_PLACEHOLDER}' /></body> </html>"
                    //String html = "<html><head></head><body><img src=\"" + "{IMAGE_PLACEHOLDER}" + "\"width=\"100%\" height=\"100%\"\"/></body></html>";
                    val imagePath = "$recordPath/$recordName"
                    val file = File(imagePath)
                    val bytesFile = ByteArray(file.length().toInt())
                    var fileInputStream: FileInputStream? = null
                    fileInputStream = FileInputStream(file)
                    fileInputStream.read(bytesFile)
                    val imageBase64 = Base64.encodeToString(bytesFile, Base64.DEFAULT)
                    val image = "data:image/png;base64,$imageBase64"
                    html = html.replace("{IMAGE_PLACEHOLDER}", image)
                    binding.docWebview.loadDataWithBaseURL("", html, "text/html", "utf-8", "")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            /*            "IMAGE" -> {
                            binding.docWebview.visibility = View.VISIBLE
                            binding.pdfView.visibility = View.GONE
                            //val base = recordPath
                            //val imagePath = "file://$base/$recordName"
                            val imagePath = uri
                            val html = "<html><body><img src=\"$imagePath\" width=\"100%\" height=\"100%\"\"/></body></html>"
                            binding.docWebview.loadDataWithBaseURL("", html, "text/html", "utf-8", "")
                        }*/
        }
    }

    private fun setClickable() {

        binding.layoutCalender.setOnClickListener {
            showDatePicker()
        }

        binding.edtCheckupDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSubmitDigitize.setOnClickListener { view ->
            val data = digitizeRecordParametersAdapter!!.healthParameterList
            val recordedDate = binding.edtCheckupDate.text.toString()
            if (digitizeRecordParametersAdapter!!.validateData()) {
                //Utilities.printLog("Data=> $data")
                viewModel.callOcrSaveDocumentApi(view, this, data, recordedDate, personId)
            }
        }

        binding.btnCancelDigitize.setOnClickListener {
            performBackClick(it)
        }

    }

    fun performBackClick(view: View) {
        //Utilities.printLog("from,code----->$from,$code")
        // Set Record in Singleton class to Empty
        RecordSingleton.getInstance()!!.setHealthRecord(HealthDocument())
        val bundle = Bundle()
        bundle.putString(Constants.CODE, code)
        if (from.equals("View", ignoreCase = true)) {
            bundle.putString(Constants.FROM, "View")
            view.findNavController()
                .navigate(R.id.action_fragmentDigitize_to_viewRecordsFragment, bundle)
        }
        if (from.equals("Digitize", ignoreCase = true)) {
            bundle.putString(Constants.FROM, "Digitize")
            view.findNavController()
                .navigate(R.id.action_fragmentDigitize_to_digitizedRecordsListFragment, bundle)
        }
    }

    private fun displayPdf() {
        //pdfFileName = recordName;
        //val basePath = recordPath
        binding.pdfView.fromUri(uri.toUri())
            .defaultPage(pageNumber)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .onPageChange(this)
            .enableAnnotationRendering(true)
            .scrollHandle(DefaultScrollHandle(context))
            .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        //binding.txtView.text = pageNumber.toString() + " , " + pageCount
    }

    private fun showDatePicker() {
        val dpd = DatePickerDialog.newInstance(
            this@DigitizeRecordFragment,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dpd.accentColor = appColorHelper.primaryColor()
        dpd.setTitle(resources.getString(R.string.DATE_OF_CHECKUP))
        dpd.maxDate = cal
        dpd.isThemeDark = false
        dpd.vibrate(false)
        dpd.dismissOnPause(false)
        dpd.showYearPickerFirst(false)
        dpd.show(this.requireActivity().fragmentManager, "CheckupDate")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date =
            year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()
        val formattedDate = DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, date)
        binding.edtCheckupDate.setText(formattedDate)
    }


}
