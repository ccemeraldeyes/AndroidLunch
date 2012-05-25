package we.should.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * WeShould Database class - contains database methods used in the 
 * 							 WeShould Android Application
 * 
 * @author  Troy Schuring
 * 			UW CSE403 SP12
 */

//TODO: rep invariant

public class WSdb {
	private SQLiteDatabase db; 
	private final Context context;
	private DBHelper dbhelper;
	

	/**
	 * WeShould Database Constructor
	 * 
	 * @param c context to use to create the database helper
	 */
	public WSdb(Context c){
		Log.v("WSDB constructor", "entering constructor");
		context=c;
		dbhelper = new DBHelper(context, ItemConst.DATABASE_NAME, null, 
									 ItemConst.DATABASE_VERSION);
	}
	
	
	/**
	 * Open database for writing
	 * 
	 * @return true if db is open and writable, false otherwise
	 * @exception ex caught SQLiteException if failure to open writable database,
	 * 			  will open readable if fails 
	 */
	public boolean open(){
		try {
			db = dbhelper.getWritableDatabase();
		} catch(SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = dbhelper.getReadableDatabase();
			return false;
		}
		db.execSQL("PRAGMA foreign_keys=ON;");
		return true;
	}
	
	
	/**
	 * Check if the database is currently open
	 * 
	 * @return true if open, false otherwise
	 */
	public boolean isOpen(){
		return db.isOpen();
	}
	
	
	/**
	 * Close open database object 
	 */
	public void close(){
		dbhelper.close(); 
	}
	
	
	/****************************************************************
	 *                      Insert Methods
	 ***************************************************************/
	
	
	/**
	 * Insert an Item into the database
	 * 
	 * @param name of Item being entered
	 * @param categoryId unique id of Item's category
	 * @param data json code holding item schema & data
	 * @return row ID of the newly inserted row
	 * @exception SQLiteConstraintException if insert violates constraints
	 * @exception IllegalArgumentException if argument format invalid
	 */
	public long insertItem(String name, int categoryId, String data)
			throws IllegalArgumentException, SQLiteConstraintException{
		
		Log.v("db.insertItem",name + " " + categoryId + " " + data);
		
		// check arguments for valid format
		if (hasNoChars(name) || hasNoChars(data) || categoryId<1){
			Log.e("db.insertItem","invalid argument - " + name + 
					" or " + data + " or " + categoryId);
			throw new IllegalArgumentException();
		}
		ContentValues newTaskValue = new ContentValues();
		newTaskValue.put(ItemConst.NAME, name);
		newTaskValue.put(ItemConst.CAT_ID, categoryId);
		newTaskValue.put(ItemConst.DATA, data);
		return db.insertOrThrow(ItemConst.TBL_NAME, null, newTaskValue);	
	}
	

