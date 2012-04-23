
package we.should.list;

import java.util.Set;

/**
 * @author Davis
 *
 */
public abstract class Category {
	
	private final String name;
	
	public Category(String name){
		this.name = name;
	}
	
	public abstract Set<Item> getItems();
	
	public abstract Item newItem();
	
	static final Set<Category> getCategories(){
		return null;
		//TODO: return all categories
	}

}


