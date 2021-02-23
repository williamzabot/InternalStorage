package com.williamzabot.cameratest

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.System.currentTimeMillis

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    var bitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botaoAbreCamera.setOnClickListener {
            val cam = Intent(ACTION_IMAGE_CAPTURE)
            startActivityForResult(cam, 1)
        }

        botaoAbreGaleria.setOnClickListener {
            val gal = Intent()
            gal.type = "image/*"
            gal.action = ACTION_GET_CONTENT
            startActivityForResult(gal, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    (data?.extras?.get("data") as? Bitmap)?.let {
                        bitmap = it
                        imagem.setImageBitmap(it)
                        saveImageInternalStorage()
                    }
                }
                2 -> imagem.setImageURI(data?.data)
            }
        }
    }

    private fun saveImageInternalStorage() {
        val permission = WRITE_EXTERNAL_STORAGE
        val isGranted = checkSelfPermission(permission)
        if (isGranted != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission), 1)
        } else {
            val directory = File(getExternalStorageDirectory(), "/turmakotlin/")
            directory.mkdir()
            val file = File(directory, currentTimeMillis().toString() + ".jpg")
            val os = FileOutputStream(file)
            os.write(convertBitmapParaByte(bitmap!!))
            os.close()
            Toast.makeText(
                this,
                "Salvo com sucesso em ${file.path}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun convertBitmapParaByte(bitmap: Bitmap): ByteArray {
        return ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
        }.toByteArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1 && checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED){
            saveImageInternalStorage()
        }
    }
}