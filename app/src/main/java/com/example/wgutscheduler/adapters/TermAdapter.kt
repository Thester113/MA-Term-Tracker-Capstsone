package com.example.wgutscheduler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Term> = object : DiffUtil.ItemCallback<Term>() {
            override fun areItemsTheSame(
                    oldTerm: Term, newTerm: Term): Boolean {
                // Term properties may have changed if reloaded from the DB, but ID is fixed
                return oldTerm.term_id == newTerm.term_id
            }

            override fun areContentsTheSame(
                    oldTerm: Term, newTerm: Term): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldTerm.term_name == newTerm.term_name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = layoutInflater.inflate(R.layout.item_search, parent, false)
        return object : RecyclerView.ViewHolder(layout){ }
    }
}