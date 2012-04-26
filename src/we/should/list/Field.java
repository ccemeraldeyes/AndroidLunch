package we.should.list;

import we.should.list.FieldType;

public enum Field {
	Name("name", FieldType.TextField),
	PhoneNumber("phoneNumber", FieldType.TextField),
	Address("address", FieldType.MultilineTextField),
	Rating("rating", FieldType.Rating),
	Comment("comment", FieldType.MultilineTextField);

	private FieldType type;
	private String name;
	
	Field(String name, FieldType f){
		this.type = f;
		this.name = name;
	}
	String key(){
		return this.name + " " + this.type;
	}
	
}
