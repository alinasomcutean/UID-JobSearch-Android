package com.example.jobssearch.adapters

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.jobssearch.R
import com.example.jobssearch.model.Job
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*

class JobsAdapter (private val context: Context,
                    private var dataSource: ArrayList<Job>) : BaseAdapter() {

    // get a reference to the LayoutInflater service
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @TargetApi(Build.VERSION_CODES.M)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position)

        // reuse OR expand the view for one entry into the list
        val rowView = inflater.inflate(R.layout.jobs_list_element, parent, false)

        //first, set the star invisible
        val starImageView = rowView.findViewById<ImageView>(R.id.starImageView)
        starImageView.visibility = View.GONE

        //set the x image invisible
        val removeImageView = rowView.findViewById<ImageView>(R.id.removeImageView)
        removeImageView.visibility = View.GONE

        //get the job's title
        val jobTitleTextView = rowView.findViewById<TextView>(R.id.jobTitleTextView)
        jobTitleTextView.text = item.title

        //get the job's type
        val jobTypeTextView = rowView.findViewById<TextView>(R.id.jobTypeTextView)
        jobTypeTextView.text = item.type

        //get the company logo
        val companyLogoImageView = rowView.findViewById<ImageView>(R.id.companyLogoImageView)
        Picasso.with(context).load(item.company_logo).placeholder(R.drawable.no_logo).into(companyLogoImageView)

        //get the company's name
        val companyNameTextView = rowView.findViewById<TextView>(R.id.companyTextView)
        companyNameTextView.text = item.company

        //get location
        val locationTextView = rowView.findViewById<TextView>(R.id.locationTextView)
        locationTextView.text = item.location

        if(item.applied) {
            rowView.setBackgroundColor(context.getColor(R.color.lightGreen))
            starImageView.visibility = View.VISIBLE
        }

        if(item.applied && item.removeApplication) {
            rowView.setBackgroundColor(context.getColor(R.color.lightRed))
            starImageView.visibility = View.GONE
            removeImageView.visibility = View.VISIBLE
        }

        return rowView
    }

    override fun getItem(position: Int): Job {
        return dataSource.elementAt(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    fun removeJob(position: Int) {
        dataSource.removeAt(position)
    }
}