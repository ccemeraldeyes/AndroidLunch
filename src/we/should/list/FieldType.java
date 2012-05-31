package we.should.list;

import java.util.ArrayList;
import java.util.List;

import we.should.R;


/**
 * Representation of possible ways to render a field.
 * 
 * @author Davis
 * 
 */
public enum FieldType {
	TextField(R.layout.edit_row_textline), 
	MultilineTextField(R.layout.edit_row_multiline),
	PhoneNumber(R.layout.edit_row_phone),
	Rating(R.layout.edit_row_rating), 
	CheckBox(R.layout.edit_row_checkbox);

	public static int size = FieldType.values().length;
	public int drawable;
	
	private FieldType(int drawable){
		this.drawable = drawable;
	}
	
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
