package weshould.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * WeShould Database class - contains database methods used in the 
 * 							 WeShould Android Application
 * @author  UW CSE403 SP12
 * 
 * most methods are sending Log verbose output.  running LogCat while executing displays information.
 * See DBexamples.txt for examples on how to call the methods and get results.
 *
 */
public class WSdb {
	private SQLiteDatabase db; 
	private final Context context;
	private DBHelper dbhelper; // database helper object
	

	/**
	 * WeShould Database Constructor
	 * @param c context to use to create the database helper
	 */
	public WSdb(Context c){
		Log.v("WSDB constructor", "entering constructor");
		context=c;
		dbhelper = new DBHelper(context, ItemConst.DATABASE_NAME, null, 
									 ItemConst.DATABASE_VERSION);
	}
	
	
	/**
	 * open database for writing
	 * @exception ex caught SQLiteException if failure to open writable database,
	 * 				 will open readable if fails 
	 * @returns true if db is open and writable, false otherwise
	 */
	public boolean open(){
		try {
			db = dbhelper.getWritableDatabase();
		} catch(SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = dbhelper.getReadableDatabase();
			return false;
		}
		return true;
	}
	
	
	/**
	 * check if the database is currently open
	 * @return true if open, false otherwise
	 */
	public boolean isOpen(){
		return db.isOpen();
	}
	
	
	/**
	 * close open database object 
	 */
	public void close(){
		dbhelper.close(); 
	}
	
	
	/****************************************************************
	 *                        Insert Methods
	 ***************************************************************/
	
	/**
	 * Insert an Item into the database
	 * 
	 * @param name of Item being entered
	 * @param categoryId unique id of Item's category
	 * @param mappable boolean true if item can be mapped, false otherwise
	 * @param data json code holding item schema & data
	 * @return row ID of the newly inserted row, or -1 if an error occurred 
	 * @exception ex caught SQLiteException if insert fails
	 */
	
	
	public long insertItem(String name, int categoryId, boolean mappable, String data){
		try{
			Log.v("WSDB.insertItem","Inserting Item");	
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(ItemConst.NAME, name);
			newTaskValue.put(ItemConst.CAT_ID, categoryId);
			newTaskValue.put(ItemConst.MAPPABLE, mappable);
			newTaskValue.put(ItemConst.DATA, data);
			return db.insert(ItemConst.TBL_NAME, null, newTaskValue);
		} catch(SQLiteException ex) {
			Log.v("Insert Item exception caught", ex.getMessage());
			return -1;				
		}
	}
	
	
	/**
	 * Insert a category into the database
	 * 
	 * @param name of category being entered
	 * @param color unique color to identify category
	 * @param schema string to identify category schema    //TODO: json also?
	 * @return row ID of the newly inserted row, or -1 if an error occurred 
	 * @exception ex caught SQLiteException if insert fails
	 * 
	 */
	public long insertCategory(String name, int color, String schema){
		try{
			Log.v("WSdb.insertCategory","Inserting category");
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(CategoryConst.NAME, name);
			newTaskValue.put(CategoryConst.COLOR, color);
			newTaskValue.put(CategoryConst.SCHEMA, schema);
			return db.insert(CategoryConst.TBL_NAME, null, newTaskValue);
		} catch(SQLiteException ex) {
			Log.v("InsertCategory exception caught", ex.getMessage());
			return -1;				
		}
	}
	
	
	/**
	 * Insert a Tag into the database
	 * 
	 * @param name of Tag
	 * @param color unique color used to identify tag
	 * @return row ID of the newly inserted row, or -1 if an error occurred 
	 * @exception ex caught SQLiteException if insert fails
	 */
	
