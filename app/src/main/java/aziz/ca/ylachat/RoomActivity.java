package aziz.ca.ylachat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class RoomActivity extends AppCompatActivity {
    private String friendEmail;
    private String roomUid;
    private String uid = "i'm empty";
    private TextView userEmail;
    private FirebaseUser current_user;
    private DocumentReference ref;
    private RecyclerView mMessageList;
    private FirestoreRecyclerAdapter adapter;
    private EditText content_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        friendEmail = getIntent().getStringExtra("FRIEND_EMAIL");
        roomUid = getIntent().getStringExtra("ROOM_UID");
        uid = roomUid;

        userEmail = (TextView) findViewById(R.id.userInfoTV);

        userEmail.setText("" + friendEmail);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        content_message = (EditText) findViewById(R.id.editMessageC);



        mMessageList = (RecyclerView) findViewById(R.id.messagesRec);

        mMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        mMessageList.setLayoutManager(linearLayoutManager);
        mMessageList.setClickable(true);



        Query q = FirebaseFirestore.getInstance().collection("rooms/allRooms/liveRooms/" + roomUid + "/allMessages");


        FirestoreRecyclerOptions<Message> option = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(q, Message.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<Message,MessageViewHolder>(option){

            @Override
            protected void onBindViewHolder(MessageViewHolder holder, int position, Message model) {

                holder.setContent(model.getContent());



            }

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.receiver_msg_layout, parent, false);


                return new MessageViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                super.onError(e);
                Log.d("TGHF!!!", "sad");
            }






        };

        adapter.notifyDataSetChanged();
        mMessageList.setAdapter(adapter);



        Log.d("getUID+em", friendEmail);
        Log.d("getUID+ui", roomUid);







       /*
        ref = FirebaseFirestore.getInstance().document("loggedUsers/" + current_user.getUid()
                + "/" + current_user.getEmail() + "_DB/messages/myMessages/myMessagesWith_" + friendEmail);
        */








    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setContent(String g) {
            TextView message_content = (TextView) mView.findViewById(R.id.messageText);
            message_content.setText(g);
        }



    }



    public void onSendButtonClicked(View view){

        final String msg = content_message.getText().toString().trim();


        if(!msg.isEmpty()){

            Map<String,Object> data = new HashMap<String, Object>();
            data.put("content", msg);

            FirebaseFirestore.getInstance().collection("rooms/allRooms/liveRooms/" + uid + "/allMessages").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {

                    if(task.isSuccessful()){
                        content_message.setText("");
                    }

                }
            });




        }


        //Log.d("getUID+Send", uid);





    }


    public void setUid(String uid){
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, RoomActivity.class);
    }
}
