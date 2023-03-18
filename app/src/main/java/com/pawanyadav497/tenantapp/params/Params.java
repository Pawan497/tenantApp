package com.pawanyadav497.tenantapp.params;

import android.provider.BaseColumns;

public class Params implements BaseColumns {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "payment_history.db";
    public static final String DATABASE_TABLE = "payment_record";

    public static final String KEY_ID = "id";
    public static final String FROM = "month_from";
    public static final String TO = "month_to";
    public static final String PAYMENT_DATE = "payment_date";
    public static final String AMOUNT_DUE = "amount_due";
    public static final String AMOUNT_PAID = "amount_paid";
    public static final String BALANCE = "balance";
    public static final String TENANT_ID = "tenant_id";

}
