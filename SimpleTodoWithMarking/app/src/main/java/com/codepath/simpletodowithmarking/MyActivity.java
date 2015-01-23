package com.codepath.simpletodowithmarking;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MyActivity extends Activity {

    ArrayList<TodoItem> items;
    MyCustomToDoAdapter itemsAdapter = null;
    ListView lvItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        itemsAdapter = new MyCustomToDoAdapter(this, R.layout.checked_list, items);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();
    }

    public void setupListViewListener(){
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return false;
                    }
                }
        );
    }

    public void onAddItem(View v){
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        TodoItem todoItem = new TodoItem(itemText, false);
        itemsAdapter.add(todoItem);
        etNewItem.setText("");
        writeItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Read item name and checked status from persistent storage */
    public void readItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        File todoCheckFile = new File(filesDir, "todo_check.txt");
        ArrayList<String> itemsTextList;
        ArrayList<String> itemsCheckList;
        items = new ArrayList<TodoItem>();

        try{
            itemsTextList = new ArrayList<String>(FileUtils.readLines(todoFile));
            itemsCheckList = new ArrayList<String>(FileUtils.readLines(todoCheckFile));

            for (String s : itemsTextList) {
                TodoItem todoItem = new TodoItem("new", false);
                todoItem.setItemName(s.toString());
                items.add(todoItem);
            }
            int idx=0;
            for (String s : itemsCheckList) {
                TodoItem todoItem = items.get(idx);
                idx++;
                if (s.toString().equals("true"))
                    todoItem.setItemDone(true);
            }

        }
        catch (IOException e){
        }

    }


    /* Write item name and checked status to persistent storage */
    public void writeItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        File todoCheckFile = new File(filesDir, "todo_check.txt");
        ArrayList<String> itemsTextList = new ArrayList<String>();
        ArrayList<String> itemsCheckList= new ArrayList<String>();

        for (TodoItem i : items)
            itemsTextList.add(new String(i.getItemName()));

        for (TodoItem i : items)
            if (i.isItemDone())
                itemsCheckList.add("true");
            else
                itemsCheckList.add("false");

        try{
            FileUtils.writeLines(todoFile, itemsTextList);
            FileUtils.writeLines(todoCheckFile, itemsCheckList);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private class MyCustomToDoAdapter extends ArrayAdapter<TodoItem> {

        public MyCustomToDoAdapter (Context context, int textViewResourceId,
                                    ArrayList<TodoItem> itemList) {
            super(context, textViewResourceId, itemList);
            items = new ArrayList<TodoItem>();
            items.addAll(itemList);
        }

        @Override
        public void add(TodoItem obj){
            super.add(obj);
            items.add(obj);
        }

        public class ViewHolder {
            CheckBox doneCheck;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null){
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.checked_list, null);

                holder = new ViewHolder();
                holder.doneCheck = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);

                holder.doneCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        TodoItem todoItem = (TodoItem) cb.getTag();
                        todoItem.setItemDone(cb.isChecked());
                        writeItems();
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (items.size()>position) {
                TodoItem todoItem = items.get(position);
                holder.doneCheck.setVisibility(View.VISIBLE);
                holder.doneCheck.setText(todoItem.getItemName());
                holder.doneCheck.setChecked(todoItem.isItemDone());
                holder.doneCheck.setTag(todoItem);
            }
            else{
                holder.doneCheck.setVisibility(View.GONE);
            }

            return convertView;
        }

    }

    public class TodoItem {
        String itemName = null;
        boolean itemDone = false;

        public TodoItem (String name, boolean itemDone){
            this.itemName = name;
            this.itemDone = itemDone;
        }

        public boolean isItemDone(){
            return itemDone;
        }
        public void setItemDone(boolean itemDone){
            this.itemDone = itemDone;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemName() {
            return itemName;
        }
    }
}
