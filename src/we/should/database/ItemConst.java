package we.should.database;

/**
 * Contains Constant values for the list items database table
 * Used in WeShould database methods
 * @author  UW CSE403 SP12
 */
public class ItemConst {
	
	public static final int 	DATABASE_VERSION=1;
	public static final String 	DATABASE_NAME="WeShould.db", // filename on device
								TBL_NAME="item", 			 // table name
								ID="id",                     // unique Id
								NAME="name",                 // item name
								CAT_ID="category_id",        // references Category Id
								DATA="data";             	 // json code								
}
