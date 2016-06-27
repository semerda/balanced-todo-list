package com.codepath.simpletodo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.simpletodo.R;
import com.codepath.simpletodo.models.NoteItem;
import com.codepath.simpletodo.utils.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SemerdaApp";

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    DBHelper itemsDB;
    // Map position in the list to NoteItem
    HashMap<Integer, com.codepath.simpletodo.models.NoteItem> hmItemsList;

    private final int REQUEST_CODE = 20;
    int itemToBeRemoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_yin_yang);

        // Create a List of Items and Display in ListView
        lvItems = (ListView)findViewById(R.id.lvItems);

        itemsDB = new com.codepath.simpletodo.utils.DBHelper(this);
        readItems();

        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);

                NoteItem note = hmItemsList.get(position);
                if (note.isUrgent == 1) {
                    text1.setTextColor(Color.parseColor("#FF0000")); // red
                } else if (note.isImportant == 1) {
                    text1.setTextColor(Color.parseColor("#FF8000")); // orange
                } else {
                    text1.setTextColor(Color.parseColor("#000000")); // black (default)
                }
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                String formattedDate = "N/A";
                Date dd = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
                try {
                    dd = sdf.parse(note.dueDate.toString());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dd);
                    formattedDate = calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
                } catch(ParseException ex) {
                    Log.w(TAG, ex);
                }
                text2.setText("DUE: " + formattedDate +
                        " ❭ ADVISE: " + getEisenhowerAdvise(note.isImportant, note.isUrgent));
                text2.setTextSize(10);

                // Dump stuff to LogCat
                Log.w(TAG, text1.toString());
                Log.w(TAG, text2.toString());

                return view;
            }
        };
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    private String getEisenhowerAdvise(int isImportant, int isUrgent) {
        // Using Dwight Eisenhower’s urgency-importance decision matrix to color code
        // Ref: http://www.gsdfaster.com/blog/how-to/dwight-eisenhowers-urgency-importance-decision-matrix/
        String msg = "";
        if (isImportant == 1 && isUrgent == 0) {
            msg = "Decide When You Will Do It";
        } else if (isImportant == 1 && isUrgent == 1) {
            msg = "Do It Immediately";
        } else if (isImportant == 0 && isUrgent == 0) {
            msg = "Do It Later";
        } else if (isImportant == 0 && isUrgent == 1) {
            msg = "Delegate To Somebody Else";
        }

        return msg;
    }

    public void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View view, int pos, long id){

                        itemToBeRemoved = pos;
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setMessage("Are you sure,You want to delete \"" + items.get(pos) + "\"?");
                        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                removeItem(itemToBeRemoved);
                            }
                        });

                        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                itemToBeRemoved = 0;
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        return true;
                    }
                });

        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View view, int pos, long id){
                        launchEditView(pos);
                    }
                });
    }

    public void launchEditView(int pos) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        NoteItem note = hmItemsList.get(pos);
        i.putExtra("detail", note.detail);
        i.putExtra("pos", pos);
        i.putExtra("isImportant", note.isImportant);
        i.putExtra("isUrgent", note.isUrgent);
        i.putExtra("dueDate", note.dueDate);

        // Launches the Edit Controller/Activity
        startActivityForResult(i, REQUEST_CODE);
    }

    // Result from Edit item activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String newDetail = data.getExtras().getString("detail");
            int newIsImportant = data.getExtras().getInt("isImportant", 0);
            int newIsUrgent = data.getExtras().getInt("isUrgent", 0);
            int pos = data.getExtras().getInt("pos", 0);
            int newDueDate = data.getExtras().getInt("dueDate", 0);
            items.set(pos, newDetail);

            NoteItem note = hmItemsList.get(pos);
            note.isImportant = newIsImportant;
            note.isUrgent = newIsUrgent;
            note.dueDate = newDueDate;
            itemsDB.updateItem(note.id, newDetail, newIsImportant, newIsUrgent, newDueDate);
            itemsAdapter.notifyDataSetChanged();
        }
    }

    // *******
    // DB CRUD

    private void readItems() {
        hmItemsList = new HashMap<>();
        ArrayList<com.codepath.simpletodo.models.NoteItem> todos = itemsDB.getAllToDos();
        Integer p = 0;
        items = new ArrayList<>();
        for (com.codepath.simpletodo.models.NoteItem todo: todos) {
            hmItemsList.put(p, todo);
            items.add(todo.detail);
            p++;
        }
    }

    public void onAddItem(View v){
        EditText etNewItem = (EditText)findViewById(R.id.etNewItem);
        String itemDetail = etNewItem.getText().toString();
        long newItemId = itemsDB.insertItem(itemDetail, 0, 0, 0);
        hmItemsList.put(itemsAdapter.getCount(), new NoteItem(newItemId, itemDetail, 0, 0, 0));
        itemsAdapter.add(itemDetail);
        etNewItem.setText("");
    }

    private void removeItem(int pos){
        items.remove(pos);
        itemsAdapter.notifyDataSetChanged();
        NoteItem note = hmItemsList.get(pos);
        hmItemsList.remove(pos);

        itemsDB.deleteItem(note.id);
    }
}
