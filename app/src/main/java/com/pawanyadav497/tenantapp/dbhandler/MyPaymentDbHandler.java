package com.pawanyadav497.tenantapp.dbhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pawanyadav497.tenantapp.model.Rent;
import com.pawanyadav497.tenantapp.params.Params;

import java.util.ArrayList;
import java.util.List;

public class MyPaymentDbHandler extends SQLiteOpenHelper {


    public MyPaymentDbHandler(Context context) {
        super(context, Params.DATABASE_NAME, null, Params.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createQuery = "CREATE TABLE IF NOT EXISTS " + Params.DATABASE_TABLE + " ( " + Params.KEY_ID + " INTEGER PRIMARY KEY, "
                + Params.FROM + " TEXT NOT NULL, " + Params.TO + " TEXT NOT NULL, " + Params.PAYMENT_DATE
                + " TEXT NOT NULL, " + Params.AMOUNT_DUE + " TEXT NOT NULL, " + Params.AMOUNT_PAID + " TEXT NOT NULL, "
                + Params.BALANCE + " TEXT NOT NULL, " + Params.TENANT_ID + " INTEGER NOT NULL"+" ) ";

        Log.d("db_tenant", "onCreate of database " + createQuery);

        sqLiteDatabase.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addRent(Rent rent, int tenantID){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.FROM, rent.getFrom());
        values.put(Params.TO, rent.getTo());
        values.put(Params.PAYMENT_DATE, rent.getPayment_date());
        values.put(Params.AMOUNT_DUE, rent.getAmt_due());
        values.put(Params.AMOUNT_PAID, rent.getAmt_paid());
        values.put(Params.BALANCE, rent.getBalance());
        values.put(Params.TENANT_ID, tenantID);

        sqLiteDatabase.insert(Params.DATABASE_TABLE,null, values);

        Log.d("db_payment", "successfully inserted " );

        sqLiteDatabase.close();

    }

    public List<Rent> getAllRentForTenant(int tenantID){
        List<Rent> rentList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        // Generate the query to read from the database
        String selectQuery = "SELECT * FROM " + Params.DATABASE_TABLE + " WHERE " + Params.TENANT_ID + " = " + tenantID;

//        String selectQuery = "SELECT * FROM " + Params.DATABASE_TABLE ;

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                Rent rent = new Rent();
                rent.setId(Integer.parseInt(cursor.getString(0)));
                rent.setFrom(cursor.getString(1));
                rent.setTo(cursor.getString(2));
                rent.setPayment_date(cursor.getString(3));
                rent.setAmt_due(cursor.getString(4));
                rent.setAmt_paid(cursor.getString(5));
                rent.setBalance(cursor.getString(6));
                rent.setTenantID(cursor.getInt(7));
                rentList.add(rent);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return rentList;
    }


    public Rent getLastRent(int tenantId){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String lastQuery = "SELECT * FROM " + Params.DATABASE_TABLE + " WHERE " + Params.TENANT_ID + " = " + tenantId + " ORDER BY " + Params.KEY_ID + " DESC LIMIT 1";

//        String lastQuery = "SELECT * FROM " + Params.DATABASE_TABLE + " ORDER BY " + Params.KEY_ID + " DESC LIMIT 1";

        Cursor cursor = sqLiteDatabase.rawQuery(lastQuery,null);

        Rent rent = new Rent();

        if(cursor.moveToFirst()){
            rent.setId(Integer.parseInt(cursor.getString(0)));
            rent.setFrom(cursor.getString(1));
            rent.setTo(cursor.getString(2));
            rent.setPayment_date(cursor.getString(3));
            rent.setAmt_due(cursor.getString(4));
            rent.setAmt_paid(cursor.getString(5));
            rent.setBalance(cursor.getString(6));
            rent.setTenantID(cursor.getInt(7));
        }

        sqLiteDatabase.close();

        return rent;
    }

    public void editRent(Rent rent){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.FROM, rent.getFrom());
        values.put(Params.TO, rent.getTo());
        values.put(Params.PAYMENT_DATE, rent.getPayment_date());
        values.put(Params.AMOUNT_DUE, rent.getAmt_due());
        values.put(Params.AMOUNT_PAID, rent.getAmt_paid());
        values.put(Params.BALANCE, rent.getBalance());
        values.put(Params.TENANT_ID, rent.getTenantID());

        sqLiteDatabase.update(Params.DATABASE_TABLE, values, Params.KEY_ID +"=?",new String[]{String.valueOf(rent.getId())});

        Log.d("payment_db", "successfully updated ");

        sqLiteDatabase.close();

    }

    public void deleteRent(Rent rent){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(Params.DATABASE_TABLE, Params.KEY_ID +"=?",new String[]{String.valueOf(rent.getId())});
        Log.d("payment_db", "successfully deleted ");
        sqLiteDatabase.close();

    }

    public String getTotalBalance(int currentTenantID){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String selectQuery = "SELECT " + Params.BALANCE + " FROM " + Params.DATABASE_TABLE + " WHERE " + Params.TENANT_ID + " = " + currentTenantID;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        int totalBalance = 0;

        if (cursor.moveToFirst()) {
            do {
                totalBalance += Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return String.valueOf(totalBalance);
    }
}
