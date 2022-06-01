package com.example.dm564project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdaptor extends RecyclerView.Adapter<CommentAdaptor.CommentViewHolder> {

    private List< Comment > comments;

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView commentTextView;
        private Comment comment;

        public CommentViewHolder(View view){
            super(view);
            commentTextView = view.findViewById(R.id.comment);
        }

        public TextView getCommentTextView(){
            return commentTextView;
        }
    }

    public CommentAdaptor( List< Comment > comments ){
        this.comments = comments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item_layout, viewGroup, false);
        return new CommentAdaptor.CommentViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdaptor.CommentViewHolder holder, int position){
        holder.comment = comments.get( position );
        holder.getCommentTextView().setText( holder.comment.userId + ": " + holder.comment.comment );
    }

    @Override
    public int getItemCount(){
        return comments.size();
    }
}
