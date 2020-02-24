package com.rooio.repairs

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.HtmlCompat
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_preferred_providers_details.*
import org.json.JSONArray
import org.json.JSONException
import java.net.URL
import java.util.ArrayList
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.InputStream

class PreferredProviderDetails: NavigationBar() {

    lateinit var error: TextView
    lateinit var backButton: ImageView
    lateinit var removeButton: Button
    lateinit var url: String
    lateinit var email: TextView
    lateinit var skills: TextView
    lateinit var licenseNumber: TextView
    lateinit var overview: TextView
    lateinit var phone: TextView
    lateinit var name: TextView
    lateinit var price: TextView
    lateinit var logo: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferred_providers_details)

        //initializes variables that are used in loadElements()
        overview = findViewById(R.id.info_overview)
        email = findViewById(R.id.info_email)
        skills = findViewById(R.id.info_skills)
        licenseNumber = findViewById(R.id.info_license_number)
        phone = findViewById(R.id.info_phone)
        logo = findViewById(R.id.logo)
        name = findViewById(R.id.name)
        price = findViewById(R.id.price)

        //sets the navigation bar onto the page
        val navInflater = layoutInflater
        val tmpView = navInflater.inflate(R.layout.activity_navigation_bar, null)

        window.addContentView(tmpView,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        //sets the action bar onto the page
        val actionbarInflater = layoutInflater
        val actionBarView = actionbarInflater.inflate(R.layout.action_bar, null)
        window.addContentView(actionBarView,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        supportActionBar!!.elevation = 0.0f

        createNavigationBar("settings")
        backButton = findViewById<View>(R.id.back_button_details) as ImageView
        removeButton = findViewById(R.id.removeProvider)

        loadProvider()
        onRemoveClick()
        onBackClick()
    }

    private fun loadProvider(){
        val bundle: Bundle ?= intent.extras
        if (bundle!=null){

            val theId = bundle.getString("addedProvider")
            url = "https://capstone.api.roopairs.com/v0/service-providers/" + theId.toString() + "/"

            val responseFunc = { jsonObject : JSONObject ->
                try {
                    loadElements(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                null
            }

            val errorFunc = { string : String ->
                error.setText(string)
                null
            }

            requestGetJsonObj(url, responseFunc, errorFunc, true)
        }
    }
    private fun onRemoveClick() {
        val responseFunc = { jsonObject : Any ->
            try {
                jsonObject as JSONObject
                startActivity(Intent (this@PreferredProviderDetails, PreferredProvidersSettings::class.java ))
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            null
        }

        val errorFunc = { string : String ->
            error.setText(string)
            null
        }
        val params = java.util.HashMap<String, Any>()
        removeButton.setOnClickListener {
            requestDeleteJsonObj(JsonRequest(false, url, params, responseFunc, errorFunc, true))
        }
    }

    @Throws(JSONException::class)
    fun loadElements(response: JSONObject) {
        var image : String
        try {
            image = response.get("logo") as String
        } catch (e: Exception) {
            // if there is no logo for the service provider
            image =""
        }
        if(!image.isNullOrEmpty())
            Picasso.with(applicationContext)
                .load(image)
                .into(logo)
        setElementTexts(overview, response,"overview", "overview")
        setElementTexts(email, response, "email", "email")
        setElementTexts(skills, response, "skills", "skills")
        setElementTexts(licenseNumber, response, "contractor_license_number", "license number")
        setElementTexts(phone, response, "phone", "phone number")
        setElementTexts(name, response, "name", "name")
        setPriceElement(price, response, "starting_hourly_rate", "")
    }

    private fun setElementTexts(element: TextView, response: JSONObject, elementName: String, name: String){
        try {
            var jsonStr = response.get(elementName) as String
            if(jsonStr.isNullOrEmpty())
                element.text = "--"
            else
                element.text = jsonStr

        }
        catch (e: Exception) {
            element.text = "--"
        }
    }

    private fun setPriceElement(element: TextView, response: JSONObject, elementName: String, name: String){
        try {
            var hoursText = getString((R.string.details_price_exception_message),response.get(elementName) as String)
            var standardText = SpannableStringBuilder(" starting cost")
            standardText.setSpan(ForegroundColorSpan(Color.parseColor("#00CA8F")), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            element.text = standardText.insert(1, "$hoursText ")
        } catch (e: Exception) {
            element.text = "--"
        }
    }


    override fun animateActivity(boolean: Boolean)
    {
    }
    private fun onBackClick() {
        backButton.setOnClickListener{
            val intent = Intent(this@PreferredProviderDetails, PreferredProvidersSettings::class.java)
            startActivity(intent);
        }
    }

}