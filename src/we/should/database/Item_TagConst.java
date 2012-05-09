package we.should.database;

/**
 * Contains Constant values for the item_tag relationship database table
 * Used in WeShould database methods
 * @author  UW CSE403 SP12
 */
public class Item_TagConst {
	public static final int 	DATABASE_VERSION=1;			
	public static final String 	DATABASE_NAME="WeShould.db",// filename on device
								TBL_NAME="item_tag",		// table name
								ITEM_ID="item_id",			// item key Id
								TAG_ID="tag_id";			// tag key Id
}
