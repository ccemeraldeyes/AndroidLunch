package we.should;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import we.should.list.Tag;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

public class SetTags extends Activity {
	
	/** The tag search bar. **/
	private AutoCompleteTextView mTagSearch;
	
	/** The add button. **/
	private Button mAdd;
	
	/** A map from every tag's name to its value. **/
	private Map<String, NameColorPair> mTags;
	
	/** A list of all of the tag names. **/
	private List<String> mTagNames;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_tags);
		
		mTags = new HashMap<String, NameColorPair>();
		for (Tag t : Tag.getTags(this)) {
			mTags.put(t.toString(), new NameColorPair(t.toString(), t.getColor()));
		}
		mTagNames = new ArrayList<String>(mTags.keySet());
		
		mTagSearch = (AutoCompleteTextView) findViewById(R.id.name);
		if (!mTagNames.isEmpty()) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
			mTagSearch.setAdapter(adapter);
		}
		
		mAdd = (Button) findViewById(R.id.add);
		final String[] colors = new String[Tag.getAllTagColors().size()];
		int i = 0;
		for (String s : Tag.getAllTagColors().keySet()) {
			colors[i++] = s;
		}
		mAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SetTags.this);
				builder.setTitle("Pick a color");
				builder.setItems(colors, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String name = mTagSearch.getText().toString();
						String color = colors[which];
						mTags.put(name, new NameColorPair(name, color));
						mTagSearch.setText("");
					}
				});
				builder.show();
			}
		});
		
		ListView lv = (ListView) findViewById(R.id.tags_list);
		lv.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1));
	}

	private static class NameColorPair {
		public String mName;
		public String mColor;
		
		public NameColorPair(String name, String color) {
			mName = name;
			mColor = color;
		}
	}
}
