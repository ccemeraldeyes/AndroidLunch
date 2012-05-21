package we.should;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import we.should.list.Tag;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SetTags extends Activity {
	
	/** The tag search bar. **/
	private AutoCompleteTextView mTagSearch;
	
	/** The add button. **/
	private Button mAdd;
	
	/** A map from every tag's name to its color. **/
	private Map<String, String> mTags;
	
	/** A list of all of the tag names. **/
	private ArrayAdapter<String> mTagNames;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_tags);
		Bundle extras = getIntent().getExtras();
		
		@SuppressWarnings("unchecked")
		Set<Tag> tagSet = (Set<Tag>) extras.getSerializable(WeShouldActivity.TAGS);
		mTags = new HashMap<String, String>();
		for (Tag t : tagSet) {
			mTags.put(t.toString(), t.getColor());
		}
		
		List<String> list = new ArrayList<String>(mTags.keySet());
		mTagNames = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		
		List<String> searchList = new ArrayList<String>();
		for (Tag t : Tag.getTags(this)) {
			searchList.add(t.toString());
		}
		mTagSearch = (AutoCompleteTextView) findViewById(R.id.name);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.autocomplete_text_view, searchList);
		mTagSearch.setAdapter(adapter);
		
		mAdd = (Button) findViewById(R.id.add);
		final String[] colors = new String[Tag.getAllTagColors().size()];
		int i = 0;
		for (String s : Tag.getAllTagColors().keySet()) {
			colors[i++] = s;
		}
		mAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final String addName = mTagSearch.getText().toString();
				for (int i = 0; i < mTagNames.getCount(); i++) {
					if (mTagNames.getItem(i).equals(addName)) {
						Toast.makeText(SetTags.this,
								"This item already contains this tag.",
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(SetTags.this);
				builder.setTitle("Pick a color");
				builder.setItems(colors, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String name = addName;
						String color = colors[which];
						mTags.put(name, color);
						mTagNames.add(name);
						mTagSearch.setText("");
					}
				});
				builder.show();
			}
		});
		
		ListView lv = (ListView) findViewById(R.id.tags_list);
		lv.setAdapter(mTagNames);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.set_tag_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save:
			save();
			break;
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}
	
	/**
	 * This method is a bit misnamed.  It actually just returns the list of tags
	 * that the user wanted to add.
	 */
	public void save() {
		Set<Tag> ret = new HashSet<Tag>();
		for (String name : mTags.keySet()) {
			ret.add(new Tag(0, name, mTags.get(name)));
		}
		Intent intent = new Intent();
		intent.putExtra(WeShouldActivity.TAGS, (Serializable) ret);
		setResult(RESULT_OK, intent);
		finish();
	}
}
