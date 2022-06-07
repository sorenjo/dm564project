package com.example.dm564project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHolder>{

    private List<Post> posts;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView textView2;

        private Post post;

        public ViewHolder(View view){
            super(view);

            textView = view.findViewById(R.id.textView2);
            textView2 = view.findViewById(R.id.textView3);

            Button likeButton = view.findViewById(R.id.btnLike);
            Button hateButton = view.findViewById(R.id.btnHate);
            Button careButton = view.findViewById(R.id.btnCare);
            Button unreactButton = view.findViewById(R.id.btnUnreact);

            if( User.active == null) {
                // user anonymous, reacting disabled
                likeButton.setVisibility(View.GONE);
                hateButton.setVisibility(View.GONE);
                careButton.setVisibility(View.GONE);
                unreactButton.setVisibility(View.GONE);
            }

            AppDatabase db = AppDatabase.getAppDatabase(view.getContext());
            ReactionDao reactionDao = db.reactionDao();

            likeButton.setOnClickListener(v -> {
                reactionDao.insert(new Reaction(User.active.id, post.id, Reaction.LIKE, false, 0, 0));
                db.syncDatabase();
            });

            hateButton.setOnClickListener(v -> {
                reactionDao.insert(new Reaction(User.active.id, post.id, Reaction.HATE, false, 0, 0));
                db.syncDatabase();
            });

            careButton.setOnClickListener(v -> {
                reactionDao.insert(new Reaction(User.active.id, post.id, Reaction.COULDNT_CARE_LESS, false, 0, 0));
                db.syncDatabase();
            });

            unreactButton.setOnClickListener(v -> {
                reactionDao.insert(new Reaction(User.active.id, post.id, Reaction.REACTION_DELETED, false, 0, 0));
                db.syncDatabase();
            });

            // goto postdetailactivity when a post is clicked.
            view.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", post.id);
                context.startActivity(intent);
            });
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getTextView2() {
            return textView2;
        }
    }

    public PostAdaptor(List<Post> posts){
        this.posts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.post = posts.get( position );
        holder.getTextView().setText( holder.post.userId + " :" );
        holder.getTextView2().setText( posts.get( position ).content );

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
