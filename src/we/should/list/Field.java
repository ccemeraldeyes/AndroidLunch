package we.should.list;

import java.util.ArrayList;
import java.util.List;

import we.should.list.FieldType;

public class Field {
	public static final Field NAME = new Field("name", FieldType.TextField);
	public static final Field PHONENUMBER = new Field("phoneNumber", FieldType.TextField);
	public static final Field ADDRESS = new Field("address", FieldType.MultilineTextField);
	public static final Field RATING = new Field("rating", FieldType.Rating);
	public static final Field COMMENT = new Field("comment", FieldType.MultilineTextField);

	private FieldType type;
	private String name;
	
	Field(String name, FieldType f){
		this.type = f;
		this.name = name;
	}
	public String toString(){
		return this.name + " " + this.type;
	}
	public static List<Field> getDefaultFields(){
		List<Field> out = new ArrayList<Field>();
		out.add(NAME);
		out.add(PHONENUMBER);
		out.add(ADDRESS);
		out.add(RATING);
		out.add(COMMENT);
		return out;
	}
}
