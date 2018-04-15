package aziz.ca.ylachat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 1- Fix registraion to match Auth
 * 2- Fix the path by adding fEmail
 * 3- Remove the debug Msgs
 */
public class SignUpActivity extends AppCompatActivity {

    private EditText email, username, password, repassword;
    private CheckBox male, female;
    private String gender;
    private FirebaseAuth mAuth;
    private DocumentReference mDBR;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = (EditText) findViewById(R.id.emailSignUpET);
        username = (EditText) findViewById(R.id.usernameSignUpET);
        password = (EditText) findViewById(R.id.passET);
        repassword = (EditText) findViewById((R.id.rePassET));
        mAuth = FirebaseAuth.getInstance();
        male = (CheckBox) findViewById(R.id.maleCheckBox);
        female = (CheckBox) findViewById(R.id.femaleCheckBox);


    }

    public void onFemaleClicked(View view){

        male.setChecked(false);
        gender = "Female";

    }


    public void onMaleClicked(View view){

        female.setChecked(false);
        gender = "Male";

    }


    public void signUpClicked(View view){



        final String fEmail, fUsername, fPass, fRePass, fGender;
        fGender = gender;
        fEmail = email.getText().toString().trim();
        fUsername = username.getText().toString().trim();
        fPass = password.getText().toString().trim();
        fRePass = repassword.getText().toString().trim();
/*
        if(female.isSelected()){

            fGender = "Female";
        }
        else if(male.isSelected()){
            fGender = "Male";
        }
*/





        if(!fEmail.isEmpty()  & !fUsername.isEmpty() & !fPass.isEmpty()
                & !fRePass.isEmpty()){

            if(fPass.equals(fRePass)){

                mAuth.createUserWithEmailAndPassword(fEmail,fPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("HI THERE**", "Regi++");


                            Map<String,Object> data = new HashMap<String, Object>();
                            data.put("Name", fUsername);
                            data.put("Email", fEmail);
                            data.put("Gender", gender);

                            mDBR = FirebaseFirestore.getInstance().document("users/" + fEmail);

                            mDBR.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("***TmaM", "+++Done");
                                    FirebaseAuth.getInstance().signOut();//sol for now untill email variation
                                    changeToLoginActivity();
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("NO!!!!", "!!G");
                                }
                            });





                        }
                    }

                });

            }else {
                //TODO return Mismatch error Mesg
                email.setText("kk");
            }





        }else {
            //TODO return empty error Mesg
            email.setText("pp");
        }




    }



    public void onLoginClicked(View v){

        Intent in = LoginActivity.makeIntent(SignUpActivity.this);
        startActivity(in);
        finish();

    }

    public void changeToLoginActivity(){
       // Intent in1 = new Intent(SignUpActivity.this, LoginActivity.class);
       Intent in1 = LoginActivity.makeIntent(SignUpActivity.this);
        startActivity(in1);
        finish();

    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }
}
