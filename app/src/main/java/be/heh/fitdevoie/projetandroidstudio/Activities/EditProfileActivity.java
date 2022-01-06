package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import be.heh.fitdevoie.projetandroidstudio.Database.User;
import be.heh.fitdevoie.projetandroidstudio.Database.UserAccessDB;
import be.heh.fitdevoie.projetandroidstudio.R;

public class EditProfileActivity extends AppCompatActivity {

    EditText et_editProfile_firstName;
    EditText et_editProfile_lastName;
    EditText et_editProfile_email;
    EditText et_editProfile_oldPassword;
    EditText et_editProfile_password;
    EditText et_editProfile_passwordConfirmation;
    TextView tv_modification_incomplete;
    Button bt_modification;
    SharedPreferences prefs_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Affiche la barre d'action avec un titre et un bouton de retour vers ChoixActivity
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Modifier le mot de passe");

        et_editProfile_firstName = (EditText) findViewById(R.id.et_editProfile_firstName);
        et_editProfile_lastName = (EditText) findViewById(R.id.et_editProfile_lastName);
        et_editProfile_email = (EditText) findViewById(R.id.et_editProfile_email);
        et_editProfile_oldPassword = (EditText) findViewById(R.id.et_editProfile_oldPassword);
        et_editProfile_password = (EditText) findViewById(R.id.et_editProfile_password);
        et_editProfile_passwordConfirmation = (EditText) findViewById(R.id.et_editProfile_passwordConfirmation);
        tv_modification_incomplete = (TextView) findViewById(R.id.tv_modification_incomplete);
        bt_modification = (Button) findViewById(R.id.bt_modification);

        //Récupération des droits et de l'ID de l'utilisateur dans les SharedPreferences
        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int userId = prefs_data.getInt("userId", -1);
        User userConnected = new User();

