package com.example.securitycamera

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class ViewFullImage : ComponentActivity() {
    private val firebaseStorageReference = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageURL = intent.getStringExtra("imgURL")

        if (imageURL == null)
            finish()

        val imageView = findViewById<ImageView>(R.id.image_full_holder)

        Glide.with(baseContext)
            .load(firebaseStorageReference.child(imageURL!!))
            .into(imageView)
    }
}