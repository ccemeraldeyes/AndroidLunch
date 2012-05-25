package we.should;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import we.should.list.Field;
import we.should.list.Item;

public class CustomDialog extends Dialog {
	private Item item;
	private double distance;
	private int position;
	public CustomDialog(Context context, Item item, double distance, int position ) {
		super(context);
		this.item = item;
		this.distance = distance;
		this.position = position;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.customdialog);
		TextView tvName = (TextView) findViewById(R.id.tvPopupName);
		TextView tvDistance = (TextView) findViewById(R.id.tvPopupDis);
		TextView tvAddr = (TextView) findViewById(R.id.tvPopUpAddr);
		Button moreInfo = (Button) findViewById(R.id.btnMoreInfo);
		tvName.setText("Name: " + item.getName());
		tvDistance.setText("Distance: " + distance + " miles");
		String addr = item.get(Field.ADDRESS);
		tvAddr.setText("Address: " + addr);
		moreInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				Intent intent = new Intent(getApplicationContext(), ViewScreen.class);
//	    		intent.putExtra(WeShouldActivity.CATEGORY, item.getCategory().getName());
//	    		intent.putExtra(WeShouldActivity.INDEX, position);
//	    		startActivity(intent, ActivityKey.VIEW_ITEM.ordinal());
			}
			
		});
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
	
}
