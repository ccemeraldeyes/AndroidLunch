package we.should;

import we.should.database.WSdb;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class WeShouldActivity extends MapActivity implements LocationListener{
	
	/** The TabHost that cycles between categories. **/
	private TabHost mTabHost;
	private MapView map;
	private WSdb db;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        map = (MapView) findViewById(R.id.mapview);
        map.setBuiltInZoomControls(true);
        
        this.mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        this.mTabHost.setup();
        Log.v("oncreate", "opening db");
        db=new WSdb(this);
        db.open();
        db.rebuildTest();
        db.close();
        Log.v("oncreate","db opened");
        
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

	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}