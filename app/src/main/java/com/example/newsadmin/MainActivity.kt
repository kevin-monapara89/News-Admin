package com.example.newsadmin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newsadmin.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI

class MainActivity : AppCompatActivity() {

    lateinit var uri: Uri
    var IMAGE_CODE = 8
    lateinit var binding: ActivityMainBinding
    lateinit var storageRef: StorageReference
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance().reference
        dbRef = FirebaseDatabase.getInstance().reference

        binding.addimg.setOnClickListener {

            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_CODE)
        }

        binding.uploadimg.setOnClickListener {

            val ref = storageRef.child("images/${uri.lastPathSegment}.jpg")
            var uploadTask = ref.putFile(uri)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    var key = dbRef.root.push().key
                    dbRef.root.child("Imageas").child(key!!).child("image").setValue(downloadUri)
                } else {
                    // Handle failures
                    // ...
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_CODE) {
                var uri = data?.data
                binding.imgposter.setImageURI(uri)
            }
        }
    }
}