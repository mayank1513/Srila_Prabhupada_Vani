package v.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import v.R;
import v.k;

import static v.k.animDuration;
import static v.k.dp10;
import static v.k.size;

public class d extends RelativeLayout implements View.OnTouchListener{
    public static final float CLICK_DRAG_TOLERANCE = 10;
    private float downX;
    private float downY;
    private float dX;
    public boolean isM_Adapter = false;
    public View bg;
    float alphaMax = 1;
    int scrollDirection = 0, direction = 1, drawerWidth = 0;
    final public static int Horizontal = 1, Vertical = 2;

    public d(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        if(getGravity()==Gravity.RIGHT) direction = -1;
        if(direction==-1) {
            addView(inflate(getContext(), R.layout.rd, null));
            drawerWidth = getResources().getDimensionPixelSize(R.dimen.r_drawer_width);
        } else {
            addView(inflate(getContext(), R.layout.d, null));
            drawerWidth = k.dWidth;
        }
        setOnTouchListener(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        int action = ev.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            scrollDirection = 0;
            downX = ev.getRawX();
            downY = ev.getRawY();
            dX = this.getX() - downX;
            peep();
        } else if(action == MotionEvent.ACTION_MOVE){
            if(scrollDirection == 0) {
                scrollDirection = Math.abs(ev.getRawX() - downX) > CLICK_DRAG_TOLERANCE ? Horizontal
                        : Math.abs(ev.getRawY() - downY) > CLICK_DRAG_TOLERANCE ? Vertical : 0;
            } else if(scrollDirection == Horizontal){
                return onTouch(this, ev);
            }
        } else if(action == MotionEvent.ACTION_UP && scrollDirection != 0 && scrollDirection != Vertical)
            return onTouch(this, ev);
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        int action = ev.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            downX = ev.getRawX();
            dX = this.getX() - downX;
            peep();
        } else if(action == MotionEvent.ACTION_MOVE){
            float newX = ev.getRawX() + dX;
            newX = direction*Math.min(0, direction*newX);
            view.animate().x(newX).setDuration(0).start();
            bg.animate().alpha(alphaMax + direction*newX/drawerWidth).setDuration(0).start();
        } else if(action == MotionEvent.ACTION_UP) {
            float upRawX = ev.getRawX();
            if(direction==1? downX >drawerWidth: downX < size.x - drawerWidth) collapse();
            else if (direction*(upRawX- downX) > 10*CLICK_DRAG_TOLERANCE) expand();
            else if(direction*(downX -upRawX) > 10*CLICK_DRAG_TOLERANCE) collapse();
            else if(Math.abs(this.getX()) > drawerWidth) collapse();
            else expand();
        }
        return true;
    }

    public void collapse(){
        bg.animate().alpha(0).setDuration(animDuration).start();
        if(direction == -1)
            this.animate().x(size.x - 2*dp10).setDuration(animDuration).start();
        else {
            this.animate().x(-size.x + 2*dp10).setDuration(animDuration).start();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isM_Adapter) findViewById(R.id.name).performClick();
                }
            }, animDuration);
        }
    }

    public void disableDrawer(){
        this.animate().x(-this.getWidth()).setDuration(0).start();
        bg.animate().alpha(0).setDuration(0).start();
    }

    public void peep(){
        if(this.getX()!=0) {
            float newX = direction*(-drawerWidth + 2 * dp10);
            this.animate().x(newX).setDuration(animDuration).start();
            bg.animate().alpha(alphaMax + direction*newX/drawerWidth).setDuration(animDuration).start();
            dX += direction*(size.x - drawerWidth + 2 * dp10);
        }
    }

    public void expand(){
        this.animate().x(0).setDuration(animDuration).start();
        bg.animate().alpha(alphaMax).setDuration(animDuration).start();
    }

    public boolean isCollapsed() {
        return this.getX()!=0;
    }
}