package com.pawanyadav497.tenantapp.myrecycleview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.pdfrelatedfile.PdfHandler;
import com.pawanyadav497.tenantapp.fragments.MyTenantDialogFragment;
import com.pawanyadav497.tenantapp.fragments.PaymentListFragment;
import com.pawanyadav497.tenantapp.model.Tenant;
import com.pawanyadav497.tenantapp.viewmodels.TenantListViewModel;

import java.util.List;

public class MyTenantRecyclerViewAdapter extends RecyclerView.Adapter<MyTenantRecyclerViewAdapter.TenantViewHolder> {

    private Context context;
    private List<Tenant> tenantList;
    private FragmentActivity activity;
    private TenantListViewModel viewModel;

    public MyTenantRecyclerViewAdapter(Context context, FragmentActivity activity , List<Tenant> tenantList) {
        this.activity = activity;
        this.context = context;
        this.tenantList = tenantList;
        this.viewModel = new ViewModelProvider(activity).get(TenantListViewModel.class);
    }

    //where to get single card as viewholder obj
    @Override
    public MyTenantRecyclerViewAdapter.TenantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tenant_item, parent, false);

        return new TenantViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyTenantRecyclerViewAdapter.TenantViewHolder holder, int position) {

        Tenant tenant = tenantList.get(position);

        holder.tenant = tenant;
        holder.name.setText(tenant.getName());
        holder.phoneNo.setText(tenant.getPhoneNo());
        holder.address.setText(tenant.getAddress());

        // Check if pdf_path is None and hide the view_pdf_btn button
        if (tenant.getPdf_path().equals("None")) {
            holder.view_pdf_btn.setVisibility(View.GONE);
        } else {
            holder.view_pdf_btn.setVisibility(View.VISIBLE);
        }

        // Check if phone no. is None and hide the view_pdf_btn button
        if (tenant.getPhoneNo().trim().equals("None")) {
            holder.callbtn.setVisibility(View.GONE);
        } else {
            holder.callbtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return tenantList.size();
    }

    public class TenantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name, phoneNo, address;
        public MaterialButton editViewbtn, deletebtn, view_pdf_btn, callbtn;
        public Tenant tenant;
        public LinearLayout tenantbtn;


    public TenantViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            name = itemView.findViewById(R.id.table_item1name);
            phoneNo = itemView.findViewById(R.id.table_item2phoneno);
            address = itemView.findViewById(R.id.table_item3address);
            editViewbtn = itemView.findViewById(R.id.edit_tenant_btn);
            deletebtn = itemView.findViewById(R.id.delete_tenant_btn);
            view_pdf_btn = itemView.findViewById(R.id.view_pdf_btn);
            tenantbtn = itemView.findViewById(R.id.linear_tenantbtn);
            callbtn = itemView.findViewById(R.id.callbtn);

            tenantbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movingTOPaymentFragment();
                }
            });

            editViewbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movingToTenantListFragment();
                }
            });

            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewModel.deleteTenant(tenant);
                    Toast.makeText(context.getApplicationContext(), "Tenant deleted successfully", Toast.LENGTH_SHORT).show();
                }
            });

            view_pdf_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PdfHandler pdfHandler = new PdfHandler(context, tenant, activity);
                    pdfHandler.openOrDownloadPdf();
                }
            });

            callbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Call on Phone No.
                    String phoneNumber = tenant.getPhoneNo();
                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(context.getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
        }

        public void movingTOPaymentFragment(){
            Fragment tenantPayListFragment = PaymentListFragment.newInstance(tenant.getTenantID());
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, tenantPayListFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }

        public void movingToTenantListFragment(){
            MyTenantDialogFragment myTenantDialogFragment = MyTenantDialogFragment.newInstance(tenant);
//            Toast.makeText(context.getApplicationContext(), "edit recyler tenant Id "+tenant.getTenantID(), Toast.LENGTH_SHORT).show();
            myTenantDialogFragment.show(activity.getSupportFragmentManager(),"my_tenant_dialog");
        }
    }


    public void setData(List<Tenant> tenantList) {
        this.tenantList = tenantList;
        notifyDataSetChanged();
    }
}

