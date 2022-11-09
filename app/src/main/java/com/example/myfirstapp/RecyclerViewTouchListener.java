package com.example.myfirstapp;

import com.example.myfirstapp.MyRecyclerViewAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of touch listener used by recyclerview to select blobs for removal. Intercepts
 * motionEvents to track finger movement and blob selection.
 */
public class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener, GestureDetector.OnGestureListener {

    private int mSelectColour;
    private int mSelectLen;
    private List<View> mSelectList = new ArrayList<>();
    private View mHeadBlob;
    private int mHeadBlobPos;
    private View mSecondBlob;
    GestureDetector mGestureDetector;

    /**
     * RecyclerView touch listener constructor that sets a gesture detector
     *
     * @param context context of current application state
     * @param recyclerView gameboard object
     */
    public RecyclerViewTouchListener(Context context, RecyclerView recyclerView) {
        mGestureDetector = new GestureDetector(context, this);
    }



    /**
     * Get action from motionEvent and starts putting together a list of legal selected blobs based
     * on finger coordinates during a continuous touch event in the order in which they were selected.
     * On release, it removes selected blobs and calls removeBlobs and refillBoard to repopulate
     *
     * @param rv RecyclerView gameboard object
     * @param event inputted touch event.
     * @return whether or not to intercept the event and pass it to onTouchEvent
     */
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {

        //If multiple fingers touch, don't handle it
        if (event.getPointerCount() > 1) return false;

        View v;
        //get finger movements on screen
        int action = event.getAction();

        //If finger is lifted, reset selectList and accompanying variables
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
            if(mSelectList.size() > 1) {
                removeBlobs(mSelectList);
                refillBoard();
            }
            mSelectLen = 0;
            mSelectList.clear();
            mHeadBlob = null;
            mSecondBlob = null;

            Log.i("tag","CLEARING SELECTLIST");
        }

        //When a finger is placed on a blob, select that colour
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            v = rv.findChildViewUnder(event.getX(), event.getY());
            if (v != null) {
                mSelectColour = v.getId();
                mSelectList.add(v);
                mHeadBlob = v;
                mHeadBlobPos = getBlobPosition(rv,v);
                mSelectLen = 1;
                printSelectList(rv);
                //Log.i("tag", "Selected colour is " + v.getId() + ", at position " + getBlobPosition(rv, v));
                return false;
            }
        }

        //Finger has started moving
        if (action == MotionEvent.ACTION_MOVE) {
            v = rv.findChildViewUnder(event.getX(),event.getY());
            //If v is a blob
            if (v != null) {
                //If it's a new blob
                if (v != mHeadBlob) {
                    //if it's the second to front blob, and we are going back in selection
                    if (v == mSecondBlob) {
                        mSelectList.remove(mSelectLen - 1);
                        mHeadBlob = mSecondBlob;
                        //if we are going back to just 1 blob, there is no second to front blob
                        if (mSelectLen == 2) {
                            mSecondBlob = null;
                        }
                        else {
                            mSecondBlob = mSelectList.get(mSelectLen - 3 );
                        }
                        mSelectLen --;
                        mHeadBlobPos = getBlobPosition(rv,mHeadBlob);
                        printSelectList(rv);
                        return false;
                    }

                    //If it's a new blob, check if we can jump, then try to jump
                    else {
                        if (!mSelectList.contains(v)) {
                            int vID = v.getId();
                            int vPos = getBlobPosition(rv,v);
                            if (vID == mSelectColour && isAdjacentToHead(v,vID,vPos)) {
                                mSelectLen ++;
                                mSelectList.add(v);
                                // markSelected(prev, selectList);
                                mSecondBlob = mHeadBlob;
                                mHeadBlob = v;
                                mHeadBlobPos = vPos;
                                Log.i("tag","headBlob is " + getBlobPosition(rv,mHeadBlob) + ", secondBlob is " + getBlobPosition(rv,mSecondBlob));
                                printSelectList(rv);
                                return false;
                            }
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }

    //handle intercepted touch events if onInterceptTouchEvent passes to it

    /**
     * passed to by onInterceptTouchEvent if it returns true
     * TODO: this function might be useful to implement in the future
     * @param recyclerView RecyclerView gameboard object
     * @param event inputted touch event
     */
    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {

    }
    //what to do when child requests touch event be disallowed

    /**
     * Called when a child requests disallowance of a touch event
     * @param willIntercept
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean willIntercept) {
        //Do nothing
    }

    /**
     * Helper function used by onInterceptTouchEvent to figure out whether a blob, v is adjacent to the
     * last legally selected blob
     *
     * @param v blob item we are checking
     * @param ID v's ID (colour)
     * @param pos v's position in the RecyclerView
     * @return true if v is adjacent to the lead blob. false otherwise
     */
    public boolean isAdjacentToHead(View v,int ID, int pos) {
            //blob is on the right edge and can't jump right
            if ((pos + 1) % 8 == 0) {
                if (isAbove(pos) || isbelow(pos) || isToLeft(pos)) return true;
            }
            //blob is on the left edge and can't jump left
            if ((pos + 1) % 8 == 0) {
                if (isAbove(pos) || isbelow(pos) || isToRight(pos)) return true;
            }
            //blob is somewhere else
            if (isAbove(pos) || isbelow(pos) || isToLeft(pos) || isToRight(pos)) return true;

        return false;
    }


    public boolean isAbove(int pos) {
        if (pos == mHeadBlobPos - 8) return true;
        return false;
    }

    public boolean isbelow(int pos) {
        if (pos == mHeadBlobPos + 8) return true;
        return false;
    }

    public boolean isToRight (int pos) {
        if (pos == mHeadBlobPos + 1) return true;
        return false;
    }

    public boolean isToLeft (int pos) {
        if (pos == mHeadBlobPos - 1) return true;
        return false;
    }

    // TODO: these functions may be useful if we want to add fucntionality for additional gestures in the future
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void removeBlobs(List<View> selectList) {};
    public void refillBoard() {};


    //convenience function to get rv child position given rv and child
    public int getBlobPosition(RecyclerView rv,View v) {
        return rv.getChildAdapterPosition(v);
    }

    public void printSelectList(RecyclerView rv) {
        Log.i("tag","SelectList is now: ");
        for (int i = 0; i < this.mSelectList.size(); i++) {
            Log.i("tag"," " + getBlobPosition(rv,mSelectList.get(i)));
        }
    }

}
