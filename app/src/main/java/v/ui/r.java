package v.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import v.R;

import static v.k.size;

public class r extends FrameLayout implements View.OnTouchListener{
    public int top_margine;
    public r(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        addView(inflate(getContext(), R.layout.ur,null));
        setOnTouchListener(this);
        dragHandle = this.findViewById(R.id.drag_handle);
        minHeight = 3*getResources().getDimensionPixelSize(R.dimen.musicControlHeight);
    }

    private View dragHandle;
    float downRawY, height;
    int minHeight;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            downRawY = motionEvent.getRawY();
            height = this.getMeasuredHeight();
        } else if(downRawY<size.y - height + 2*dragHandle.getHeight()) {
            if (action == MotionEvent.ACTION_MOVE){
            View parent = (View) this.getParent();
                float rawY = motionEvent.getRawY();
//                this.height = this.height + rawY - downRawY;
                MarginLayoutParams p = (MarginLayoutParams) this.getLayoutParams();
                p.topMargin = (int)rawY;
                if(p.topMargin!=0)
                    top_margine = p.topMargin;
                this.setLayoutParams(p);
            } else if (action == MotionEvent.ACTION_UP){
                top_margine = top_margine<2*minHeight/3?2*minHeight/3:top_margine>size.y-minHeight?size.y - minHeight:top_margine;
                MarginLayoutParams p = (MarginLayoutParams) this.getLayoutParams();
                p.topMargin = top_margine;
                this.setLayoutParams(p);
            }
        }
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent){
        onTouch(this, motionEvent);
        return false;
    }
}
