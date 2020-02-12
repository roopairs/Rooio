package com.rooio.repairs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.activity_jobs.*
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList
import android.widget.ImageView
import android.widget.Toast
import java.net.URL


import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_dashboard.view.*


private val have  = ArrayList<ServiceProviderData>()
class Dashboard : NavigationBar() {

    private var repairType: TextView? = null
    private var name: TextView? = null
    private var time: TextView? = null
    private var address: TextView? = null
    private var image: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        repairType = findViewById<View>(R.id.repairType) as TextView
        name = findViewById<View>(R.id.name) as TextView
        time = findViewById<View>(R.id.time) as TextView
        address = findViewById<View>(R.id.address) as TextView
        image = findViewById<View>(R.id.image) as ImageView


        //sets the navigation bar onto the page
        val nav_inflater = layoutInflater
        val tmpView = nav_inflater.inflate(R.layout.activity_navigation_bar, null)

        window.addContentView(tmpView,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        //sets the action bar onto the page

        val actionbar_inflater = layoutInflater
        val poopView = actionbar_inflater.inflate(R.layout.action_bar, null)
        window.addContentView(poopView,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        supportActionBar!!.elevation = 0.0f
        populate_test()

        createNavigationBar("dashboard")
    }

    fun populate_test(){

        val nameVal = "Luna Red"
        val imageVal = "http://www.lunaredslo.com/images/luna_red_logo.png"
        val repairTypeVal = "Hot Equipment Repair"
        val addressVal = "1023 Chorro St. San Luis Obispo"
        val timeVal = "Oct 4, '19 6:30pm PDT"

        repairType!!.text = repairTypeVal
        name!!.text = nameVal
        address!!.text = addressVal
        time!!.text = timeVal
        name_greeting!!.text = "Hi, " + firstName + "!"

        Picasso.with(this)
                .load(imageVal)
                .into(image)



    }





    override fun animateActivity(boolean: Boolean){
        val viewGroup = findViewById<ViewGroup>(R.id.dashboardConstraint)

        //changing the width of the notableJobs and newJobRequest
        val notableJobs = viewGroup.findViewById<ViewGroup>(R.id.notableJobs)
        val newJobRequest = viewGroup.findViewById<ViewGroup>(R.id.newJobRequest)
        val allJobs = viewGroup.findViewById<ViewGroup>(R.id.allJobs)


        TransitionManager.beginDelayedTransition(viewGroup)
        val boxParams1 = notableJobs.layoutParams
        val boxParams2 = newJobRequest.layoutParams
        val boxParams3 = allJobs.layoutParams

        val p2 = if (boolean) 1004 else 803
        boxParams1.width = p2
        boxParams2.width = p2
        boxParams3.width = p2


        //calling the transitions
        notableJobs.layoutParams = boxParams1
        newJobRequest.layoutParams = boxParams2
    }

}
