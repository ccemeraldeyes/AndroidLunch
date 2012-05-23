package we.should.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Helper object to create, open, and/or manage a database.
 * @author UW CSE403 SP12
 */
public class DBHelper extends SQLiteOpenHelper{

	// Create table strings
	private static final String CREATE_TABLE_CATEGORY="create table " +      
	                CategoryConst.TBL_NAME + " (" +
                    CategoryConst.ID + " integer primary key autoincrement, " +
	                CategoryConst.NAME + " text UNIQUE not null, " +
                    CategoryConst.COLOR + " text not null, " +
	                CategoryConst.SCHEMA + " text not null);";	
	
	private static final String CREATE_TABLE_ITEM="create table " +			
	                ItemConst.TBL_NAME + " (" +
			        ItemConst.ID +" integer primary key autoincrement, "+
	                ItemConst.NAME + " text UNIQUE not null, " +
			        ItemConst.CAT_ID + " integer references " +
	                   CategoryConst.TBL_NAME + "(" + CategoryConst.ID + "), " +
	                ItemConst.DATA + " text not null);";
	
	private static final String CREATE_TABLE_ITEMTAG="create table " + 
					Item_TagConst.TBL_NAME + " (" + 
					Item_TagConst.ITEM_ID + " integer references " + 
					  ItemConst.TBL_NAME + "(" + ItemConst.ID + "), " + 
					Item_TagConst.TAG_ID + " integer references " + 
					  TagConst.TBL_NAME + "(" + TagConst.ID +"), " +
					"primary key (" + Item_TagConst.ITEM_ID + 
					  ", " + Item_TagConst.TAG_ID + "));";
	
	private static final String CREATE_TABLE_TAG="create table " + 
					TagConst.TBL_NAME + " ("+ 
					TagConst.ID + " integer primary key autoincrement, " + 
					TagConst.NAME + " text UNIQUE not null, " +
					TagConst.COLOR + " text not null);";	
	
	
	
	
	/**
	 * Construct DBHelper object
	 * 
	 * @param context to use to open or create the database
	 * @param name of database to open
	 * @param factory to use for creating cursor objects, or null for the default
	 * @param version number of database
	 */
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, ItemConst.DATABASE_NAME, factory, ItemConst.DATABASE_VERSION);
	}
	   

	@Override
	/**
	 * Creates tables if they do not exist
	 * 
	 * @param db name of database to create
	 */
	public void onCreate(SQLiteDatabase db) {
		Log.v("DBhelper.onCreate","Creating all the tables");
		try {
			createTables(db);
		} catch(SQLiteException ex) {
			Log.v("Create table exception", ex.getMessage());
		}
	}
	

	@Override
	/**
	 * When upgrading database
	 * All data will be lost
	 * 
	 * @param db name of database to upgrade
	 * @param oldVersion version number of existing database
	 * @param newVersion version number of new database
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
		int newVersion) {
		Log.w("DB Upgrade", "Upgrading from version "+ oldVersion
			+" to "+ newVersion +", which will destroy all old data");
			dropAllTables(db);
			onCreate(db);
	}
	
	
	@Override
	/**
	 * @param db database being opened
	 */
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
	
	
	/**
	 * Removes all tables & data from the database
	 * **All data will be lost
	 * 
	 * @param db name of database to clear
	 */
	public void dropAllTables(SQLiteDatabase db){
		Log.v("DBHelper.dropAllTables", "Dropping all tables");
	
		try {

			// drop order matters to avoid database constraint exception
			db.execSQL("drop table if exists "+ Item_TagConst.TBL_NAME);
			db.execSQL("drop table if exists "+ ItemConst.TBL_NAME);
			db.execSQL("drop table if exists "+ CategoryConst.TBL_NAME);
			db.execSQL("drop table if exists "+ TagConst.TBL_NAME);
			
		} catch (SQLiteException ex) {
			Log.e("DBHelper.dropAllTables","Ooops! Error");
			ex.printStackTrace();
		}
		Log.v("DBhelper.dropAllTables", "Exiting in good status");
	}
	
	
	/**
	 * Create all tables required for WeShould application. Requires 
	 * that the tables do not currently exist in the database.
	 *
	 * @param db name of database to create tables in
	 */
	public void createTables(SQLiteDatabase db){
		Log.v("DBhelper.createTables","Creating tables");
		try {
			// create order matters to avoid constraint exception		
			db.execSQL(CREATE_TABLE_CATEGORY);
			db.execSQL(CREATE_TABLE_ITEM);
			db.execSQL(CREATE_TABLE_TAG);
			db.execSQL(CREATE_TABLE_ITEMTAG);
			
		} catch(SQLiteException ex) {
			Log.e("DBHelper.createTables exception", ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Rebuild database - drops all tables and recreates them
	 * **All data will be lost
	 * 
	 * @param db name of database
	 */
	public void rebuild(SQLiteDatabase db){
		Log.v("DBhelper.rebuildTest","Drop & Create tables.");
		try {
			dropAllTables(db);
			createTables(db);
		} catch(SQLiteException ex) {
			Log.e("DBHelper.rebuildTest exception", ex.getMessage());
			ex.printStackTrace();
		}
	}
}

