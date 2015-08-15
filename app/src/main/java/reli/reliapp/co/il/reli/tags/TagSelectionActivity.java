package reli.reliapp.co.il.reli.tags;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import reli.reliapp.co.il.reli.R;
import reli.reliapp.co.il.reli.custom.CustomActivity;
import reli.reliapp.co.il.reli.dataStructures.ReliTag;

public class TagSelectionActivity extends CustomActivity {

    private ArrayAdapter<String> adapter;
    private ListView lv;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_selection);

        // Listview Data
        ReliTag reliTags[] = getTagList();
        String tagsNames[] = new String[reliTags.length];
        for (int i = 0; i < reliTags.length; i++) {
            tagsNames[i] = reliTags[i].getTagName();
        }

        button =  (Button) findViewById(R.id.testbutton);
        button.setOnClickListener(this);

        lv = (ListView) findViewById(R.id.tags_list_view);
        EditText inputSearch = (EditText) findViewById(R.id.tags_search);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.tag_item, R.id.tag_name, tagsNames);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, tagsNames);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adapter);


        // Enabling Search Filter
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                TagSelectionActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    public void onClick(View v) {
        SparseBooleanArray checked = lv.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectedItems.add(adapter.getItem(position));
        }

        String[] outputStrArr = new String[selectedItems.size()];
        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
        }

        Toast.makeText(getApplicationContext(), Arrays.toString(outputStrArr), Toast.LENGTH_SHORT).show();
    }
        

    private ReliTag[] getTagList() {
        // TODO - change (so we will retrieve it from the parse table)
        ReliTag[] reliTagArray = {new ReliTag("Tag One"), new ReliTag("Tag Two")};
        return reliTagArray;
    }
}









//
//
//
//
//    ListView myListView;
//    Button getResult;
//
//    private ArrayList<String> dayOfWeekList = new ArrayList<String>();
//
//    private void initDayOfWeekList(){
//        dayOfWeekList.add("Sunday");
//        dayOfWeekList.add("Monday");
//        dayOfWeekList.add("Tuesday");
//        dayOfWeekList.add("Wednesday");
//        dayOfWeekList.add("Thursday");
//        dayOfWeekList.add("Friday");
//        dayOfWeekList.add("Saturday");
//
//    }
//
//    MyArrayAdapter myArrayAdapter;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initDayOfWeekList();
//        setContentView(R.layout.activity_main);
//
//        myListView = (ListView)findViewById(R.id.list);
//
//        myArrayAdapter = new MyArrayAdapter(
//                this,
//                R.layout.row,
//                android.R.id.text1,
//                dayOfWeekList
//        );
//
//        myListView.setAdapter(myArrayAdapter);
//        myListView.setOnItemClickListener(myOnItemClickListener);
//
//        getResult = (Button)findViewById(R.id.getresult);
//        getResult.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                String result = "";
//
//    /*
//    //getCheckedItemPositions
//    List<Integer> resultList = myArrayAdapter.getCheckedItemPositions();
//    for(int i = 0; i < resultList.size(); i++){
//     result += String.valueOf(resultList.get(i)) + " ";
//    }
//    */
//
//                //getCheckedItems
//                List<String> resultList = myArrayAdapter.getCheckedItems();
//                for(int i = 0; i < resultList.size(); i++){
//                    result += String.valueOf(resultList.get(i)) + "\n";
//                }
//
//                myArrayAdapter.getCheckedItemPositions().toString();
//                Toast.makeText(
//                        getApplicationContext(),
//                        result,
//                        Toast.LENGTH_LONG).show();
//            }});
//
//    }
//
//    OnItemClickListener myOnItemClickListener
//            = new OnItemClickListener(){
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//            myArrayAdapter.toggleChecked(position);
//
//        }};
//
//    private class MyArrayAdapter extends ArrayAdapter<String>{
//
//        private HashMap<Integer, Boolean> myChecked = new HashMap<Integer, Boolean>();
//
//        public MyArrayAdapter(Context context, int resource,
//                              int textViewResourceId, List<String> objects) {
//            super(context, resource, textViewResourceId, objects);
//
//            for(int i = 0; i < objects.size(); i++){
//                myChecked.put(i, false);
//            }
//        }
//
//        public void toggleChecked(int position){
//            if(myChecked.get(position)){
//                myChecked.put(position, false);
//            }else{
//                myChecked.put(position, true);
//            }
//
//            notifyDataSetChanged();
//        }
//
//        public List<Integer> getCheckedItemPositions(){
//            List<Integer> checkedItemPositions = new ArrayList<Integer>();
//
//            for(int i = 0; i < myChecked.size(); i++){
//                if (myChecked.get(i)){
//                    (checkedItemPositions).add(i);
//                }
//            }
//
//            return checkedItemPositions;
//        }
//
//        public List<String> getCheckedItems(){
//            List<String> checkedItems = new ArrayList<String>();
//
//            for(int i = 0; i < myChecked.size(); i++){
//                if (myChecked.get(i)){
//                    (checkedItems).add(dayOfWeekList.get(i));
//                }
//            }
//
//            return checkedItems;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View row = convertView;
//
//            if(row==null){
//                LayoutInflater inflater=getLayoutInflater();
//                row=inflater.inflate(R.layout.row, parent, false);
//            }
//
//            CheckedTextView checkedTextView = (CheckedTextView)row.findViewById(R.id.text1);
//            checkedTextView.setText(dayOfWeekList.get(position));
//
//            Boolean checked = myChecked.get(position);
//            if (checked != null) {
//                checkedTextView.setChecked(checked);
//            }
//
//            return row;
//        }
//
//    }
//
//}