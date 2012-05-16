package we.should.communication;

import java.util.ArrayList;
import java.util.List;

import we.should.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ApproveReferral extends Activity {
	
	private Button mButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.approve_referral);
		
		ListView lv = (ListView) findViewById(R.id.referralList);
		List<Referral> list = new ArrayList<Referral>();
		list.add(new Referral("The Kraken", "Will", false));
		list.add(new Referral("Reboot", "Troy", false));
		lv.setAdapter(new ReferralAdapter(this, list));
		
		mButton = (Button) findViewById(R.id.approve);
		mButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// get which items are approved from the referraladapter
				// save them
				finish();
			}
			
		});
	}

}
