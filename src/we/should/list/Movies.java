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
	
	public Movies(){
		super("Movies", Field.getMovieFields());
	}
	
	@Override
	public Item newItem() {
		Item i = new MovieItem(this);
		return i;
	}
}
