/**
 * 
 */
package we.should.list;

import java.util.*;

import android.content.Context;

/**
 * @author Davis
 *
 */
public class Movies extends GenericCategory {
	
	public Movies(Context ctx){
		super("Movies", Field.getMovieFields(), ctx);
	}
	
	@Override
	public Item newItem() {
		Item i = new MovieItem(this);
		return i;
	}
}
