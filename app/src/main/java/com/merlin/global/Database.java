package com.merlin.global;

import android.content.Context;

import com.merlin.database.DaoMaster;
import com.merlin.database.DaoSession;

public final class Database {
    private final String DB_NAME = "merlin.db";
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private static Context mContext;

    protected static void init(Context context){
        mContext=context;
    }

    public DaoMaster master() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
        return null!=daoMaster?daoMaster:(daoMaster = new DaoMaster(mHelper.getWritableDatabase()));
    }
}
