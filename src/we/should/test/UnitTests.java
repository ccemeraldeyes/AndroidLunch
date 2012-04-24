package we.should.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class UnitTests extends TestSuite {

	public static Test suite() {
		return new UnitTests();
	}
	public UnitTests() {
		this("Unit Tests");
	}
	public UnitTests(String name) {
		super(name);
		addTestSuite(ItemUnitTest.class);
		addTestSuite(DBUnitTest.class);
	}

}
