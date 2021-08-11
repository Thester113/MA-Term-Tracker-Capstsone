package com.example.wgutscheduler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wgutscheduler.Entity.Course
import com.example.wgutscheduler.R

/**
 * Created by Thomas Hester on 8/11/21 in the Software Development program.
 */
class CourseAdapter (private val layoutInflater: LayoutInflater) : ListAdapter<Course, RecyclerView.ViewHolder?>(CourseAdapter.DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView
        val item = getItem(position)
        val itemType = view.findViewById<TextView>(R.id.itemType)
        val itemName = view.findViewById<TextView>(R.id.itemName)
        itemType.text = view.context.getString(R.string.Course)
        itemName.text = item?.course_name.orEmpty()
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Course> = object : DiffUtil.ItemCallback<Course>() {
            override fun areItemsTheSame(
                    oldCourse: Course, newCourse: Course): Boolean {
                // Course properties may have changed if reloaded from the DB, but ID is fixed
                return oldCourse.course_id == newCourse.course_id
            }

            override fun areContentsTheSame(
                    oldCourse: Course, newCourse: Course): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldCourse.course_name == newCourse.course_name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = layoutInflater.inflate(R.layout.item_search, parent, false)
        return object : RecyclerView.ViewHolder(layout){ }
    }
}