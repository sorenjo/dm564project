package com.example.dm564project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PostDetailActivity extends AppCompatActivity {

    private TextView userText;
    private TextView contentText;
    private RecyclerView recyclerViewReactions;
    private RecyclerView recyclerViewComments;
    private AppDatabase db;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getAppDatabase( this );
        Intent intent = getIntent();
        post = db.postDao().findById( intent.getIntExtra( "postId",0 ) );

        setContentView(R.layout.activity_post_detail);
        userText = findViewById(R.id.userText);
        contentText = findViewById(R.id.contentText);
        userText.setText( post.userId );
        contentText.setText( post.content );

        recyclerViewReactions = findViewById(R.id.recyclerViewReactions);
        recyclerViewReactions.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewReactions.setItemAnimator(new DefaultItemAnimator());
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewComments.setItemAnimator(new DefaultItemAnimator());
System.out.println("bruh");
        if( User.active == null) {
            // user anonymous, posting disabled
            findViewById( R.id.addCommentButton ).setEnabled( false );
            findViewById( R.id.editTextCommentContent ).setEnabled( false );
        }

        init();
    }

    private void init() {
        ReactionAdaptor reactionAdaptor = new ReactionAdaptor( db.reactionDao().getFor(post.id) );
        recyclerViewReactions.setAdapter(reactionAdaptor);
        CommentAdaptor commentAdaptor = new CommentAdaptor( db.commentDao().getAll() );
        recyclerViewComments.setAdapter(commentAdaptor);
    }

    public void createComment(View view) {
        AppDatabase db = AppDatabase.getAppDatabase( getApplicationContext() );
        CommentDao commentDao = db.commentDao();
        EditText commentEditText = findViewById( R.id.editTextCommentContent);

        String commentContent = commentEditText.getText().toString();
        commentEditText.setText("Comment");
        commentDao.insert( new Comment( commentDao.nextId(), User.active.id, post.id, commentContent,0) );
        init();
    }
}