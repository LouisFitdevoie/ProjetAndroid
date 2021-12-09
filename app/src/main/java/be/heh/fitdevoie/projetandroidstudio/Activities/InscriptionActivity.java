package be.heh.fitdevoie.projetandroidstudio.Activities;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
    Button bt_inscription;

    boolean firstName_ok = false;
    boolean lastName_ok = false;
    boolean email_ok = false;
    boolean pwd_ok = false;

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
        bt_inscription = (Button) findViewById(R.id.bt_inscription);
    }

    public void onInscriptionClickManager(View v) {
        final int BT_INSCRIPTION = R.id.bt_inscription;
        final int BT_HOME = R.id.home;
        switch(v.getId()) {
            case BT_INSCRIPTION:
                tv_inscription_incomplete.setText("");
                String inscription_incomplete_description = "Veuillez corriger/remplir les champs suivants :";

                try {

                    if(et_inscription_firstName.getText().toString().trim().length() == 0) {
                        this.firstName_ok = false;
                        inscription_incomplete_description += "\n- Prénom";
                    } else {
                        this.firstName_ok = true;
                    }

                    if(et_inscription_lastName.getText().toString().trim().length() == 0) {
                        this.lastName_ok = false;
                        inscription_incomplete_description += "\n- Nom de famille";
                    } else {
                        this.lastName_ok = true;
                    }

                    if(et_inscription_email.getText().toString().trim().length() == 0) {
                        this.email_ok = false;
                        inscription_incomplete_description += "\n- Adresse email";
                    } else {
                        this.email_ok = true;
                    }

                    if(et_inscription_password.getText().toString().trim().length() == 0 || et_inscription_passwordConfirmation.getText().toString().trim().length() == 0) {
                        this.pwd_ok = false;
                        if(et_inscription_password.getText().toString().trim().length() == 0) {
                            inscription_incomplete_description += "\n- Mot de passe";
                        }
                        if(et_inscription_passwordConfirmation.getText().toString().trim().length() == 0) {
                            inscription_incomplete_description += "\n- Confirmation du mot de passe";
                        }
                    } else {
                        if(et_inscription_password.getText().toString().equals(et_inscription_passwordConfirmation.getText().toString())) {
                            this.pwd_ok = true;
                        } else {
                            inscription_incomplete_description += "\n- Les mots de passe entrés sont différents";
                            this.pwd_ok = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("FN : " + this.firstName_ok + " LN : " + this.lastName_ok + " MAIL : " + this.email_ok + " PWD : " + this.pwd_ok);
                if(this.firstName_ok && this.lastName_ok && this.email_ok && this.pwd_ok) {
                    tv_inscription_incomplete.setText("");
                    tv_inscription_incomplete.setVisibility(View.INVISIBLE);
                    System.out.println(inscription_incomplete_description);
                    //User user1 = new User(et_inscription_firstName.getText().toString(),et_inscription_lastName.getText().toString(),et_inscription_email.getText().toString(),et_inscription_password.getText().toString());
                    UserAccessDB userDB = new UserAccessDB(this);
                    userDB.openForWrite();
                    //userDB.insertUser(user1);
                    userDB.Close();

                    Intent toTest = new Intent(this, MainActivity.class);
                    startActivity(toTest);
                    finish();
                } else {

                    tv_inscription_incomplete.setText(inscription_incomplete_description);
                    tv_inscription_incomplete.setMaxHeight(600);
                    tv_inscription_incomplete.setVisibility(View.VISIBLE);
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
