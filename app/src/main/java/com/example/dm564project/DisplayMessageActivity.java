package com.example.dm564project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
        UserDao userDao = db.userDao();

        TextView textView = findViewById(R.id.textView);
        textView.setText(userDao.findById(message).name);
    }
}