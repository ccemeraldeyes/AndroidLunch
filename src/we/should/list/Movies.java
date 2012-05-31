/**
 * 
 */
package we.should.list;

import android.content.Context;

/**
 * This is a special movie category that returns Movie items 
 * from newItem(). It can be extended to have more behavior potentially.
 * 
 * Rep Invariant:
 * for(Item i : this.items)
 *      i instanceof MovieItem
 *      
 * @author Davis
 *
 */
public class Movies extends GenericCategory {
	
	public Movies(Context ctx){
		super(Category.Special.Movies.toString(), Field.getMovieFields(), ctx);
		checkRep();
	}
	
	@Override
	public Item newItem() {
		Item i = new MovieItem(this, ctx);
		return i;
	}
	private void checkRep(){
		for(Item i : this.items){
			assert i instanceof MovieItem;
		}
	}
}
