package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.userrow_newmessage.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title ="Select User"

//val adapter=GroupAdapter<GroupieViewHolder>()

       // adapter.add(UserItem())
        //adapter.add(UserItem())
        //adapter.add(UserItem())
       // recyclerview_newMessage.adapter=adapter

        fetchUsers()


    }

    private fun fetchUsers() {
      val ref=  FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter=GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    Log.d("Newmessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))

                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem=item as UserItem
                    val intent=Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra("User_key",userItem.user)
                    startActivity(intent)
                    finish()
                }
                recyclerview_newMessage.adapter=adapter
            }

        })
    }
}

class UserItem(val user:User): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.userrow_newmessage
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
         //will be called later for each user object
        viewHolder.itemView.username_tv_newmessage.text=user.username
        Picasso.get().load(user.profileimgurl).into(viewHolder.itemView.imageView_newmessagerow)
    }

}


//class CustomAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>{
  //very long code
//}