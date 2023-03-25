package com.uruksys.bazarapp_merchants;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PurchasesRequestsRecyclerAdapter extends RecyclerView.Adapter<PurchasesRequestsRecyclerAdapter.MyViewHolder> implements View.OnClickListener {


    Context myContext;
    int resource;
    ArrayList<PurchasesRequestsModel> purchasesRequestsArrayList;
    String whichRequests = "all";

    public PurchasesRequestsRecyclerAdapter(Context context, int resource, ArrayList<PurchasesRequestsModel> purchasesRequestsArrayList, String whichRequests) {

        this.myContext = context;
        this.resource = resource;
        this.purchasesRequestsArrayList = purchasesRequestsArrayList;
        this.whichRequests = whichRequests;
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

        PurchasesRequestsModel purchasesRequestsModel = purchasesRequestsArrayList.get(position);

        holder.txtInvoiceDate.setText(purchasesRequestsModel.getRecipeDate());
        holder.txtInvoiceNumber.setText(purchasesRequestsModel.getInvoice() + "");
        holder.txtInvoiceCost.setText(purchasesRequestsModel.getRecipeCost() + "");
        holder.txtPaidAmount.setText(purchasesRequestsModel.getPaidAmount() + "");
        holder.txtCustomerName.setText(purchasesRequestsModel.getCustomerName());
        holder.txtCustomerMobile.setText(purchasesRequestsModel.getCustomerMobile());
        holder.txtCustomerProvince.setText(purchasesRequestsModel.getProvince());

        if (purchasesRequestsModel.getRecipeStatus().equals("pending")) {

            holder.txtRecipeStatusText.setTextColor(myContext.getColor(R.color.colorPrimaryDark));
            holder.txtRecipeStatusText.setText("PENDING");
        } else {
            holder.txtRecipeStatusText.setTextColor(myContext.getColor(R.color.colorAccent));
            holder.txtRecipeStatusText.setText("APPROVED");
        }

        holder.imgDisplayItemsOfInvoice.setTag(position);
        holder.imgDisplayItemsOfInvoice.setOnClickListener(this);

        holder.imgDisplayMessagingOfInvoice.setTag(position);
        holder.imgDisplayMessagingOfInvoice.setOnClickListener(this);

        holder.imgCallCustomer.setTag(position);
        holder.imgCallCustomer.setOnClickListener(this);

        holder.imgCancelOrder.setTag(position);
        holder.imgCancelOrder.setOnClickListener(this);

        holder.imgEditOnOrder.setTag(position);
        holder.imgEditOnOrder.setOnClickListener(this);

        holder.btnDisplayCustomerLocInMap.setTag(position);
        holder.btnDisplayCustomerLocInMap.setOnClickListener(this);

        Log.d("hello", purchasesRequestsModel.getCustomerName());
    }


    @Override
    public int getItemCount() {
        return purchasesRequestsArrayList.size();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgDisplayMessagingOfInvoice) {

            int position = (int) view.getTag();
            int invoice = purchasesRequestsArrayList.get(position).getInvoice();
            Intent intent = new Intent(myContext, MessagingCustomerForInvoiceActivity.class);
            intent.putExtra("invoice", invoice);
            myContext.startActivity(intent);
//            notifyDataSetChanged();
        }
        else if (view.getId() == R.id.imgDisplayItemsOfInvoice) {

            int position = (int) view.getTag();
            int invoice = purchasesRequestsArrayList.get(position).getInvoice();
            Intent intent = new Intent(myContext, NewSellsRequests_ItemsOfInvoiceActivity.class);
            intent.putExtra("invoice", invoice);
            myContext.startActivity(intent);
        }
        else if (view.getId() == R.id.imgCallCustomer) {

            int position = (int) view.getTag();
            String mobile  = purchasesRequestsArrayList.get(position).getCustomerMobile();

            if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                NewSellsRequestsActivity.SelectedInvoice_CustomerMobile = mobile;
                ActivityCompat.requestPermissions(
                        ((NewSellsRequestsActivity)myContext),
                        new String[]{Manifest.permission.CALL_PHONE},
                        LoginActivity.MY_PHONE_CALL_PERMISSION_CODE_IMAGE);
            }
            else{

                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + mobile));
                myContext.startActivity(intent);
            }

        }
        else if (view.getId() == R.id.imgEditOnOrder) {

            int position = (int) view.getTag();


            OpenDialogToEditOrder(position);
        }
        else if (view.getId() == R.id.imgCancelOrder) {

            int position = (int) view.getTag();
            int invoice = purchasesRequestsArrayList.get(position).getInvoice();

            AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                    .setMessage("Are you sure you want to delete the invoice")
                    .setPositiveButton("yes", (dialogInterface, i) -> {

                        DeletePurchaseOrder(invoice);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("no", (dialogInterface, i) -> {

                        dialogInterface.dismiss();
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else if (view.getId() == R.id.btnDisplayCustomerLocInMap){

            int position = (int) view.getTag();
            double lat = purchasesRequestsArrayList.get(position).getLat();
            double lng = purchasesRequestsArrayList.get(position).getLng();

            Intent intent = new Intent(myContext , MapsActivity.class);
            intent.putExtra("lat" , lat);
            intent.putExtra("lng" , lng);
            myContext.startActivity(intent);
        }
    }



    private void OpenDialogToEditOrder(int position) {

        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view1 = inflater.inflate(R.layout.edit_purchase_req_invoice_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                .setView(view1);

        AlertDialog alertDialog = builder.create();


        int invoice = purchasesRequestsArrayList.get(position).getInvoice();
        String recipeStatus = purchasesRequestsArrayList.get(position).getRecipeStatus();
        String customerMobile = purchasesRequestsArrayList.get(position).getCustomerMobile();
        double paidAmount = purchasesRequestsArrayList.get(position).getPaidAmount();

        TextView txtDeliveredRecipeSelectionWarning = view1.findViewById(R.id.txtDeliveredRecipeSelectionWarning);
        txtDeliveredRecipeSelectionWarning.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) txtDeliveredRecipeSelectionWarning.getLayoutParams();
        params.height = 0;
        txtDeliveredRecipeSelectionWarning.setLayoutParams(params);

        EditText etxtCustomerMobile = view1.findViewById(R.id.etxtCustomerMobile);
        etxtCustomerMobile.setText(customerMobile);
        EditText etxtPaidAmount = view1.findViewById(R.id.etxtPaidAmount);
        etxtPaidAmount.setText(paidAmount + "");
        RadioButton rbRecipeStatusApproved = view1.findViewById(R.id.rbRecipeStatusApproved);
        if (recipeStatus.equals("approved")) rbRecipeStatusApproved.setChecked(true);
        RadioButton rbRecipeStatusPending = view1.findViewById(R.id.rbRecipeStatusPending);
        if (recipeStatus.equals("pending")) rbRecipeStatusPending.setChecked(true);
        RadioButton rbRecipeStatusDelivered = view1.findViewById(R.id.rbRecipeStatusDelivered);
        if (recipeStatus.equals("delivered")) rbRecipeStatusDelivered.setChecked(true);
        Button btnConfirmPurchaseReqEdit = view1.findViewById(R.id.btnConfirmPurchaseReqEdit);
        Button btnCancelPurchaseReqEdit = view1.findViewById(R.id.btnCancelPurchaseReqEdit);


        rbRecipeStatusDelivered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) txtDeliveredRecipeSelectionWarning.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    txtDeliveredRecipeSelectionWarning.setLayoutParams(params);
                    txtDeliveredRecipeSelectionWarning.setVisibility(View.VISIBLE);
                } else {
                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) txtDeliveredRecipeSelectionWarning.getLayoutParams();
                    params.height = 0;
                    txtDeliveredRecipeSelectionWarning.setLayoutParams(params);
                    txtDeliveredRecipeSelectionWarning.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnCancelPurchaseReqEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnConfirmPurchaseReqEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String customerMobile = etxtCustomerMobile.getText().toString();
                double paidAmount = Double.parseDouble(etxtPaidAmount.getText().toString());
                String recipeStatus = null;
                if (rbRecipeStatusApproved.isChecked()) recipeStatus = "approved";
                else if (rbRecipeStatusDelivered.isChecked()) recipeStatus = "delivered";
                else if (rbRecipeStatusPending.isChecked()) recipeStatus = "pending";

                if (recipeStatus.equals("delivered"))
                    MoveRecipeToSellsArchive(invoice, customerMobile, paidAmount, recipeStatus);
                else
                    EditOrder(invoice, customerMobile, paidAmount, recipeStatus);

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }



    private void MoveRecipeToSellsArchive(int invoice, String customerMobile, double paidAmount, String recipeStatus) {
        AtomicBoolean replayThread = new AtomicBoolean(false);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("invoice", invoice);
            jsonObject.put("customerMobile", customerMobile);
            jsonObject.put("paidAmount", paidAmount);
            jsonObject.put("recipeStatus", recipeStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        do {
            new Thread(() -> {
                RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/purchaseReqDeliveredToCustomer_admin.php")
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("purchaseReqDeliveredToCustomer_admin", "on background");


                    String finalS = s;
                    ((NewSellsRequestsActivity) myContext).runOnUiThread(() -> {

                        Log.d("purchaseReqDeliveredToCustomer_admin", finalS);

                        if (finalS.contains("moved successfully")) {

                            for (PurchasesRequestsModel purchasesRequestsModel : purchasesRequestsArrayList) {
                                if (purchasesRequestsModel.getInvoice() == invoice) {

                                    purchasesRequestsArrayList.remove(purchasesRequestsModel);
                                    break;
                                }
                            }
                            for (PurchasesRequestsModel purchasesRequestsModel : NewSellsRequestsActivity.PurchasesRequestsInvoicesArrayList) {
                                if (purchasesRequestsModel.getInvoice() == invoice) {

                                    NewSellsRequestsActivity.PurchasesRequestsInvoicesArrayList.remove(purchasesRequestsModel);
                                    break;
                                }
                            }
                            Toast.makeText(myContext, "تم نقل الفاتورة الى ارشيف المبيعات", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(myContext, "فشل في التعديل", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("purchaseReqDeliveredToCustomer_admin", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }



    private void EditOrder(int invoice, String customerMobile, double paidAmount, String recipeStatus) {

        AtomicBoolean replayThread = new AtomicBoolean(false);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("invoice", invoice);
            jsonObject.put("customerMobile", customerMobile);
            jsonObject.put("paidAmount", paidAmount);
            jsonObject.put("recipeStatus", recipeStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        do {
            new Thread(() -> {
                RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/editPurchaseRequest_admin.php")
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("EditPurchaseRequest_admin", "on background");


                    String finalS = s;
                    ((NewSellsRequestsActivity) myContext).runOnUiThread(() -> {

                        Log.d("EditPurchaseRequest_admin", finalS);

                        if (finalS.contains("edited successfully")) {
                            if (whichRequests.equals("all")) {

                                for (PurchasesRequestsModel purchasesRequestsModel : NewSellsRequestsActivity.PurchasesRequestsInvoicesArrayList) {
                                    if (purchasesRequestsModel.getInvoice() == invoice) {

                                        purchasesRequestsModel.setRecipeStatus(recipeStatus);
                                        purchasesRequestsModel.setCustomerMobile(customerMobile);
                                        purchasesRequestsModel.setPaidAmount(paidAmount);
                                        break;
                                    }
                                }
                            } else {

                                for (PurchasesRequestsModel purchasesRequestsModel : purchasesRequestsArrayList) {
                                    if (purchasesRequestsModel.getInvoice() == invoice) {

                                        if (recipeStatus.equals(purchasesRequestsModel.getRecipeStatus())) {

                                            purchasesRequestsModel.setCustomerMobile(customerMobile);
                                            purchasesRequestsModel.setPaidAmount(paidAmount);
                                        } else {

                                            purchasesRequestsArrayList.remove(purchasesRequestsModel);
                                        }
                                        break;
                                    }
                                }
                                for (PurchasesRequestsModel purchasesRequestsModel : NewSellsRequestsActivity.PurchasesRequestsInvoicesArrayList) {
                                    if (purchasesRequestsModel.getInvoice() == invoice) {

                                        purchasesRequestsModel.setRecipeStatus(recipeStatus);
                                        purchasesRequestsModel.setCustomerMobile(customerMobile);
                                        purchasesRequestsModel.setPaidAmount(paidAmount);
                                        break;
                                    }
                                }
                            }
                            Toast.makeText(myContext, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(myContext, "فشل في التعديل", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("EditPurchaseRequest_admin", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }



    private void DeletePurchaseOrder(int invoice) {
        AtomicBoolean replayThread = new AtomicBoolean(false);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("invoice", invoice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        do {
            new Thread(() -> {
                RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/DeletePurchaseRequest_admin.php")
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("newSellsRequestActivity", "on background");


                    String finalS = s;
                    ((NewSellsRequestsActivity) myContext).runOnUiThread(() -> {

                        Log.d("newSellsRequestActivity", finalS);

                        if (finalS.contains("deleted successfully")) {
                            if (whichRequests.equals("all")) {

                                for (PurchasesRequestsModel purchasesRequestsModel : NewSellsRequestsActivity.PurchasesRequestsInvoicesArrayList) {
                                    if (purchasesRequestsModel.getInvoice() == invoice) {
                                        NewSellsRequestsActivity.PurchasesRequestsInvoicesArrayList.remove(purchasesRequestsModel);
                                        break;
                                    }
                                }
                            } else {

                                for (PurchasesRequestsModel purchasesRequestsModel : purchasesRequestsArrayList) {
                                    if (purchasesRequestsModel.getInvoice() == invoice) {
                                        purchasesRequestsArrayList.remove(purchasesRequestsModel);
                                        break;
                                    }
                                }
                                for (PurchasesRequestsModel purchasesRequestsModel : NewSellsRequestsActivity.PurchasesRequestsInvoicesArrayList) {
                                    if (purchasesRequestsModel.getInvoice() == invoice) {
                                        NewSellsRequestsActivity.PurchasesRequestsInvoicesArrayList.remove(purchasesRequestsModel);
                                        break;
                                    }
                                }
                            }
                            Toast.makeText(myContext, "تم الحذف", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(myContext, "فشل في حذف الفاتورة", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("newSellsRequestActivity", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDisplayItemsOfInvoice;
        TextView txtInvoiceDate, txtInvoiceNumber, txtInvoiceCost, txtPaidAmount, txtCustomerName, txtCustomerMobile,
                txtCustomerProvince, txtRecipeStatusText;
        ImageView imgDisplayMessagingOfInvoice, imgCallCustomer, imgEditOnOrder, imgCancelOrder;
        Button btnDisplayCustomerLocInMap;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDisplayItemsOfInvoice = itemView.findViewById(R.id.imgDisplayItemsOfInvoice);
            txtInvoiceDate = itemView.findViewById(R.id.txtInvoiceDate);
            txtInvoiceNumber = itemView.findViewById(R.id.txtInvoiceNumber);
            txtInvoiceCost = itemView.findViewById(R.id.txtInvoiceCost);
            imgDisplayMessagingOfInvoice = itemView.findViewById(R.id.imgDisplayMessagingOfInvoice);
            txtRecipeStatusText = itemView.findViewById(R.id.txtRecipeStatusText);
            txtPaidAmount = itemView.findViewById(R.id.txtPaidAmount);
            imgCallCustomer = itemView.findViewById(R.id.imgCallCustomer);
            imgEditOnOrder = itemView.findViewById(R.id.imgEditOnOrder);
            imgCancelOrder = itemView.findViewById(R.id.imgCancelOrder);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtCustomerMobile = itemView.findViewById(R.id.txtCustomerMobile);
            txtCustomerProvince = itemView.findViewById(R.id.txtCustomerProvince);
            btnDisplayCustomerLocInMap = itemView.findViewById(R.id.btnDisplayCustomerLocInMap);
        }
    }
}
