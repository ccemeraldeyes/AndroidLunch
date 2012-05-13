package we.should.list;

import java.util.*;

import we.should.list.FieldType;
/**
 * 
 * @author Davis Shepherd
 * This is a helper class that is used to inform the UI how to render different fields
 * in each item/category. It contains simply a name and a field type.
 *
 */
public class Field {
	
	public static final Field NAME = new Field("name", FieldType.TextField);
	public static final Field PHONENUMBER = new Field("phone number", FieldType.PhoneNumber);
	public static final Field ADDRESS = new Field("address", FieldType.MultilineTextField);
	public static final Field RATING = new Field("rating", FieldType.Rating);
	public static final Field COMMENT = new Field("comment", FieldType.MultilineTextField);
	public static final Field TAGS = new Field("tags", FieldType.MultilineTextField);
 
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
	 * Creates a new field object matching the string desc
	 * @param desc formatted as <name> : <type>
	 * @throws IllegalArgumentException
	 */
	protected Field(String desc) throws IllegalArgumentException{
		String[] sp = desc.split(":");
		try{
			this.type = FieldType.values()[Integer.parseInt(sp[1])];
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("The input string: " + desc + " is improperly formatted!");
		}
		this.name = sp[0];
	}
	/**
	 * @return this.name + " " + this.type;
	 */
	public String toString(){
		return this.name + " " + this.type;
	}
	/**
	 * @return the type of this
	 */
	public FieldType getType() {
		return type;
	}
	/**
	 * @return the name of this
	 */
	public String getName() {
		return name;
	}
	/**
	 * Used to flatten the object for DB usage
	 * @return a unique identifying string for this
	 */
	protected String toDB(){
		int fieldType = this.type.ordinal();
		return this.name + ":" + fieldType;
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
	/**
	 * Returns a list of Fields associated with the Movie category
	 * @return a list of relevant Movie fields.
	 */
	public static List<Field> getMovieFields(){
		List<Field> out = new LinkedList<Field>();
		out.add(NAME);
		out.add(RATING);
		out.add(COMMENT);
		return out;
	}
	/**
	 * @ param other object to which the comparison is made
	 * @ return true if the name and type of other is equal to this
	 */
	@Override
	public boolean equals(Object other){
		if(other == this) return true;
		if(other == null || !(other instanceof Field)) return false;
		Field cp= Field.class.cast(other);
		return this.type.equals(cp.type) && this.name.equals(cp.name);
	}
	/**
	 * @return a hash code 
	 */
	@Override
	public int hashCode(){
		return this.name.hashCode()*10 + this.type.ordinal();
	}
}
