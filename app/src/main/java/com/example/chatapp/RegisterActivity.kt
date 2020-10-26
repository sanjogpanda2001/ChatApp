package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button.setOnClickListener {
            performRegister()
        }
        already_have_account.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_register.setOnClickListener {
            Log.d("MainActivity","select photo")
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }
var selectedphotoUri: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){
        Log.d("RegisterActivity","Photo was selected")
        selectedphotoUri=data.data
        val bitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,selectedphotoUri)
        val bitmapDrawable=BitmapDrawable(bitmap)
        Select_photo_imgview.setImageBitmap(bitmap)
        select_photo_register.alpha=0f
    }
    }
    private fun performRegister(){
        //firebse auth
        val email=email_edittext.text.toString()
        val password=password_edittext.text.toString()
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"please enter credentials",Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener
                //if successful
                Log.d("Main","succesfully created user with id ${it.result?.user?.uid}")
                uploadImagetoFirebaseStorage()
            }.addOnFailureListener{
                Log.d("Main","failed to create user: ${it.message}")
                Toast.makeText(this,"failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImagetoFirebaseStorage() {
        var filename=UUID.randomUUID().toString()
 val ref=FirebaseStorage.getInstance().getReference("/images/$filename")
    ref.putFile(selectedphotoUri!!)
        .addOnSuccessListener {
            Log.d("RegisterActivity","successfully uploaded image: ${it.metadata?.path}")
       ref.downloadUrl.addOnSuccessListener {
           Log.d("RegisterActivity","file location $it")
           saveUserToDatabase(it.toString())
       }
        }.addOnFailureListener{

        }
    }
    private fun saveUserToDatabase(profileimgurl: String){
       val uid= FirebaseAuth.getInstance().uid ?:""
       val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user=User(uid,username_edittext.text.toString(),profileimgurl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Registeractivity","saved user to firebase db")

val intent=Intent(this,LatestMessagesActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//back to home not to previous activity

                startActivity(intent)
            }
    }
}
@Parcelize
class User(val uid:String,val username:String,val profileimgurl:String):Parcelable{
    constructor():this("","","")
}
