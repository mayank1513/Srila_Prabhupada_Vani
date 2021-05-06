package v.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import v.R;

import static v.k.dp10;
import static v.k.dWidth;
import static v.k.fullScreenTabs;
import static v.k.mainTabs;
import static v.k.size;

public class t extends RelativeLayout implements View.OnTouchListener{
    public static float CLICK_DRAG_TOLERANCE = 10;
    private float downRawX, downRawY, dX;
    public View container;
    int tabWidth, scrollDirection = 0;
    final public static int Horizontal = 1, Vertical = 2;

    public int tabInd = -1, maxTabs = 3, duration = 350, tContext = mainTabs;
    public Button divider;

    public t(Context context, AttributeSet attrs) {
        super(context, attrs);
        callbacks = (Callbacks) context;
        CLICK_DRAG_TOLERANCE = 2*dp10;
        addView(inflate(getContext(), R.layout.ut,null));
        setOnTouchListener(this);
        container = this.findViewById(R.id.container);
        divider = this.findViewById(R.id.divider);
        init();
    }

    public void init(){
//        https://stackoverflow.com/questions/4328838/create-a-custom-view-by-inflating-a-layout
        divider.animate().scaleX(1f/maxTabs).start();
        tabWidth = size.x/maxTabs;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent){
        divider.animate().alpha(1).setDuration(0).start();
        if(tContext == fullScreenTabs)
            divider.animate().alpha(.5f).setDuration(500).start();
        int action = motionEvent.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            callbacks.hideFullScreenMenu();
            scrollDirection = 0;
            downRawX = motionEvent.getRawX();
            downRawY = motionEvent.getRawY();
            dX = this.getX() - downRawX;
        } if(action == MotionEvent.ACTION_MOVE){
            if(scrollDirection == 0) {
                if (Math.abs(motionEvent.getRawX() - downRawX) > CLICK_DRAG_TOLERANCE) {
                    scrollDirection = Horizontal;
                } else if(Math.abs(motionEvent.getRawY() - downRawY) > CLICK_DRAG_TOLERANCE){
                    scrollDirection = Vertical;
                }
            } else if(scrollDirection == Horizontal){
                return onTouch(this, motionEvent);
            } else if(scrollDirection == Vertical){
                return false;
            }
        } if(action == MotionEvent.ACTION_UP){
            if(!(scrollDirection == 0||scrollDirection==Vertical))
                return onTouch(this, motionEvent);
        }
        return false;
    }
    float scale = 0, dividerPos = 0;
    @Override
    public boolean onTouch(View view, final MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            downRawX = motionEvent.getRawX();
            dX = this.getX() - downRawX;
            return true;
        } else if(action == MotionEvent.ACTION_MOVE){
            float newX = motionEvent.getRawX() + dX;
            scale = newX/ dWidth;
            divider.animate().translationX(dividerPos-scale*size.x/maxTabs).setDuration(0).start();
            container.animate().rotationY(90*scale).scaleX(1-Math.abs(scale)).scaleY(1-Math.abs(scale)).alpha(0.7f-Math.abs(scale))
                    .translationX(scale* dWidth).setDuration(0).start();
            return true;
        } else if(action == MotionEvent.ACTION_UP){
            final float upRawX = motionEvent.getRawX();
            if(Math.abs(upRawX-downRawX)<2*CLICK_DRAG_TOLERANCE){
                setTabButtons(tabInd);
                return true;
            }
            scale = scale<0?-.9f:.9f;
            divider.animate().translationX(dividerPos-scale*size.x/maxTabs).setDuration(duration).start();
            container.animate().rotationY(90*scale).scaleX(1-Math.abs(scale)).scaleY(1-Math.abs(scale)).alpha(0.7f-Math.abs(scale))
                    .translationX(scale* dWidth).setDuration(duration).start();
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (upRawX-downRawX > 2*CLICK_DRAG_TOLERANCE)
                        setTabButtons((maxTabs + tabInd - 1) % maxTabs);
                    else if(downRawX - upRawX > 2*CLICK_DRAG_TOLERANCE)
                        setTabButtons((tabInd +1)% maxTabs);
                    else
                        setTabButtons(tabInd);
                }
            }, duration);
            return true;
        }
        return false;
    }

    public void setTabButtons(int ind){
        if(ind == -1) ind = tabInd;
        if(ind == tabInd){
            container.animate().scaleX(1).scaleY(1).alpha(1).rotationY(0).translationX(0).setDuration(duration).start();
        } else if((maxTabs +ind-1)% maxTabs == tabInd){
            container.animate().rotationY(-300).translationX(dWidth).setDuration(0).start();
            container.animate().scaleX(1).scaleY(1).alpha(1).rotationY(-360).translationX(0).setDuration(duration).start();
        } else {
            container.animate().rotationY(300).translationX(-dWidth).setDuration(0).start();
            container.animate().scaleX(1).scaleY(1).alpha(1).rotationY(360).translationX(0).setDuration(duration).start();
        }
        tabInd = ind;
        divider.animate().translationX(dividerPos = (-size.x + tabWidth)/2 + tabInd*tabWidth).setDuration(duration).start();
        callbacks.setTab(tContext, tabInd);
    }

    Callbacks callbacks;
    public interface Callbacks{
        void setTab(int tContext, int tabInd);
        void hideFullScreenMenu();
    }
}
