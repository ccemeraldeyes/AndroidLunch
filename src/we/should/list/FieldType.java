package we.should.list;


/**
 * Representation of possible ways to render a field.
 * 
 * @author Davis
 * 
 */
public enum FieldType {
	TextField, MultilineTextField, PhoneNumber, Rating;

	public static int size = FieldType.values().length;

	public static FieldType get(int type) {
		return FieldType.values()[type];
	}
}
