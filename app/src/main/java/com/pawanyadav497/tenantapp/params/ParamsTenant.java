package com.pawanyadav497.tenantapp.params;

import android.provider.BaseColumns;

public class ParamsTenant implements BaseColumns {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "tenant.db";
        public static final String DATABASE_TABLE = "tenant_record";

        public static final String NAME = "name";
        public static final String PHONE_NO = "phone_no";
        public static final String PDF_PATH = "pdf_path";
        public static final String TENANT_ID = "tenant_id";
        public static final String ADDRESS = "address";


}
