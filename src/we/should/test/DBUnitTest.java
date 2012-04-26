package we.should.test;


import junit.framework.Test;
import we.should.database.WSdb;
import android.content.Context;
import android.test.AndroidTestCase;

/**
 * @author Troy
 *
 */
public class DBUnitTest extends AndroidTestCase{
	
	private WSdb tdb; // testdb - opened before every test in setup
	private WSdb tdb2; // for tests which need to open or close a db
	Context context;

	/**
	 * @throws java.lang.Exception
	 */
	
	//TODO: close db on test exit, destruct database on testcase exit 
	//TODO: verify setup gives empty db each time;
	
	public void setUp() throws Exception {
		tdb=new WSdb(context);
		tdb.open();
	}

	/**
	 * @throws java.lang.Exception
	 */
	
	
	public void tearDown() throws Exception {
		if (tdb.isOpen())
			tdb.close();
	}

	public void testOpen(){
		tdb2.open();
		assertTrue(tdb2.isOpen());
		tdb2.close();
	}
	
	public void testOpenWritable(){
		assertTrue(tdb2.open());
		tdb2.close();
	}
	

	public void testClose(){
		tdb2.open();
		assertTrue(tdb2.isOpen());
		tdb2.close();
		assertFalse(tdb2.isOpen());
	}
	
	
	
	
	//*********************** Insert Tests ************************************
	//TODO: Test exceptions
	
	
	// insert item, verify item is inserted at line 1
	public void testInsertItem(){
		long return_val=tdb.insertItem("testItem1", 1, true, "testItem1 data");
		assertEquals(return_val,1);
	}
	
	
	public void testInsertCategory(){
		long return_val=tdb.insertCategory("testCat1", 999999, "testCat1 schema");
		assertEquals(return_val,1);
	}
	
	public void testInsertTag(){
		long return_val=tdb.insertTag("testTag1");
		assertEquals(return_val,1);
	}

	
	public void testInsertItemTagRelationship(){
		long return_val=tdb.insertItem_Tag(1,1);
		assertTrue(return_val>0);
	}
	
	
	public static Test suite(){
		return new DBUnitTest();
	}

}
