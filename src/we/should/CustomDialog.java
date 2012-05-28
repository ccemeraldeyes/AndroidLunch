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
//TODO: Comment this file
public class CustomDialog extends Dialog {
	private Item item;
	private double distance;
	public CustomDialog(Context context, Item item, double distance ) {
		super(context);
		this.item = item;
		this.distance = distance;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.customdialog);
		setTitle("Item Info");
		TextView tvName = (TextView) findViewById(R.id.tvPopupName);
		TextView tvDistance = (TextView) findViewById(R.id.tvPopupDis);
		TextView tvAddr = (TextView) findViewById(R.id.tvPopUpAddr);
		Button moreInfo = (Button) findViewById(R.id.btnMoreInfo);
		tvName.setText(item.getName());
		tvDistance.setText(distance + " miles away");
		String addr = item.get(Field.ADDRESS);
		tvAddr.setText(addr);
		moreInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(CustomDialog.this.getContext(), ViewScreen.class);
	    		intent.putExtra(WeShouldActivity.CATEGORY, item.getCategory().getName());
	    		intent.putExtra(WeShouldActivity.INDEX, item.getId());
	    		CustomDialog.this.startActivity(intent);
			}
			
		});
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	private void startActivity(Intent i){
		this.dismiss();
		getContext().startActivity(i);

	}
	
	
}
