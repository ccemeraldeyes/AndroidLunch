package we.should.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class GenericCategory extends Category {
	private Set<Field> fields;
	private List<Item> items; 
	
	public GenericCategory(String name, Set<Field> fields) {
		super(name);
		this.fields = fields;
		this.items = new ArrayList<Item>();
		
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
	public Set<Field> getFields(){
		return Collections.unmodifiableSet(fields);
	}
	@Override
	public boolean removeItem(Item i) {
		return this.items.remove(i);
	}

}
