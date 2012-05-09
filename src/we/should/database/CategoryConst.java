package we.should.database;

/**
 * Contains Constant values for the list items database table
 * Used in WeShould database methods
 * @author  UW CSE403 SP12
 */
public class CategoryConst {
	public static final int 	DATABASE_VERSION=1;
	public static final String 	DATABASE_NAME="WeShould.db",// filename on device
								TBL_NAME="category",		// table name
								ID="id", 					// unique id		
								NAME="name",				// category name
								COLOR="color",				// color rgb hex value
								SCHEMA="schema";			// json code
}
