package com.pawanyadav497.tenantapp.myrecycleviewcallback;

import android.os.Bundle;

import androidx.recyclerview.widget.DiffUtil;

import com.pawanyadav497.tenantapp.model.Rent;

import java.util.List;

public class RentDiffCallback extends DiffUtil.Callback {

    private final List<Rent> oldRentList;
    private final List<Rent> newRentList;

    public RentDiffCallback(List<Rent> oldRentList, List<Rent> newRentList) {
        this.oldRentList = oldRentList;
        this.newRentList = newRentList;
    }

    @Override
    public int getOldListSize() {
        return oldRentList.size();
    }

    @Override
    public int getNewListSize() {
        return newRentList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldRentList.get(oldItemPosition).getId() == newRentList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Rent oldRent = oldRentList.get(oldItemPosition);
        Rent newRent = newRentList.get(newItemPosition);

        return  oldRent.getFrom().equals(newRent.getFrom()) &&
                oldRent.getTo().equals(newRent.getTo()) &&
                oldRent.getPayment_date().equals(newRent.getPayment_date()) &&
                oldRent.getAmt_due().equals(newRent.getAmt_due()) &&
                oldRent.getAmt_paid().equals(newRent.getAmt_paid()) &&
                oldRent.getBalance().equals(newRent.getBalance()) &&
                oldRent.getTenantID() == newRent.getTenantID();
    }


    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Rent oldRent = oldRentList.get(oldItemPosition);
        Rent newRent = newRentList.get(newItemPosition);

        Bundle diffBundle = new Bundle();
        if (!oldRent.getFrom().equals(newRent.getFrom())) {
            diffBundle.putString("from", newRent.getFrom());
        }
        if (!oldRent.getTo().equals(newRent.getTo())) {
            diffBundle.putString("to", newRent.getTo());
        }
        if (!oldRent.getPayment_date().equals(newRent.getPayment_date())) {
            diffBundle.putString("payment_date", newRent.getPayment_date());
        }
        if (!oldRent.getAmt_due().equals(newRent.getAmt_due())) {
            diffBundle.putString("amt_due", newRent.getAmt_due());
        }
        if (!oldRent.getAmt_paid().equals(newRent.getAmt_paid())) {
            diffBundle.putString("amt_paid", newRent.getAmt_paid());
        }
        if (!oldRent.getBalance().equals(newRent.getBalance())) {
            diffBundle.putString("balance", newRent.getBalance());
        }
        if (oldRent.getTenantID() != newRent.getTenantID()) {
            diffBundle.putInt("tenantID", newRent.getTenantID());
        }

        if (diffBundle.size() == 0) {
            return null;
        } else {
            return diffBundle;
        }
    }
}
