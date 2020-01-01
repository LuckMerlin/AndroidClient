//package com.merlin.database;
//
//import android.content.Context;
//
//public class GreenDao {
//
//    private Context mContext;
//    private DaoMaster mDaoMaster;
//    private DaoSession mDaoSession;
//
//    private GreenDao(Context context) {
//        mContext = context;
//    }
//
//    private static volatile GreenDao instance = null;
//    public static GreenDao getInstance(Context context){
//        if (instance==null){
//            synchronized (GreenDao.class){
//                if (instance==null){
//                    instance = new GreenDao(context);
//                }
//            }
//        }
//        return instance;
//    }
//
//    public void init() {
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "user.db");
//        mDaoMaster = new DaoMaster(helper.getWritableDb());
//        mDaoSession = mDaoMaster.newSession();
//        userDao = mDaoSession.getUserDao();
//    }
//
//}