        //Si l'ID récupéré est inférieur à 0 -> on le redirige vers MainActivity
        //Sinon -> ouvre accès à la DB, récupère les infos de l'utilisateur et les affiche puis ferme l'accès
        if(userId >= 0) {
            UserAccessDB db_modification = new UserAccessDB(this);
            db_modification.openForRead();
            userConnected = db_modification.getUserWithId(userId);
            et_editProfile_firstName.setText(userConnected.getFirstName());
            et_editProfile_lastName.setText(userConnected.getLastName());
            et_editProfile_email.setText(userConnected.getEmailAddress().replace("'",""));
            db_modification.Close();
        } else {
            Intent toMain = new Intent(this, MainActivity.class);
            startActivity(toMain);
            finish();
        }
    }

    //Si l'utilisateur clique sur le bouton de retour natif (triangle vers la gauche) -> redirige vers ChoixActivity
    @Override
    public void onBackPressed() {
        Intent toChoixAutomate = new Intent(this, ChoixActivity.class);
        startActivity(toChoixAutomate);
        finish();
    }

    public void onEditProfileClickManager(View v) {

        final int BT_MODIFICATION = R.id.bt_modification;

        switch (v.getId()) {
            //Au clic sur le bouton "Modifier"
            case BT_MODIFICATION:
                String modificationIncomplete = "";

                //Vérifie que le champ de l'ancien mot de passe soit complété
                if(et_editProfile_oldPassword.getText().toString().trim().length() == 0) {
                    modificationIncomplete = "Veuillez rentrer votre ancien mot de passe";
                    tv_modification_incomplete.setText(modificationIncomplete);
                    tv_modification_incomplete.setMaxHeight(600);
                    tv_modification_incomplete.setVisibility(View.VISIBLE);
                    break;
                } else {
                    //Vérifie que le champ du nouveau mot de passe soit complété
                    if(et_editProfile_password.getText().toString().trim().length() == 0) {
                        modificationIncomplete = "Veuillez rentrer votre nouveau mot de passe";
                        tv_modification_incomplete.setText(modificationIncomplete);
                        tv_modification_incomplete.setMaxHeight(600);
                        tv_modification_incomplete.setVisibility(View.VISIBLE);
                        break;
                    } else {
                        //vérifie que le champ de confirmation du nouveau mot de passe soit complété
                        if(et_editProfile_passwordConfirmation.getText().toString().trim().length() == 0) {
                            modificationIncomplete = "Veuillez rentrer votre confirmation de mot de passe";
                            tv_modification_incomplete.setText(modificationIncomplete);
                            tv_modification_incomplete.setMaxHeight(600);
                            tv_modification_incomplete.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }

                //Si le nouveau mot de passe a moins de 8 caractères -> affiche message d'erreur
                if(et_editProfile_password.getText().toString().trim().length() < 8) {
                    modificationIncomplete = "Veuillez rentrer un nouveau mot de passe qui contient minimum 8 caractères";
                    tv_modification_incomplete.setText(modificationIncomplete);
                    tv_modification_incomplete.setMaxHeight(600);
                    tv_modification_incomplete.setVisibility(View.VISIBLE);
                    break;
                //Si le nouveau mot de passe et la confirmation sont différents -> affiche message d'erreur
                } else if(!et_editProfile_password.getText().toString().equals(et_editProfile_passwordConfirmation.getText().toString())) {
                    modificationIncomplete = "Le nouveau mot de passe entré est différent de la confirmation du nouveau mot de passe";
                    tv_modification_incomplete.setText(modificationIncomplete);
                    tv_modification_incomplete.setMaxHeight(600);
                    tv_modification_incomplete.setVisibility(View.VISIBLE);
                    break;
                //Si le nouveau mot de passe est le même que l'ancien -> affiche message d'erreur
                } else if(et_editProfile_oldPassword.getText().toString().equals(et_editProfile_password.getText().toString())) {
                    modificationIncomplete = "Le nouveau mot de passe entré est le même que l'ancien, veuillez le changer";
                    tv_modification_incomplete.setText(modificationIncomplete);
                    tv_modification_incomplete.setMaxHeight(600);
                    tv_modification_incomplete.setVisibility(View.VISIBLE);
                    break;
                //Si tout est bon -> récupération du mot de passe enregistré dans la DB pour voir si le mot de passe entré est correct
                } else {
                    UserAccessDB db_editPassword = new UserAccessDB(this);
                    db_editPassword.openForRead();

                    int userId = prefs_data.getInt("userId", -1);
                    if(userId >= 0) {
                        User userWithId = db_editPassword.getUserWithId(userId);
                        db_editPassword.Close();
                        //Si le mot de passe entré est différent de celui enregistré dans la DB -> affiche message d'erreur
                        if(!userWithId.getPassword().equals(et_editProfile_oldPassword.getText().toString())) {
                            modificationIncomplete = "L'ancien mot de passe entré est incorrect, veuillez réessayer";
                            tv_modification_incomplete.setText(modificationIncomplete);
                            tv_modification_incomplete.setMaxHeight(600);
                            tv_modification_incomplete.setVisibility(View.VISIBLE);
                            break;
                        //Sinon affiche un AlertDialog qui demande de confirmer qu'on veut bien changer le mot de passe et qui signale que l'utilisateur sera déconnecté
                        } else {
                            AlertDialog.Builder alertDialogModification = new AlertDialog.Builder(this);
                            alertDialogModification.setTitle("Voulez-vous vraiment vous changer votre mot de passe ?");
                            alertDialogModification.setMessage("Vous serez déconnecté de l'application");

                            //Si l'utilisateur clique sur "Oui"
                            ////-> Met à jour l'utilisateur dans la base de données
                            ////-> Réinitialise les données enregistrées dans les SharedPreferences (= Déconnexion)
                            ////-> Redirige vers MainActivity
                            alertDialogModification.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    User userToEdit = userWithId;
                                    userToEdit.setPassword(et_editProfile_password.getText().toString());

                                    db_editPassword.openForWrite();
                                    db_editPassword.updateUser(userId, userToEdit);
                                    db_editPassword.Close();


                                    SharedPreferences.Editor editeur_prefs = prefs_data.edit();
                                    editeur_prefs.putInt("rights", -1);
                                    editeur_prefs.putInt("userId", -1);
                                    editeur_prefs.commit();
                                    Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(toMain);
                                    finish();
                                }
                            });
                            //Si l'utilisateur clique sur "Non" -> affiche un message qui confirme l'annulation du changement de mot de passe et réinitialise les champs
                            alertDialogModification.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(getApplicationContext(), "Modification du mot de passe annulée", Toast.LENGTH_LONG).show();
                                    et_editProfile_oldPassword.setText(null);
                                    et_editProfile_password.setText(null);
                                    et_editProfile_passwordConfirmation.setText(null);
                                }
                            });
                            alertDialogModification.create();
                            alertDialogModification.show();
                        }
                    } else {
                        db_editPassword.Close();
                    }
                }
                break;
        }
    }

    //Si l'utilisateur clique sur le bouton de retour de la barre d'action -> redirige vers ChoixActivity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent toChoixAutomate = new Intent(this, ChoixActivity.class);
                startActivity(toChoixAutomate);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
