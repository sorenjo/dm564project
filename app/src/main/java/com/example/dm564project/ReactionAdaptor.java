package com.example.dm564project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReactionAdaptor extends RecyclerView.Adapter<ReactionAdaptor.ReactionViewHolder> {

    private List<Reaction> reactions;

    public static class ReactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView reactionTextView;
        private Reaction reaction;

        public ReactionViewHolder(View view){
            super(view);
            reactionTextView = view.findViewById(R.id.reaction);
        }

        public TextView getReactionTextView(){
            return reactionTextView;
        }
    }

    public ReactionAdaptor(List< Reaction > reactions ){
        this.reactions = reactions;
    }

    @Override
    public ReactionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reaction_item_layout, viewGroup, false);
        return new ReactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder, int position){
        holder.reaction = reactions.get(position);
        holder.getReactionTextView().setText(holder.reaction.userId + Reaction.reactionTexts[holder.reaction.type] + "this post");
    }

    @Override
    public int getItemCount(){
        return reactions.size();
    }
}
