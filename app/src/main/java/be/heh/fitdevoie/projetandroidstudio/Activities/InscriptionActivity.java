package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

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
    TextView tv_inscription_firstUser;
    Button bt_inscription;

    boolean firstName_ok = false;
    boolean lastName_ok = false;
    boolean email_ok = false;
    boolean pwd_ok = false;
    boolean firstUser = true;

    SharedPreferences prefs_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        //Affichage d'une barre d'action avec titre + bouton retour vers MainActivity
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Inscription");

        et_inscription_firstName = (EditText) findViewById(R.id.et_inscription_firstName);
        et_inscription_lastName = (EditText) findViewById(R.id.et_inscription_lastName);
        et_inscription_email = (EditText) findViewById(R.id.et_inscription_email);
        et_inscription_password = (EditText) findViewById(R.id.et_inscription_password);
        et_inscription_passwordConfirmation = (EditText) findViewById(R.id.et_inscription_passwordConfirmation);
        tv_inscription_incomplete = (TextView) findViewById(R.id.tv_inscription_incomplete);
        tv_inscription_firstUser = (TextView) findViewById(R.id.tv_inscription_firstUser);
        bt_inscription = (Button) findViewById(R.id.bt_inscription);

        //Récupération des Shared Preferences avec les droits et l'ID de l'utilisateur
        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        UserAccessDB userDB = new UserAccessDB(this);
        userDB.openForRead();
        try {
            ArrayList<User> existingUsers = userDB.getAllUser();
            if(existingUsers.isEmpty()) {
                tv_inscription_firstUser.setVisibility(View.VISIBLE);
            } else {
                tv_inscription_firstUser.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        userDB.Close();
    }

    public void onInscriptionClickManager(View v) {

        final int BT_INSCRIPTION = R.id.bt_inscription;

        switch(v.getId()) {
            //Au clic sur le bouton d'inscription
            case BT_INSCRIPTION:
                tv_inscription_incomplete.setText("");
                String inscription_incomplete_description = "Veuillez corriger/remplir les champs suivants :";

                try {
                    //Vérifie si champ prénom rempli
                    if(et_inscription_firstName.getText().toString().trim().length() == 0) {
                        this.firstName_ok = false;
                        inscription_incomplete_description += "\n- Prénom";
                    } else {
                        this.firstName_ok = true;
                    }

                    //Vérifie si champ nom rempli
                    if(et_inscription_lastName.getText().toString().trim().length() == 0) {
                        this.lastName_ok = false;
                        inscription_incomplete_description += "\n- Nom de famille";
                    } else {
                        this.lastName_ok = true;
                    }

                    //vérifie si champ email rempli
                    if(et_inscription_email.getText().toString().trim().length() == 0) {
                        this.email_ok = false;
                        inscription_incomplete_description += "\n- Adresse email";
                    } else {
                        //Vérifie si c'est bien une adresse mail
                        if(Patterns.EMAIL_ADDRESS.matcher(et_inscription_email.getText().toString()).matches()) {
                            this.email_ok = true;
                        } else {
                            inscription_incomplete_description += "\n- L'adresse email entrée n'en est pas une";
                        }
                    }

                    //Vérifie si un des champs de mots de passe n'est pas complété
                    if(et_inscription_password.getText().toString().trim().length() == 0 || et_inscription_passwordConfirmation.getText().toString().trim().length() == 0) {
                        this.pwd_ok = false;
                        if(et_inscription_password.getText().toString().trim().length() == 0) {
                            inscription_incomplete_description += "\n- Mot de passe";
                        }
                        if(et_inscription_passwordConfirmation.getText().toString().trim().length() == 0) {
                            inscription_incomplete_description += "\n- Confirmation du mot de passe";
                        }
                    } else {
                        //Vérifie si le mot de passe entré a moins de 8 caractères
                        if(et_inscription_password.getText().toString().trim().length() < 8) {
                            this.pwd_ok = false;
                            inscription_incomplete_description += "\n- Le mot de passe entré est trop court (min 8 caractères)";
                        } else {
                            //Vérifie si le mot de passe et la confirmation sont les mêmes
                            if(et_inscription_password.getText().toString().equals(et_inscription_passwordConfirmation.getText().toString())) {
                                this.pwd_ok = true;
                            } else {
                                inscription_incomplete_description += "\n- Les mots de passe entrés sont différents";
                                this.pwd_ok = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Si tous les champs sont remplis correctement
                if(this.firstName_ok && this.lastName_ok && this.email_ok && this.pwd_ok) {
                    tv_inscription_incomplete.setText("");
                    tv_inscription_incomplete.setVisibility(View.INVISIBLE);
                    System.out.println(inscription_incomplete_description);
                    //Formatte l'email pour l'enregistrement dans la DB
                    String emailFormatted = "'" + et_inscription_email.getText().toString() + "'";

                    UserAccessDB userDB = new UserAccessDB(this);

                    //Ouvre un accès en lecture à la DB pour voir si c'est le premier utilisateur ou pas
                    userDB.openForRead();
                    try {
                        ArrayList<User> existingUsers = userDB.getAllUser();
                        if(existingUsers.isEmpty()) {
                            this.firstUser = true;
                        } else {
                            this.firstUser = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    userDB.Close();

                    //Crée un utilisateur -> si premier utilisateur -> donne droit admin
                    User userToCreate;
                    if(firstUser) {
                        userToCreate = new User(et_inscription_firstName.getText().toString(),et_inscription_lastName.getText().toString(),emailFormatted,et_inscription_password.getText().toString(), 0);
                    } else {
                        userToCreate = new User(et_inscription_firstName.getText().toString(),et_inscription_lastName.getText().toString(),emailFormatted,et_inscription_password.getText().toString(), 1);
                    }

                    //Ouvre un accès en écriture dans la DB et crée l'utilisateur dans la DB
                    userDB.openForWrite();
                    try {
                        userDB.insertUser(userToCreate);
                        System.out.println("\n\nUser créé\n\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("\n\nErreur création user\n\n");
                    }
                    userDB.Close();

                    //Connecte l'utilisateur en enregistrant ses droits et son ID dans les SharedPreferences
                    SharedPreferences.Editor editeur_prefs = prefs_data.edit();
                    editeur_prefs.putInt("rights", userToCreate.getRights());
                    editeur_prefs.putInt("userId", userToCreate.getUserId());
                    editeur_prefs.commit();

                    //Redirige vers ChoixActivity
                    Intent toChoixAutomate = new Intent(this, ChoixActivity.class);
                    startActivity(toChoixAutomate);
                    finish();
                } else {

                    //Affiche le message d'erreur complet
                    tv_inscription_incomplete.setText(inscription_incomplete_description);
                    tv_inscription_incomplete.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    //Au clic sur le bouton de retour -> redirige vers MainActivity
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
