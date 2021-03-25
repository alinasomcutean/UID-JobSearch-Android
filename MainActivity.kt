package com.example.jobssearch

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.jobssearch.adapters.JobsAdapter
import com.example.jobssearch.model.Job
import com.example.jobssearch.model.JobsInfoSingleton
import com.example.jobssearch.model.api.ErrorDetails
import com.example.jobssearch.model.api.GetJobsResponse
import com.example.jobssearch.rest_api.JobsAPI
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private lateinit var javaScriptMenuItem: MenuItem
    private lateinit var javaMenuItem: MenuItem
    private lateinit var pythonMenuItem: MenuItem
    private lateinit var rubyMenuItem: MenuItem

    private val jobsAPI = JobsAPI.create()
    private lateinit var jobsAdapter: JobsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar = findViewById<ProgressBar>(R.id.jobsProgressBar)
        progressBar.visibility = View.GONE
    }

    //create the menu in the activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.categories_menu, menu)

        //find each item from the meniu and save them to use later
        javaScriptMenuItem = menu!!.findItem(R.id.javaScriptOption)
        javaMenuItem = menu.findItem(R.id.javaOption)
        pythonMenuItem = menu.findItem(R.id.pythonOption)
        rubyMenuItem = menu.findItem(R.id.rubyOption)

        return super.onCreateOptionsMenu(menu)
    }

    private fun setActionNever() {
        javaScriptMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        javaMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        pythonMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        rubyMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
    }

    private fun callAPI(category: String) {
        jobsAPI.getJobs(category).enqueue(object : retrofit2.Callback<ArrayList<GetJobsResponse>> {
            override fun onFailure(call: Call<ArrayList<GetJobsResponse>>, t: Throwable) {
                val noResults = findViewById<TextView>(R.id.noResultsTextView)
                noResults.text = resources.getString(R.string.call_api_failure)
                println(t.stackTrace.get(0).toString())
            }

            override fun onResponse(call: Call<ArrayList<GetJobsResponse>>, response: Response<ArrayList<GetJobsResponse>>) {
                //println(response.raw().request.url)
                if(response.isSuccessful) {
                    val noResults = findViewById<TextView>(R.id.noResultsTextView)
                    noResults.text = ""
                    println(R.drawable.no_logo)

                    var jobsList = ArrayList<Job>()
                    for (j in response.body()!!) {
                        val job = Job(j.type, j.company, j.location, j.title, j.company_logo?:R.drawable.no_logo.toString())

                        jobsList.add(job)
                    }
                    JobsInfoSingleton.jobsList = jobsList

                    getListView()
                } else {
                    var errorDetails = ErrorDetails(resources.getString(R.string.no_error))
                    try {
                        if(response.errorBody() != null) {
                            val rawErrorDetails = response.errorBody()!!.string()
                            val parser = Gson()
                            errorDetails = parser.fromJson(rawErrorDetails, ErrorDetails::class.java)
                        }
                    } catch (e: Exception) {
                        errorDetails = ErrorDetails(resources.getString(R.string.retrieve_error_fails))
                    }

                    val noResults = findViewById<TextView>(R.id.noResultsTextView)
                    noResults.text = errorDetails.message
                }
            }
        })
    }

    private fun getListView() {
        //get the jobs' list
        jobsAdapter = JobsAdapter(this, JobsInfoSingleton.jobsList)

        val jobsListView = findViewById<ListView>(R.id.jobsListView)
        jobsListView.adapter = jobsAdapter

        registerForContextMenu(jobsListView)

        val progressBar = findViewById<ProgressBar>(R.id.jobsProgressBar)
        progressBar.visibility = View.VISIBLE
        jobsListView.visibility = View.GONE

        Timer("jobsLoadingAnimation").schedule(3000) {
            Handler(mainLooper).post{
                progressBar.visibility = View.GONE
                jobsListView.visibility = View.VISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setActionNever()

        if(item.itemId == R.id.javaOption) {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            callAPI(item.title.toString())
        } else if(item.itemId == R.id.javaScriptOption) {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            callAPI(item.title.toString())
        } else if(item.itemId == R.id.pythonOption) {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            callAPI(item.title.toString())
        } else if(item.itemId == R.id.rubyOption) {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            callAPI(item.title.toString())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        //check if the menu is created for the targeted list
        if(v!!.id == R.id.jobsListView) {
            //identify the item selected
            val info = menuInfo as AdapterView.AdapterContextMenuInfo
            menu!!.setHeaderTitle(jobsAdapter.getItem(info.position).title)

            //load the menu
            menuInflater.inflate(R.menu.job_contextual_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo

        //check if was selected the apply option
        if(item.itemId == R.id.jobApplyOption) {
            val builder = AlertDialog.Builder(this)

            builder.setTitle(resources.getString(R.string.confirm))
                .setMessage(resources.getString(R.string.apply_job))
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    jobsAdapter.getItem(info.position).applied = true
                    jobsAdapter.notifyDataSetChanged()
                }
                .setNegativeButton(resources.getString(R.string.no), null)

            builder.create().show()
        }

        //check if was selected the remove application offer
        if(item.itemId == R.id.jobRemoveOption) {
            val builder = AlertDialog.Builder(this)

            builder.setTitle(resources.getString(R.string.confirm))
                .setMessage(resources.getString(R.string.remove_job))
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    jobsAdapter.getItem(info.position).removeApplication = true
                    jobsAdapter.notifyDataSetChanged()
                }
                .setNegativeButton(resources.getString(R.string.no), null)

            builder.create().show()
        }

        //check if was selected the delete element option
        if(item.itemId == R.id.deleteElementOption) {
            val builder = AlertDialog.Builder(this)

            builder.setTitle(resources.getString(R.string.confirm))
                .setMessage(resources.getString(R.string.delete_job))
                .setPositiveButton(resources.getString(R.string.yes)) { _, _->
                    jobsAdapter.removeJob(info.position)
                    jobsAdapter.notifyDataSetChanged()
                }
                .setNegativeButton(resources.getString(R.string.no), null)

            builder.create().show()
        }

        return super.onContextItemSelected(item)
    }

}
