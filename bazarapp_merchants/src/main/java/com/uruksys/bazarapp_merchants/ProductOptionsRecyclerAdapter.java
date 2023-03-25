package com.uruksys.bazarapp_merchants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductOptionsRecyclerAdapter  extends RecyclerView.Adapter<ProductOptionsRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    Context myContext;
    int resource;
    ArrayList<ProductOptionsModel> productOptionsArrayList;
    RecyclerView recyclerView ;

    public ProductOptionsRecyclerAdapter(Context context , int resource, ArrayList<ProductOptionsModel> productOptionsArrayList , RecyclerView recyclerView) {

        this.myContext = context;
        this.resource = resource;
        this.productOptionsArrayList = productOptionsArrayList;
        this.recyclerView = recyclerView;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        itemView.setOnClickListener(this);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ProductOptionsModel productOptionsModel = productOptionsArrayList.get(position);

        holder.txtOptionTitle.setText(productOptionsModel.getOptionTitle());
        holder.txtOptionSellPrice.setText(productOptionsModel.getSellPrice());
        holder.txtOptionDiscount.setText(productOptionsModel.getDiscount());
        holder.txtOptionAvailableQty.setText(productOptionsModel.getAvailableQty());
        holder.txtOptionDescription.setText(productOptionsModel.getDescription());

        Log.d("hello", productOptionsModel.getOptionTitle());

    }


    @Override
    public int getItemCount() {
        return productOptionsArrayList.size();
    }



    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                .setMessage("Remove clicked product option?")
                .setNegativeButton("No", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int itemPosition = recyclerView.getChildLayoutPosition(view);
                        productOptionsArrayList.remove(itemPosition);
                        notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtOptionTitle, txtOptionSellPrice , txtOptionDiscount, txtOptionAvailableQty , txtOptionDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOptionTitle = itemView.findViewById(R.id.txtOptionTitle);
            txtOptionSellPrice = itemView.findViewById(R.id.txtOptionSellPrice);
            txtOptionDiscount = itemView.findViewById(R.id.txtOptionDiscount);
            txtOptionAvailableQty = itemView.findViewById(R.id.txtOptionAvailableQty);
            txtOptionDescription = itemView.findViewById(R.id.txtOptionDescription);
        }
    }
}
