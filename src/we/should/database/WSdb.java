package we.should.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

//TODO: add transactions & rollbacks?? 

/**
 * WeShould Database class - contains database methods used in the 
 * 							 WeShould Android Application<br/>
 * 
 * NOTE: Most methods are sending Log verbose output.  Running LogCat while executing displays information.
 * See DBexamples.txt for examples on how to call the methods and parse results.
 * @author  UW CSE403 SP12
 * 
 * 
 *
 */
public class WSdb {
	private SQLiteDatabase db; 
	private final Context context;
	private DBHelper dbhelper;
	

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
	 * 
	 * @exception ex caught SQLiteException if failure to open writable database,
	 * 				 will open readable if fails 
	 * @return true if db is open and writable, false otherwise
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
	 * check if the database is currently open
	 * 
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
	 * 
	 */
	
	
	public long insertItem(String name, int categoryId, boolean mappable, String data){
		if (hasNoChars(name) || hasNoChars(data) || categoryId<1)
			return -1;
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
	 * @param color 6 digit RGB value of color to identify category
	 * @param schema string to identify category schema 
	 * @return row ID of the newly inserted row, or -1 if an error occurred 
	 * @exception ex caught SQLiteException if insert fails
	 * @exception SQLConstraintException
	 */
	public long insertCategory(String name, String color, String schema){
		
		//check for null and empty strings
		if (hasNoChars(name) || hasNoChars(color) || hasNoChars(schema))
			return -1;
		
		// validate color hex value
		if (!isHexString(color) || color.length()!=6){ 
			Log.e("WSdb.insertCategory", "color not a hex value");
			return -1;
		}
		
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
	 * @return row ID of the newly inserted row, or -1 if an error occurred 
	 * @exception ex caught SQLiteException if insert fails
	 */
	
	public long insertTag(String name){
		if(hasNoChars(name))
			return -1;
		try{
			Log.v("WSdb.insertTag","inserting tag");
			ContentValues newTaskValue = new ContentValues();
			newTaskValue.put(TagConst.NAME, name);
			return db.insert(TagConst.TBL_NAME, null, newTaskValue);
		} catch(SQLiteException ex) {
			Log.v("InsertTag exception caught", ex.getMessage());
			return -1;				
		}
	}
	
	
	/**
	 * Insert an item-tag relationship into the database- "Tag an item"
	 * 
	 * @param itemID key id of item to be tagged
	 * @param tagID key id of tag to be placed on item
	 * @return row ID of newly inserted row, or -1 if an error occurred
	 * @exception ex caught SQLiteException if insert fails
	 * 
	 */
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
		return db.query(ItemConst.TBL_NAME, null, null,null, null,
						null, "name");
	}
	
	/**
	 * get the item with id == itemId
	 * 
	 * @return cursor to the item
	 * 
	 * SQL query
	 * select * from item where id=[given id]
	 */
	public Cursor getItem(int itemId){
		String where = ItemConst.ID + "=" + itemId;
		return db.query(ItemConst.TBL_NAME, null , where, null, null,
						null, null);
	}
	
	
	/**
	 * get the tag with id == tagId
	 * 
	 * @return cursor to the tag
	 * 
	 * SQL query
	 * select * from tag where tagid=[given id]
	 */
	public Cursor getTag(int tagId){
		String where = TagConst.ID + "=" + tagId;
		return db.query(TagConst.TBL_NAME, null , where, null, null,
						null, null);
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
	 * get the category with id=catId
	 * 
	 * @param catId key id of the category you want to return
	 * @return cursor to single category
	 * 
	 * SQL query
	 * select * from category where id=[given id]
	 */
	public Cursor getCategory(int catId){
		String where=CategoryConst.ID + "=" + catId;
		return db.query(CategoryConst.TBL_NAME, null, where, null,
						null, null, null);
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
		return db.query(TagConst.TBL_NAME, null, null, null, null,
						null, "name");
	}
	
	
	/**
	 * getAllItemsOfTag - get every item with given tag
	 * 
	 * @param tagId key id of the tag of the items to return
	 * @return cursor to list of all item id# with the given tag
	 *  
	 * SQL query
	 * select * from item_tag, item 
	 *   where item_tag.tag_id=tagId 
	 *   and item_tag.item_id = item.id
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
	 * getAllTagsOfItem - get every tag of item with given id
	 * 
	 * @param  itemId id of the item to get all tags of
	 * @return cursor to list of all tag id# of the given item
	 *  
	 * SQL query
	 * select * from item_tag, tag
	 *   where item_tag.item_id=itemId
	 *   and item_tag.tag_id= tag.id
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
	 * get all items of the given category
	 * 
	 * @param catId id if the category
	 * @return cursor to all items in this category
	 * 
	 * select * from item where cat_id=[given id]
	 */
	public Cursor getItemsOfCategory(int catId){
		String where=ItemConst.CAT_ID + "=" + catId;
		return db.query(ItemConst.TBL_NAME, null, where, null,
						null, null, null);
	}
	
	
		
	
	//TODO: great for testing, should remove for release
	/**
	 * executes sql query
	 * @param sql the SQL query
	 * @param selection may include ?s in where clause which will be
	 * 		  replaced by vlaues from selection[]
	 * @return cursor to results
	 */
	public Cursor rawQuery(String sql, String[]selection){
		return db.rawQuery(sql, selection);
	}
	
	
	
	
	/****************************************************************
	 *                         Updates
	 *                      Rename, Move
	 ***************************************************************/
	
	/**
	 * Change the color of a Category
	 * 
	 * @param catID id of category to update
	 * @param color new color of category
	 * @return number of rows updated (0 if failed, 1 if success)
	 */
	public int updateCategoryColor(int catID, String color){
	
		Log.v("DB.updateCatColor","change color of categoryId=" + 
		          catID + " to #" +  color);
		if (!isHexString(color) || color.length()!=6 || catID<1)
			return 0;
		int affected=0;
		ContentValues updateValue = new ContentValues();
		updateValue.put(CategoryConst.COLOR, color);
		String whereClause=CategoryConst.ID + "=" + catID;
		affected=db.update(CategoryConst.TBL_NAME, updateValue, whereClause, null);
		return affected;
		
	}
	
	
	
	/** Change the name of a Category
	 * 
	 * @param catID id of category to update
	 * @param newName new name of category
	 *
	 */
	public int updateCategoryName(int catID, String newName){
		Log.v("DB.updateCatName","change name of categoryId=" + 
		          catID + " to " +  newName);
		if (hasNoChars(newName) || catID<1)
			return 0;
		int affected=0;
		ContentValues updateValue = new ContentValues();
		updateValue.put(CategoryConst.NAME, newName);
		String whereClause=CategoryConst.ID + "=" + catID;
		affected=db.update(CategoryConst.TBL_NAME, updateValue, whereClause, null);
		return affected;
	}
	
	
	/**
	 * Change the name of a Tag
	 * 
	 * @param tagID id of tag to update
	 * @param newName new name of tag
	 */
	public int updateTagName(int tagID, String newName){
		Log.v("DB.updateTagName","change name of tagId=" + 
		          tagID + " to " +  newName);
		if (hasNoChars(newName) || tagID<1)
			return 0;
		int affected=0;
		ContentValues updateValue = new ContentValues();
		updateValue.put(TagConst.NAME, newName);
		String whereClause=TagConst.ID + "=" + tagID;
		affected = db.update(TagConst.TBL_NAME, updateValue, whereClause, null);
		return affected;
	}
	
	/**
	 * Change the name of an item
	 * 
	 * @param itemID id of item to update
	 * @param newName new name of item
	 */
	public int updateItemName(int itemID, String newName){
		Log.v("DB.updateItemName","change name of itemId=" + 
		          itemID + " to " +  newName);
		
		if (hasNoChars(newName) || itemID<1)
			return 0;
		
		int affected=0;
		ContentValues updateValue = new ContentValues();
		updateValue.put(ItemConst.NAME, newName);
		String whereClause=ItemConst.ID + "=" + itemID;
		affected=db.update(ItemConst.TBL_NAME, updateValue, whereClause, null);
		return affected;
	}
	
	
	
	/****************************************************************
	 *                         Deletes
	 ***************************************************************/
	
	
	/**
	 * deletes item with given id.  Also deletes its tag associations
	 *  
	 * @param itemId id of item to be deleted
	 * @return true on successful deletion, 
	 *         false if transaction conflicts with referential 
	 *               integrity and transaction rolled back
	 */
	public boolean deleteItem(int itemId){
		
		db.beginTransaction();
		try{
			// delete the item-tag associations
			db.delete(Item_TagConst.TBL_NAME, Item_TagConst.ITEM_ID +
					  "=" + itemId, null);
			//delete the item
			db.delete(ItemConst.TBL_NAME, ItemConst.ID + "=" + itemId, 
					null);
		} finally{
			if (db.inTransaction())
				db.endTransaction();
			else
				return false;
		}
		return true;
	}
	
	// TODO: items with this category?
	/**
	 * deletes Category with given id.  
	 *
	 * @param catId id of the category to be deleted
	 */
	public boolean deleteCategory(int catId){
		
		// do not delete category if there are items associated with it
		Cursor c = getItemsOfCategory(catId);
		if (c.getCount()>0) return false;
		
		int affected = db.delete(CategoryConst.TBL_NAME,  
				       CategoryConst.ID + "=" + catId, null);
		
		// if no rows deleted, return false
		if (affected>0)
			return true;
		else
			return false;
	}
	
	
	/**
	 * deletes tag with given id.  Also deletes its item_tag associations
	 *
	 * @param tagId id of tag to be deleted
	 */
	public boolean deleteTag(int tagId){
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
	
	
	/****************************************************************
	 * 						Testing
	 * 				Rebuild database and fill with test data
	 ***************************************************************/
	
	
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
	        insertCategory("Cat 1", "123456", "schema for cat 1");
	        insertCategory("cat 2", "654321", "schema for cat 2");
		    insertItem("Itemname1", 2, true, "DATA here");
	        insertItem("Itemname2", 1, false, "DATA2 here");    
	        insertTag("tag1");
	        insertTag("tag2");
	        insertItem_Tag(1,2);
	        insertItem_Tag(1,1);
		} catch (Exception e) {
			Log.v("NotesDB.fillTables exception: ", e.getMessage());
		}
	}	
	
	
	/****************************************************************
	 * 						Helper Functions
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
		if(hasNoChars(str))
			return false;
		for (int i=0; i < str.length(); i++){
			if(!isHexChar(str.charAt(i)))
				return false;
		}		
		return true;
	}
	
	/**
	 * 
	 * @param str
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

