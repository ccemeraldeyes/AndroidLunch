package we.should.database;

/**
 * Contains Constant values for the colors associated with categories
 * Used in WeShould database methods
 * @author  UW CSE403 SP12
 */
public class ColorConst {
	public static final int 	DATABASE_VERSION=1;
	public static final String 	DATABASE_NAME="WeShould.db",// filename on device
								TBL_NAME="color",			// table name
								ID="id", 					// unique id		
								NAME="name",				// category name
								RGB="rgb",				// color rgb hex value
								DRAWABLE="drawable";		// link to drawable
}