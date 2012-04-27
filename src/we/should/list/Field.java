package we.should.list;

import java.util.*;

import we.should.list.FieldType;

public class Field {
	public static final Field NAME = new Field("name", FieldType.TextField);
	public static final Field PHONENUMBER = new Field("phoneNumber", FieldType.TextField);
	public static final Field ADDRESS = new Field("address", FieldType.MultilineTextField);
	public static final Field RATING = new Field("rating", FieldType.Rating);
	public static final Field COMMENT = new Field("comment", FieldType.MultilineTextField);

	private FieldType type;
	private String name;
	
	/**
	 * Creates a new field object with the given name and fieldType
	 * @param name - represents the type of data to be held by this
	 * @param f - the type of data entry method. Must be from FieldType enum.
	 */
	public Field(String name, FieldType f){
		this.type = f;
		this.name = name;
	}
	/**
	 * @return this.name + " " + this.type;
	 */
	public String toString(){
		return this.name + " " + this.type;
	}
	/**
	 * Returns a list of the static default variables.
	 * @return the list of fields for name, address, rating, and comment.
	 */
	public static List<Field> getDefaultFields(){
		List<Field> out = new LinkedList<Field>();
		out.add(NAME);
		out.add(PHONENUMBER);
		out.add(ADDRESS);
		out.add(RATING);
		out.add(COMMENT);
		return out;
	}
}
