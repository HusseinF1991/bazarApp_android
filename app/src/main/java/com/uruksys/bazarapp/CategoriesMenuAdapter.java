package com.uruksys.bazarapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;


public class CategoriesMenuAdapter extends RecyclerView.Adapter<CategoriesMenuAdapter.MyViewHolder> implements View.OnClickListener {


    Context myContext;
    int resource;
    ArrayList<CategoriesMenuModel> oneLevelCategoryList;
    private SearchForCategoryClickListener mListener;

    public CategoriesMenuAdapter(Context context, int resource, ArrayList<CategoriesMenuModel> oneLevelCategoryList) {
        myContext = context;
        this.resource = resource;
        this.oneLevelCategoryList = oneLevelCategoryList;
        this.mListener = (SearchForCategoryClickListener) myContext;
    }


    @Override
    public void onClick(View view) {
        int position = MainActivity_MainFragment.categoriesRecyclerView.getChildLayoutPosition(view);
        ArrayList<CategoriesMenuModel> selectedOneLevelCategoryList = new ArrayList<>();


        for (CategoriesMenuModel categoriesMenuModel : MainActivity_MainFragment.categoriesMenuModelArrayList) {
            if (categoriesMenuModel.getParentCategoryId() == oneLevelCategoryList.get(position).getCategoryId()
                    && oneLevelCategoryList.get(position).getCategoryId() != 0) {
                selectedOneLevelCategoryList.add(categoriesMenuModel);
            }
        }


        if (selectedOneLevelCategoryList.size() > 0) {

            selectedOneLevelCategoryList.add(0, new CategoriesMenuModel
                    (0, MainActivity_MainFragment.selectCategoryLevel + 1, myContext.getString(R.string.ViewAll),
                            oneLevelCategoryList.get(position).getCategoryId(), null));


            MainActivity_MainFragment.selectedParentCategoryId = oneLevelCategoryList.get(position).getCategoryId();
            oneLevelCategoryList = selectedOneLevelCategoryList;
            MainActivity_MainFragment.selectCategoryLevel++;
            MainActivity_MainFragment.imgCancelCategoriesMenu.setVisibility(View.INVISIBLE);
            MainActivity_MainFragment.imgReturnToPreviousCat.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        } else {

            //show the items for the selected category
            if (oneLevelCategoryList.get(position).getCategoryId() != 0) {

                mListener.onSearchForCategoryClickListener(oneLevelCategoryList.get(position).getCategoryId());
            } else {

                mListener.onSearchForCategoryClickListener(oneLevelCategoryList.get(position).getParentCategoryId());
            }
        }
    }


    public interface SearchForCategoryClickListener {
        void onSearchForCategoryClickListener(int categoryId);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtCategoryTitle;
        ImageView imgArrowToSubCat;

        MyViewHolder(View view) {
            super(view);

            imgArrowToSubCat = view.findViewById(R.id.imgArrowToSubCat);
            txtCategoryTitle = view.findViewById(R.id.txtCategoryTitle);
            SharedPreferences sharedPreferences = myContext.getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
            if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

                imgArrowToSubCat.setScaleX(-1);
            }
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

        CategoriesMenuModel categoriesMenuModel = oneLevelCategoryList.get(position);
        holder.txtCategoryTitle.setText(categoriesMenuModel.getCategoryTitle());
    }


    @Override
    public int getItemCount() {
        return oneLevelCategoryList.size();
    }
}
