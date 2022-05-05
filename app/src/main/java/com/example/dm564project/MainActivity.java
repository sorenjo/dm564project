package com.example.dm564project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.dm564project.MESSAGE";

    //UserDao userDao = db.userDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createUser(View view){
        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
        UserDao userDao = db.userDao();
        EditText id = findViewById(R.id.editTextId);
        EditText name = findViewById(R.id.editTextName);
        String userid = id.getText().toString();
        String username = name.getText().toString();

        if (  ) {
            Toast.makeText(getApplicationContext(), "Username is already in use.", Toast.LENGTH_SHORT).show();
        } else {
            //userDao.insert(new User(userid, username));
            Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_SHORT).show();
        }
        //intent.putExtra(EXTRA_MESSAGE, userid);
        //startActivity(intent);
    }
}