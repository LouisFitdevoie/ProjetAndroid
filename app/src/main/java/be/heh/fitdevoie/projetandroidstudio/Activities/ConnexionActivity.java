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

        //Affiche une barre d'action avec un bouton qui renvoie vers MainActivity
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Connexion");

        et_connexion_email = (EditText) findViewById(R.id.et_connexion_email);
        et_connexion_password = (EditText) findViewById(R.id.et_connexion_password);
        tv_connexion_error = (TextView) findViewById(R.id.tv_connexion_error);
        email_ok = false;
        pwd_ok = false;

        //Récupération des Shared Preferences avec les droits et l'ID de l'utilisateur
        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userToLog = new User();
    }

    public void onConnexionClickManager(View v) {

        final int BT_CONNEXION = R.id.bt_connexion;

        switch(v.getId()) {
            //Au clic sur le bouton de connexion
            case BT_CONNEXION:
                tv_connexion_error.setText("");
                String connexion_error = "";

                //Ouverture de la base de données en lecture
                UserAccessDB userDB = new UserAccessDB(this);
                userDB.openForRead();
                try {
                    //vérifie si le champ d'email est vide
                    if(et_connexion_email.getText().toString().trim().length() == 0) {
                        //Si vide affiche un message d'erreur
                        email_ok = false;
                        connexion_error = "Veuillez remplir le champ d'adresse email";
                        tv_connexion_error.setText(connexion_error);
                        tv_connexion_error.setVisibility(View.VISIBLE);
                    } else {
                        //Sinon -> vérifie que ce qui est rentré est bien une adresse mail
                        if(!Patterns.EMAIL_ADDRESS.matcher(et_connexion_email.getText().toString()).matches()) {
                            //Si non -> affiche message d'erreur
                            this.email_ok = false;
                            connexion_error = "Vous n'avez pas entré une adresse mail correcte";
                            tv_connexion_error.setText(connexion_error);
                            tv_connexion_error.setVisibility(View.VISIBLE);
                        } else {
                            //Si c'est bien une adresse mail
                            //Vérifie si un utilisateur avec cette adresse mail existe dans la DB
                            User userReturned = userDB.getUserWithEmail(et_connexion_email.getText().toString());
                            if(userReturned != null) {
                                if(userReturned.getEmailAddress() != null) {
                                    //Sinon -> email OK
                                    this.email_ok = true;
                                } else {
                                    //Si elle n'existe pas -> affiche message erreur
                                    this.email_ok = false;

                                    connexion_error = "Aucun compte avec cette adresse email n'existe";
                                    tv_connexion_error.setText(connexion_error);
                                    tv_connexion_error.setVisibility(View.VISIBLE);
                                }
                            } else {
                                //Si elle n'existe pas -> affiche message erreur
                                this.email_ok = false;

                                connexion_error = "Aucun compte avec cette adresse email n'existe";
                                tv_connexion_error.setText(connexion_error);
                                tv_connexion_error.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    //Si l'adresse email est correcte et existe dans la DB
                    if(email_ok) {
                        //Vérifie que le champ de mot de passe soit complété
                        if(et_connexion_password.getText().toString().trim().length() == 0) {
                            //Sinon -> message erreur
                            pwd_ok = false;
                            connexion_error = "Veuillez remplir le champ de mot de passe";
                            tv_connexion_error.setText(connexion_error);
                            tv_connexion_error.setVisibility(View.VISIBLE);
                        } else {
                            //Si le champ est complété -> vérifie que le mot de passe soit correct
                            User userReturned = userDB.getUserWithEmail(et_connexion_email.getText().toString());
                            if(!et_connexion_password.getText().toString().equals(userReturned.getPassword())) {
                                pwd_ok = false;
                                connexion_error = "Le mot de passe entré est incorrect";
                                tv_connexion_error.setText(connexion_error);
                                tv_connexion_error.setVisibility(View.VISIBLE);
                            } else {
                                pwd_ok = true;
                                connexion_error = "";
                                tv_connexion_error.setText(connexion_error);
                                tv_connexion_error.setVisibility(View.GONE);
                                userToLog = userReturned;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Après les vérifications -> ferme l'accès en lecture à la DB
                userDB.Close();

                //Si l'email et le mot de passe sont corrects
                if(email_ok && pwd_ok) {
                    //Enregistre dans les SharedPreferences l'ID et les droits de l'utilisateur
                    SharedPreferences.Editor editeur_datas = prefs_data.edit();
                    editeur_datas.putInt("userId", userToLog.getUserId());
                    editeur_datas.putInt("rights", userToLog.getRights());
                    editeur_datas.commit();

                    //Redirige vers ChoixActivity
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
            //Si l'utilisateur appuie sur le bouton de retour de la barre d'action -> redirige vers MainActivity
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
