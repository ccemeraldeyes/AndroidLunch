package we.should.list;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class GenericCategory extends Category {
	private Map<String, FieldType> fields;
	private List<Item> items; 
	
	public GenericCategory(String name, Map<String, FieldType> fields) {
		super(name);
		this.fields = fields;
		
	}
	@Override
	public Set<Item> getItems() {
		Set<Item> out = new HashSet<Item>(items);
		return Collections.unmodifiableSet(out);
	}

	@Override
	public Item newItem() {
		Item i = new GenericItem(this);
		return i;
	}
	
	@Override
	public boolean addItem(Item i) {
		return this.items.add(i);
	}
	@Override
	public Map<String, FieldType> getFields(){
		return Collections.unmodifiableMap(fields);
	}

}
