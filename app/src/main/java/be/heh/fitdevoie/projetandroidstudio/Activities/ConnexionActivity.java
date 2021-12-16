package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
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

public class ConnexionActivity extends AppCompatActivity {

    EditText et_connexion_email;
    EditText et_connexion_password;
    TextView tv_connexion_error;

    boolean email_ok;
    boolean pwd_ok;

    SharedPreferences prefs_data;
    User userToLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        et_connexion_email = (EditText) findViewById(R.id.et_connexion_email);
        et_connexion_password = (EditText) findViewById(R.id.et_connexion_password);
        tv_connexion_error = (TextView) findViewById(R.id.tv_connexion_error);
        email_ok = false;
        pwd_ok = false;

        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userToLog = new User();
    }

    public void onConnexionClickManager(View v) {
        final int BT_CONNEXION = R.id.bt_connexion;
        switch(v.getId()) {
            case BT_CONNEXION:
                tv_connexion_error.setText("");
                String connexion_error = "";

                UserAccessDB userDB = new UserAccessDB(this);
                userDB.openForRead();
                try {
                    if(et_connexion_email.getText().toString().trim().length() == 0) {
                        email_ok = false;
                        connexion_error = "Veuillez remplir le champ d'adresse email";
                        tv_connexion_error.setText(connexion_error);
                        tv_connexion_error.setMaxHeight(600);
                        tv_connexion_error.setVisibility(View.VISIBLE);
                    } else {
                        if(!Patterns.EMAIL_ADDRESS.matcher(et_connexion_email.getText().toString()).matches()) {
                            this.email_ok = false;
                            connexion_error = "Vous n'avez pas entré une adresse mail correcte";
                            tv_connexion_error.setText(connexion_error);
                            tv_connexion_error.setMaxHeight(600);
                            tv_connexion_error.setVisibility(View.VISIBLE);
                        } else {
                            User userReturned = userDB.getUserWithEmail(et_connexion_email.getText().toString());
                            if(userReturned.getEmailAddress() == null) {
                                this.email_ok = false;

                                connexion_error = "Aucun compte avec cette adresse email n'existe";
                                tv_connexion_error.setText(connexion_error);
                                tv_connexion_error.setMaxHeight(600);
                                tv_connexion_error.setVisibility(View.VISIBLE);

                                System.out.println("\n\nUser not found\n\n");
                            } else {
                                this.email_ok = true;
                                System.out.println("\n\nUser found : ID=" + userReturned.getUserId() + " FN=" + userReturned.getFirstName() + " LN=" + userReturned.getLastName() + " EMAIL=" + userReturned.getEmailAddress() + " PWD=" + userReturned.getPassword());
                            }
                        }
                    }
                    if(email_ok) {
                        if(et_connexion_password.getText().toString().trim().length() == 0) {
                            pwd_ok = false;
                            connexion_error = "Veuillez remplir le champ de mot de passe";
                            tv_connexion_error.setText(connexion_error);
                            tv_connexion_error.setMaxHeight(600);
                            tv_connexion_error.setVisibility(View.VISIBLE);
                        } else {
                            User userReturned = userDB.getUserWithEmail(et_connexion_email.getText().toString());
                            if(!et_connexion_password.getText().toString().equals(userReturned.getPassword())) {
                                pwd_ok = false;
                                connexion_error = "Le mot de passe entré est incorrect";
                                tv_connexion_error.setText(connexion_error);
                                tv_connexion_error.setMaxHeight(600);
                                tv_connexion_error.setVisibility(View.VISIBLE);
                            } else {
                                pwd_ok = true;
                                connexion_error = "";
                                tv_connexion_error.setText(connexion_error);
                                tv_connexion_error.setVisibility(View.INVISIBLE);
                                userToLog = userReturned;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userDB.Close();

                if(email_ok && pwd_ok) {
                    System.out.println("Email et pwd OK");
                    SharedPreferences.Editor editeur_datas = prefs_data.edit();
                    editeur_datas.putInt("userId", userToLog.getUserId());
                    editeur_datas.putInt("rights", userToLog.getRights());
                    editeur_datas.commit();

                    Intent toChoixAutomate = new Intent(this, ChoixActivity.class);
                    startActivity(toChoixAutomate);
                    finish();
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
