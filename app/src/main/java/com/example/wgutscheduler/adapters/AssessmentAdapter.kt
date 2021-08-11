package com.example.wgutscheduler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Assessment> = object : DiffUtil.ItemCallback<Assessment>() {
            override fun areItemsTheSame(
                    oldAssessment: Assessment, newAssessment: Assessment): Boolean {
                // Assessment properties may have changed if reloaded from the DB, but ID is fixed
                return oldAssessment.assessment_id == newAssessment.assessment_id
            }

            override fun areContentsTheSame(
                    oldAssessment: Assessment, newAssessment: Assessment): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldAssessment.assessment_name == newAssessment.assessment_name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = layoutInflater.inflate(R.layout.item_search, parent, false)
        return object : RecyclerView.ViewHolder(layout){ }
    }
}