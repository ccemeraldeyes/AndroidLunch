
package we.should.list;

import java.util.Map;
import java.util.Set;

/**
 * @author Davis Shepherd
 *
 */
public abstract class Category {
	
	protected String name;
	
	public Category(String name){
		this.name = name;
	}
	public abstract Set<Item> getItems();
	
	public abstract Item newItem();
	
	public abstract boolean addItem(Item i);
	
	public abstract Map<String, FieldType> getFields();
	
	static final Set<Category> getCategories(){
		return null;
		//TODO: parses the DataBase and Returns a Set of Category objects
	}

}


