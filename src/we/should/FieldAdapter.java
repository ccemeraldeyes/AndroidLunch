package we.should;

import java.util.ArrayList;
import java.util.List;

import we.should.list.FieldType;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FieldAdapter extends ArrayAdapter<FieldAdapter.ProtoField> {
	
	/** The Context we are passed (must be castable to Activity). **/
	private Context mContext;
	
	public FieldAdapter(Context context, List<ProtoField> data) {
		super(context, R.layout.new_row);
		mContext = context;
		addAll(data);
	}
	

	private void addAll(List<ProtoField> data) {
		for (ProtoField pff : data) {
			add(pff);
		}
	}
	
	public List<ProtoField> getAll() {
		List<ProtoField> fields = new ArrayList<ProtoField>();
		for (int i = 0; i < getCount(); i++) {
			fields.add(getItem(i));
		}
		return fields;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ProtoFieldView protoRow = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(R.layout.new_row, parent, false);
			
			protoRow = new ProtoFieldView();
			protoRow.name = (FixedEditText) row.findViewById(R.id.name);
			protoRow.type = (Spinner) row.findViewById(R.id.type);
			protoRow.removeField = (Button) row.findViewById(R.id.removeField);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, FieldType.getTypes());
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			protoRow.type.setAdapter(adapter);
			row.setTag(protoRow);
		} else {
			protoRow = (ProtoFieldView) row.getTag();
		}
		
		final ProtoField pf = getItem(position);
		protoRow.name.setText(pf.name);
		protoRow.type.setSelection(pf.type);
		
		protoRow.name.removeAllListeners();
		protoRow.name.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				pf.name = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			
		});
		
		protoRow.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				pf.type = pos;
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		
		protoRow.removeField.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				FieldAdapter.this.remove(pf);
			}
			
		});
		
		return row;
	}
	
	public static class ProtoField {
		public String name;
		public int type;
	}

	private static class ProtoFieldView {
		public FixedEditText name;
		public Spinner type;
		public Button removeField;
	}

}
