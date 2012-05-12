package we.should.list;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

/**
 * Representation of possible ways to render a field. Not fully implemented
 * 
 * @author Davis
 * 
 */
public enum FieldType {
	TextField, MultilineTextField, Rating;

	/**
	 * Returns a new view corresponding to this field type. Implemented as a
	 * static method instead of as a method on each enum in order to avoid
	 * needing reflection.
	 * 
	 * @param f
	 *            The FieldType
	 * @param ctx
	 *            The Context
	 * @return a new view corresponding to this FieldType
	 */
	public static View getView(FieldType f, Context ctx) {
		switch (f) {
		case TextField:
			return new EditText(ctx);
		case MultilineTextField:
			EditText et = new EditText(ctx);
			et.setLines(5);
			return et;
		case Rating:
			return new RatingBar(ctx);
		default:
			throw new IllegalStateException("You added an enum and forgot to "
					+ "add it here");
		}
	}
}
