package we.should.search;

import we.should.CustomDialog;
import we.should.list.Item;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * A CustomPinPoint on the map.
 * @author Lawrence
 */
public class CustomPinPoint extends Overlay {
	private Item item;
	private GeoPoint point;
	private Projection projection;
	private int color;
	private Context context;
	
	public CustomPinPoint(Context c, Item item, GeoPoint p, Projection projection, int color) {
		this.context = c;
		this.item = item;
		this.point = p;
		this.projection = projection;
		this.color = color;
	}
	
	/**
	 * Draw a Pin.
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Paint mPaint = new Paint();
		mPaint.setStrokeWidth(2);
	    mPaint.setColor(color);     
	    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	    mPaint.setAntiAlias(true);
		Point p = new Point();
		projection.toPixels(point,p);
		Point p1 = new Point(p.x - 5, p.y -15);
		Point p2 = new Point(p.x, p.y);
		Point p3 = new Point(p.x + 5, p.y -15);
		canvas.drawCircle(p.x, p.y - 15, 5, mPaint);
		Path path = new Path();
	    path.setFillType(Path.FillType.EVEN_ODD);
	    path.moveTo(p1.x,p1.y);
	    path.lineTo(p2.x, p2.y);
	    path.lineTo(p3.x,p3.y);
	    path.lineTo(p1.x, p1.y);
	    path.close();
	    canvas.drawPath(path, mPaint);
	    Paint textPaint = new Paint();
	    textPaint.setStrokeWidth(3);
	    textPaint.setColor(Color.BLACK);
	    canvas.drawText(item.getName(), p.x, p.y, textPaint);
	}

	/**
	 * Detect the tap on the image.
	 */
	@Override
	public boolean onTap(GeoPoint clickpoint, MapView mapView) {
		// This can be used to find the distance.
		// float[] results = new float[1];
		// Location.distanceBetween(myLoc.getLatitudeE6(),myLoc.getLongitudeE6()
		// ,placeLocation.getLatitudeE6(), placeLocation.getLongitudeE6()
		// ,results);
		Point p1 = new Point();
		Point p2 = new Point();
		projection.toPixels(clickpoint, p1);
		projection.toPixels(point, p2);
		if(p1.x > p2.x - 10 && p1.x < p2.x + 10 &&
				p1.y > p2.y - 15 && p1.y < p2.y + 5) {
			Dialog dialog = new CustomDialog(context, item, 0, 0);
			dialog.show();
			return true;
		} else {		
			return super.onTap(clickpoint, mapView);
		}
	}
}
