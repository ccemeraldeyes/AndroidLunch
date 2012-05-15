package we.should.communication;

import we.should.ActivityKey;
import we.should.R;
import we.should.WeShouldActivity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class GetReferralsService extends IntentService {

	public GetReferralsService() {
		super("we.should.communication.GetReferralsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String username = extras.getString(WeShouldActivity.ACCOUNT_NAME);
		
		// do yo shit
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) getSystemService(ns);
		
		int icon = R.drawable.restaurant;
		String tickerText = "New referrals!";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "New referrals!";
		CharSequence contentText = "You have [X] new referrals awaiting your approval.";
		Intent notificationIntent = new Intent(this, ApproveReferral.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
		nm.notify(ActivityKey.NEW_REFERRAL.ordinal(), notification);
	}

}
