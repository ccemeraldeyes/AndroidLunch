package we.should.list;

import java.util.*;


public class GenericCategory extends Category {
	private List<Field> fields;
	private List<Item> items; 
	
	public GenericCategory(String name, List<Field> fields) {
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
	protected boolean addItem(Item i) {
		return this.items.add(i);
	}
	@Override
	public List<Field> getFields(){
		return Collections.unmodifiableList(fields);
	}
	@Override
	protected boolean removeItem(Item i) {
		return this.items.remove(i);
	}

}
