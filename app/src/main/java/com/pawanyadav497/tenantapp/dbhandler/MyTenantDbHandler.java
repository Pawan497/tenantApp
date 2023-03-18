package com.pawanyadav497.tenantapp.dbhandler;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pawanyadav497.tenantapp.model.Tenant;
import com.pawanyadav497.tenantapp.params.ParamsTenant;

import java.util.ArrayList;
import java.util.List;

public class MyTenantDbHandler extends SQLiteOpenHelper {

    private OnDatabaseChangedListener mListener;

    public MyTenantDbHandler(Context context) {
        super(context, ParamsTenant.DATABASE_NAME, null, ParamsTenant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createQuery = "CREATE TABLE IF NOT EXISTS " + ParamsTenant.DATABASE_TABLE + " ( " + ParamsTenant.TENANT_ID + " INTEGER PRIMARY KEY, "
                + ParamsTenant.NAME + " TEXT NOT NULL, " + ParamsTenant.PHONE_NO + " TEXT NOT NULL, " + ParamsTenant.PDF_PATH
                + " TEXT NOT NULL, " + ParamsTenant.ADDRESS + " TEXT NOT NULL" + " ) ";

        Log.d("db_tenant", "onCreate of database " + createQuery);

        sqLiteDatabase.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addTenant(Tenant tenant){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ParamsTenant.NAME, tenant.getName());
        values.put(ParamsTenant.PHONE_NO, tenant.getPhoneNo());
        values.put(ParamsTenant.PDF_PATH, tenant.getPdf_path());
        values.put(ParamsTenant.ADDRESS, tenant.getAddress());

        sqLiteDatabase.insert(ParamsTenant.DATABASE_TABLE,null, values);

        Log.d("db_tenant", "successfully inserted db path=>" +tenant.getPdf_path());

        sqLiteDatabase.close();

    }

    public List<Tenant> getAllTenant(){
        List<Tenant> tenantList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        //Generate the query to read from the database
        String selectQuerry = " SELECT * FROM " + ParamsTenant.DATABASE_TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuerry, null);

        if(cursor.moveToFirst()){
            do {
                Tenant tenant = new Tenant();
                tenant.setTenantID(Integer.parseInt(cursor.getString(0)));
                tenant.setName(cursor.getString(1));
                tenant.setPhoneNo(cursor.getString(2));
                tenant.setPdf_path(cursor.getString(3));
                tenant.setAddress(cursor.getString(4));
                tenantList.add(tenant);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return tenantList;
    }

    public void editDatabase(Tenant tenant) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ParamsTenant.NAME, tenant.getName());
        values.put(ParamsTenant.PHONE_NO, tenant.getPhoneNo());
        values.put(ParamsTenant.PDF_PATH, tenant.getPdf_path());
        values.put(ParamsTenant.ADDRESS, tenant.getAddress());

        sqLiteDatabase.update(ParamsTenant.DATABASE_TABLE, values, ParamsTenant.TENANT_ID + "=?", new String[]{String.valueOf(tenant.getTenantID())});

        Log.d("db_tenant", "successfully updated ");

        sqLiteDatabase.close();

        //XXXX
        if (mListener != null) {
            mListener.onDatabaseChanged();
        }
    }

    public void deleteTenant(Tenant tenant) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(ParamsTenant.DATABASE_TABLE, ParamsTenant.TENANT_ID + "=?", new String[]{String.valueOf(tenant.getTenantID())});
        Log.d("db_tenant", "successfully deleted ");
        sqLiteDatabase.close();

        if (mListener != null) {
            mListener.onDatabaseChanged();
        }
    }

    //To update the list when the database is changed,
    //Define a listener interface
    public interface OnDatabaseChangedListener {
        void onDatabaseChanged();
    }

    public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mListener = listener;
    }

    public int getLastTenantId(){

        int idTenantLast = 0;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String selectQuerry = " SELECT " + ParamsTenant.TENANT_ID + " FROM " + ParamsTenant.DATABASE_TABLE + " ORDER BY " + ParamsTenant.TENANT_ID + " DESC LIMIT 1";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuerry, null);
        if(cursor.moveToFirst()){
            idTenantLast = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        sqLiteDatabase.close();

        return idTenantLast;
    }

    @SuppressLint("Range")
    public String getName(int tenantId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        // Generate the query to read from the database
        String selectQuery = "SELECT " + ParamsTenant.NAME + " FROM " + ParamsTenant.DATABASE_TABLE + " WHERE " + ParamsTenant.TENANT_ID + " = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, new String[]{String.valueOf(tenantId)});

        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ParamsTenant.NAME));
        }

        cursor.close();
        sqLiteDatabase.close();

        return name;
    }


}

