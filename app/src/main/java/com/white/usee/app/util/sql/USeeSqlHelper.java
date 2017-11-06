package com.white.usee.app.util.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 10037 on 2016/7/30 0030.
 */

public class USeeSqlHelper extends SQLiteOpenHelper {
    private static int Db_Version = 1;
    private static String Db_Name = "USee.db";
    public static String Table_Name = "danmus";

    public USeeSqlHelper(Context context) {
        super(context, Db_Name, null, Db_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists " + Table_Name + " ("+USeeSqlDanmu.id+" integer primary key, "+USeeSqlDanmu.content+" text, "+USeeSqlDanmu.userid+" text, "+USeeSqlDanmu.create_time+" text)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + Table_Name;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
}