	public long insertTag(String name /*, int color*/){
		try{
			Log.v("WSdb.insertTag","inserting tag");
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(TagConst.NAME, name);
			//newTaskValue.put(TagConst.COLOR, color);
			return db.insert(TagConst.TBL_NAME, null, newTaskValue);
		} catch(SQLiteException ex) {
			Log.v("InsertTag exception caught", ex.getMessage());
			return -1;				
		}
	}
	
	
	public long insertItem_Tag(int itemID, int tagID){
		try{
			Log.v("WSdb.insertTag","inserting tag");
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(Item_TagConst.ITEM_ID, itemID);
			newTaskValue.put(Item_TagConst.TAG_ID, tagID);
			return db.insert(Item_TagConst.TBL_NAME, null, newTaskValue);
		} catch(SQLiteException ex) {
			Log.v("InsertTag exception caught", ex.getMessage());
			return -1;				
		}
	}
	
	
	
	
	/**
	 * Database queries
	 * 
	 * format: db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
	 * 
	 * Parameters
	 *		table - 	The table name to compile the query against.
	 *		columns - 	A list of which columns to return. Passing null will return all columns,
	 *				 	which is discouraged to prevent reading data from storage that isn't 
	 *					going to be used.
	 *		selection - A filter declaring which rows to return, formatted as an SQL WHERE clause 
	 *					(excluding the WHERE itself). Passing null will return all rows for the 
	 *					given table.
	 *		selectionArgs - You may include ?s in selection, which will be replaced by the values 
	 *						from selectionArgs, in order that they appear in the selection. The 
	 *						values will be bound as Strings.
	 *		groupBy - 	A filter declaring how to group rows, formatted as an SQL GROUP BY clause 
	 *					(excluding the GROUP BY itself). Passing null will cause the rows to not 
	 *					be grouped.
	 *		having - 	A filter declare which row groups to include in the cursor, if row grouping 
	 *					is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). 
	 *					Passing null will cause all row groups to be included, and is required when 
	 *					row grouping is not being used.
	 *		orderBy - 	How to order the rows, formatted as an SQL ORDER BY clause (excluding the 
	 *					ORDER BY itself). Passing null will use the default sort order, which may 
	 *					be unordered.
	 */
	
	
	
	
	/**
	 * getAllItems ordered by name
	 * 
	 * @return cursor to ordered list of items
	 * 
	 * SQL query
	 * select * from item order by name
	 */
	public Cursor getAllItems(){
		return db.query(ItemConst.TBL_NAME, null, null,
				null, null, null, "name");
	}
	
	/**
	 * getAllItems ordered by name
	 * 
	 * @return cursor to the item
	 * 
	 * SQL query
	 * select * from item where id=[given id]
	 */
	public Cursor getItem(int id){
		return db.query(ItemConst.TBL_NAME, null , ItemConst.ID + "=" + id,
				null, null, null, null);
	}
	
	
	/**
	 * getAllCategories ordered by name
	 * 
	 * @return cursor to ordered list of categories
	 * 
	 * SQL query
	 * select * from category order by name
	 */
	public Cursor getAllCategories(){
		return db.query(CategoryConst.TBL_NAME, null, null,
				null, null, null, "name");
	}
	
	
	/**
	 * getAllTags ordered by name
	 * 
	 * @return cursor to ordered list of tags
	 * 
	 * SQL query
	 * select * from tag order by name
	 */
	public Cursor getAllTags(){
		return db.query(TagConst.TBL_NAME, null, null,
				null, null, null, "name");
	}
	
	
	/**
	 * getAllItemsOfTag - get every item with given tag
	 * 
	 * @param  - id of the tag of the items to return
	 * @return cursor to list of all item id# with the given tag
	 *  
	 * SQL query
	 * select * from item_tag where item_tag.item_id=tagId
	 */
	public Cursor getItemsOfTag(int tagId){
		String sqlStatement = "Select * from " + ItemConst.TBL_NAME + 
				" i, " + Item_TagConst.TBL_NAME + " it " +
				"where i." + ItemConst.ID + " = it." + Item_TagConst.ITEM_ID +
				" and it." + Item_TagConst.TAG_ID + " = " + tagId;
		
		return db.rawQuery(sqlStatement,null);
	}
	
	
	/**
	 * getAllItemsOfTag - get every item with given tag
	 * 
	 * @param  - id of the tag of the items to return
	 * @return cursor to list of all item id# with the given tag
	 *  
	 * SQL query
	 * select * from item_tag where item_tag.item_id=tagId
	 */
	public Cursor getTagsOfItem(int itemId){
		String sqlStatement = "Select * from " + TagConst.TBL_NAME + 
				" t, " + Item_TagConst.TBL_NAME + " it " +
				"where t." + TagConst.ID + " = it." + Item_TagConst.TAG_ID +
				" and it." + Item_TagConst.ITEM_ID + " = " + itemId;
		
		return db.rawQuery(sqlStatement,null);
	}
	
		
	
	//TODO: great for testing, should remove for release
	/**
	 * executes sql query
	 * @param sql the SQL query
	 * @param selection may include ?s in where clause which will be
	 * 		  replaced by vlaues from selection[]
	 * @return
	 */
	public Cursor rawQuery(String sql, String[]selection){
		return db.rawQuery(sql, selection);
	}
	
	
	
	
	/****************************************************************
	 *                         Updates
	 *                      Rename, Move
	 ***************************************************************/
	
