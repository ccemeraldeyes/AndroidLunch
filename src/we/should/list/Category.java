
package we.should.list;

import java.util.*;

import we.should.database.*;

/**
 * @author Davis Shepherd
 *
 */
public abstract class Category {
	
	private final String name;
	
	/**
	 * Creates a new abstract Category with the given name.
	 * @param name - name of this category
	 */
	protected Category(String name){
		this.name = name;
	}
	/**
	 * Returns the items contained in this. That is the items that have both be created with
	 * newItem() and then saved with Item.save().
	 * @return the set of items in this
	 */
	public abstract Set<Item> getItems();
	
	
	/**
	 * Returns a new item in this Category. This item is not added until Item.save() is called.
	 * @return a new Item(C)
	 */
	public abstract Item newItem();
	
	/**
	 * Adds the given item to this
	 * @param i - the item to be added
	 * @return true always
	 */
	protected abstract boolean addItem(Item i);
	
	/**
	 * Removes this given item i if it is contained in this and returns true. Otherwise returns false
	 * @param i
	 * @return true if item is removed, false otherwise
	 * @modifies removes i from this.Items
	 */
	protected abstract boolean removeItem(Item i);
	
	/**
	 * Returns the list of Fields associated with this category
	 * @return a list of Field objects that can be edited.
	 */
	public abstract List<Field> getFields();
	
	/**
	 * Returns the name of this Category
	 * @return name of this category
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Parses the database and returns all categories that have been created
	 * @return the list of created categories
	 */
	static final Set<Category> getCategories(){
		return null;
		//TODO: parse DB and return the categories contained.
	}
	

}
