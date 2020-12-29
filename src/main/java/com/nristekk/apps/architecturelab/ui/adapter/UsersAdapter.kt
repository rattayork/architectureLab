package com.nristekk.apps.architecturelab.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nristekk.apps.architecturelab.R
import com.nristekk.apps.architecturelab.database.items.User


class UsersAdapter(private val userList:List<User>): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardFirstName:TextView
        val cardLastName:TextView
        val cardEmailAddr:TextView
        val cardTel:TextView
        init {
            cardFirstName = view.findViewById<TextView>(R.id.cardFirstName)
            cardLastName = view.findViewById<TextView>(R.id.cardLastName)
            cardEmailAddr = view.findViewById<TextView>(R.id.cardEmailAddr)
            cardTel = view.findViewById<TextView>(R.id.cardTel)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_user, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList.get(position)

        holder.cardFirstName.text = user.firstName
        holder.cardLastName.text = "${user.lastName} (${user.age})"
        holder.cardEmailAddr.text = user.email
        holder.cardTel.text = user.tel

    }

    override fun getItemCount(): Int {
        return userList.size
    }



}