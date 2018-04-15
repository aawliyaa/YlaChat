package aziz.ca.ylachat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * now the app is sign in/out ready
 * next my friends
 * log out is done :)
 */
public class Home2Activity extends AppCompatActivity {


    private DocumentReference mDBR;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.navigation, menu);


        return true;
        //return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
           /* case R.id.navigation_home:
                Intent in1 = new Intent(this,ProfileActivity.class);
                this.startActivity(in1);
                return true;*/
            case R.id.navigation_dashboard:
               // Intent in2 = new Intent(this, AddFriendActivity.class);
                Intent in2 = AddFriendActivity.makeIntent(Home2Activity.this);
                //in case there is a bug that the user is signed out during adding you can finish() this activity
                this.startActivity(in2);
               // NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.navigation_dashboard2:
                Intent in3 = new Intent(this, MyFriendsActivity.class);
                this.startActivity(in3);
                // NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.navigation_notifications:
                //Intent in4 = new Intent(this,LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                Intent in4 = LoginActivity.makeIntent(Home2Activity.this);
                this.startActivity(in4);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static Intent makeIntent(Context con) {

        return new Intent(con, Home2Activity.class);
    }
}
