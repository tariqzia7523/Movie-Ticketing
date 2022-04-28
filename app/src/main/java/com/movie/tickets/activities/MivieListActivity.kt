package com.movie.tickets.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.movie.tickets.R
import com.movie.tickets.adapters.MyAdapterformovies
import com.movie.tickets.databinding.ActivityMivieListBinding
import com.movie.tickets.models.MovieItem

class MivieListActivity : AppCompatActivity() {
    lateinit var binding: ActivityMivieListBinding
    lateinit var myRef: DatabaseReference
    var list = ArrayList<MovieItem>()
    lateinit var progressDialog: ProgressDialog
    lateinit var adapter: MyAdapterformovies
    var user: FirebaseUser? = null
    var userId: String? = ""
    val TAG = "***Movie"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMivieListBinding.inflate(layoutInflater)
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

        binding.movieList.layoutManager = GridLayoutManager(this,2)
        adapter = MyAdapterformovies(this, list, userId)
        binding.movieList.adapter = adapter

        myRef = FirebaseDatabase.getInstance().getReference("MOVIES")
        progressDialog.show()

        getAllData()

        try{
            if (!userId.equals(getString(R.string.admin_id))) {
                binding.addMovie.visibility = View.GONE
            }else{
                binding.addMovie.setOnClickListener {
                    startActivity(Intent(this@MivieListActivity,AddMoviesActivity::class.java))
                }
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
                    Log.e(TAG, "user data changed")
                    Log.e(TAG, "user data changed " + dataSnapshot.key)
                    list.clear()
                    for (dataSnapshot1 in dataSnapshot.children) {
                        val item: MovieItem? = dataSnapshot1.getValue(MovieItem::class.java)
                        item!!.id = dataSnapshot1.key
                        list.add(item)
                    }
                    adapter.notifyDataSetChanged()
                    progressDialog.dismiss()


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
}