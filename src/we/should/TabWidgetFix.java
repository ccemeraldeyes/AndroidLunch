package we.should;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabWidget;

/**
 * Shores up a nasty bug in Android's base TabWidget.  Required for dynamic
 * tabs in a tabhost.
 * 
 * @author Will
 */

public class TabWidgetFix extends TabWidget {
	
	/** The dummy view to return. **/
	private final View mDummy;
	
	public TabWidgetFix(Context ctx) {
		super(ctx);
		mDummy = new View(ctx);
	}
	
	public TabWidgetFix(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mDummy = new View(ctx);
	}
	
	public TabWidgetFix(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
		mDummy = new View(ctx);
	}
	
	@Override
	public View getChildAt(int i) {
		if (i < 0 || i >= getChildCount()) {
			return mDummy;
		}
		return super.getChildAt(i);
	}
}
