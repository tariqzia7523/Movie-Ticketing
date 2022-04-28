package com.movie.tickets.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.movie.tickets.R
import com.movie.tickets.databinding.ActivityAddMoviesBinding
import com.movie.tickets.models.MovieItem
import java.io.FileNotFoundException
import java.io.InputStream

class AddMoviesActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddMoviesBinding
    var movieModelGlobal : MovieItem? = null
    var imageUri : Uri? = null
    var userId: String? = ""
    lateinit var progressDialog: ProgressDialog
    val myRef = FirebaseDatabase.getInstance().getReference("MOVIES")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.please_wait))


        try{
            movieModelGlobal = intent!!.extras!!.get(getString(R.string.movie_model)) as MovieItem
            Glide.with(this@AddMoviesActivity).load(movieModelGlobal!!.imageLink ).into(binding.image)
            binding.maxSeats.setText(movieModelGlobal!!.maxSeats)
            binding.movieName.setText(movieModelGlobal!!.name)
//            binding.save.text = getString(R.string.edit)

        }catch (e : Exception){
            e.printStackTrace()
            binding.delete.visibility = View.GONE
            binding.viewSeats.visibility = View.GONE
            movieModelGlobal = MovieItem()
            movieModelGlobal!!.id = myRef.push().key
        }

        val user = FirebaseAuth.getInstance().currentUser
        userId = try {
            user!!.uid
        }catch (e : Exception){
            e.printStackTrace()
            ""
        }

        binding.image.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 100)
        }

        binding.save.setOnClickListener {
            if(binding.movieName.text.toString().equals("") || binding.maxSeats.text.toString().equals("")){
                Toast.makeText(this@AddMoviesActivity, getString(R.string.add_all_data), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(imageUri == null && movieModelGlobal!!.imageLink.equals("")){
                val myRef = FirebaseDatabase.getInstance().getReference("MOVIES")
                progressDialog.show()
                val update = HashMap<String , Any>()
                update.put("name",binding.movieName.text.toString())
                update.put("imageLink",movieModelGlobal!!.imageLink)
                update.put("maxSeats", binding.maxSeats.text.toString())
                myRef.child(movieModelGlobal!!.id!!).updateChildren(update).addOnSuccessListener {
                    Toast.makeText(this@AddMoviesActivity, "Saved Successfully", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    startActivity(Intent(this@AddMoviesActivity,MivieListActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }else{
                addMoview(this@AddMoviesActivity,movieModelGlobal!!)
            }
        }
        binding.delete.setOnClickListener {
            val myRef = FirebaseDatabase.getInstance().getReference("MOVIES")
            if(movieModelGlobal != null)
            myRef.child(movieModelGlobal!!.id!!).removeValue().addOnSuccessListener {
                Toast.makeText(this@AddMoviesActivity, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AddMoviesActivity,MivieListActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }

        binding.viewSeats.setOnClickListener {
            startActivity(Intent(this@AddMoviesActivity,MainActivity::class.java).putExtra(getString(R.string.movie_model),movieModelGlobal)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data!!.data
                val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                binding.image.setImageBitmap(selectedImage)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this@AddMoviesActivity, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this@AddMoviesActivity, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }


    fun addMoview(context: Context,  movieItem: MovieItem ){
        progressDialog.show()
        if(imageUri != null){
            val mStorageRef = FirebaseStorage.getInstance().reference
            val riversRef = mStorageRef.child( movieItem.id!!);
            riversRef.putFile(imageUri!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot?> {
                    riversRef.getDownloadUrl().addOnSuccessListener(
                        OnSuccessListener<Uri> { uri ->
                            Log.d("image View", "onSuccess: uri= $uri")
                            movieModelGlobal!!.imageLink =  uri.toString()
                            addMovie(movieModelGlobal!!)
                            progressDialog.dismiss()
                        })
                })
        }else{
            addMovie(movieModelGlobal!!)
        }
    }

    fun addMovie(movieModel : MovieItem){
        progressDialog.show()
        val update = HashMap<String , Any>()
        update.put("name",binding.movieName.text.toString())
        update.put("imageLink",movieModel.imageLink)
        update.put("maxSeats", binding.maxSeats.text.toString())
        myRef.child(movieModelGlobal!!.id!!).updateChildren(update).addOnSuccessListener {
            Toast.makeText(this@AddMoviesActivity, "Saved Successfully", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            startActivity(Intent(this@AddMoviesActivity,MivieListActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}