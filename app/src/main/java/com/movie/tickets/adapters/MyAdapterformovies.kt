package com.movie.tickets.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.movie.tickets.R
import com.movie.tickets.activities.AddMoviesActivity
import com.movie.tickets.activities.MainActivity
import com.movie.tickets.models.MovieItem
import kotlin.collections.ArrayList


class MyAdapterformovies(var context: Context, var data: ArrayList<MovieItem>,var userId : String?) : RecyclerView.Adapter<MyAdapterformovies.MyViewHolder>() {
    var TAG = "***Adapter"

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var movie_name: AppCompatTextView
        var image_view: AppCompatImageView
        init {
            movie_name = view.findViewById(R.id.movie_name)
            image_view = view.findViewById(R.id.image_view)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(data[position].imageLink ).into(holder.image_view)
        holder.movie_name.text = data[position].name

       holder.itemView.setOnClickListener {
           if(userId!!.equals(context.getString(R.string.admin_id)))
                context.startActivity(Intent(context,AddMoviesActivity::class.java).putExtra(context.getString(R.string.movie_model),data[position]))
           else
               context.startActivity(Intent(context,MainActivity::class.java).putExtra(context.getString(R.string.movie_model),data[position]))
       }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun deleteItem(movieItem: MovieItem){
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_confirmation))
            .setMessage(context.getString(R.string.dleteion_dis)) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog, which ->

                data.remove(movieItem)
                notifyDataSetChanged()
//                if(data.size == 0)
//                    (context as MainActivity).deleteParking(parkingModel)
                dialog.dismiss()
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }




}