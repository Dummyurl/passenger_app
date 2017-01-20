package com.passengerapp.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.model.MenuItemData;

import java.util.List;

/**
 * Created by adventis on 1/16/16.
 */
public class MenuAdapter extends BaseAdapter {

    private Context mContext;
    private List<MenuItemData> mItems;
    public MenuAdapter(Context context,List<MenuItemData> items)
    {
        super();
        mContext=context;
        mItems=items;

    }

    public int getCount()
    {
        // return the number of records in cursor
        return mItems.size();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent)
    {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.main_menu_item, null);

        // move the cursor to required position
        MenuItemData item = mItems.get(position);

        // get the reference of textViews
        TextView title =(TextView)view.findViewById(R.id.menuAdapterTitle);
        title.setText(item.getTitle());

        view.setTag(item.getId());

        return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}
