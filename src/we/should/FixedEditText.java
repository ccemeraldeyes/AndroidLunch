package we.should;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * An extension of EditText that actually allows us to remove all listeners
 * rather than having to keep a handle to them.
 * 
 * @author Will
 */

public class FixedEditText extends EditText {
	
	/** All of the TextWatchers that have been registered with this EditText. **/
	private Set<TextWatcher> mWatchers = new HashSet<TextWatcher>();

	public FixedEditText(Context context) {
		super(context);
	}
	
	public FixedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FixedEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void addTextChangedListener(TextWatcher watcher) {
		mWatchers.add(watcher);
		super.addTextChangedListener(watcher);
	}
	
	@Override
	public void removeTextChangedListener(TextWatcher watcher) {
		mWatchers.remove(watcher);
		super.removeTextChangedListener(watcher);
	}
	
	/**
	 * Removes all registered listeners from this EditText, something that the
	 * default Android EditText DOES NOT DO BECAUSE IT'S DUMB.
	 */
	public void removeAllListeners() {
		for (TextWatcher watcher : mWatchers) {
			super.removeTextChangedListener(watcher);
		}
		mWatchers.clear();
	}

}
