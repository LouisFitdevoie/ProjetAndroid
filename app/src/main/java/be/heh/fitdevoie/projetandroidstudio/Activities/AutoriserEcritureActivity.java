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

import be.heh.fitdevoie.projetandroidstudio.Adapter.Adapter;
import be.heh.fitdevoie.projetandroidstudio.Database.User;
import be.heh.fitdevoie.projetandroidstudio.Database.UserAccessDB;
import be.heh.fitdevoie.projetandroidstudio.R;

public class AutoriserEcritureActivity extends AppCompatActivity {

    ListView lv_authorize_userList;
    TextView tv_authorize_error;

    SharedPreferences prefs_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoriser_ecriture);

        lv_authorize_userList = (ListView) findViewById(R.id.lv_authorize_userList);
        tv_authorize_error = (TextView) findViewById(R.id.tv_authorize_error);

        tv_authorize_error.setVisibility(View.GONE);

        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Autoriser à l'écriture");

        UserAccessDB userDB = new UserAccessDB(this);
        userDB.openForRead();
        ArrayList<User> userList = userDB.getAllUser();

        userDB.Close();

        if(userList.isEmpty()) {
            tv_authorize_error.setText("La base de données est vide");
            tv_authorize_error.setVisibility(View.VISIBLE);
        } else {
            Adapter adapter = new Adapter(this, userList);
            lv_authorize_userList.setAdapter(adapter);
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
}