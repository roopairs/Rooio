package com.rooio.repairs;


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.arch.core.util.Function
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import kotlinx.android.synthetic.main.activity_jobs_archived.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class JobsArchived  : NavigationBar() {

        private lateinit var completedList: ListView
        private lateinit var completedConstraint: ConstraintLayout

        val statuses = arrayListOf<String>()

        companion object{

                @JvmStatic private var archivedJobs = ArrayList<JSONObject>()
                @JvmStatic private var completedJobs = ArrayList<JSONObject>()
                @JvmStatic private var declinedJobs = ArrayList<JSONObject>()
                @JvmStatic private var cancelledJobs = ArrayList<JSONObject>()
        }

        private var completedButton: Button? = null

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                initialize()
                setNavigationBar()
                setActionBar()
                createNavigationBar(NavigationType.JOBS)
                onClick()
                clearLists()
                loadJobs()

        }

        private fun initialize(){
                setContentView(R.layout.activity_jobs_archived)
                completedButton = findViewById(R.id.button)
                //sets the navigation bar onto the page
                completedList = findViewById<View>(R.id.completedList) as ListView
                completedConstraint = findViewById<View>(R.id.completedConstraint) as ConstraintLayout

        }

        private fun onClick() {
                completedButton!!.setOnClickListener { startActivity(Intent(this@JobsArchived, Jobs::class.java)) }
        }

        private fun loadJobs(){
                loadArchivedJobs()
        }

        private fun loadArchivedJobs(){
                val Archived = "?status=1&status=3&status=4&ordering=-completion_time"
                val url = "service-locations/$userLocationID/jobs/$Archived"
                requestJson(Request.Method.GET, JsonType.ARRAY, JsonRequest(false, url,
                        null, responseFunc, errorFunc, true))
        }
        private fun clearLists(){
                archivedJobs.clear()
                completedJobs.clear()
                cancelledJobs.clear()
                declinedJobs.clear()
        }

        private fun sortJobsList(list: ArrayList<JSONObject>){

                Collections.sort(list, JSONComparator())

                Log.e("sort", "start");
                for (obj in list) {
                        Log.e("sort", obj.getString("status_time_value"));
                }
        }
        private fun populateLists(responseObj: JSONArray){
                for (i in 0 until responseObj.length()) {
                        val job = responseObj.getJSONObject(i)

                        if (job.getInt("status") == 3){
                                completedJobs.add(job)
                                sizes("completed")
                        }
                        else if(job.getInt("status") == 4){
                                cancelledJobs.add(job)
                                sizes("cancelled")
                        }
                        else if(job.getInt("status") == 1){
                                declinedJobs.add(job)
                                sizes("declined")
                        }

                }
                //Put each job in a list. Append all lists together to sort
                for (job in completedJobs){
                        archivedJobs.add(job)
                }
                for (job in declinedJobs){
                        archivedJobs.add(job)
                }
                for (job in cancelledJobs){
                        archivedJobs.add(job)
                }

                sortJobsList(archivedJobs)

                val customAdapter = JobsCustomerAdapter(this, archivedJobs)
                if (archivedJobs.size != 0) completedList.adapter = customAdapter
        }

        @JvmField
        var responseFunc = Function<Any, Void?> { jsonObj: Any ->
                val responseObj = jsonObj as JSONArray
                populateLists(responseObj)

                null
        }
        @JvmField
        var errorFunc = Function<String, Void?> { string: String? ->

                null
        }

        private fun sizes(str: String) {
                var value = 0
                if (str in statuses) {
                        value = 200
                } else {
                        statuses.add(str)
                        value = 260
                }
                set_size( value)
        }
        private fun set_size( value: Int){

                val params = completedConstraint!!.layoutParams
                params.height += value
                completedConstraint!!.layoutParams = params
                val size = completedList!!.layoutParams
                size.height += value
                completedList!!.layoutParams = size


        }override fun animateActivity(boolean: Boolean){

        }
}