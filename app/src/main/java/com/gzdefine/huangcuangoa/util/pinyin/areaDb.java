package com.gzdefine.huangcuangoa.util.pinyin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class areaDb {
	public static final String DATABASE_NAME = "area.db";
	public static final int DATABASE_VERSON =26;
	public static final String TABLE_HARD = "plan";
	public static final String TABLEHARD =
			"create table if not EXISTS plan("+
					"_id INTEGER PRIMARY KEY," + // rowID
					"city_id INTEGER  NOT NULL ," + // id
					"city_code TEXT," +//编号
					"city_name TEXT," +//城市名字
					"newbackup1 TEXT,"+   //备用1
					"newbackup2 TEXT,"+   //备用2
					"newbackup3 TEXT,"+   //备用3
					"newbackup4 TEXT,"+   //备用4
					"newbackup5 TEXT,"+   //备用5
					"newbackup6 TEXT"+    //备用6
					");";
	private Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase mSQLiteDatabase = null;

	public areaDb(Context context) {
		this.context = context;
	}
	//打开并建立数据库
	public void open() {
		dbHelper = new DatabaseHelper(context);
		mSQLiteDatabase = dbHelper.getWritableDatabase();
		mSQLiteDatabase.execSQL(TABLEHARD);
	}
	//关闭数据库
	public void close() {
		mSQLiteDatabase.close();
		dbHelper.close();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSON);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(TABLEHARD);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_HARD);
			onCreate(db);
		}
	}

	//清楚数据库所有数据
	public void cleardb(){
		mSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HARD);
		mSQLiteDatabase.execSQL(TABLEHARD);
	}
	// 查找所有组
	public Cursor getall() {
		String sql="select * from "+TABLE_HARD ;
		return mSQLiteDatabase.rawQuery(sql, null);
	}

	public String getpath(){
		return mSQLiteDatabase.getPath();
	}

	// 查找数据
	public Cursor getmsgfromdatebydate(int datel) {
		String sql="select _id, city_id,city_code,city_name from "+TABLE_HARD+" where city_id = '"+datel+"'";
		return mSQLiteDatabase.rawQuery(sql, null);
	}
	public Cursor getmsgpomall(String city_id, String datel, String dateb) {
		String sql="select city_id,city_code,city_name from "+TABLE_HARD+" where pom = '"+city_id+"' and date >= '"+datel+"' and date < '"+dateb+"' order by date desc";
		return mSQLiteDatabase.rawQuery(sql, null);
	}

	//根据某个ID删除单条内容
	public int deletebydate(String id){
		return mSQLiteDatabase.delete(TABLE_HARD, "_id = '"+id+"'", null);
	}
	//写入数据
	public long inserDataToContacts(int city_id,int city_code,String city_name){
		ContentValues con=new ContentValues();
		con.put("city_id",city_id);
		con.put("city_code",city_code);
		con.put("city_name", city_name);
		con.put("newbackup1", "");
		con.put("newbackup2", "");
		con.put("newbackup3", "");
		con.put("newbackup4", "");
		con.put("newbackup5", "");
		con.put("newbackup6", "");
		return mSQLiteDatabase.insert(TABLE_HARD, null, con);
	}
	//修改UPTAG
	public void updateDataToMessages(int city_id, int city_code, String city_name, String id){
		ContentValues con=new ContentValues();
		con.put("city_id",city_id);
		con.put("city_code",city_code);
		con.put("city_name", city_name);
		con.put("newbackup1", "");
		con.put("newbackup2", "");
		con.put("newbackup3", "");
		con.put("newbackup4", "");
		con.put("newbackup5", "");
		con.put("newbackup6", "");
		mSQLiteDatabase.update(TABLE_HARD, con, "_id='"+id+"'", null);
	}
	//删除整个数据库
	public boolean dropDataToUpdate(){

//		String sql = "DROP TABLE IF EXISTS "+"'"+ TABLE_HARD+"'"; 

		try {
			//执行SQL语句
			mSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_HARD);

			return true;

		} catch (SQLException e) {

			return false;

		}


	}
}