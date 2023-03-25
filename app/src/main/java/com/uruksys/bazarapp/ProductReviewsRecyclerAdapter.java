package com.uruksys.bazarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProductReviewsRecyclerAdapter extends RecyclerView.Adapter<ProductReviewsRecyclerAdapter.MyViewHolder> {

    Context myContext;
    int resource;
    ArrayList<ItemReviewsModel> itemReviewsArrayList;

    public ProductReviewsRecyclerAdapter(Context myContext, int resource, ArrayList<ItemReviewsModel> itemReviewsArrayList) {

        this.myContext = myContext;
        this.resource = resource;
        this.itemReviewsArrayList = itemReviewsArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(myContext).inflate(resource, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public int getItemCount() {
        return itemReviewsArrayList.size();
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.txtReviewerName.setText(itemReviewsArrayList.get(position).getReviewerName());
        holder.txtReviewTitle.setText(itemReviewsArrayList.get(position).getTitle());
        holder.txtReviewBody.setText(itemReviewsArrayList.get(position).getBody());
        holder.txtReviewCountNumber.setText(position + 1 + "");

        try {
            // parse the String "29/07/2013" to a java.util.Date object
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date newDate = isoFormat.parse(itemReviewsArrayList.get(position).getCreatedAt());
            // format the java.util.Date object to the desired format
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
            String date = dateTimeFormat.format(newDate);
            holder.txtReviewDate.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.txtReviewDate.setText(itemReviewsArrayList.get(position).getCreatedAt());
        }

        switch ((int) itemReviewsArrayList.get(position).getRate()) {
            case 1:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                break;
            case 2:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                break;
            case 3:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                break;
            case 4:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar4.setImageResource(R.drawable.ic_star_yellow);
                break;
            case 5:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar4.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar5.setImageResource(R.drawable.ic_star_yellow);
                break;
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtReviewCountNumber, txtReviewerName, txtReviewTitle, txtReviewBody, txtReviewDate;
        ImageView imgProductRateStar1, imgProductRateStar2, imgProductRateStar3, imgProductRateStar4, imgProductRateStar5;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtReviewBody = itemView.findViewById(R.id.txtReviewBody);
            txtReviewCountNumber = itemView.findViewById(R.id.txtReviewCountNumber);
            txtReviewTitle = itemView.findViewById(R.id.txtReviewTitle);
            txtReviewerName = itemView.findViewById(R.id.txtReviewerName);
            txtReviewDate = itemView.findViewById(R.id.txtReviewDate);
            imgProductRateStar1 = itemView.findViewById(R.id.imgProductRateStar1);
            imgProductRateStar2 = itemView.findViewById(R.id.imgProductRateStar2);
            imgProductRateStar3 = itemView.findViewById(R.id.imgProductRateStar3);
            imgProductRateStar4 = itemView.findViewById(R.id.imgProductRateStar4);
            imgProductRateStar5 = itemView.findViewById(R.id.imgProductRateStar5);
        }
    }
}
