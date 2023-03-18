package com.pawanyadav497.tenantapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pawanyadav497.tenantapp.dbhandler.MyTenantDbHandler;
import com.pawanyadav497.tenantapp.model.Tenant;

import java.util.List;

public class TenantListViewModel extends AndroidViewModel {

    private final MyTenantDbHandler myDbHandler;
    private final MutableLiveData<List<Tenant>> tenantsLiveData;

    public TenantListViewModel( Application application) {
        super(application);

        myDbHandler = new MyTenantDbHandler(application.getApplicationContext());
        tenantsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Tenant>> getTenantsLiveData() {
        loadTenants();
        return tenantsLiveData;
    }

    private void loadTenants() {
        List<Tenant> tenants = myDbHandler.getAllTenant();
        tenantsLiveData.postValue(tenants);
    }

    public void addTenant(Tenant tenant) {
        myDbHandler.addTenant(tenant);
        loadTenants();
    }

    public int getLastTenantID(){
        return myDbHandler.getLastTenantId();
    }

    public void deleteTenant(Tenant tenant) {
        myDbHandler.deleteTenant(tenant);
        loadTenants();
    }

    public void editTenant(Tenant tenant) {
        myDbHandler.editDatabase(tenant);
        loadTenants();
    }

}
