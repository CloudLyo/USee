package com.white.usee.app.util.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.white.usee.app.R;
import com.white.usee.app.bean.USeeDanmu;
import com.white.usee.app.model.DanmuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10037 on 2016/7/30 0030.
 */

public class USeeSqlDanmu {
    public static String id = "Id";
    public static String content = "Content";
    public static String userid = "Userid";
    public static String create_time = "create_time";

    private static final String TAG = "USEEDANMU";
    private final String [] USEE_COLUMNS=new String[]{id,content,userid,create_time};
    private Context context;
    private USeeSqlHelper uSeeSqlHelper;

    public USeeSqlDanmu(Context context){
        this.context = context;
        uSeeSqlHelper = new USeeSqlHelper(context);
    }
    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = uSeeSqlHelper.getReadableDatabase();
            // select count(Id) from Orders
            cursor = db.query(uSeeSqlHelper.Table_Name, new String[]{"COUNT(Id)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 初始化数据
     */
    public void initTable(){
        SQLiteDatabase db = null;

        try {
            db = uSeeSqlHelper.getWritableDatabase();
            db.beginTransaction();


            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }
    /**
     * 新增一条数据
     */
    public boolean insertDate(DanmuModel danmuModel){
        SQLiteDatabase db = null;

        try {
            db = uSeeSqlHelper.getWritableDatabase();
            db.beginTransaction();

            // insert into Orders(Id, CustomName, OrderPrice, Country) values (7, "Jne", 700, "China");
            ContentValues contentValues = new ContentValues();
            contentValues.put(id, danmuModel.getId());
            contentValues.put(content, danmuModel.getMessages());
            contentValues.put(userid, danmuModel.getUserId());
            contentValues.put(create_time, danmuModel.getCreate_time());
            db.insertOrThrow(uSeeSqlHelper.Table_Name, null, contentValues);

            db.setTransactionSuccessful();
            return true;
        }catch (SQLiteConstraintException e){
            Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }
    /**
     * 执行自定义SQL语句
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")){
                Toast.makeText(context, R.string.strUnableSql, Toast.LENGTH_SHORT).show();
            }else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")){
                db = uSeeSqlHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
                Toast.makeText(context, R.string.strSuccessSql, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.strErrorSql, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public boolean insertDatas(List<DanmuModel> danmuModels){
        SQLiteDatabase db = uSeeSqlHelper.getWritableDatabase();
        for (DanmuModel danmuModel:danmuModels){
        try {
            db.beginTransaction();

            // insert into Orders(Id, CustomName, OrderPrice, Country) values (7, "Jne", 700, "China");
            ContentValues contentValues = new ContentValues();
            contentValues.put(id, danmuModel.getId());
            contentValues.put(content, danmuModel.getMessages());
            contentValues.put(userid, danmuModel.getUserId());
            contentValues.put(create_time, danmuModel.getCreate_time());
            db.insertOrThrow(uSeeSqlHelper.Table_Name, null, contentValues);
            db.setTransactionSuccessful();
            return true;
        }catch (SQLiteConstraintException e){
            Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        }
        return false;
    }

    /**
     * 查询数据库中所有数据
     */
    public List<DanmuModel> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = uSeeSqlHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(uSeeSqlHelper.Table_Name, USEE_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<DanmuModel> orderList = new ArrayList<DanmuModel>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseOrder(cursor));
                }
                return orderList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    private DanmuModel parseOrder(Cursor cursor) {
        DanmuModel danmuModel = new DanmuModel();
        danmuModel.setUserId(cursor.getString(cursor.getColumnIndex(userid)));
        danmuModel.setId(cursor.getString(cursor.getColumnIndex(id)));
        danmuModel.setMessages(cursor.getString(cursor.getColumnIndex(content)));
        danmuModel.setCreate_time(cursor.getString(cursor.getColumnIndex(create_time)));
        return danmuModel;

    }

}
