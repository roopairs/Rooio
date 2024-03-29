package com.rooio.repairs

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.arch.core.util.Function
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.android.volley.Request
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_create_job_details.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


//Job details can be viewed when clicking on a job request found under the Jobs tab
class CreateJobDetails: RestApi() {

    private val timePickerInterval = 15
    private lateinit var minutePicker: NumberPicker
    private lateinit var displayedValues: ArrayList<String>

    private lateinit var restuarantLocation: TextView
    private lateinit var serviceType: Spinner
    private lateinit var whatHappened: TextInputEditText
    private lateinit var date: DatePicker
    private lateinit var time: TimePicker
    private lateinit var contact: TextInputEditText
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var phoneNumberText: TextView
    private lateinit var sendRequestButton: Button
    private lateinit var errorMsg: TextView
    private lateinit var errorMsgTime: TextView

    private lateinit var equipmentName: TextView
    private lateinit var manufacturer: TextView
    private lateinit var modelNumber: TextView
    private lateinit var location: TextView
    private lateinit var serialNumber: TextView
    private lateinit var lastServiceBy: TextView
    private lateinit var lastServiceDate: TextView
    private lateinit var manufacturerText: TextView
    private lateinit var modelText: TextView
    private lateinit var locationText: TextView
    private lateinit var serialText: TextView
    private lateinit var lastServiceByText: TextView
    private lateinit var lastServiceDateText: TextView
    private lateinit var backButton: ImageView
    private lateinit var dropDown: ImageView
    private lateinit var equipmentDivider: ImageView
    private lateinit var equipmentLayout: ConstraintLayout
    private lateinit var viewEquipment: TextView
    private lateinit var transitionsContainer: ViewGroup
    private lateinit var viewGroup: ViewGroup
    private lateinit var scrollView: ScrollView
    private lateinit var loadingPanel: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_job_details)

        onResume()
        initializeVariables()
        initializeAnimationVariables()
        setActionBar()
        initializeUI()
        onSendRequest()
        onBack()
        onDropDown()
        onPause()
    }

    //Initializes variables that are used in loadElements()
    private fun initializeVariables() {
        restuarantLocation = findViewById(R.id.restaurantLocation)
        serviceType = findViewById(R.id.serviceTypeSpinner)
        whatHappened = findViewById(R.id.whatHappenedInput)
        date = findViewById(R.id.dateInput)
        time = findViewById(R.id.timeInput)
        contact = findViewById(R.id.contactInput)
        phoneNumber = findViewById(R.id.phoneNumberInput)
        phoneNumberText = findViewById(R.id.phoneNumberText)

        var phoneNumberTextString = "What's their phone number?"
        setOptionalTextGray(phoneNumberTextString, phoneNumberTextString.length)

        // changing UI to display contact name in the prompt
        contact.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val name = contact.text
            if(!hasFocus && !name!!.isBlank()){
                phoneNumberTextString = "What's $name's phone number?"
                setOptionalTextGray(phoneNumberTextString, phoneNumberTextString.length)
            }
        }

        errorMsg = findViewById(R.id.errorMessage)
        errorMsgTime = findViewById(R.id.errorMessageTime)
        sendRequestButton = findViewById(R.id.sendRequestButton)
        transitionsContainer = findViewById(R.id.jobDetailLayout)
        viewGroup = findViewById(R.id.jobDetailTitleLayout)
        scrollView = findViewById(R.id.jobDetailScrollView)
        serviceType.adapter =
                ArrayAdapter<ServiceType>(this, R.layout.spinner_item, ServiceType.values()).also { adapter ->
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                }

        // initializing date picker
        date.minDate = Calendar.getInstance().timeInMillis
        date.setOnClickListener {
            errorMsgTime.visibility = View.GONE
            val datetimeInput = Calendar.getInstance()
            val curCalendar = Calendar.getInstance()

            //Current hour and current minute are used as time.hour and time.minute are API level 23
            datetimeInput.set(Calendar.HOUR_OF_DAY, time.currentHour)
            datetimeInput.set(Calendar.MINUTE, time.currentMinute * timePickerInterval)
            datetimeInput.set(Calendar.MONTH, date.month)
            datetimeInput.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)

            if(datetimeInput.timeInMillis < curCalendar.timeInMillis){
                errorMsgTime.visibility = View.VISIBLE
            }
        }

        // initializing time picker
        setTimePickerInterval(time)

        // resetting error message
        time.setOnTimeChangedListener { _, _, _ ->  errorMsgTime.visibility = View.GONE }
    }

    private fun setOptionalTextGray(phoneTextString: String, startGrayIndex: Int) {
        var stringWithOpt = "$phoneTextString (optional)"
        var phoneSpannableString = SpannableString(stringWithOpt.toUpperCase())
        phoneSpannableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.darkGray)),
                startGrayIndex,
                stringWithOpt.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        phoneNumberText.text = phoneSpannableString
    }

    // sets time picker to show 15 minute intervals
    private fun setTimePickerInterval(timePicker: TimePicker) {
        try {
            val classForid = Class.forName("com.android.internal.R\$id")
            val field: Field = classForid.getField("minute")
            minutePicker = timePicker.findViewById(field.getInt(null)) as NumberPicker
            minutePicker.minValue = 0
            minutePicker.maxValue = 3
            displayedValues = ArrayList()
            var i = 0
            while (i < 60) {
                displayedValues.add(String.format("%02d", i))
                i += timePickerInterval
            }
            minutePicker.displayedValues = displayedValues.toArray(arrayOfNulls<String>(0))
            minutePicker.wrapSelectorWheel = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Initializes variables that are used in loadElements and animated
    private fun initializeAnimationVariables() {
        //Equipment dropdown
        equipmentLayout = transitionsContainer.findViewById(R.id.equipmentLayout)
        equipmentName = transitionsContainer.findViewById(R.id.equipmentName)
        dropDown = transitionsContainer.findViewById(R.id.dropDown)
        manufacturer = transitionsContainer.findViewById(R.id.manufacturerInfo)
        modelNumber = transitionsContainer.findViewById(R.id.modelInfo)
        location = transitionsContainer.findViewById(R.id.locationInfo)
        serialNumber = transitionsContainer.findViewById(R.id.serialInfo)
        lastServiceBy = transitionsContainer.findViewById(R.id.lastServiceByInfo)
        lastServiceDate = transitionsContainer.findViewById(R.id.lastServiceDateInfo)
        manufacturerText = transitionsContainer.findViewById(R.id.manufacturerText)
        modelText = transitionsContainer.findViewById(R.id.modelText)
        locationText = transitionsContainer.findViewById(R.id.locationText)
        serialText = transitionsContainer.findViewById(R.id.serialText)
        lastServiceByText = transitionsContainer.findViewById(R.id.lastServiceByText)
        lastServiceDateText = transitionsContainer.findViewById(R.id.lastServiceDateText)
        equipmentDivider = transitionsContainer.findViewById(R.id.equipmentDivider)
        viewEquipment = transitionsContainer.findViewById(R.id.viewEquipment)
        backButton = viewGroup.findViewById(R.id.backButton)
        loadingPanel = findViewById(R.id.loadingPanel)
    }

    // initializing restaurant location text and equipment widget
    private fun initializeUI() {
        loadingPanel.visibility = View.GONE
        requestLocation()
        requestEquipmentInfo()
    }

    // sending JSONRequest for the restaurant location
    private fun requestLocation() {
        val url = "service-locations/$userLocationID/"
        val request = JsonRequest(false, url, null, responseFuncLoad, errorFuncLoad, true)
        requestJson(Request.Method.GET, JsonType.OBJECT, request)
    }

    @JvmField
    // set restaurant location text at top of screen
    var responseFuncLoad = Function<Any, Void?>{ jsonResponse: Any? ->
        try{
            val jsonObject = jsonResponse as JSONObject
            restuarantLocation.text = jsonObject.getString("physical_address_formatted")
        } catch (e: JSONException){
            errorMsg.text = e.toString()
        }
        null
    }

    @JvmField
    var errorFuncLoad = Function<String, Void?> {
        restuarantLocation.text = it
        null
    }

    private fun requestEquipmentInfo() {
        // get equipment information from whichever piece of equipment that the user chose earlier
        val equipmentID = intent.getStringExtra("equipment")
        // if "general [type] (no appliance)" is chosen
        if (equipmentID == "null"){
            equipmentLayout.visibility = View.GONE
            viewEquipment.visibility = View.GONE
        }
        else {
            val url = "service-locations/$userLocationID/equipment/$equipmentID/"
            val request = JsonRequest(false, url, null, responseFuncEquipment, errorFuncEquipment, true)
            requestJson(Request.Method.GET, JsonType.OBJECT, request)
        }
    }

    @JvmField
    val responseFuncEquipment = Function<Any, Void?> {jsonResponse: Any? ->
        try{
            val jsonObject = jsonResponse as JSONObject
            setEquipmentInfo(jsonObject)
        } catch (e: JSONException){
            errorMsg.text = e.toString()
        }
        null
    }

    @JvmField
    val errorFuncEquipment = Function<String, Void?> {
        errorMsg.text = it
        null
    }

    private fun setEquipmentInfo(response: JSONObject){
        setElementText(equipmentName, response, "display_name")
        setElementText(manufacturer, response, "manufacturer")
        setElementText(location, response, "location")
        setElementText(lastServiceBy, response, "last_service_by")
        setElementText(lastServiceDate, response, "last_service_date")
        setElementText(modelNumber, response, "model_number")
        setElementText(serialNumber, response, "serial_number")
    }

    private fun setElementText(element: TextView, response: JSONObject, elementName: String){
        val txt = response[elementName].toString()
        if(!txt.isBlank() && txt != "null")
            element.text = txt
        else
            element.text = "--"
    }


    private fun onSendRequest() {
        val params = HashMap<Any?, Any?>()
        val url = "service-locations/$userLocationID/jobs/"
        sendRequestButton.setOnClickListener {
            errorMsg.text = ""
            if(intent.getStringExtra("equipment") == "null")
                params["equipment"] = ArrayList<String>()
            else
                params["equipment"] = arrayOf(intent.getStringExtra("equipment"))
            params["service_company"] = intent.getIntExtra("company", 0)
            params["service_category"] = intent.getIntExtra("type", 0)
            params["service_type"] = (serviceTypeSpinner.selectedItem as ServiceType).getInt()
            params["details"] = whatHappened.text.toString()
            params["point_of_contact_name"] = contact.text.toString()
            params["point_of_contact_phone"] = phoneNumber.text.toString()
            params["requested_arrival_time"] = formatRequestDate()
            val request = JsonRequest(false, url, params, responseFuncSendRequest, errorFuncSendRequest, true)
            sendJobRequest(request)
        }
    }

    // formatting the request time into ISO 8601 format
    private fun formatRequestDate() : String {
        val timeZone = TimeZone.getDefault()
        val requestDate = Calendar.getInstance(timeZone)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestDate.set(date.year, date.month, date.dayOfMonth, time.hour, time.minute*timePickerInterval)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return sdf.format(requestDate.time)
    }

    @JvmField
    val responseFuncSendRequest = Function<Any, Void?> {
        loadingPanel.visibility = View.GONE
        sendRequestButton.visibility = View.VISIBLE
        startActivity(Intent(this@CreateJobDetails, Dashboard::class.java))
        null
    }

    @JvmField
    val errorFuncSendRequest = Function<String, Void?> {
        loadingPanel.visibility = View.GONE
        sendRequestButton.visibility = View.VISIBLE
        errorMsg.text = "Error while sending job request. Please try again later."
        scrollView.fullScroll(ScrollView.FOCUS_UP)
        null
    }

    private fun sendJobRequest(request: JsonRequest) {
        if(validJobRequest(request) && phoneValidate()) {
            loadingPanel.visibility = View.VISIBLE
            sendRequestButton.visibility = View.GONE
            requestJson(Request.Method.POST, JsonType.OBJECT, request)
        }
        else if(!phoneValidate()){
            errorMsg.text = resources.getString(R.string.phone_number_error)
            scrollView.fullScroll(ScrollView.FOCUS_UP)
        }
        else {
            errorMsg.text = resources.getString(R.string.fill_out_fields)
            scrollView.fullScroll(ScrollView.FOCUS_UP)
        }
    }

    private fun phoneValidate(): Boolean{
        val phone = phoneNumber.text.toString()
        return (phone.isEmpty()) || (phone.length == 9) || (phone.length == 10)
    }

    private fun validJobRequest(request: JsonRequest) : Boolean{
        val serviceCompany = request.params?.get("service_company")
        val serviceCategory = request.params?.get("service_category")
        val serviceType = request.params?.get("service_type")
        val details = request.params?.get("details")
        val contactName = request.params?.get("point_of_contact_name")
        val requestedTime = request.params?.get("requested_arrival_time")

        return (serviceCompany != "") && (serviceCategory != "") && (serviceType != "") && (details != "") && (contactName != "") && (requestedTime != "") && validDateTime()
    }

    // making sure valid time is selected
    private fun validDateTime(): Boolean{
        errorMsgTime.visibility = View.GONE
        val datetimeInput = Calendar.getInstance()
        val curCalendar = Calendar.getInstance()

        datetimeInput.set(Calendar.HOUR_OF_DAY, time.currentHour)
        datetimeInput.set(Calendar.MINUTE, time.currentMinute * timePickerInterval)
        datetimeInput.set(Calendar.MONTH, date.month)
        datetimeInput.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)

        if(datetimeInput.timeInMillis < curCalendar.timeInMillis){
            errorMsgTime.visibility = View.VISIBLE
            return false
        }

        return true
    }

    // Goes back to the previous page
    private fun onBack(){
        backButton.setOnClickListener {
            val newIntent = Intent(this@CreateJobDetails, ChooseServiceProvider::class.java)
            newIntent.putExtra("equipment", intent.getStringExtra("equipment"))
            newIntent.putExtra("company", intent.getIntExtra("company", 0))
            newIntent.putExtra("type", intent.getIntExtra("type", 0))
            startActivity(newIntent)
        }
    }

    //Initially closes the equipment dropdown and allows the user to collapse or expand
    private fun onDropDown() {
        var visible = false
        setVisibility(View.GONE)
        val initial = equipmentLayout.layoutParams
        initial.height = 90
        dropDown.setOnClickListener{
            TransitionManager.beginDelayedTransition(transitionsContainer)
            visible = !visible
            val v = if (visible) View.VISIBLE else View.GONE
            val op = if (visible) View.GONE else View.VISIBLE
            viewEquipment.visibility = op
            setVisibility(v)
            val rotate = if (visible) 180f else 0f
            dropDown.rotation = rotate
            val params = equipmentLayout.layoutParams
            val p = if (visible) 395 else 90
            params.height = p
        }
    }

    //Switches the visibility of Equipment UI elements
    private fun setVisibility(v: Int) {
        manufacturerText.visibility = v
        modelText.visibility = v
        locationText.visibility = v
        serialText.visibility = v
        lastServiceByText.visibility = v
        lastServiceDateText.visibility = v
        manufacturer.visibility = v
        modelNumber.visibility = v
        location.visibility = v
        serialNumber.visibility = v
        lastServiceBy.visibility = v
        lastServiceDate.visibility = v
        equipmentDivider.visibility = v
    }
}