package we.should;

import android.os.Bundle;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
public class Map extends MapActivity{
    MapView map;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        map = (MapView) findViewById(R.id.mvMain);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
