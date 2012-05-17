package we.should.list;

import java.util.ArrayList;
import java.util.List;


/**
 * Representation of possible ways to render a field.
 * 
 * @author Davis
 * 
 */
public enum FieldType {
	TextField, MultilineTextField, PhoneNumber, Rating, CheckBox;

	public static int size = FieldType.values().length;

	public static FieldType get(int type) {
		return FieldType.values()[type];
	}
	
	public static List<String> getTypes() {
		List<String> ret = new ArrayList<String>();
		for (FieldType ft : FieldType.values()) {
			ret.add(ft.toString());
		}
		return ret;
	}
}
