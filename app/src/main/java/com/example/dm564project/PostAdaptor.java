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
                likeButton.setVisibility( View.GONE );
                hateButton.setVisibility( View.GONE );
                careButton.setVisibility( View.GONE );
                unreactButton.setVisibility( View.GONE );
            }

            AppDatabase db = AppDatabase.getAppDatabase( view.getContext() );
            ReactionDao reactionDao = db.reactionDao();

            likeButton.setOnClickListener(v -> {
                reactionDao.insert(new Reaction( User.active.id, post.id, Reaction.LIKE, false ));
            });

            hateButton.setOnClickListener(v -> {
                reactionDao.insert(new Reaction( User.active.id, post.id, Reaction.HATE, false ));
            });

            careButton.setOnClickListener(v -> {
                reactionDao.insert(new Reaction( User.active.id, post.id, Reaction.COULDNT_CARE_LESS, false ));
            });

            unreactButton.setOnClickListener(v -> {
                reactionDao.insert(new Reaction( User.active.id, post.id, Reaction.REACTION_DELETED, false ));
            });

            view.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent( context, PostDetailActivity.class );
                intent.putExtra("postId", post.id );//TODO det her navn bør nok være en konstant tilhørende den her klasse.
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
        holder.getTextView().setText( holder.post.userId + " :" ); //TODO Do not concatenate text displayed with `setText`. Use resource string with placeholders.
        holder.getTextView2().setText( posts.get( position ).content );

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
