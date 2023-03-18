package com.pawanyadav497.tenantapp.myrecycleview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.fragments.MyDialogFragment;
import com.pawanyadav497.tenantapp.model.Rent;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Rent> rentList;

    public MyRecyclerViewAdapter(Context context, List<Rent> rentList) {
        this.context = context;
        this.rentList = rentList;
    }

    //where to get single card as viewholder obj
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter.ViewHolder holder, int position) {

        Rent rent = rentList.get(position);


        holder.rent = rent;
        holder.month_from.setText(rent.getFrom());
        holder.month_to.setText(rent.getTo());
        holder.date.setText(rent.getPayment_date());
        holder.due.setText(rent.getAmt_due());
        holder.paid.setText(rent.getAmt_paid());


        int balance = Integer.parseInt(rent.getBalance());

        if (balance <= 0) {
            // balance is zero or negative, so set text color to green
            holder.balance.setTextColor(Color.GREEN);
        } else {
            // balance is positive, so set text color to red
            holder.balance.setTextColor(Color.RED);
        }

        holder.balance.setText(String.valueOf(balance));

//        holder.balance.setText(rent.getBalance());
    }

    @Override
    public int getItemCount() {
        return rentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView month_from, month_to, date, due, paid, balance;
        public Rent rent;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            month_from = itemView.findViewById(R.id.table_item1);
            month_to = itemView.findViewById(R.id.table_item2);
            date = itemView.findViewById(R.id.table_item3address);
            due = itemView.findViewById(R.id.table_item4);
            paid = itemView.findViewById(R.id.table_item5);
            balance = itemView.findViewById(R.id.table_item6);

//            balance.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context.getApplicationContext(), "Edit Payment History!", Toast.LENGTH_SHORT).show();

            MyDialogFragment myDialogFragment = MyDialogFragment.newInstance(rent);
            myDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(),"my_dialog");
        }
    }
}
