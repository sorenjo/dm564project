package com.example.dm564project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PostDetailActivity extends AppCompatActivity {

    private TextView userText;
    private TextView contentText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getAppDatabase( this );
        PostDao postDao = db.postDao();
        ReactionDao reactionDao = db.reactionDao();

        setContentView(R.layout.activity_post_detail);
        userText = findViewById(R.id.userText);
        contentText = findViewById(R.id.contentText);
        recyclerView = findViewById(R.id.recyclerViewReactions);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent intent = getIntent();
        Post post = postDao.findById( intent.getIntExtra( "postId",0 ) );

        ReactionAdaptor reactionAdaptor = new ReactionAdaptor( reactionDao.getFor(post.id) );
        recyclerView.setAdapter(reactionAdaptor);



        userText.setText( post.user_id );
        contentText.setText( post.content );
    }
}