	/**
	 * Change the color of a Cagetory
	 * 
	 * @param catID id of category to update
	 * @param color new color of category
	 */
	public void UpdateCategoryColor(int catID, int color){
		Log.v("DB.updateCatColor","change color of categoryId=" + 
	          catID + " to #" +  color);
		ContentValues updateValue = new ContentValues();
		updateValue.put(CategoryConst.COLOR, color);
		String whereClause=CategoryConst.ID + "=" + catID;
		db.update(CategoryConst.TBL_NAME, updateValue, whereClause, null);
	}
	
	
	/**
	 * Change the name of a Category
	 * 
	 * @param catID id of category to update
	 * @param newName new name of category
	 */
	public void UpdateCategoryName(int catID, String newName){
		Log.v("DB.updateCatName","change name of categoryId=" + 
		          catID + " to " +  newName);
		ContentValues updateValue = new ContentValues();
		updateValue.put(CategoryConst.NAME, newName);
		String whereClause=CategoryConst.ID + "=" + catID;
		db.update(CategoryConst.TBL_NAME, updateValue, whereClause, null);
	}
	
	
	/**
	 * Change the name of a Tag
	 * 
	 * @param tagID id of tag to update
	 * @param newName new name of tag
	 */
	public void UpdateTagName(int tagID, String newName){
		Log.v("DB.updateTagName","change name of tagId=" + 
		          tagID + " to " +  newName);
		ContentValues updateValue = new ContentValues();
		updateValue.put(TagConst.NAME, newName);
		String whereClause=TagConst.ID + "=" + tagID;
		db.update(TagConst.TBL_NAME, updateValue, whereClause, null);
	}
	
	/**
	 * Change the name of an item
	 * 
	 * @param itemID id of item to update
	 * @param newName new name of item
	 */
	public void UpdateItemName(int itemID, String newName){
		Log.v("DB.updateItemName","change name of itemId=" + 
		          itemID + " to " +  newName);
		ContentValues updateValue = new ContentValues();
		updateValue.put(ItemConst.NAME, newName);
		String whereClause=ItemConst.ID + "=" + itemID;
		db.update(ItemConst.TBL_NAME, updateValue, whereClause, null);
	}
	
	
	/****************************************************************
	 *                         Deletes
	 *          Note: items are deleted from DB only, not memory
	 ***************************************************************/
	
	
	/**
	 * deletes item with given id.  Also deletes its tag associations
	 *
	 * @param itemId
	 */
	public void deleteItem(int itemId){
		// delete the item-tag associations
		db.delete(Item_TagConst.TBL_NAME, 
				  Item_TagConst.ITEM_ID + "=" + itemId, null);
		//delete the item
		db.delete(ItemConst.TBL_NAME, ItemConst.ID + "=" + itemId, null);
	}
	
	
	/**
	 * deletes Category with given id.  // TODO: items with this category?
	 *
	 * @param catId id of the category to be deleted
	 */
	public void deleteCategory(int catId){
		db.delete(CategoryConst.TBL_NAME,  CategoryConst.ID + "=" + 
					catId, null);
	}
	
	
	/**
	 * deletes tag with given id.  Also deletes its item_tag associations
	 *
	 * @param tagId id of tag to be deleted
	 */
	public void deleteTag(int tagId){
		// delete the item-tag associations
		db.delete(Item_TagConst.TBL_NAME, 
				  Item_TagConst.TAG_ID + "=" + tagId, null);
		//delete the item
		db.delete(TagConst.TBL_NAME, TagConst.ID + "=" + tagId, null);
	}
	
	
	/****************************************************************
	 * 						Testing
	 * 				Rebuild database and fill with test data
	 ***************************************************************/
	
	/**
	 * Drops tables, re-creates them and fills with sample data
	 */
	public void rebuildTest(){
		dbhelper.dropAllTables(db);
		dbhelper.createTables(db);
		fillTables();
	}
	
	
	/**
	 * Fill tables with testing data
	 */
	public void fillTables(){
		try {
			Log.v("WSdb.fillTables","enter test data");
			insertItem("Itemname1", 2, true, "DATA here");
	        insertItem("Itemname2", 1, false, "DATA2 here");
	        insertCategory("Cat 1", 123456, "schema for cat 1");
	        insertCategory("cat 2", 654321, "schema for cat 2");
	        insertTag("tag1");//, 888888);
	        insertTag("tag2");//, 555555);
	        insertItem_Tag(1,2);
	        insertItem_Tag(1,1);
		} catch (Exception e) {
			Log.v("NotesDB.fillTables exception: ", e.getMessage());
		}
	}	
}

