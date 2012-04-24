package we.should.list;

import java.util.Map;
import java.util.Set;

public class GenericCategory extends Category {
	private Map<String, FieldType> fields;
	private Set<Item> items; 
	
	public GenericCategory(String name, Map<String, FieldType> fields) {
		super(name);
		this.fields = fields;
		
	}
	@Override
	public Set<Item> getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item newItem() {
		// TODO Auto-generated method stub
		return null;
	}

}
