package com.example.wgutscheduler.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wgutscheduler.Activity.AssessmentDetails
import com.example.wgutscheduler.Activity.SearchActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Assessment
import com.example.wgutscheduler.R

/**
 * Created by Thomas Hester on 8/11/21 in the Software Development program.
 */
class AssessmentAdapter (private val layoutInflater: LayoutInflater) : ListAdapter<Assessment, RecyclerView.ViewHolder?>(AssessmentAdapter.DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView
        val item = getItem(position)
        val itemType = view.findViewById<TextView>(R.id.itemType)
        val itemName = view.findViewById<TextView>(R.id.itemName)
        itemType.text = view.context.getString(R.string.Assessment)
        itemName.text = item?.assessment_name.orEmpty()
        view.setOnClickListener{
            val db = DataBase.getInstance(it.context.applicationContext)!!
            val intent = Intent(it.context, AssessmentDetails::class.java)
            intent.putExtra("termID",db.assessmentDao()?.getTermId(item.course_id_fk) )
            intent.putExtra("courseID", item.course_id_fk)
            intent.putExtra("assessmentID", item.assessment_id)
            startActivity(it.context, intent,null)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Assessment> = object : DiffUtil.ItemCallback<Assessment>() {
            override fun areItemsTheSame(
                    oldAssessment: Assessment, newAssessment: Assessment): Boolean {
                return oldAssessment.assessment_id == newAssessment.assessment_id
            }

            override fun areContentsTheSame(
                    oldAssessment: Assessment, newAssessment: Assessment): Boolean {
                return oldAssessment.assessment_name == newAssessment.assessment_name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = layoutInflater.inflate(R.layout.item_search, parent, false)

        return object : RecyclerView.ViewHolder(layout){ }
    }
}