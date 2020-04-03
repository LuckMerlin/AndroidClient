package com.merlin.global;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.merlin.database.DaoMaster;
import com.merlin.database.DaoSession;

public final class Database {
    private final String DB_NAME = "merlin1.db";
    private DaoMaster daoMaster;
    private static Context mContext;

    protected static void init(Context context){
        mContext=context;
    }

    public DaoMaster master() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
        return null!=daoMaster?daoMaster:(daoMaster = new DaoMaster(mHelper.getWritableDatabase()));
    }
}
