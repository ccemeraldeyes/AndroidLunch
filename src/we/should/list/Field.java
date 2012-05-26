package we.should.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author Davis Shepherd
 * This is a helper class that is used to inform the UI how to render different fields
 * in each item/category. It contains simply a name and a field type.
 *
 */

public class Field implements Comparable<Field>{

	
	public static final Field NAME = new Field("name", FieldType.TextField, 0);
	public static final Field WEBSITE = new Field("website", FieldType.TextField, 1);
	public static final Field PHONENUMBER = new Field("phone number", FieldType.PhoneNumber, 2);
	public static final Field ADDRESS = new Field("address", FieldType.MultilineTextField, 3);
	public static final Field RATING = new Field("rating", FieldType.Rating, 4);
	public static final Field COMMENT = new Field("comment", FieldType.MultilineTextField, 5);
	public static final Field TAGS = new Field("tags", FieldType.MultilineTextField, 6);
	
	private static Map<String, Field> fieldMap;
	static {
		Map<String, Field> map = new HashMap<String, Field>();
		List<Field> fields = getAllFields();
		for(Field f : fields){
			map.put(f.name, f);
		}
		fieldMap = Collections.unmodifiableMap(map);
	}
	private static final int DEFAULT_ORDER = -1;
	
	private FieldType type;
	private String name;
	private int order;
	
	/**
	 * Creates a new field object with the given name and fieldType
	 * @param name - represents the type of data to be held by this
	 * @param f - the type of data entry method. Must be from FieldType enum.
	 */
	public Field(String name, FieldType f){
		this.type = f;
		this.name = name;
		this.order = DEFAULT_ORDER;
	}
	private Field(String name, FieldType f, int order){
		this(name, f);
		this.order = order;
	}
	/**
	 * Creates a new field object matching the string desc
	 * @param desc formatted as <name> : <type>
	 * @throws IllegalArgumentException
	 */
	protected Field(String desc) throws IllegalArgumentException{
		String[] sp = desc.split(":");
		this.name = sp[0];
		try{
			this.name = sp[0];
			this.type = FieldType.values()[Integer.parseInt(sp[1])];
			this.order = Integer.parseInt(sp[2]);
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("The input string: " + desc + " is improperly formatted!");
		} catch(IndexOutOfBoundsException e){

			Field def = fieldMap.get(this.name);
			if(def != null) this.order = def.order;
			else this.order = DEFAULT_ORDER;
		}
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
		return this.name + ":" + fieldType + ":"  + order;

	}
	public static List<Field> getAllFields(){
		List<Field> out = new LinkedList<Field>();
		out.add(NAME);
		out.add(WEBSITE);
		out.add(PHONENUMBER);
		out.add(ADDRESS);
		out.add(RATING);
		out.add(COMMENT);
		out.add(TAGS);
		return out;
	}
	/**
	 * Returns a list of the static default variables.
	 * @return the list of fields for name, address, rating, and comment.
	 */
	public static List<Field> getDefaultFields(){
		List<Field> out = new LinkedList<Field>();
		out.add(NAME);
		out.add(WEBSITE);
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
		out.add(WEBSITE);
		out.add(RATING);
		out.add(COMMENT);
		return out;
	}
	
	/**
	 * Returns a list of fields with reserved names.
	 * @return a list of fields that have special behavior
	 */
	public static List<String> getReservedNames() {
		List<String> list = new ArrayList<String>();
		list.add(NAME.getName().toLowerCase());
		list.add(WEBSITE.getName().toLowerCase());
		list.add(PHONENUMBER.getName().toLowerCase());
		list.add(ADDRESS.getName().toLowerCase());
		list.add(COMMENT.getName().toLowerCase());
		list.add(TAGS.getName().toLowerCase());
		return list;
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

//	public int compareTo(Object another) {
//		if (this == another) return 0;
//		Field o = (Field) another;
//		if(o.order == this.order) return this.name.compareTo(o.name);
//		if(o.order == DEFAULT_ORDER) return -1;
//		if(this.order == DEFAULT_ORDER) return 1;
//		return this.order - o.order;
		
	public int compareTo(Field another) {
		if (this == another) return 0;
		if(another.order == this.order) return this.name.compareTo(another.name);
		if(another.order == DEFAULT_ORDER) return -1;
		if(this.order == DEFAULT_ORDER) return 1;
		return this.order - another.order;
	}
}
