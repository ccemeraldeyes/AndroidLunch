package we.should.list;

import java.util.*;

/**
 * 
 * @author Davis
 * 
 * Representation Invariants:
 * 
 * For all Items i in items{
 * 		i.category == this
 * }
 */
public class GenericCategory extends Category {
	private List<Field> fields;
	private List<Item> items; 
	
	
	public GenericCategory(String name, List<Field> fields) {
		super(name);
		this.fields = fields;
		this.items = new ArrayList<Item>();
		checkRep();
		
	}
	/**
	 * Checks the representation invariant.
	 * Throws an assertion error if it is violated.
	 */
	private void checkRep(){
		for(Item i : items){
			assert(i.getCategory() == this);
		}
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
		Boolean output = this.items.add(i);
		checkRep();
		return output;
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
