package com.example.dm564project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class PostsActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        db = AppDatabase.getAppDatabase(getApplicationContext());

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if( User.active == null) {
            // user anonymous, posting disabled
            findViewById( R.id.addPostButton ).setEnabled( false );
            findViewById( R.id.editTextPostContent ).setEnabled( false );
        }
        init();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing( false );
            db.syncDatabase();
            init();
        });
    }

    private void init() {
        PostAdaptor postAdaptor = new PostAdaptor(db.postDao().getAll());
        recyclerView.setAdapter(postAdaptor);
    }

    public void createPost(View view) {
        AppDatabase db = AppDatabase.getAppDatabase( getApplicationContext() );
        PostDao postDao = db.postDao();
        EditText postContentEditText = findViewById( R.id.editTextPostContent );

        String postContent = postContentEditText.getText().toString();
        postContentEditText.setText("Post content");
        postDao.insert( new Post( postDao.nextId(), User.active.id, postContent, false, 0, 0) );
        db.syncDatabase();
        init();
    }
}