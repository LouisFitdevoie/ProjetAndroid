package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import be.heh.fitdevoie.projetandroidstudio.Database.User;
import be.heh.fitdevoie.projetandroidstudio.Database.UserAccessDB;
import be.heh.fitdevoie.projetandroidstudio.R;

public class ConnexionActivity extends AppCompatActivity {

    EditText et_connexion_email;
    EditText et_connexion_password;

    boolean email_ok = false;
    boolean pwd_ok = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        et_connexion_email = (EditText) findViewById(R.id.et_connexion_email);
        et_connexion_password = (EditText) findViewById(R.id.et_connexion_password);
    }

    public void onConnexionClickManager(View v) {
        final int BT_CONNEXION = R.id.bt_connexion;
        switch(v.getId()) {
            case BT_CONNEXION:

                UserAccessDB userDB = new UserAccessDB(this);
                userDB.openForRead();

                ArrayList<User> tab_user = userDB.getAllUser();
                userDB.Close();

                try {
                    if(et_connexion_email.getText().toString().trim().length() == 0) {
                        System.out.println("empty edittext");
                        email_ok = false;
                    } else {
                        if(tab_user.isEmpty()) {
                            System.out.println("empty tab");
                            email_ok = false;
                        } else {
                            if(userDB.getUser(et_connexion_email.getText().toString()) != null) {
                                User userToConnect = userDB.getUser(et_connexion_email.getText().toString());
                                Toast.makeText(this, userToConnect.getId() + " " + userToConnect.getFirstName() + " " + userToConnect.getLastName() + " " + userToConnect.getEmail() + " " + userToConnect.getPassword(),Toast.LENGTH_LONG);
                            } else {
                                Toast.makeText(this, "no email correspondant", Toast.LENGTH_LONG);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
