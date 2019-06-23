package com.jayanti.imageupload

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


var PICK_IMAGE_REQUEST = 1;

class MainActivity() : AppCompatActivity(), View.OnClickListener{
    override fun onClick(p0: View) {
        if(p0 == mButtonChooseImage)
        {
            showFileChooser()
        }
        else if (p0 == mButtonUpload)
        {
            uploadFile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                image_view!!.setImageBitmap(bitmap)
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
        }

    }


    private fun uploadFile() {
        if(filePath != null) {
            Log.e("THERE","${filePath}")
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val imageRef = storageReference!!.child("images/" + UUID.randomUUID().toString())
            imageRef.putFile(filePath!!).addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"File Uploaded",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"Upload Failed",Toast.LENGTH_LONG).show()
            }
            .addOnProgressListener { takeSnapShot ->
                val progress = 100 * takeSnapShot.bytesTransferred/takeSnapShot.totalByteCount
                progressDialog.setMessage("Uploaded "+progress.toInt()+"%...")
            }
        }
    }

    private fun showFileChooser() {
        val intent= Intent()
        intent.type= "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"select picture "), PICK_IMAGE_REQUEST)
    }

    lateinit var mButtonChooseImage: Button
    lateinit var mButtonUpload: Button
    lateinit var mImageView: ImageView

    lateinit var filePath: Uri
    internal var storage: FirebaseStorage?= null
    internal var storageReference:StorageReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mButtonChooseImage= findViewById(R.id.button_choose_image)
        mButtonUpload = findViewById(R.id.button_upload)
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        mButtonChooseImage.setOnClickListener(this)
        mButtonUpload.setOnClickListener(this)
    }


}



