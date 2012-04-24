
package we.should.list;

import java.util.Map;
import java.util.Set;

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
	
	static final Set<Category> getCategories(){
		return null;
		//TODO: parses the DataBase and Returns a Set of Category objects
	}

}


