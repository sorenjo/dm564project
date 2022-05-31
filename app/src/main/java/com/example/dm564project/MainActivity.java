package com.example.dm564project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.dm564project.MESSAGE";
    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getAppDatabase( getApplicationContext() );
        db.syncDatabase();
        System.out.println("Sync done");
    }

    public void createUser(View view) {
        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
        UserDao userDao = db.userDao();

        EditText id = findViewById(R.id.editTextId);
        EditText name = findViewById(R.id.editTextName);

        String userid = id.getText().toString();
        String username = name.getText().toString();

        if (userDao.doesExist(userid))
            Toast.makeText(getApplicationContext(), "Username is already in use", Toast.LENGTH_SHORT).show();
        else {
            User user = new User(userid, username, false, 0, 0);
            userDao.insert(user);
            User.active = user;
            Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_SHORT).show();
            db.syncDatabase();
            gotoPosts( view );
        }
    }

    public void continueAsAnonUser(View view) {
        User.active = null;
        gotoPosts( view );
    }

    public void gotoPosts(View view){
        Intent intent = new Intent(this, PostsActivity.class);
        startActivity( intent );
    }
}