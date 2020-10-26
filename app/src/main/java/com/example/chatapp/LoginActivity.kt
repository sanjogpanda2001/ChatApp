package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
login_button.setOnClickListener {
    val email=email_login.text.toString()
    val pass=password_login.text.toString()
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass)
       /* .addOnCompleteListener{

        }.addOnFailureListener{

        } */
}
back_to_regs.setOnClickListener {
    finish()
}
    }
}
