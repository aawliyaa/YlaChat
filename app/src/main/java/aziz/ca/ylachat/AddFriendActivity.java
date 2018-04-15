package aziz.ca.ylachat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class AddFriendActivity extends AppCompatActivity {


    private TextView confirmation;
    private DocumentReference mDBR;
    private CollectionReference mCDBR;
    private EditText email;
    private String uuid;
    private  Map<String,Object> dataOfTheUser, dataOfAddedFriend, universalMap;
    private boolean checkForUserFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       email = (EditText) findViewById(R.id.addedFriendET);

       confirmation = (TextView) findViewById(R.id.conf);

       confirmation.setVisibility(View.INVISIBLE);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);//error
                Intent in2 = new Intent(this, Home2Activity.class);//the sol for now
                this.startActivity(in2);
                finish();
                return true;
                //add default
        }
        return super.onOptionsItemSelected(item);
    }




    public void onAddFriendClicked(View view){

        final String errorMsg, fAddedEmail;
        errorMsg = "Try Again !";
        fAddedEmail = email.getText().toString().trim();

        /**
         * TODO
         *  check for isEmpty error otherwise.
         *
         */
//--------------------------------------------------------------------------------------------------------------------------------------------------------------START FOR THE USER WHO's ADDING-----
        if(!fAddedEmail.isEmpty()){

            mDBR = FirebaseFirestore.getInstance().document("users/" + fAddedEmail);
            Task<DocumentSnapshot> documentSnapshotTask = mDBR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //  int num = 0;

                        final DocumentSnapshot addedUserInfo = task.getResult();


                        if(addedUserInfo != null && addedUserInfo.exists()){
                            //user is there

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

                            String getUID = current_user.getUid();
                            final String getEmail = current_user.getEmail();

                            DocumentReference ref = FirebaseFirestore.getInstance()
                                    .document("loggedUsers/" + getUID + "/" + getEmail + "_DB"
                                            + "/friends/addedAndBlockedFriends/addedFriends");

                            final String refPath = ref.getPath();
                            Log.d("ERR***!!", "" + refPath);




                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){

                                        DocumentSnapshot d2 = task.getResult();

                                        if(d2 != null && d2.exists()){
                                            //...
                                            // final int num = 1;
                                            //bug?

                                            Map<String, Object> data = new HashMap<String, Object>();
                                            data.put("Number of Friends", 69);//change 69 to be +1
                                            DocumentReference ref2 = FirebaseFirestore.getInstance().document(refPath);
                                            ref2.set(data);
                                            //data.clear();

                                        }else{
                                            final int num = 1;
                                            //bug?

                                            Map<String, Object> data = new HashMap<String, Object>();
                                            data.put("Number of Friends", num);
                                            DocumentReference ref2 = FirebaseFirestore.getInstance().document(refPath);
                                            ref2.set(data);
                                            //data.clear();
                                        }
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });


                            ref = FirebaseFirestore.getInstance().document(refPath + "/myAddedFriends/" + fAddedEmail + "_DBP");
                            // Profile addedUserProfile = addedUserInfo.toObject(Profile.class);


                            // Map<String, Object> dataF = new HashMap<String, Object>();
                            Map<String, Object> dataF = addedUserInfo.getData();
                            // dataF.put("Name", addedUserProfile.getName());
                            //dataF.put("Gender", addedUserProfile.getGender());

                            ref.set(dataF);
//--------------------------------------------------------------------------------------------------------------------------------------------------------------END FOR THE USER WHO ADD-----




//--------------------------------------------------------------------------------------------------------------------------------------------------------------START FOR THE USER WHO HAS BEEN ADDED-----
                            //final String[] uid = new String[1];//nulllllllll

                            FirebaseFirestore.getInstance().collection("loggedUsers").whereEqualTo("Email", fAddedEmail).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            String idd = "";
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    //uid[0] = document.getId();
                                                    idd = document.getId();
                                                    Log.d("tag", uuid+ " => " + document.getData());
                                                }

                                               DocumentReference ref2 = FirebaseFirestore.getInstance().document("loggedUsers/" + idd + "/" + fAddedEmail + "_DB" + "/friends/addedAndBlockedFriends/addedFriends");

                                                Log.d("tagNN**", ref2.getPath());



                                                final String refPath2 = ref2.getPath();

                                                ref2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){

                                                            DocumentSnapshot d2 = task.getResult();

                                                            if(d2 != null && d2.exists()){
                                                                //...
                                                                // final int num = 1;
                                                                //bug?

                                                                Map<String, Object> data = new HashMap<String, Object>();
                                                                data.put("Number of Friends", 69);//change 69 to be +1
                                                                DocumentReference ref3 = FirebaseFirestore.getInstance().document(refPath2);
                                                                ref3.set(data);
                                                                //data.clear();

                                                            }else{
                                                                final int num = 1;
                                                                //bug?

                                                                Map<String, Object> data = new HashMap<String, Object>();
                                                                data.put("Number of Friends", num);
                                                                DocumentReference ref3 = FirebaseFirestore.getInstance().document(refPath2);
                                                                ref3.set(data);
                                                                //data.clear();
                                                            }
                                                        }

                                                    }
                                                });

                                                //ref2 =  FirebaseFirestore.getInstance().document(refPath2 + "/myAddedFriends/" +getEmail+ "_DBP");
                                              ref2 =  FirebaseFirestore.getInstance().document("users/" + getEmail);


                                              ref2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                      if(task.isSuccessful()){

                                                          DocumentReference ref22 =  FirebaseFirestore.getInstance().document(refPath2 + "/myAddedFriends/" + getEmail + "_DBP");

                                                          ref22.set(task.getResult().getData());




                                                      }


                                                  }
                                              });





                                              //  final String currentUEmail = getEmail;


                                            } else {
                                                Log.d("tag", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                            // final String uui = uid[0].trim();

                            //Log.d("tagNN", uid[0]+ " => " + uui);











                            confirmation.setVisibility(View.VISIBLE);


                        }else{
                            //user not found
                        }


                    }//else user not found<--- this is wrong
                }
            });


        }






        //confirmation.setText("GOL");

        //confirmation.setVisibility(View.INVISIBLE);

        //confirmation.setTextColor(Color.RED);







    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddFriendActivity.class);
    }
}