	/**
	 * Insert a category into the database
	 * 
	 * @param name of category being entered - maximum length 32 characters
	 * @param color color associated with this Category
	 * @param schema json string to identify category schema 
	 * @return row ID of the newly inserted row
	 * @exception SQLiteConstraintException if insert violates constraints
	 * @exception IllegalArgumentException if argument format invalid
	 */
	public long insertCategory(String name, String color, String schema)
			throws IllegalArgumentException, SQLiteConstraintException{
		
		Log.v("InsertCategory", "arguments-- " + name + ", " + color + ", " + schema);
		
		//check arguments for null and empty strings
		if (hasNoChars(name) || hasNoChars(color) || hasNoChars(schema)){
			Log.e("db.insertCategory","argument format error");
			throw new IllegalArgumentException ();
		}
		ContentValues newTaskValue = new ContentValues();
		newTaskValue.put(CategoryConst.NAME, name);
		newTaskValue.put(CategoryConst.COLOR, color);
		newTaskValue.put(CategoryConst.SCHEMA, schema);
		
		return db.insertOrThrow(CategoryConst.TBL_NAME, null, newTaskValue);
	}
	
	
	/**
	 * Insert a Tag into the database
	 * 
	 * @param name of Tag - maximum length 32 characters
	 * @param color color to be associated with this tag
	 * @return row ID of the newly inserted tag
	 * @exception SQLiteConstraintException if insert violates constraints
	 * @exception IllegalArgumentException if argument format invalid
	 */
	public long insertTag(String name, String color)
			throws IllegalArgumentException, SQLiteConstraintException{
		
		Log.v("WSdb.insertTag","inserting tag" + name);
		
		// check argument for null or empty string
		if(hasNoChars(name) || hasNoChars(color)){
			Log.e("db.insertTag","argument (name) is empty");
			throw new IllegalArgumentException();
		}
		ContentValues newTaskValue = new ContentValues();
		newTaskValue.put(TagConst.NAME, name);
		newTaskValue.put(TagConst.COLOR, color);
		
		return db.insertOrThrow(TagConst.TBL_NAME, null, newTaskValue);
	}
	
	
	/**
	 * Insert an item-tag relationship into the database- "Tag an item"
	 * 
	 * @param itemID key id of item to be tagged
	 * @param tagID key id of tag to be placed on item
	 * @return row ID of newly inserted row
	 * @exception SQLiteConstraintException if insert violates constraints
	 * @exception IllegalArgumentException if argument format invalid
	 */
	public long insertItem_Tag(int itemID, int tagID)
			throws IllegalArgumentException, SQLiteConstraintException{
		Log.v("WSdb.insertItem_Tag","inserting (item,tag)=(" + itemID + "," + tagID + ")");
		
		if (itemID < 1 || tagID < 1){
			Log.e("db.insertItem_Tag","invalid id argument");
			throw new IllegalArgumentException();
		}
		
		ContentValues newTaskValue = new ContentValues();
		newTaskValue.put(Item_TagConst.ITEM_ID, itemID);
		newTaskValue.put(Item_TagConst.TAG_ID, tagID);
		return db.insertOrThrow(Item_TagConst.TBL_NAME, null, newTaskValue);
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
	 * Get the list of all items ordered by id
	 * 
	 * @return cursor to list of all items ordered by id (default)
	 */
	public Cursor getAllItems(){
		return db.query(ItemConst.TBL_NAME, null, null,null, null,
						null, null);
	}
	
	/**
	 * Get the item with id=<code>itemId</code>
	 * 
	 * @param itemId key id of item to get
	 * @return cursor to the list containing item if it exists in the 
	 * 			database
	 */
	public Cursor getItem(int itemId){
		String where = ItemConst.ID + "=" + itemId;
		return db.query(ItemConst.TBL_NAME, null , where, null, null,
						null, null);
	}
	
	/**
	 * Get the list of all categories ordered by id
	 * 
	 * @return cursor to list of all categories ordered by id (default)
	 */
	public Cursor getAllCategories(){
		return db.query(CategoryConst.TBL_NAME, null, null,
				null, null, null, null);
	}
	
	
	/**
	 * Get the category with id=<code>catId</code>
	 * 
	 * @param catId key id of the category you want to return
	 * @return cursor to list containing category if it exists in the 
	 * 			database
	 */
	public Cursor getCategory(int catId){
		String where=CategoryConst.ID + "=" + catId;
		return db.query(CategoryConst.TBL_NAME, null, where, null,
						null, null, null);
	}
	
	
	/**
	 * Get the list of all tags ordered by id
	 * 
	 * @return cursor to ordered list of tags
	 */
	public Cursor getAllTags(){
		return db.query(TagConst.TBL_NAME, null, null, null, null,
						null, null);
	}
	
	/**
	 * Get the tag with id=<code>tagId</code>
	 * 
	 * @param tagId key id of tag to get
	 * @return cursor to the list containing tag if it exists in 
	 * 			database
	 */
	public Cursor getTag(int tagId){
		String where = TagConst.ID + "=" + tagId;
		return db.query(TagConst.TBL_NAME, null , where, null, null,
						null, null);
	}
	
	/**
	 * Get every item with tag <code>tagId</code>
	 * 
	 * @param taagId key id of the tag of the items to return
	 * @return cursor to list of all item id# with the given tag
	 */
	public Cursor getItemsOfTag(int tagId){
		
		String from= ItemConst.TBL_NAME + " i, " + 
					 Item_TagConst.TBL_NAME + " it ";
		
		String where="i." + ItemConst.ID + " = it." + 
					 Item_TagConst.ITEM_ID + " and it." + 
					 Item_TagConst.TAG_ID + " = " + tagId;
		
		String sqlStatement = "Select * from " + from + " where " + where;
		
		return db.rawQuery(sqlStatement,null);
	}
	
	/**
	 * Get every tag of of an item with given <code>itemId</code>
	 * 
	 * @param  itemId id of the item to get all tags of
	 * @return cursor to list of all tag id# of the given item
	 */
	public Cursor getTagsOfItem(int itemId){
		
		String from = TagConst.TBL_NAME + " t, " + 
					  Item_TagConst.TBL_NAME + " it";
		
		String where = "t." + TagConst.ID + " = it." + 
					   Item_TagConst.TAG_ID + " and it." + 
				       Item_TagConst.ITEM_ID + " = " + itemId;
		
		String sqlStatement = "Select * from " + from + " where " + 
				       		   where;
		
		return db.rawQuery(sqlStatement,null);
	}
	

	/**
	 * get all items of the category with id=<code>catId</code>
	 * 
	 * @param catId id of the category
	 * @return cursor to all items in this category
	 */
	public Cursor getItemsOfCategory(int catId){
		String where=ItemConst.CAT_ID + "=" + catId;
		return db.query(ItemConst.TBL_NAME, null, where, null,
						null, null, null);
	}

	
	/**
	 * check to see if a given item has a given tag
	 * 
	 * @param itemId key id of item 
	 * @param tagId key id of tag
	 * @return true if this item is associated with this tag, 
	 *         false otherwise
	 */
	public boolean isItemTagged(int itemId, int tagId){
		String where = (itemId + "=" + Item_TagConst.ITEM_ID +
				" and " + tagId + "=" + Item_TagConst.TAG_ID);
		
		Cursor c = db.query(Item_TagConst.TBL_NAME, null, where, null,
						null, null, null);
	
		if (c.getCount() > 0){
			c.close();
			return true; // item tag exists already
		}else{
			c.close();
			return false;
		}
	} 
	
		
	/****************************************************************
	 *                         Updates
	 *                      Rename, Move
	 ***************************************************************/
	
	/**
	 * update all category fields other than key id to given values
	 * 
	 * @param catID id of category to update
	 * @param name string name of category - maximum length of 32 characters
	 * @param colorID id of new color of category
	 * @param schema JSON string defining category fields
	 * @return true if update is successful, false or exception otherwise
	 * @exception SQLiteConstraintException if insert violates constraints
	 * @exception IllegalArgumentException if argument format invalid
	 * @return true if update succeeded, 
	 * 			false if failed and transaction rolled back
	 */
	public boolean updateCategory(int catID, String name, String color, String schema) 
					throws IllegalArgumentException, SQLiteConstraintException{

		Log.v("DB.updateCategory","update category categoryID=" + 
		          catID);

		// check arguments for valid format
		if (hasNoChars(name) || hasNoChars(color) || hasNoChars(schema)){
			Log.e("db.updateCategory","Invalid Argument format");
			throw new IllegalArgumentException();
		}	
		
		ContentValues updateValue = new ContentValues();
		updateValue.put(CategoryConst.NAME, name);
		updateValue.put(CategoryConst.COLOR, color);
		updateValue.put(CategoryConst.SCHEMA, schema);
		String whereClause=CategoryConst.ID + "=" + catID;
		
		db.beginTransaction();
		int affected=0;
		affected=db.updateWithOnConflict(CategoryConst.TBL_NAME, updateValue, 
				whereClause, null, SQLiteDatabase.CONFLICT_ROLLBACK);
		
		if (affected > 0){
			db.setTransactionSuccessful();
			db.endTransaction();
			return true;
		}else{
    		Log.e("db.updateCategory", "update failed & rolled back, " +
    				"check constraint exception");
			db.endTransaction();
			return false;
		}
	}
	

	/**
	 * Update a Tag
	 * 
	 * @param tagID id of tag to update
	 * @param newName new name of tag
	 * @param color new color of tag
	 * @return true if update succeeded, 
	 * 			false if failed and transaction rolled back
	 */
	public boolean updateTag(int tagID, String newName, String color)
			throws IllegalArgumentException, SQLiteConstraintException{

		Log.v("DB.updateTag","update tagId=" + tagID);
		
		if (hasNoChars(newName) || hasNoChars(color) || tagID<1){
			Log.e("db.updateTag","Invalid argument");
			throw new IllegalArgumentException();
		}
		int affected=0;
		ContentValues updateValue = new ContentValues();
		updateValue.put(TagConst.NAME, newName);
		updateValue.put(TagConst.COLOR, color);
		String whereClause=TagConst.ID + "=" + tagID;
		db.beginTransaction();
		affected = db.updateWithOnConflict(TagConst.TBL_NAME, updateValue, 
				whereClause, null, SQLiteDatabase.CONFLICT_ROLLBACK);
		if (affected > 0){
			db.setTransactionSuccessful();
			db.endTransaction();
			return true;
		}else{
			db.endTransaction();
			throw new SQLiteConstraintException();		}
	}
	
	/**
	 * Update all fields of an item other than the key id 
	 * 
	 * @param itemID id of item to update
	 * @param name new name of item
	 * @param catId id of category item belongs to
	 * @param data string of JSON field data
	 * @return true if update succeeded, 
	 * 			false if failed and transaction rolled back
	 */
	public boolean updateItem(int itemID, String name, int catId, String data)
			throws IllegalArgumentException, SQLiteConstraintException{

		Log.v("DB.updateItem","updating item itemId=" + itemID);
		
		if (hasNoChars(name) || itemID<1){
			Log.e("db.updateItem","Invalid argument");
			throw new IllegalArgumentException();
		}
		int affected=0;
		
		ContentValues updateValue = new ContentValues();
		updateValue.put(ItemConst.NAME, name);
		updateValue.put(ItemConst.CAT_ID, catId);
		updateValue.put(ItemConst.DATA, data);
		
		String whereClause=ItemConst.ID + "=" + itemID;
		affected = db.updateWithOnConflict(ItemConst.TBL_NAME, updateValue, 
				whereClause, null,SQLiteDatabase.CONFLICT_ROLLBACK);
		if (affected > 0) 
			return true;
		else
			return false;
	}
	
	
	/****************************************************************
	 *                          Deletes
	 ***************************************************************/
	
	/**
	 * Deletes an item and item-tag associations
	 *  
	 * @param itemId id of item to be deleted
	 * @return true on successful deletion, 
	 *         false if no rows deleted
	 */
	public boolean deleteItem(int itemId)
			throws SQLiteConstraintException{
		int affected=0;
		// delete the item-tag associations
		affected=db.delete(Item_TagConst.TBL_NAME, 
				 Item_TagConst.ITEM_ID + "=" + itemId, null);         
		Log.v("WSdb.deleteItem",
				  "deleted " + affected + " item tag associations");
		//delete the item
		affected=db.delete(ItemConst.TBL_NAME, ItemConst.ID + "=" + itemId, 
				null);
	
		if (affected>0){
			Log.v("WSdb.deleteItem", "deleted " + affected + " item");		
			return true;
		}
		return false;	
	}
	
	
	/**
	 * Deletes a Category. Requires no items to have this category id
	 *
	 * @param catId id of the category to be deleted
	 * @return true if category deleted, false otherwise
	 */
	public boolean deleteCategory(int catId)
			throws SQLiteConstraintException{

		Log.v("WSdb.deleteCategory","cat id=" + catId);	
		
		// do not delete category if there are items associated with it		
		Cursor c = getItemsOfCategory(catId);
		if (c.getCount()>0){
			Log.e("db.deleteCategory", 
			"cannot delete category if items of this category exist");
			c.close();
			return false;
		}
		c.close();
		//delete the category
		int affected = db.delete(CategoryConst.TBL_NAME,
				       CategoryConst.ID + "=" + catId, null);		
		
		if (affected>0){
			Log.v("WSdb.deleteCategory","deleted " + affected + " category");		
			return true;
		}else
			return false;	
	}
	
	
	/**
	 * Deletes a tag and its item_tag associations
	 *
	 * @param tagId id of tag to be deleted
	 * @return true if tag deleted, false otherwise
	 */
	public boolean deleteTag(int tagId)
			throws SQLiteConstraintException{

		// delete the item-tag associations		
		int affected = db.delete(Item_TagConst.TBL_NAME,
				Item_TagConst.TAG_ID + "=" + tagId, null);		
		Log.v("WSdb.deleteTag",
			  "deleted " + affected + " item tag associations");		
		//delete the Tag		
		affected=db.delete(TagConst.TBL_NAME, 
				TagConst.ID + "=" + tagId, null);		
		Log.v("WSdb.deleteTag","deleted " + affected + " tags");
		if (affected > 0)			
			return true;		
		else			
			return false;	
	}
	
	/**
	 * Delete an item-tag relationship -- "Untag an item"
	 * 
	 * @param itemId id of item
	 * @param tagId id of tag
	 * @return true if successfully deleted, false otherwise
	 */

	public boolean deleteItemTagRel(int itemId, int tagId)
			throws SQLiteConstraintException{
		int affected=0;
		String where = Item_TagConst.ITEM_ID + "=" + itemId + " and " +
		               Item_TagConst.TAG_ID + "=" + tagId;
		
		affected=db.delete(Item_TagConst.TBL_NAME, where, null);
		
		if (affected > 0)			
			return true;		
		else			
			return false;
	}

	/****************************************************************
	 *                          Testing
	 *         Rebuild database and fill with test data
	 ***************************************************************/
	
	/**
	 * Drop all tables then create all tables
	 */
	public void rebuildTables(){
		dbhelper.dropAllTables(db);
		dbhelper.createTables(db);
	}
	
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
	        insertCategory("Cat 1", "abc123", "schema for cat 1");
	        insertCategory("cat 2", "abc123", "schema for cat 2");
		    insertItem("Itemname1", 2, "DATA here");
	        insertItem("Itemname2", 1, "DATA2 here");    
	        insertTag("tag1", "abc123");
	        insertTag("tag2", "abc123");
	        insertItem_Tag(1,2);
	        insertItem_Tag(1,1);
		} catch (Exception e) {
			Log.v("NotesDB.fillTables exception: ", e.getMessage());
		}
	}	
	
	
	/****************************************************************
	 *                     Helper Functions
	 ***************************************************************/
	
	/**
	 * Test a character to see if it is a hex value
	 * 
	 * @param c character to test if hex value
	 * @return true if c is a hex value, false otherwise
	 */
	public static boolean isHexChar(char c){
		c= Character.toUpperCase(c);
		if (Character.isDigit(c) || c>='A' && c<='F')
			return true;
		else 
			return false;
	}
	
	
	/**
	 * Test a string to see if in contains strictly Hex characters
	 * 
	 * @param str String to test if hex value
	 * @return true if str is a hex value, false if null or otherwise
	 */
	public static boolean isHexString(String str){
		if(hasNoChars(str)) return false;
		
		for (int i=0; i < str.length(); i++){
			if(!isHexChar(str.charAt(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * Check the given string for any character other than space
	 * 
	 * @param str string to check
	 * @return true on empty or whitespace, false on null or other
	 */
	public static boolean hasNoChars(String str){
		if (str==null) return true;
	
		//remove all white space
		str=str.replaceAll("\\s+", "");	
		if (str.length()==0) return true;
		
		return false;
	}
}
