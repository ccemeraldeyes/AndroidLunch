/**
 * 
 */
package we.should.list;

import android.content.Context;

/**
 * @author Davis
 *
 */
public class Movies extends GenericCategory {
	
	public Movies(Context ctx){
		super(Category.Special.Movies.toString(), Field.getMovieFields(), ctx);
	}
	
	@Override
	public Item newItem() {
		Item i = new MovieItem(this, ctx);
		return i;
	}
}
