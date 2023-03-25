package com.uruksys.bazarapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemTypesRecyclerAdapter extends RecyclerView.Adapter<ItemTypesRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    Context myContext;
    int resource;
    ArrayList<ItemTypeDetailsModel> itemTypesDetailsArrayList;
    ProductInfoActivity productInfoActivity;


    public ItemTypesRecyclerAdapter(
            Context context,
            int resource,
            ArrayList<ItemTypeDetailsModel> itemTypesDetailsArrayList,
            ProductInfoActivity productInfoActivity
    ) {

        this.myContext = context;
        this.resource = resource;
        this.itemTypesDetailsArrayList = itemTypesDetailsArrayList;
        this.productInfoActivity = productInfoActivity;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.myTextView) {

            int position = (int) view.getTag();

            MyViewHolder viewHolder1 = (MyViewHolder) ProductInfoActivity.productOptionsRecycler
                    .findViewHolderForAdapterPosition(ProductInfoActivity.selectedItemTypePositionInArrayList);
            if (viewHolder1 != null)
                viewHolder1.myTextView.setBackgroundColor(Color.TRANSPARENT);

            ProductInfoActivity.selectedItemTypePositionInArrayList = ProductInfoActivity.productOptionsRecycler.getChildLayoutPosition(view);

            MyViewHolder viewHolder2 = (MyViewHolder) ProductInfoActivity.productOptionsRecycler
                    .findViewHolderForAdapterPosition(ProductInfoActivity.selectedItemTypePositionInArrayList);
            viewHolder2.myTextView.setBackgroundColor(Color.parseColor("#e8eaf6"));

            ProductInfoActivity.selectedItemTypeModel = itemTypesDetailsArrayList.get(ProductInfoActivity.selectedItemTypePositionInArrayList);

            productInfoActivity.setSelectedItemTypeDetails();
            productInfoActivity.GetProductQtyInCart();
        }
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView myTextView;

        MyViewHolder(View view) {
            super(view);

            myTextView = view.findViewById(R.id.myTextView);
        }
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

        ItemTypeDetailsModel itemTypeDetails = itemTypesDetailsArrayList.get(position);

        holder.myTextView.setText(itemTypeDetails.getTypeName());

        if (position == ProductInfoActivity.selectedItemTypePositionInArrayList) {
            holder.myTextView.setBackgroundColor(Color.parseColor("#e8eaf6"));
        }
        holder.myTextView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return itemTypesDetailsArrayList.size();
    }
}
