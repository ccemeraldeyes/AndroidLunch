package we.should.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import we.should.WeShouldActivity;
import we.should.database.WSdb;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RestoreService extends IntentService{

	public RestoreService() {
		super("we.should.communication.RestoreService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		String username = extras.getString(WeShouldActivity.ACCOUNT_NAME);
		
		boolean done = false;
		String dbstring = "";
		int index = 0;
		
		while(!done){
		
			HttpClient httpclient = new DefaultHttpClient();
	
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    nameValuePairs.add(new BasicNameValuePair("user_email", username));
		    nameValuePairs.add(new BasicNameValuePair("index", ""+index+""));
			
			String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
			
			HttpGet httpget = new HttpGet("http://23.23.237.174/restore?"+paramString);
						
			try {
				
				HttpResponse response = httpclient.execute(httpget);
				
				InputStream is = response.getEntity().getContent();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				int i=0;
				while(i != -1){
					i = is.read();
					baos.write(i);
				}
				
				
				
				byte[] buf = baos.toByteArray(); //TODO: figure out how to not hardcode this
				//				is.read(buf);
				JSONObject resp = new JSONObject(new String(buf));
				
				String doneString = resp.getString("done");
				if(doneString.equals("true")){
					done = true;
				}
				
				Log.d("RESTORE RESPONSE", new String(buf));
	

				dbstring += resp.getString("data");
				//TODO: do the same for tags and items
			   		    
	
			} catch (ClientProtocolException e) {
			    // TODO Auto-generated catch block
				Log.v("RESTORE SERVICE", e.getMessage());
			} catch (IOException e) {
			    // TODO Auto-generated catch block
				Log.v("RESTORE SERVICE", e.getMessage());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			index++;
			
		}
		
//		String data = "Est omittam propriae comprehensam ad, ex delectus iracundia eam. Et vel eligendi inimicus periculis, mentitum oporteat persequeris duo ea. Ne vim reque dolor, qui possit option eu. Duo equidem delicatissimi eu, augue quaeque periculis ea ius. Ei nam quando maiorum alienum. Commune ocurreret incorrupte an pro, dicant moderatius definiebas in cum, inani molestiae nam eu. Equidem volumus nam in, eu etiam graeci referrentur sit. Modus ubique adolescens mea ne, eos in utinam apeirian similique. Cu has movet commune explicari. Quo ne stet propriae. Ex eos velit albucius. Cu amet labores usu. Qui animal labores persequeris in. Sanctus qualisque efficiantur te vel, debitis dolorem ne sea. Qui posse eruditi ut, nec errem solet possim ne. Est et democritum assueverit omittantur. Eu per eruditi docendi nominavi, sea eruditi placerat detraxit ad, id est ornatus invenire. Ad sea unum alterum necessitatibus, ad antiopam maluisset usu, et epicurei ocurreret est. Id tempor incorrupte mel. His graeco persius denique ei. In has scaevola recteque ocurreret, ei nam ullum errem latine. Volumus splendide has ad. Adhuc idque accusata cu sed, ea tantas maiestatis consetetur qui. Pri an audire concludaturque. Aeque assueverit eu per. Ne vix labitur persequeris, et erat detraxit usu, ne usu quando molestie tacimates. Id eam commodo equidem appellantur, cum ne sapientem reprehendunt. Nec labores legimus indoctum id. Cu viris labores mel. Ex mei dico laudem, posse atomorum imperdiet eos cu. Delicata pertinacia qui ex. Ad suas pertinacia pro. Id usu molestiae abhorreant. Mundi ridens quo id, usu no omittam reprimique, munere qualisque est eu. Sit at appareat facilisis, mei libris incorrupte definitionem in. An his esse assentior inciderint, vis ea blandit oporteat, omnes congue exerci eum ut. Vim at movet quaerendum intellegebat, ut vis philosophia mediocritatem, mea ea nonumy evertitur definitiones. Eu mel laoreet disputando, ut nibh atqui mediocrem quo. Mea mutat quaerendum eu. Et usu harum nominati. Ei est stet sanctus indoctum. Sea solum aperiam ut, ei est graecis scriptorem. Usu ut erant essent, dolorum scaevola urbanitas qui ex. Reque maiestatis usu et. Ei mea nullam ponderum. Idque possit te qui. Nonumy perfecto ei mei, quo ex diam falli reformidans. Ea mazim vidisse gloriatur pri, ei ius novum quando patrioque. Omnium molestie usu id, eos ea delenit pertinacia definitiones, te mea quot vocibus quaestio. An aliquid erroribus nec, discere euismod recusabo sit eu. Vim et liber copiosae. Mel vide vidisse utroque ei, sale graece rationibus pro in. Nam populo omnium ceteros te, qui vidisse cotidieque ea. Mutat omnesque urbanitas te quo, his no stet repudiare percipitur. Ex per labitur abhorreant consetetur, porro fugit nemore ei vix, in eos eleifend salutandi posidonium. Nec cu aliquam apeirian, ius at novum quodsi fierent. Nec ut assum sadipscing scribentur. Solum homero nominavi te usu, qui elit utinam cu. Agam oportere vix id. Et duo sint quot. Exerci ridens labore sed an, sed ad modo docendi, sea in sint case posidonium. Mea eu alterum intellegat, an sumo nominati eos. Sint illud forensibus ex mei, malis veniam apeirian ea vis, choro volumus explicari nam ex. Eam alterum pericula euripidis cu. Aliquid posidonium quo cu, quod omittam vis eu. Usu in quando utroque eleifend, te autem volutpat abhorreant nec. Vel quem gubergren concludaturque no, novum graece meliore at pri, ea nam iuvaret elaboraret efficiendi. Maiorum recteque ex ius, ut commune mediocrem ullamcorper has, id mel verear vidisse conclusionemque. Vim error errem facilis at. Ea tritani repudiandae vix, case principes te has, pro dolores expetenda ei. Pro an veritus oporteat deseruisse, cu putent delenit ponderum per. Est tale posidonium eu, ut est quem percipitur repudiandae. Est copiosae intellegam neglegentur ne, errem ocurreret mea te. Mel te homero alterum deterruisset, te vix audire noluisse. Cum facete tractatos deterruisset te.Ne eos graece periculis, ius oblique eripuit intellegat ea, sed dicunt explicari hendrerit ad. Ex has bonorum eruditi delenit, an has pericula persecuti. Ad omnis reformidans vix. Mea facilisi perfecto ex. Aliquid nostrum quo cu, vix ut sale consequuntur, fugit dicat nihil quo id. Dolorum blandit persequeris nam ex. Vix omnes utamur dolorum eu, nec ignota tacimates et. Qui meliore adipiscing et, eu facilisi urbanitas pri. Agam docendi ullamcorper vel eu, odio cetero ius ex, ut vero etiam nam. Nec quem saperet expetenda ei, ad est wisi iusto, cu mea aperiri delectus. Saepe commodo mel ne, eos vivendo delicatissimi et. Vim no fugit explicari philosophia, has ex dolorum singulis mnesarchum. Id pro commodo pertinacia, cibo timeam gloriatur eu quo. Ex vis appetere consetetur, tritani impedit splendide quo eu, vel diam diceret at. Sumo maiestatis ullamcorper ius ea. Volumus iudicabit et sit, quas nihil ex duo. Est iusto integre suavitate eu, no nam audiam bonorum neglegentur, ex summo nusquam usu. Stet delectus id mea, viris legendos dissentias mei ut. Nihil legimus ea usu, ei has fugit suscipit. Et per tale dolorem, cu primis qualisque pro. Est dicit semper aliquam eu. Offendit explicari eam id, eum ea latine forensibus. Has ex consul legendos. Pro nullam tritani ut. Est eu meliore deserunt electram, eros singulis eu mei. At agam duis mea. Pericula necessitatibus sed an. At viderer dissentiunt vituperatoribus est, esse appetere prodesset mei te. Ea vel affert iracundia pertinacia. No iudico delectus mei. At probatus consetetur scriptorem qui. Ex fastidii periculis euripidis nec, ea dicant expetenda has. Elit alterum graecis ne sit. Eam libris quaestio patrioque in. Te eos evertitur deseruisse adipiscing, natum inimicus vulputate pri ut. Nam fierent electram ex, stet conceptam pro ad, quando deterruisset an eam. At sed consul prodesset voluptaria. Eu eros saepe audire vim. An vis vidisse invidunt gubergren. Populo moderatius ea vix, numquam admodum nec te. Sea reque ignota et. Pri elit erant consul ex. Unum malorum voluptatum duo in. An mea falli vivendo forensibus. Eam ridens instructior consectetuer eu. Id ornatus accusam vituperatoribus cum, illud democritum reformidans no cum. Ne eam illud integre abhorreant, ei has veri liberavisse. Ei ius quem splendide. Simul elaboraret eos ad, vim eu postea referrentur. Aliquid sapientem ex per. Vix autem dicant dolorem eu. Mea ridens fastidii id. Ea sit impedit suavitate, sit atqui probatus at. Qui mundi commune molestiae id, in qui nusquam posidonium, pri cu suas ponderum. Quis insolens id ius, vis graeco invidunt conceptam in. Vix dolor quodsi persecuti et, malorum accusam quo ex, his modus inani et. Eos quem mazim ne. Omnesque nominavi est no, platonem molestiae ea mel. Prima equidem omnesque no sit, eum putent prodesset no. Nec esse docendi detracto ea. Est iisque malorum eu, ex nostrud salutandi assentior cum, mei ei essent sententiae neglegentur. Ius aeque perfecto scriptorem an. Laoreet fabellas neglegentur eos te. Melius civibus interpretaris an sed. An his posidonium reprimique, ad usu consul dissentias, cum id meis prompta inermis. Vel graecis moderatius at, dicunt deseruisse disputando vim te. Vero choro in eum, no nam quis illum. Elitr verear ad usu, no hinc expetendis persequeris eum. Et mundi deleniti appellantur qui, et rebum saepe fabellas sit. At facete accusamus pri, movet soleat euismod vix ei, sit nominavi adipiscing an. Deleniti praesent urbanitas ad vel, tamquam quaerendum definitiones has ea. Te atqui harum conclusionemque nam, eos dolorem mnesarchum adversarium ad. Vix liber postulant ne. Mel partem accumsan ei, ex qui alia noster, no populo sanctus mea. Eam inimicus adolescens an. No amet nisl vis, mutat mundi patrioque nec cu, ut tamquam dignissim mel. Est menandri signiferumque eu. Aliquip consequuntur mei et, ubique impetus no nam. At falli deterruisset has, falli dictas per ad, admodum accumsan delectus ex mea. Ad vix ubique accusata, ei nostro nostrum maiestatis cum. Iusto doctus ius no, vel et populo everti volutpat. Usu ei facer atomorum assentior, consul essent ius ne. No putent discere ius, vel ex putant accusata recusabo, no sea prima partiendo prodesset. Eu usu exerci dictas signiferumque. At lorem malorum euripidis mel. Et novum cotidieque ullamcorper eam. Praesent constituto ei usu. Id simul eligendi mea, ad equidem placerat mea. Has et viris causae, vel dico iudico facete te. Putent mnesarchum consetetur nec te, ea decore senserit nam. Putent eruditi consetetur est.";
//		Log.d("COMPARE RESTORE TO BACKUP", ""+data.equals(dbstring)+"");
		
		
		WSdb db = new WSdb(this);
		db.open();
		db.Restore(dbstring);
		db.close();
		//do db restore from string
		
		Intent weshouldactivity = new Intent();
		weshouldactivity.setClass(getApplicationContext(), WeShouldActivity.class);
		weshouldactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(weshouldactivity);
	}

}
