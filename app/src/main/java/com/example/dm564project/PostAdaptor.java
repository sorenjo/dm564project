package com.example.dm564project;

import android.content.Context;
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
        private final Button likeButton;
        private Post post;

        public ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.textView2);
            textView2 = view.findViewById(R.id.textView3);
            likeButton = view.findViewById(R.id.btnLike);
            //TODO de andre reaktioner. Samme fremgangsmåde som btnLike.
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //reaction TODO
                    System.out.println("----------------------- " + post.id + " was liked");
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("--------------- " + post.content + " was clicked" );
                }
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
        holder.getTextView().setText( holder.post.user + " :" ); //TODO Do not concatenate text displayed with `setText`. Use resource string with placeholders.
        holder.getTextView2().setText( posts.get( position ).content );

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
