package com.uruksys.bazarapp_merchants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoriesActivity extends AppCompatActivity {

    AutoCompleteTextView etxtMainCat, etxtSub1Cat, etxtSub2Cat;
    Spinner sub2CatSpinner, sub1CatSpinner, mainCatSpinner;
    ProgressBar categoriesActivityProgressBar;
    Button btnAddMainCat, btnUpdateMainCat, btnDeleteMainCat, btnAddSub1Cat, btnUpdateSub1Cat, btnDeleteSub1Cat,
            btnAddSub2Cat, btnUpdateSub2Cat, btnDeleteSub2Cat;

    final String strMainCatSpinnerTitle = "Choose main category";
    final String strSub1CatSpinnerTitle = "Choose sub 1 category";
    final String strSub2CatSpinnerTitle = "Choose sub 2 category";
    final String strCatForDeletionUsedByProduct = "You can't delete this category because there is a product categorized under it";
    final String strSubCatToSelectedCatForDeletion = "you can't delete this category because there is sub category related to it!";
    final String strDeleteCategoryDialogMsg = "Are you sure you want to delete this category?";
    final String strAlertDialogYesBtn = "Yes";
    final String strAlertDialogNoBtn = "No";
    final String strAlertDialogCancelBtn = "Cancel";
    final String strSameNameForUpdateCatName = "Your new title is the same as the old title for the category";
    final String strEmptyCatTitle = "please enter the new Title";

    ArrayList<CategoriesModel> categoriesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        etxtMainCat = findViewById(R.id.etxtMainCat);
        etxtSub1Cat = findViewById(R.id.etxtSub1Cat);
        etxtSub2Cat = findViewById(R.id.etxtSub2Cat);
        sub2CatSpinner = findViewById(R.id.sub2CatSpinner);
        sub1CatSpinner = findViewById(R.id.sub1CatSpinner);
        mainCatSpinner = findViewById(R.id.mainCatSpinner);
        categoriesActivityProgressBar = findViewById(R.id.categoriesActivityProgressBar);
        btnAddMainCat = findViewById(R.id.btnAddMainCat);
        btnUpdateMainCat = findViewById(R.id.btnUpdateMainCat);
        btnDeleteMainCat = findViewById(R.id.btnDeleteMainCat);
        btnAddSub1Cat = findViewById(R.id.btnAddSub1Cat);
        btnUpdateSub1Cat = findViewById(R.id.btnUpdateSub1Cat);
        btnDeleteSub1Cat = findViewById(R.id.btnDeleteSub1Cat);
        btnAddSub2Cat = findViewById(R.id.btnAddSub2Cat);
        btnUpdateSub2Cat = findViewById(R.id.btnUpdateSub2Cat);
        btnDeleteSub2Cat = findViewById(R.id.btnDeleteSub2Cat);

        categoriesArrayList = new ArrayList<>();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        categoriesActivityProgressBar.setVisibility(View.VISIBLE);


        getCategories();
        OnSelectingCatInSpinner();
        AddNewCatBtnClicked();
        DeleteCatBtnClicked();
        UpdateCatBtnClicked();
    }


    private void UpdateCatBtnClicked() {
        btnUpdateMainCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtMainCat.getText().toString().trim().equals(mainCatSpinner.getSelectedItem().toString())) {
                    Toast.makeText(CategoriesActivity.this, strSameNameForUpdateCatName, Toast.LENGTH_LONG).show();
                } else if (etxtMainCat.getText().toString().trim().equals("")) {
                    Toast.makeText(CategoriesActivity.this, strEmptyCatTitle, Toast.LENGTH_LONG).show();
                } else {

                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("1") &&
                                categoriesModel.getCategoryTitle().equals(mainCatSpinner.getSelectedItem().toString())) {

                            UpdateCategoryTitle(categoriesModel.getCategoryId(), etxtMainCat.getText().toString().trim());
                        }
                    }
                }
            }
        });


        btnUpdateSub1Cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtSub1Cat.getText().toString().trim().equals(sub1CatSpinner.getSelectedItem().toString())) {

                    Toast.makeText(CategoriesActivity.this, strSameNameForUpdateCatName, Toast.LENGTH_LONG).show();
                } else if (etxtSub1Cat.getText().toString().trim().equals("")) {

                    Toast.makeText(CategoriesActivity.this, strEmptyCatTitle, Toast.LENGTH_LONG).show();
                } else {

                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("2")
                                && categoriesModel.getCategoryTitle().equals(sub1CatSpinner.getSelectedItem().toString())
                                && categoriesModel.getParentCatTitle().equals(mainCatSpinner.getSelectedItem().toString())) {

                            UpdateCategoryTitle(categoriesModel.getCategoryId(), etxtSub1Cat.getText().toString().trim());
                        }
                    }
                }
            }
        });


        btnUpdateSub2Cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etxtSub2Cat.getText().toString().trim().equals(sub2CatSpinner.getSelectedItem().toString())) {

                    Toast.makeText(CategoriesActivity.this, strSameNameForUpdateCatName, Toast.LENGTH_LONG).show();
                } else if (etxtSub2Cat.getText().toString().trim().equals("")) {

                    Toast.makeText(CategoriesActivity.this, strEmptyCatTitle, Toast.LENGTH_LONG).show();
                } else {

                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("3")
                                && categoriesModel.getCategoryTitle().equals(sub2CatSpinner.getSelectedItem().toString())
                                && categoriesModel.getParentCatTitle().equals(sub1CatSpinner.getSelectedItem().toString())) {

                            UpdateCategoryTitle(categoriesModel.getCategoryId(), etxtSub2Cat.getText().toString().trim());
                        }
                    }
                }
            }
        });
    }


    private void UpdateCategoryTitle(String categoryId, String newCatTitle) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        categoriesActivityProgressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("newCatTitle", newCatTitle);
            jsonObject.put("categoryId", categoryId);

        } catch (JSONException e) {

            e.printStackTrace();
        }

        new Thread(() -> {


            RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));
            Request request = new Request.Builder()
                    .url(LoginActivity.serverIp + "/updateCatTitle.php")
                    .post(requestBody)
                    .build();

            String s = null;
            try {
                Response response = LoginActivity.client.newCall(request).execute();

                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("hello", "IOException error " + e.getMessage());
            }

            String finalS = s;
            Log.d("hello", finalS);
            runOnUiThread(() -> {

                if (finalS.contains("updated successfully")) {

                    getCategories();

                    Toast.makeText(CategoriesActivity.this, finalS, Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(CategoriesActivity.this, finalS, Toast.LENGTH_LONG).show();

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    categoriesActivityProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }).start();
    }


    private void AddNewCatBtnClicked() {
        //add main category
        btnAddMainCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtMainCat.getText().toString().trim().equals("")) {
                    Toast.makeText(CategoriesActivity.this, "ادخل عنوان التصنيف الجديد", Toast.LENGTH_SHORT).show();
                } else {
                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("1") && categoriesModel.getCategoryTitle().equals(etxtMainCat.getText().toString().trim())) {
                            Toast.makeText(CategoriesActivity.this, "العنوان المدخل موجود مسبقا", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    AddNewCategory(1, etxtMainCat.getText().toString().trim(), null, null);
                }
            }
        });


        //add sub 1 category
        btnAddSub1Cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtSub1Cat.getText().toString().trim().equals("")) {
                    Toast.makeText(CategoriesActivity.this, "ادخل عنوان التصنيف الجديد", Toast.LENGTH_SHORT).show();
                } else {
                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("2")
                                && categoriesModel.getCategoryTitle().equals(etxtSub1Cat.getText().toString().trim())
                                && categoriesModel.getParentCatTitle().equals(mainCatSpinner.getSelectedItem().toString())) {
                            Toast.makeText(CategoriesActivity.this, "العنوان المدخل موجود مسبقا", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("1") &&
                                categoriesModel.getCategoryTitle().equals(mainCatSpinner.getSelectedItem().toString())) {

                            AddNewCategory(2, etxtSub1Cat.getText().toString().trim()
                                    , categoriesModel.getCategoryId(), categoriesModel.getCategoryTitle());
                        }
                    }
                }
            }
        });


        //add sub 2 category
        btnAddSub2Cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtSub2Cat.getText().toString().trim().equals("")) {
                    Toast.makeText(CategoriesActivity.this, "ادخل عنوان التصنيف الجديد", Toast.LENGTH_SHORT).show();
                } else {
                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("3")
                                && categoriesModel.getCategoryTitle().equals(etxtSub2Cat.getText().toString().trim())
                                && categoriesModel.getParentCatTitle().equals(sub1CatSpinner.getSelectedItem().toString())) {
                            Toast.makeText(CategoriesActivity.this, "العنوان المدخل موجود مسبقا", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("2") &&
                                categoriesModel.getCategoryTitle().equals(sub1CatSpinner.getSelectedItem().toString())
                                //&& categoriesModel.getParentCategoryId().equals("1")
                                && categoriesModel.getParentCatTitle().equals(mainCatSpinner.getSelectedItem().toString())) {

                            AddNewCategory(3, etxtSub2Cat.getText().toString().trim()
                                    , categoriesModel.getCategoryId(), categoriesModel.getCategoryTitle());
                        }
                    }
                }
            }
        });
    }


    private void AddNewCategory(int categoryLevel, String categoryTitle, String parentCategoryId, String parentCatTitle) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        categoriesActivityProgressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("categoryLevel", categoryLevel);
            jsonObject.put("categoryTitle", categoryTitle);
            jsonObject.put("parentCategoryId", parentCategoryId);
            jsonObject.put("parentCatTitle", parentCatTitle);

        } catch (JSONException e) {

            e.printStackTrace();
        }

        new Thread(() -> {


            RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));
            Request request = new Request.Builder()
                    .url(LoginActivity.serverIp + "/addNewCategory.php")
                    .post(requestBody)
                    .build();

            String s = null;
            try {
                Response response = LoginActivity.client.newCall(request).execute();

                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("hello", "IOException error " + e.getMessage());
            }

            String finalS = s;
            runOnUiThread(() -> {

                if (finalS.contains("added successfully")) {

                    getCategories();

                    Toast.makeText(CategoriesActivity.this, finalS, Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(CategoriesActivity.this, finalS, Toast.LENGTH_SHORT).show();

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    categoriesActivityProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }).start();
    }


    private void DeleteCatBtnClicked() {
        btnDeleteMainCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sub1CatSpinner.getCount() > 1) {
                    Toast.makeText(CategoriesActivity.this, strSubCatToSelectedCatForDeletion, Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoriesActivity.this)
                            .setMessage(strDeleteCategoryDialogMsg)
                            .setNegativeButton(strAlertDialogNoBtn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton(strAlertDialogYesBtn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                                        if (categoriesModel.getCategoryLevel().equals("1") && categoriesModel.getCategoryTitle().equals(mainCatSpinner.getSelectedItem().toString())) {

                                            DeleteCategory(1, categoriesModel.getCategoryId());
                                        }
                                    }
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });


        btnDeleteSub1Cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sub2CatSpinner.getCount() > 1) {
                    Toast.makeText(CategoriesActivity.this, strSubCatToSelectedCatForDeletion, Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoriesActivity.this)
                            .setMessage(strDeleteCategoryDialogMsg)
                            .setNegativeButton(strAlertDialogNoBtn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton(strAlertDialogYesBtn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                                        if (categoriesModel.getCategoryLevel().equals("2")
                                                && categoriesModel.getCategoryTitle().equals(sub1CatSpinner.getSelectedItem().toString())
                                                && categoriesModel.getParentCatTitle().equals(mainCatSpinner.getSelectedItem().toString())) {

                                            DeleteCategory(2, categoriesModel.getCategoryId());
                                        }
                                    }
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });


        btnDeleteSub2Cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CategoriesActivity.this)
                        .setMessage(strDeleteCategoryDialogMsg)
                        .setNegativeButton(strAlertDialogNoBtn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton(strAlertDialogYesBtn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                for (CategoriesModel categoriesModel : categoriesArrayList) {
                                    if (categoriesModel.getCategoryLevel().equals("3")
                                            && categoriesModel.getCategoryTitle().equals(sub2CatSpinner.getSelectedItem().toString())
                                            && categoriesModel.getParentCatTitle().equals(sub1CatSpinner.getSelectedItem().toString())) {

                                        DeleteCategory(3, categoriesModel.getCategoryId());
                                    }
                                }
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }


    private void DeleteCategory(int categoryLevel, String categoryId) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        categoriesActivityProgressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("categoryLevel", categoryLevel);
            jsonObject.put("categoryId", categoryId);

        } catch (JSONException e) {

            e.printStackTrace();
        }

        new Thread(() -> {


            RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));
            Request request = new Request.Builder()
                    .url(LoginActivity.serverIp + "/deleteCategory.php")
                    .post(requestBody)
                    .build();

            String s = null;
            try {
                Response response = LoginActivity.client.newCall(request).execute();

                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("hello", "IOException error " + e.getMessage());
            }

            String finalS = s;
            Log.d("hello", finalS);
            runOnUiThread(() -> {

                if (finalS.contains("category in use")) {

                    Toast.makeText(CategoriesActivity.this, strCatForDeletionUsedByProduct, Toast.LENGTH_LONG).show();

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    categoriesActivityProgressBar.setVisibility(View.INVISIBLE);

                } else if (finalS.contains("deleted successfully")) {

                    getCategories();

                    Toast.makeText(CategoriesActivity.this, finalS, Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(CategoriesActivity.this, finalS, Toast.LENGTH_LONG).show();

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    categoriesActivityProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }).start();
    }


    private void getCategories() {
        new Thread(() -> {

            Request request = new Request.Builder()
                    .url(LoginActivity.serverIp + "/getCategories.php")
                    .build();

            String s = null;
            try {
                Response response = LoginActivity.client.newCall(request).execute();

                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("hello", "IOException error " + e.getMessage());
            }

            String finalS = s;
            runOnUiThread(() -> {

                try {
                    JSONArray jsonArray = new JSONArray(finalS);
                    categoriesArrayList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String categoryId = jsonObject.getString("categoryId");
                        String categoryTitle = jsonObject.getString("categoryTitle");
                        String categoryLevel = jsonObject.getString("categoryLevel");
                        String parentCategoryId = jsonObject.getString("parentCategoryId");
                        String parentCatTitle = jsonObject.getString("parentCatTitle");

                        categoriesArrayList.add(new CategoriesModel(categoryId, categoryTitle, categoryLevel, parentCategoryId, parentCatTitle));
                    }

                    PopulateMainCat();

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    categoriesActivityProgressBar.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("hello", "JSONException error " + e.getMessage());
                }
            });
        }).start();
    }


    private void PopulateMainCat() {

        ArrayList<String> mainCategoriesArrayList = new ArrayList<>();
        mainCategoriesArrayList.add(strMainCatSpinnerTitle);
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("1")) {
                mainCategoriesArrayList.add(categoriesModel.getCategoryTitle());
            }
        }

        mainCatSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, mainCategoriesArrayList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {

                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView TV = (TextView) view;
                if (position == 0) {
                    TV.setTextColor(Color.GRAY);
                } else {
                    TV.setTextColor(Color.RED);
                }
                return view;
            }
        });
        PopulateSub1Cat();
    }


    private void PopulateSub1Cat() {

        ArrayList<String> sub1CategoriesArrayList = new ArrayList<>();
        sub1CategoriesArrayList.add(strSub1CatSpinnerTitle);
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("2") && mainCatSpinner.getSelectedItem().toString().equals(categoriesModel.getParentCatTitle())) {
                sub1CategoriesArrayList.add(categoriesModel.getCategoryTitle());
            }
        }

        sub1CatSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, sub1CategoriesArrayList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {

                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView TV = (TextView) view;
                if (position == 0) {
                    TV.setTextColor(Color.GRAY);
                } else {
                    TV.setTextColor(Color.RED);
                }
                return view;
            }
        });
        PopulateSub2Cat();
    }


    private void PopulateSub2Cat() {

        ArrayList<String> sub2CategoriesArrayList = new ArrayList<>();
        sub2CategoriesArrayList.add(strSub2CatSpinnerTitle);
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("3") && sub1CatSpinner.getSelectedItem().toString().equals(categoriesModel.getParentCatTitle())) {
                sub2CategoriesArrayList.add(categoriesModel.getCategoryTitle());
            }
        }

        sub2CatSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, sub2CategoriesArrayList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {

                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView TV = (TextView) view;
                if (position == 0) {
                    TV.setTextColor(Color.GRAY);
                } else {
                    TV.setTextColor(Color.RED);
                }
                return view;
            }
        });
    }


    private void OnSelectingCatInSpinner() {
        mainCatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PopulateSub1Cat();
                if (!mainCatSpinner.getSelectedItem().toString().equals(strMainCatSpinnerTitle)) {

                    etxtMainCat.setText(mainCatSpinner.getSelectedItem().toString());
                    etxtSub1Cat.setEnabled(true);
                    btnDeleteMainCat.setEnabled(true);
                    btnUpdateMainCat.setEnabled(true);
                } else {

                    etxtMainCat.setText("");
                    etxtSub1Cat.setEnabled(false);
                    btnDeleteMainCat.setEnabled(false);
                    btnUpdateMainCat.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sub1CatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PopulateSub2Cat();
                if (!sub1CatSpinner.getSelectedItem().toString().equals(strSub1CatSpinnerTitle)) {

                    etxtSub1Cat.setText(sub1CatSpinner.getSelectedItem().toString());
                    etxtSub2Cat.setEnabled(true);
                    btnDeleteSub1Cat.setEnabled(true);
                    btnUpdateSub1Cat.setEnabled(true);
                } else {

                    etxtSub1Cat.setText("");
                    etxtSub2Cat.setEnabled(false);
                    btnDeleteSub1Cat.setEnabled(false);
                    btnUpdateSub1Cat.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sub2CatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!sub2CatSpinner.getSelectedItem().toString().equals(strSub2CatSpinnerTitle)) {

                    etxtSub2Cat.setText(sub2CatSpinner.getSelectedItem().toString());
                    btnDeleteSub2Cat.setEnabled(true);
                    btnUpdateSub2Cat.setEnabled(true);
                } else {

                    etxtSub2Cat.setText("");
                    btnDeleteSub2Cat.setEnabled(false);
                    btnUpdateSub2Cat.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}