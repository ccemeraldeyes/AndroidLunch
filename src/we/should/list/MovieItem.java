/**
 * 
 */
package we.should.list;

import android.content.Context;

/**
 * @author Davis
 *
 */
public class MovieItem extends GenericItem {

	protected MovieItem(Category c, Context ctx) {
		super(c, ctx);
	}
	protected MovieItem(String cName, Context ctx){
		super(cName, ctx);
	}

}
