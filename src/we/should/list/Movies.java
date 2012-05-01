/**
 * 
 */
package we.should.list;

import java.util.*;

/**
 * @author Davis
 *
 */
public class Movies extends GenericCategory {
	private static List<Field> fields = Field.getMovieFields();
	
	public Movies(){
		super("Movies", fields);
	}
	
	@Override
	public Item newItem() {
		Item i = new MovieItem(this);
		return i;
	}
}
