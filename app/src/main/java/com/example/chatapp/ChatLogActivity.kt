package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.textView_to_row
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
companion object{
    val TAG="ChatLog"
}
    val adapter=GroupAdapter<GroupieViewHolder>()

    var toUser:User?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
recyclerview_chatlog.adapter=adapter
//val username=intent.getStringExtra("User_key")
  toUser=intent.getParcelableExtra<User>("User_key")
        if (toUser != null) {
            supportActionBar?.title=toUser!!.username
        }

     //   setUpdummyData()
ListenForMessages()
        send_chatlog.setOnClickListener {
            Log.d(TAG,"attempt to send message")
            performSendMessage()
        }
    }

    private fun ListenForMessages() {
        val ref=FirebaseDatabase.getInstance().getReference("/message")
    ref.addChildEventListener(object :ChildEventListener{
        override fun onCancelled(error: DatabaseError) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val chatmessage=snapshot.getValue(ChatMessage::class.java)
            if (chatmessage != null) {
                Log.d(TAG, chatmessage.text)
                if (chatmessage.fromId == FirebaseAuth.getInstance().uid) {
                    val currentuser=LatestMessagesActivity.currentUser ?: return
                    adapter.add(chatFromItem(chatmessage.text,currentuser))
                } else {
                    //val toUser=intent.getParcelableExtra<User>("User_key")
                    adapter.add(chatToItem(chatmessage.text,toUser!!))
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

    })
    }

    class ChatMessage(val id:String,val text:String,val fromId:String,val toId:String,val timestamp:Long){
        constructor():this("","","","",-1)
    }
    private fun performSendMessage() {
        //how do we send a message to firebase
        val user=intent.getParcelableExtra<User>("User_key")
        val text=editText_chatlog.text.toString()
        val fromId=FirebaseAuth.getInstance().uid
        val toId= user?.uid
        val reference=FirebaseDatabase.getInstance()
            .getReference("/message").push()
        val chatMessage=ChatMessage(reference.key!!,text,fromId!!,toId!!,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"saved our chat message...${reference.key}")
            }

    }

//    private fun setUpdummyData() {
//        val adapter= GroupAdapter<GroupieViewHolder>()
//        adapter.add(chatFromItem("from message"))
//        adapter.add(chatToItem("to message"))
//
//        recyclerview_chatlog.adapter=adapter
//    }
}

class chatFromItem(val text:String,val user:User):Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return  R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
viewHolder.itemView.textView_to_row.text=text

        val uri=user.profileimgurl
        val targetImageView=viewHolder.itemView.imageView_f
        Picasso.get().load(uri).into(targetImageView)
    }

}
class chatToItem(val text:String, val user: User):Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return  R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.textView_to_row.text=text

        //load image
        val uri=user.profileimgurl
        val targetImageView=viewHolder.itemView.imageView_to
        Picasso.get().load(uri).into(targetImageView)
    }

}