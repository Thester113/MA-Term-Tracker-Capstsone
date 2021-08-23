package com.example.wgutscheduler.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wgutscheduler.Activity.CourseDetails
import com.example.wgutscheduler.Activity.TermDetails
import com.example.wgutscheduler.Entity.Term
import com.example.wgutscheduler.R


/**
 * Created by Thomas Hester on 8/10/21 in the Software Development program.
 */
 class TermAdapter(private val layoutInflater: LayoutInflater) : ListAdapter<Term, RecyclerView.ViewHolder?>(TermAdapter.DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView
        val item = getItem(position)
        val itemType = view.findViewById<TextView>(R.id.itemType)
        val itemName = view.findViewById<TextView>(R.id.itemName)
        itemType.text = view.context.getString(R.string.Term)
        itemName.text = item?.term_name.orEmpty()
        view.setOnClickListener{
            val intent = Intent(it.context, TermDetails::class.java)
            intent.putExtra("termID",item.term_id)
            ContextCompat.startActivity(it.context, intent, null)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Term> = object : DiffUtil.ItemCallback<Term>() {
            override fun areItemsTheSame(
                    oldTerm: Term, newTerm: Term): Boolean {
                return oldTerm.term_id == newTerm.term_id
            }

            override fun areContentsTheSame(
                    oldTerm: Term, newTerm: Term): Boolean {
                return oldTerm.term_name == newTerm.term_name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = layoutInflater.inflate(R.layout.item_search, parent, false)
        return object : RecyclerView.ViewHolder(layout){ }
    }
}