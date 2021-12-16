package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import be.heh.fitdevoie.projetandroidstudio.Database.User;
import be.heh.fitdevoie.projetandroidstudio.Database.UserAccessDB;
import be.heh.fitdevoie.projetandroidstudio.R;

public class AutoriserEcritureActivity extends AppCompatActivity {

    TextView tv_authorizeWrite_userId;
    TextView tv_authorizeWrite_firstname;
    TextView tv_authorizeWrite_lastname;
    TextView tv_authorizeWrite_email;
    TextView tv_authorizeWrite_rights;
    Button bt_authorizeWrite_next;
    Button bt_authorizeWrite_previous;
    SharedPreferences prefs_data;

    UserAccessDB userDB = new UserAccessDB(this);
    ArrayList<User> userList = new ArrayList<>();
    int currentId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoriser_ecriture);

        tv_authorizeWrite_userId = (TextView) findViewById(R.id.tv_authorizeWrite_userId);
        tv_authorizeWrite_firstname = (TextView) findViewById(R.id.tv_authorizeWrite_firstname);
        tv_authorizeWrite_lastname = (TextView) findViewById(R.id.tv_authorizeWrite_lastname);
        tv_authorizeWrite_email = (TextView) findViewById(R.id.tv_authorizeWrite_email);
        tv_authorizeWrite_rights = (TextView) findViewById(R.id.tv_authorizeWrite_rights);
        bt_authorizeWrite_next = (Button) findViewById(R.id.bt_auhorizeWrite_next);
        bt_authorizeWrite_previous = (Button) findViewById(R.id.bt_authorizeWrite_previous);

        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int userConnectedId = prefs_data.getInt("userId", -1);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Autoriser à l'écriture");

        userDB.openForRead();
        userList = userDB.getAllUser();
        userDB.Close();

        if(userConnectedId == userList.get(currentId).getUserId()) {
            currentId += 1;
        }

        bt_authorizeWrite_previous.setEnabled(false);
        showUserDetail(currentId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent toChoix = new Intent(this, ChoixActivity.class);
                startActivity(toChoix);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAuthorizeWriteClickManager(View view) {
        final int BT_NEXT = R.id.bt_auhorizeWrite_next;
        final int BT_PREVIOUS = R.id.bt_authorizeWrite_previous;
        int connectedUserID = prefs_data.getInt("userId", -1);

        switch(view.getId()) {
            case BT_NEXT:
                int tableSize = userList.size() - 1;
                if(currentId == tableSize) {
                    currentId = tableSize - 1;
                } else if(connectedUserID == userList.get(currentId - 1).getUserId()) {
                    if(currentId == tableSize) {
                        currentId = tableSize;
                    } else {
                        currentId += 2;
                    }
                } else if(currentId == tableSize - 1) {
                    currentId += 1;
                } else {
                    currentId += 1;
                }

                if(currentId == tableSize) {
                    bt_authorizeWrite_next.setEnabled(false);
                } else {
                    bt_authorizeWrite_previous.setEnabled(true);
                }

                showUserDetail(currentId);
                break;
            case BT_PREVIOUS:
                if(currentId == 0) {
                    currentId = 0;
                } else if(connectedUserID == userList.get(currentId - 1).getUserId()) {
                    if(currentId == 1) {
                        currentId = 1;
                    } else {
                        currentId -= 2;
                    }
                } else if(currentId == 1) {
                    currentId -= 1;
                } else {
                    currentId -= 1;
                }

                if(currentId == 1) {
                    bt_authorizeWrite_previous.setEnabled(false);
                } else {
                    bt_authorizeWrite_previous.setEnabled(true);
                }

                showUserDetail(currentId);
                break;
        }
    }

    public void showUserDetail(int id) {
        User userToShow = userList.get(id);
        tv_authorizeWrite_userId.setText("UserId : " + userToShow.getUserId());
        tv_authorizeWrite_firstname.setText("Prénom : " + userToShow.getFirstName());
        tv_authorizeWrite_lastname.setText("Nom : " + userToShow.getLastName());
        tv_authorizeWrite_email.setText("Email : " + userToShow.getEmailAddress());
        tv_authorizeWrite_rights.setText("Droits : " + userToShow.getRights());
    }
}