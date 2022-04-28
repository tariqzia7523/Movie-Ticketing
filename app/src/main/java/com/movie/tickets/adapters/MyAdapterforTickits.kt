package com.movie.tickets.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.movie.tickets.R
import com.movie.tickets.activities.MainActivity
import com.movie.tickets.models.TicketModel
import kotlin.collections.ArrayList


class MyAdapterforTickits(var context: Context, var data: ArrayList<TicketModel>, var userId : String?) : RecyclerView.Adapter<MyAdapterforTickits.MyViewHolder>() {
    var TAG = "***Adapter"

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var key: AppCompatTextView
        var discription: AppCompatTextView
        var status: AppCompatTextView
        var edit: AppCompatImageView
        var delete: AppCompatImageView
        var bottom_option: LinearLayoutCompat
        init {
            key = view.findViewById(R.id.key_id)
            discription = view.findViewById(R.id.discription)
            status = view.findViewById(R.id.status)
            edit = view.findViewById(R.id.edit)
            delete = view.findViewById(R.id.delete)
            bottom_option = view.findViewById(R.id.bottom_option)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.key.text = context.getString(R.string.ticket_number)+(position + 1).toString()
        holder.status.text = data[position].status
        holder.discription.text = data[position].description
        if(!userId.equals(context.getString(R.string.admin_id))) {
            holder.edit.visibility = View.GONE
            holder.delete.visibility = View.GONE
            holder.bottom_option.visibility = View.GONE
        }
        else {
            holder.edit.visibility = View.VISIBLE
            holder.delete.visibility = View.VISIBLE
            holder.bottom_option.visibility = View.VISIBLE
        }
        holder.edit.setOnClickListener {
            (context as MainActivity).updatetICKETS(data[position])
        }
        holder.delete.setOnClickListener {
            try{
                deleteItem(data[position])
            }catch ( e : Exception){
                e.printStackTrace()

            }
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun deleteItem(parkingModel: TicketModel){
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_confirmation))
            .setMessage(context.getString(R.string.dleteion_dis)) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog, which ->

                data.remove(parkingModel)
                notifyDataSetChanged()
                (context as MainActivity).deleteTicket(parkingModel)
                dialog.dismiss()
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setFilter(newList: ArrayList<TicketModel>) {
        data = ArrayList()
        data.addAll(newList)
        notifyDataSetChanged()
    }


}