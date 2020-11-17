package com.example.appchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MsgHolder>{

    static ArrayList<Message> messages;
    final int VIEW_TYPE_MESSAGE_RECEIVED = 0;
    final int VIEW_TYPE_MESSAGE_SENT = 1;

    static class MsgHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layoutR,layoutS;
        TextView messageR,messageS;
        TextView timeR,timeS;
        TextView name;

        public MsgHolder(@NonNull View itemView) {
            super(itemView);
            layoutR = itemView.findViewById(R.id.layout_received);
            layoutS = itemView.findViewById(R.id.layout_sent);

            messageR = itemView.findViewById(R.id.messageR);
            messageS = itemView.findViewById(R.id.messageS);

            timeR = itemView.findViewById(R.id.timeR);
            timeS = itemView.findViewById(R.id.timeS);

            name = itemView.findViewById(R.id.nameR);
        }
    }

    public MessagesAdapter(ArrayList<Message> mes){
        messages = mes;
    }

    @NonNull
    @Override
    public MsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MsgHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MsgHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                holder.layoutS.setVisibility(View.VISIBLE);
                holder.layoutR.setVisibility(View.GONE);
                holder.messageS.setText(messages.get(position).getMessage());
                holder.timeS.setText(messages.get(position).getTime());
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                holder.layoutS.setVisibility(View.GONE);
                holder.layoutR.setVisibility(View.VISIBLE);
                holder.messageR.setText(messages.get(position).getMessage());
                holder.timeR.setText(messages.get(position).getTime());
                holder.name.setText(messages.get(position).getName());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getName().equals(""))
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}