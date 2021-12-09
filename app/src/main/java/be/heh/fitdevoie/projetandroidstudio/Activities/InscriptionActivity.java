package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import be.heh.fitdevoie.projetandroidstudio.Database.User;
import be.heh.fitdevoie.projetandroidstudio.Database.UserAccessDB;
import be.heh.fitdevoie.projetandroidstudio.R;

public class InscriptionActivity extends AppCompatActivity {

    EditText et_inscription_firstName;
    EditText et_inscription_lastName;
    EditText et_inscription_email;
    EditText et_inscription_password;
    EditText et_inscription_passwordConfirmation;
    TextView tv_inscription_incomplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        et_inscription_firstName = (EditText) findViewById(R.id.et_inscription_firstName);
        et_inscription_lastName = (EditText) findViewById(R.id.et_inscription_lastName);
        et_inscription_email = (EditText) findViewById(R.id.et_inscription_email);
        et_inscription_password = (EditText) findViewById(R.id.et_inscription_password);
        et_inscription_passwordConfirmation = (EditText) findViewById(R.id.et_inscription_passwordConfirmation);
        tv_inscription_incomplete = (TextView) findViewById(R.id.tv_inscription_incomplete);
    }

    public void onInscriptionClickManager(View v) {
        final int BT_INSCRIPTION = R.id.bt_inscription;
        final int BT_HOME = R.id.home;
        switch(v.getId()) {
            case BT_INSCRIPTION:
                System.out.println("Click sur inscription");
                tv_inscription_incomplete.setText("");
                String inscription_incomplete_description = "Veuillez corriger/remplir les champs suivants :\n";
                Boolean pwd_ok = false;
                try {
                    if(et_inscription_password.getText().toString().trim().length() == 0 || et_inscription_passwordConfirmation.getText().toString().trim().length() == 0) {
                        pwd_ok = false;
                        if(et_inscription_password.getText().toString().trim().length() == 0) {
                            inscription_incomplete_description += "- Mot de passe\n";
                        }
                        if(et_inscription_passwordConfirmation.getText().toString().trim().length() == 0) {
                            inscription_incomplete_description += "- Confirmation du mot de passe\n";
                        }
                    } else {
                        if(et_inscription_password.getText().toString().equals(et_inscription_passwordConfirmation.getText().toString())) {
                            System.out.println("pwd ok");
                            tv_inscription_incomplete.setVisibility(View.INVISIBLE);
                            pwd_ok = true;
                        } else {
                            System.out.println("pwd pas ok");
                            tv_inscription_incomplete.setVisibility(View.VISIBLE);
                            pwd_ok = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //User user1 = new User(et_inscription_firstName.getText().toString(),et_inscription_lastName.getText().toString(),et_inscription_email.getText().toString(),et_inscription_password.getText().toString());

                UserAccessDB userDB = new UserAccessDB(this);
                userDB.openForWrite();
                //userDB.insertUser(user1);
                userDB.Close();
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