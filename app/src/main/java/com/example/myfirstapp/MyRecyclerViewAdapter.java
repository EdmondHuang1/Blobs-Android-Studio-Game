package com.example.myfirstapp;

import com.example.myfirstapp.RecyclerViewTouchListener;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides binding from resources to RecyclerView items such as image binding, data binding to ViewHolder,
 * and general RecyclerView construction
 *
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>  {
    //colour ID's for blobs
    private static final int BLUE_ID = 2131099734;
    private static final int GREEN_ID = 2131099735;
    private static final int ORANGE_ID = 2131099736;
    private static final int RED_ID = 2131099737;
    private static final int YELLOW_ID = 2131099738;


    private List<Integer> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    /**
     * constructs the adapter
     * @param context application's active state context
     * @param data list of RecyclerView items-to-be
     */
    MyRecyclerViewAdapter(Context context, List<Integer> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
    }


    /**
     * Inflates each RecyclerView item and sets its metadata before returning it
     * @param viewGroup RecyclerView gameboard object
     * @param i Item position
     * @return returns the newly created viewholder of the item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_layout_recyclerview, viewGroup, false);
        int w = viewGroup.getMeasuredWidth() / 8;
        view.setMinimumWidth(w);
        view.setMinimumHeight(w);
        return new ViewHolder(view);

    }

    /**
     * Binds image and ID to each RecyclerView Item
     * @param viewHolder Item's ViewHolder
     * @param i item's position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemView.setId(mData.get(i));
        viewHolder.myImageView.setImageResource(mData.get(i));
    }

    /**
     * Gets the number of items in the RecyclerView
     * @return returns number of items
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        ImageView myImageView;
        ViewHolder(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.imageID);
        }
    }

    // convenience method for getting data at click position
    int getItem(int pos) {
        return mData.get(pos);
    }
}

