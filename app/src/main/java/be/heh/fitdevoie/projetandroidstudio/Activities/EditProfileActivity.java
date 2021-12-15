package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        et_editProfile_firstName = (EditText) findViewById(R.id.et_editProfile_firstName);
        et_editProfile_lastName = (EditText) findViewById(R.id.et_editProfile_lastName);
        et_editProfile_email = (EditText) findViewById(R.id.et_editProfile_email);
        et_editProfile_oldPassword = (EditText) findViewById(R.id.et_editProfile_oldPassword);
        et_editProfile_password = (EditText) findViewById(R.id.et_editProfile_password);
        et_editProfile_passwordConfirmation = (EditText) findViewById(R.id.et_editProfile_passwordConfirmation);
        tv_modification_incomplete = (TextView) findViewById(R.id.tv_modification_incomplete);
        bt_modification = (Button) findViewById(R.id.bt_modification);

        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int userId = prefs_data.getInt("userId", -1);
        User userConnected = new User();

        if(userId >= 0) {
            UserAccessDB db_modification = new UserAccessDB(this);
            db_modification.openForRead();
            userConnected = db_modification.getUserWithId(userId);
            et_editProfile_firstName.setText(userConnected.getFirstName());
            et_editProfile_lastName.setText(userConnected.getLastName());
            et_editProfile_email.setText(userConnected.getEmailAddress());
        } else {
            Intent toMain = new Intent(this, MainActivity.class);
            startActivity(toMain);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent toChoixAutomate = new Intent(this, ChoixAutomateActivity.class);
        startActivity(toChoixAutomate);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int BT_MODIFICATION = R.id.bt_modification;

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent toChoixAutomate = new Intent(this, ChoixAutomateActivity.class);
                startActivity(toChoixAutomate);
                finish();
                return true;
            case BT_MODIFICATION:


                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
