package aziz.ca.ylachat;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MyFriendsActivity extends AppCompatActivity {

    private FirebaseUser current;
    private RecyclerView mFriendsList;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();


        mFriendsList = (RecyclerView) findViewById(R.id.friendsListRec);

        mFriendsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        mFriendsList.setLayoutManager(linearLayoutManager);
        mFriendsList.setClickable(true);

        current = mAuth.getCurrentUser();

        Query q = FirebaseFirestore.getInstance().collection("loggedUsers/"
                + current.getUid() + "/" + current.getEmail() +
                "_DB/friends/addedAndBlockedFriends/addedFriends/myAddedFriends");


        FirestoreRecyclerOptions<Profile> option = new FirestoreRecyclerOptions.Builder<Profile>()
                .setQuery(q, Profile.class)
                .build();

//just need to add the user to the list

         adapter = new FirestoreRecyclerAdapter<Profile, ProfileViewHolder>(option) {



            @Override
            protected void onBindViewHolder(ProfileViewHolder holder, int position, Profile model) {

                holder.setEmail(model.getEmail());
                holder.setName(model.getName());

            }


            @Override
            public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.display_friends, parent, false);


                return new ProfileViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                super.onError(e);
                Log.d("TGHF!!!", "sad");
            }
        };

        adapter.notifyDataSetChanged();
        mFriendsList.setAdapter(adapter);


        //recyc

    }

    @Override
    protected void onStart() {
        super.onStart();
       // current = FirebaseAuth.getInstance().getCurrentUser();


        mFriendsList.addOnItemTouchListener(new RecyclerItemClickListener(this, mFriendsList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final String fiendEmail = ((TextView) mFriendsList.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.emailV)).getText().toString();



                FirebaseFirestore.getInstance().document("loggedUsers/"
                        + current.getUid() + "/" + current.getEmail()
                        + "_DB/messages/myMessages/myMessagesWith_" + fiendEmail)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){


                            DocumentSnapshot documentOfMessages = task.getResult();


                            if(documentOfMessages != null && documentOfMessages.exists()){

                                Map<String,Object> d = documentOfMessages.getData();


                                final String uidEx = (String) d.get("uid");
                                Intent in = RoomActivity.makeIntent(MyFriendsActivity.this);
                                in.putExtra("FRIEND_EMAIL", fiendEmail);
                                in.putExtra("ROOM_UID", uidEx);
                                startActivity(in);





                            }else{

                                Map<String,Object> d = new HashMap<String, Object>();

                                d.put("owner1", current.getEmail());
                                d.put("owner2", fiendEmail);

                                FirebaseFirestore.getInstance().collection("rooms/allRooms/liveRooms")
                                        .add(d).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if(task.isSuccessful()){

                                            String roomUid = task.getResult().getId();

                                            final Map<String,Object> data = new HashMap<String, Object>();
                                            data.put("uid", roomUid);

                                            FirebaseFirestore.getInstance().document("loggedUsers/"
                                                    + current.getUid() + "/" + current.getEmail()
                                                    + "_DB/messages/myMessages/myMessagesWith_" +fiendEmail )
                                                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("ta*/*/*/", "Room uid has been copied to the current user");
                                                }
                                            });



                                            FirebaseFirestore.getInstance().collection("loggedUsers")
                                                    .whereEqualTo("Email", fiendEmail)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                            if(task.isSuccessful()){

                                                                final String userUid = task.getResult().getDocuments().get(0).getId();

                                                                FirebaseFirestore.getInstance().document("loggedUsers/"
                                                                        + userUid + "/" + fiendEmail
                                                                        + "_DB/messages/myMessages/myMessagesWith_" +current.getEmail() )
                                                                        .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d("ta*/*/*/", "Room uid has been copied to friend user");


                                                                        Log.d("Change", "last step " + fiendEmail);// call the other activity


                                                                        Intent in = RoomActivity.makeIntent(MyFriendsActivity.this);
                                                                        in.putExtra("FRIEND_EMAIL", fiendEmail);
                                                                        in.putExtra("ROOM_UID",( (String) data.get("uid")));
                                                                        startActivity(in);


                                                                    }
                                                                });




                                                            }


                                                        }



                                                    });


                                            Log.d("Change", "last step" + fiendEmail);

                                        }


                                    }
                                });





                            }





                        }




                    }
                });










                //Log.d("Change", "last ???");
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));





      adapter.startListening();



    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEmail(String g) {
            TextView email_content = (TextView) mView.findViewById(R.id.emailV);
            email_content.setText(g);
        }

        public void setName(String username) {
            TextView username_content = (TextView) mView.findViewById(R.id.usernameV);
            username_content.setText(username);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);//error
                Intent in2 = new Intent(this, Home2Activity.class);//the sol for now
                this.startActivity(in2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void fingPrint(View v){


        TextView t = (TextView) findViewById(R.id.emailV);

        String s = t.getText().toString();

        Log.d("room", s);

        Toast.makeText(MyFriendsActivity.this, "Please Click on the Email", Toast.LENGTH_LONG).show();








    }



}
