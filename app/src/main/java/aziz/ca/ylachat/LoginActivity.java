package aziz.ca.ylachat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *               +--------------------------------+
 *	             |   Google Firebase based		  |
 *               +----+---------------------------+---------------+
 *               |NOTE| This project is Still under contraction   |
 *               |----| Keep up for more update in the nearest    |
 *               |    | future.      Thank You :)                 |
 *               +------------------------------------------------+
 *
 *
 * LoginActivity Class
 *
 * This class will handel the functionality of the login screen.
 * This screen will offer login via Email and Password
 */
public class LoginActivity extends AppCompatActivity {

    private EditText email, pass;
    private FirebaseAuth mAuth;
    private DocumentReference mDBR;
    private CollectionReference mDBRC;
    private boolean flagForDoc, flagForFriendsDoc, flagForMessagesDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email = (EditText) findViewById(R.id.loginEmail);
        pass = (EditText) findViewById(R.id.loginPass);

        mAuth = FirebaseAuth.getInstance();


        flagForDoc = false;
        flagForFriendsDoc = false;
        flagForMessagesDoc = false;



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if(cUser != null){//need to check for email varivacation

            //call the home activity with recpect to the fact that sperate the user.

            Log.e("who", "cUser: " + cUser.getEmail());

            changeToHomeActivity();
        }//ELSE nothing to do

    }

    /**
     *
     * @param v the button LOGIN
     */
    public void onLoginClicked(View v){

        final String fEmail, fPass;
        fEmail = email.getText().toString().trim();
        fPass = pass.getText().toString().trim();

        //Check for emptiness of the

        /**
         * This if statement will Check for emptiness of the
         * Email and Password
         */
        if(!fEmail.isEmpty() && !fPass.isEmpty()){


            /**
             * Using FirebaseAuth.signInWithEmailAndPassword(String Email, String Password)
             *
             * this methodd offer a onSuccess() method that ensure the login
             */
            mAuth.signInWithEmailAndPassword(fEmail,fPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    final String user_id = mAuth.getCurrentUser().getUid();
                    final String user_email = mAuth.getCurrentUser().getEmail();//i may remove it

                    mDBR = FirebaseFirestore.getInstance().document("loggedUsers/" + user_id);


                    mDBR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                //this part from the firebase docs

                                /**
                                 * if the user have already logged in before
                                 * then the screen will change to the user's Home activity
                                 * else
                                 * it will create a new Home Activity
                                 */
                                if (document != null && document.exists()) {
                                    Log.d("TAGG", "*we found it*DocumentSnapshot data: " + document.getData());
                                    //if doc found what to do? change to home activity
                                    changeToHomeActivity();
                                } else {//this
                                    Log.e("TAGG********", "No such document if doc ! exists");

                                    //Welcome the User
                                    //show some tutorials

                                    DocumentReference ref = FirebaseFirestore.getInstance().document("loggedUsers/" + user_id);


                                    Map<String,Object> data = new HashMap<String, Object>();
                                    data.put("Email", user_email);

                                    ref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Log.d("TAG!!**", "Data has been added_Create the doc with uID");
                                           // flagForDoc = true; get red of it
                                            mDBRC = FirebaseFirestore.getInstance().collection("loggedUsers/" + user_id + "/" + user_email + "_DB/" );

                                            Map<String,Object> dataFriends = new HashMap<String, Object>();
                                            dataFriends.put("Name", "All my friends");

                                            Map<String,Object> dataMessages = new HashMap<String, Object>();//need to finish from here
                                            dataMessages.put("NumberOfMessages", 0);


                                            mDBRC.document("friends").set(dataFriends).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("TAG****", "doc friends have been created");

                                                    flagForFriendsDoc = true;
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("TAG****", "doc friends not created");
                                                }
                                            });


                                            mDBRC.document("messages").set(dataMessages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("TAG****", "doc messages have been created");

                                                    flagForMessagesDoc = true;
                                                    if(flagForFriendsDoc && flagForMessagesDoc){
                                                        changeToHomeActivity();
                                                    }else{
                                                        Log.e("TAG!!**", "flag for doc and messages");
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("TAG!!!!", "doc messages not created");
                                                }
                                            });//i need to pase the whole thing here it migt work



                                            //done


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {


                                        }
                                    });





                                }
                            } else {
                                Log.d("TAGG", "get failed with ", task.getException());
                            }
                        }
                    });





                 /*
                    mDBR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {



                            Test1 e = documentSnapshot.toObject(Test1.class);

                            String em = e.getEmail();

                            if(em.equalsIgnoreCase(user_email)){//get red off it


                                changeToHomeActivity();


                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(LoginActivity.this, "Hi there", Toast.LENGTH_LONG).show();
                            Log.e("User***", "!!! not");

                        }
                    });
                    */



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("err***", "sign!!!"+ e.getMessage());

                }
            });
        }else{
            Log.d("err***", "empty!!!");
        }


    }

    public void onSignUpClicked(View v){
        changeToSignUPActivity();


    }

    private void changeToSignUPActivity() {
        Intent innt = SignUpActivity.makeIntent(LoginActivity.this);
        startActivity(innt);
        finish();
    }


    public void changeToHomeActivity(){

        //Intent in = new Intent(LoginActivity.this, Home2Activity.class);

        Intent in = Home2Activity.makeIntent(LoginActivity.this);
        startActivity(in);
        finish();


    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}

