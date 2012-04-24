package we.should;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

public class WeShouldActivity extends MapActivity {
	
	/** The TabHost that cycles between categories. **/
	private TabHost mTabHost;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.mTabHost.setup();
        
        // Just spoof the tabs for the ZFR.  This would be dynamically loaded
        // once we begin work on production code.
        // pittsw: 4/20/12
        TabHost.TabSpec spec;
        View view;
        
        spec = mTabHost.newTabSpec("restaurants").setIndicator("  Restaurants  ")
        		.setContent(android.R.id.list);
        mTabHost.addTab(spec);

        spec = mTabHost.newTabSpec("movies").setIndicator("  Movies  ")
        		.setContent(android.R.id.list);
        mTabHost.addTab(spec);
        
        spec = mTabHost.newTabSpec("other").setIndicator("  Other  ")
        		.setContent(android.R.id.list);
        mTabHost.addTab(spec);

        for (int i = 0; i < 10; i++) {
	        spec = mTabHost.newTabSpec("other").setIndicator("  Other " + i + "  ")
	        		.setContent(android.R.id.list);
	        mTabHost.addTab(spec);
        }

        mTabHost.setCurrentTab(0);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}