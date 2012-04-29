
package we.should.list;

import java.util.*;

import we.should.database.*;

/**
 * @author Davis Shepherd
 *
 */
public abstract class Category {
	
	private final String name;
	
	
	public Category(String name){
		this.name = name;
	}
	public abstract Set<Item> getItems();
	
	
	
	public abstract Item newItem();
	
	protected abstract boolean addItem(Item i);
	
	protected abstract boolean removeItem(Item i);
	
	public abstract List<Field> getFields();
	
	public String getName(){
		return this.name;
	}
	
	static final Set<Category> getCategories(){
		return null;
		//TODO: parse DB and return the categories contained.
	}
	

}
