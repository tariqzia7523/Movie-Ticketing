package com.movie.tickets.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.movie.tickets.R
import com.movie.tickets.adapters.MyAdapterforTickits
import com.movie.tickets.databinding.ActivityAddSeatsSlotsBinding
import com.movie.tickets.models.MovieItem
import com.movie.tickets.models.TicketModel


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddSeatsSlotsBinding
    lateinit var myRef: DatabaseReference
    var list = ArrayList<TicketModel>()
    val TAG = "***TicketsSLot"
    lateinit var progressDialog: ProgressDialog
    lateinit var adapter: MyAdapterforTickits
    var user: FirebaseUser? = null
    var userId: String? = ""
    var totalSeats : Int = 0
    var movieModelGlobal : MovieItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddSeatsSlotsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.please_wait))

        user = FirebaseAuth.getInstance().currentUser
        try{
            userId = user!!.uid
        }catch (e : Exception){
            e.printStackTrace()
            userId = ""
        }

        try{
            movieModelGlobal = intent!!.extras!!.get(getString(R.string.movie_model)) as MovieItem
            totalSeats = movieModelGlobal!!.maxSeats!!.toInt()
        }catch (e : java.lang.Exception){
            e.printStackTrace()
        }

        binding.alllist.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapterforTickits(this, list, userId)
        binding.alllist.adapter = adapter

        myRef = FirebaseDatabase.getInstance().getReference("TICKETS").child(movieModelGlobal!!.id!!)
        progressDialog.show()



        binding.addTicket.setOnClickListener {
            updatetICKETS(null)
        }

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }

        getAllData()

        try{
            if (!userId.equals(getString(R.string.admin_id))) {
                binding.addTicket.visibility = View.GONE
                binding.logout.visibility = View.GONE
            }
        }catch (e : Exception){
            e.printStackTrace()
            Log.e("***TAG","Exceptoion a gai ")
        }
    }

    private fun getAllData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    var avalibel = 0
                    Log.e(TAG, "user data changed")
                    Log.e(TAG, "user data changed " + dataSnapshot.key)
                    list.clear()
                    for (dataSnapshot1 in dataSnapshot.children) {
                        val model: TicketModel? =
                            dataSnapshot1.getValue(TicketModel::class.java)
                        model!!.id = dataSnapshot1.key
                        if (model.status.contains(getString(R.string.avalibel), true))
                            avalibel += 1
                        list.add(model)
                    }
                    adapter.notifyDataSetChanged()
                    progressDialog.dismiss()

                    if (!userId.equals(getString(R.string.admin_id)))
                        binding.total.text =
                            "Welcome User\n" + getString(R.string.total_seats) + avalibel +" / "+ totalSeats
                    else
                        binding.total.text =
                            "Welcome Admin\n" + getString(R.string.total_seats) + avalibel +" / "+ totalSeats
                } catch (c: Exception) {
                    c.printStackTrace()
                    Log.e(TAG, "exception while retriving data from users")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "exception")
                Log.e(TAG, databaseError.message)
                databaseError.toException().printStackTrace()
                progressDialog.dismiss()
            }
        })
    }


    fun updatetICKETS(ticke: TicketModel?) {
        if (!userId.equals(getString(R.string.admin_id))) {
            return
        }
        Log.e("***Check","Total seats "+ totalSeats)
        Log.e("***Check","list size "+ list.size)
        if(list.size >= totalSeats){
            Toast.makeText(this@MainActivity, getString(R.string.add_more_seats_first), Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_seat_pop_up)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val discription: AppCompatEditText = dialog.findViewById(R.id.discription)
        val status: Spinner = dialog.findViewById(R.id.status)
        val add_slots: AppCompatTextView = dialog.findViewById(R.id.add_slots)

        if (ticke != null) {
            discription.setText(ticke.description)
            if (ticke.status.contains(getString(R.string.avalibel), false))
                status.setSelection(1)
            else
                status.setSelection(2)
            add_slots.text = getString(R.string.update_seats)
        }


        add_slots.setOnClickListener {
            if (!discription.text.toString().equals("") || status.selectedItemPosition == 0) {
                Toast.makeText(this@MainActivity, getString(R.string.add_all_data), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ticke == null) {
                val model = TicketModel()
                model.status = status.selectedItem.toString()
                model.description = discription.text.toString()
                addTicket(model)
            } else {
                ticke.description = discription.text.toString()
                ticke.status = status.selectedItem.toString()
                updateData(ticke)
            }
            dialog.dismiss()
        }

        dialog.show()
        val window: Window = dialog.getWindow()!!
        window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    }


    fun deleteTicket(model: TicketModel) {
        if (!userId.equals(getString(R.string.admin_id))) {
            return
        }
        progressDialog.show()
        Log.e("valueis",myRef.key.toString())
        myRef.child(model.id!!).removeValue()
    }

    fun updateData(model: TicketModel) {
        if (!userId.equals(getString(R.string.admin_id))) {
            return
        }
        progressDialog.show()
        val update = HashMap<String, Any>()
        update.put("status", model.status)
        update.put("description", model.description)

        myRef.child(model.id!!).updateChildren(update)
    }

    fun addTicket(model: TicketModel) {
        if (!userId.equals(getString(R.string.admin_id))) {
            return
        }
        progressDialog.show()
        myRef.push().setValue(model)
    }


}