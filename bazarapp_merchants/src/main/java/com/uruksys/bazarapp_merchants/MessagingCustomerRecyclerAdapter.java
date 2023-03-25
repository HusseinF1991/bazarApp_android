package com.uruksys.bazarapp_merchants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagingCustomerRecyclerAdapter  extends RecyclerView.Adapter<MessagingCustomerRecyclerAdapter.MyViewHolder>  {


    Context myContext;
    int resource;
    ArrayList<CustomerMessagingModel> customerMessagingArrayList;

    public MessagingCustomerRecyclerAdapter(Context context, int resource, ArrayList<CustomerMessagingModel> customerMessagingArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.customerMessagingArrayList = customerMessagingArrayList;
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

        CustomerMessagingModel customerMessagingModel = customerMessagingArrayList.get(position);

        holder.txtMessageDate.setText(customerMessagingModel.getMessageDate());
        holder.txtMessageBody.setText(customerMessagingModel.getMessageBody());

        if(customerMessagingModel.getSender().equals("customer")){
            holder.txtMessageBody.setBackground(myContext.getDrawable(R.drawable.outgoing_message_bg));
            holder.txtMessageBody.setTextDirection(View.TEXT_DIRECTION_LTR);
        }
        else{
            holder.txtMessageBody.setBackground(myContext.getDrawable(R.drawable.incoming_message_bg));
            holder.txtMessageBody.setTextDirection(View.TEXT_DIRECTION_RTL);
        }
    }


    @Override
    public int getItemCount() {
        return customerMessagingArrayList.size();
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
