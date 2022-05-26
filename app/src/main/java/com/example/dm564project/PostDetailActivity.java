package com.example.dm564project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PostDetailActivity extends AppCompatActivity {

    private TextView userText;
    private TextView contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        userText = findViewById(R.id.userText);
        contentText = findViewById(R.id.contentText);
        Intent intent = getIntent();
        AppDatabase db = AppDatabase.getAppDatabase( this );
        PostDao postDao = db.postDao();
        Post post = postDao.findById( intent.getIntExtra( "postId",0 ) );
        userText.setText( post.user_id );
        contentText.setText( post.content );
    }
}