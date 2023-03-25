package com.uruksys.bazarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagingSupportRecyclerAdapter extends RecyclerView.Adapter<MessagingSupportRecyclerAdapter.MyViewHolder>  {


    Context myContext;
    int resource;
    ArrayList<InvoiceShopChatModel> supportMessagingArrayList;

    public MessagingSupportRecyclerAdapter(Context context, int resource, ArrayList<InvoiceShopChatModel> supportMessagingArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.supportMessagingArrayList = supportMessagingArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        InvoiceShopChatModel invoiceShopChatModel = supportMessagingArrayList.get(position);

        holder.txtMessageDate.setText(invoiceShopChatModel.getMsgDate());
        holder.txtMessageBody.setText(invoiceShopChatModel.getCreatedAt());

        if(invoiceShopChatModel.getSender().equals(MainActivity.CHAT_SENDER.manager.toString())){
            holder.txtMessageBody.setBackground(myContext.getDrawable(R.drawable.outgoing_msg_background));
            holder.txtMessageBody.setTextDirection(View.TEXT_DIRECTION_LTR);
        }
        else{
            holder.txtMessageBody.setBackground(myContext.getDrawable(R.drawable.incoming_msg_background));
            holder.txtMessageBody.setTextDirection(View.TEXT_DIRECTION_RTL);
        }
    }


    @Override
    public int getItemCount() {
        return supportMessagingArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtMessageDate, txtMessageBody;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMessageDate = itemView.findViewById(R.id.txtMessageDate);
            txtMessageBody = itemView.findViewById(R.id.txtMessageBody);
        }
    }
}
