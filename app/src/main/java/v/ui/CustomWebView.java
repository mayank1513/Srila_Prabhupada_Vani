package v.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import v.k;

public class CustomWebView extends WebView implements View.OnTouchListener{
    private GestureDetector gestureDetector;

    @Override
    public void goBack() {
        k.err = false;
        super.goBack();
    }

    public CustomWebView(Context context) {
        super(context);
    }
    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!this.canScrollVertically(0)){
//            if(motionEvent.getAction())
        }
        return false;
    }
}