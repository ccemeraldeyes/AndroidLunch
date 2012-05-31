/**
 * 
 */
package we.should.list;

import android.content.Context;

/**
 * This is a special MovieItem that can potentiall override the 
 * getAddresses() method and return addresses of theatre locations etc.
 * 
 * Rep Invariant:
 * 	this.c instanceof Movies
 * @author Davis
 *
 */
public class MovieItem extends GenericItem {

	protected MovieItem(Category c, Context ctx) {
		super(c, ctx);
		checkRep();
	}
	
	private void checkRep(){
		assert this.c instanceof Movies;
	}

}
