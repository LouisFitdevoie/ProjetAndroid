package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import be.heh.fitdevoie.projetandroidstudio.Database.User;
import be.heh.fitdevoie.projetandroidstudio.Database.UserAccessDB;
import be.heh.fitdevoie.projetandroidstudio.R;

public class AutoriserEcritureActivity extends AppCompatActivity {

    ArrayList<User> userList = new ArrayList<>();

    TextView tv_authorize_error;
    TextView tv_authorize_emailAddress;
    TextView tv_authorize_rights;
    Button bt_authorize_changeRights;
    Button bt_authorize_next;
    Button bt_authorize_previous;

    SharedPreferences prefs_data;

    int currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoriser_ecriture);

        tv_authorize_error = (TextView) findViewById(R.id.tv_authorize_error);

        tv_authorize_error.setVisibility(View.GONE);

        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Autoriser à l'écriture");

        UserAccessDB userDB = new UserAccessDB(this);
        userDB.openForRead();
        userList = userDB.getAllUser();

        userDB.Close();

        tv_authorize_emailAddress = (TextView) findViewById(R.id.tv_authorize_emailAddress);
        tv_authorize_rights = (TextView) findViewById(R.id.tv_authorize_rights);
        bt_authorize_changeRights = (Button) findViewById(R.id.bt_authorize_changeRights);
        bt_authorize_next = (Button) findViewById(R.id.bt_authorize_next);
        bt_authorize_previous = (Button) findViewById(R.id.bt_authorize_previous);

        currentId = 0;

        bt_authorize_previous.setEnabled(false);
        if(userList.isEmpty()) {
            bt_authorize_next.setEnabled(false);
            tv_authorize_error.setText("Base de données vide !");
        } else {
            tv_authorize_emailAddress.setText("Adresse mail : " + userList.get(currentId).getEmailAddress().replace("'",""));
            if(userList.get(currentId).getRights() == 0) {
                tv_authorize_rights.setText("Droits d'administration : Oui");
            } else {
                tv_authorize_rights.setText("Droits d'administration : Non");
            }
            if(userList.size() == 1) {
                bt_authorize_next.setEnabled(false);
            } else {
                bt_authorize_next.setEnabled(true);
            }
        }
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

    //Si l'utilisateur appuie sur le bouton natif de retour (Triangle vers la gauche)
    @Override
    public void onBackPressed() {
        //Redirige vers ChoixActivity
        Intent toChoix = new Intent(this, ChoixActivity.class);
        startActivity(toChoix);
        finish();
        return;
    }

    public void onAuthorizeClickManager(View view) {

        final int BT_AUTHORIZE_CHANGERIGHTS = R.id.bt_authorize_changeRights;
        final int BT_AUTHORIZE_NEXT = R.id.bt_authorize_next;
        final int BT_AUTHORIZE_PREVIOUS = R.id.bt_authorize_previous;

        switch(view.getId()) {
            case BT_AUTHORIZE_CHANGERIGHTS:
                User userToUpdate = new User();
                UserAccessDB userAccessDB = new UserAccessDB(this);
                userAccessDB.openForRead();
                int adminNumber = userAccessDB.getAdminNumber();
                userAccessDB.Close();

                tv_authorize_error.setVisibility(View.GONE);

                if(userList.get(currentId).getRights() == 0 && adminNumber == 1) {
                    tv_authorize_error.setText("Cet utilisateur est le seul administrateur, vous devez d'abord donner les droits d'administration à un autre utilisateur avant de les retirer à celui-ci !");
                    tv_authorize_error.setVisibility(View.VISIBLE);

                    break;
                } else if(userList.get(currentId).getRights() == 0 && adminNumber > 1) {

                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
                    alertDialog.setMessage("Voulez-vous vraiment retirer les droits d'administration à cet utilisateur ?\nSi vous êtes cet utilisateur, vous serez déconnecté !");
                    alertDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            userToUpdate.setUserId(userList.get(currentId).getUserId());
                            userToUpdate.setEmailAddress(userList.get(currentId).getEmailAddress());
                            userToUpdate.setFirstName(userList.get(currentId).getFirstName());
                            userToUpdate.setLastName(userList.get(currentId).getLastName());
                            userToUpdate.setRights(1);
                            userToUpdate.setPassword(userList.get(currentId).getPassword());

                            userAccessDB.openForWrite();
                            userAccessDB.updateUser(userList.get(currentId).getUserId(), userToUpdate);
                            userAccessDB.Close();

                            Toast.makeText(getApplicationContext(), "Retrait des droits d'administration effectué !", Toast.LENGTH_SHORT).show();

                            userAccessDB.openForRead();
                            userList = userAccessDB.getAllUser();
                            userAccessDB.Close();

                            if(userList.get(currentId).getRights() == 0) {
                                tv_authorize_rights.setText("Droits d'administration : Oui");
                            } else {
                                tv_authorize_rights.setText("Droits d'administration : Non");
                            }

                            if(userToUpdate.getUserId() == prefs_data.getInt("userId", -1)) {
                                Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(toMain);
                                finish();
                            }
                        }
                    });

                    alertDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "Retrait des droits d'administration annulé", Toast.LENGTH_LONG).show();
                        }
                    });
                    alertDialog.setCancelable(false);

                    AlertDialog alert = alertDialog.create();
                    alert.show();

                    break;
                } else if(userList.get(currentId).getRights() != 0) {

                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
                    alertDialog.setMessage("Voulez-vous vraiment donner les droits d'administration à cet utilisateur ?");

                    alertDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            userToUpdate.setUserId(userList.get(currentId).getUserId());
                            userToUpdate.setEmailAddress(userList.get(currentId).getEmailAddress());
                            userToUpdate.setFirstName(userList.get(currentId).getFirstName());
                            userToUpdate.setLastName(userList.get(currentId).getLastName());
                            userToUpdate.setRights(0);
                            userToUpdate.setPassword(userList.get(currentId).getPassword());

                            userAccessDB.openForWrite();
                            userAccessDB.updateUser(userList.get(currentId).getUserId(), userToUpdate);
                            userAccessDB.Close();

                            Toast.makeText(getApplicationContext(), "Attribution des droits d'administration effectuée !", Toast.LENGTH_SHORT).show();

                            userAccessDB.openForRead();
                            userList = userAccessDB.getAllUser();
                            userAccessDB.Close();

                            if(userList.get(currentId).getRights() == 0) {
                                tv_authorize_rights.setText("Droits d'administration : Oui");
                            } else {
                                tv_authorize_rights.setText("Droits d'administration : Non");
                            }
                        }
                    });

                    alertDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "Attribution des droits d'administration annulée", Toast.LENGTH_LONG).show();
                        }
                    });
                    alertDialog.setCancelable(false);

                    AlertDialog alert = alertDialog.create();
                    alert.show();

                    break;
                }


            case BT_AUTHORIZE_NEXT:
                tv_authorize_error.setVisibility(View.GONE);

                if(currentId == userList.size() - 1) {
                    break;
                } else {
                    currentId++;
                    tv_authorize_emailAddress.setText("Adresse mail : " + userList.get(currentId).getEmailAddress().replace("'",""));
                    if(userList.get(currentId).getRights() == 0) {
                        tv_authorize_rights.setText("Droits d'administration : Oui");
                    } else {
                        tv_authorize_rights.setText("Droits d'administration : Non");
                    }
                    if(currentId == userList.size() - 1) {
                        bt_authorize_next.setEnabled(false);
                    } else {
                        bt_authorize_next.setEnabled(true);
                    }
                    bt_authorize_previous.setEnabled(true);
                    break;
                }

            case BT_AUTHORIZE_PREVIOUS:
                tv_authorize_error.setVisibility(View.GONE);

                if(currentId == 0) {
                    break;
                } else {
                    currentId--;
                    tv_authorize_emailAddress.setText("Adresse mail : " + userList.get(currentId).getEmailAddress().replace("'",""));
                    if(userList.get(currentId).getRights() == 0) {
                        tv_authorize_rights.setText("Droits d'administration : Oui");
                    } else {
                        tv_authorize_rights.setText("Droits d'administration : Non");
                    }
                    if(currentId == 0) {
                        bt_authorize_previous.setEnabled(false);
                    } else {
                        bt_authorize_previous.setEnabled(true);
                    }
                    bt_authorize_next.setEnabled(true);
                    break;
                }
        }
    }
}