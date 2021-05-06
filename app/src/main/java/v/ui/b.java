package v.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.util.Date;

import v.R;

import static v.k.dp;

public class b extends RelativeLayout implements View.OnTouchListener {
    View musicControls, ui_bottom;
    public int duration = 250, h, uiBottomH, musicControlH;
    float downRawY;
    static float CLICK_DRAG_TOLERANCE = 10;
    long tDown;
    final public static String featureId = "bottom_drawer";

    public b(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    ValueAnimator featureDisplayAnimator;
    ValueAnimator collapseAnimator = new ValueAnimator();

    public void init(Context context) {
        callBacks = (CallBacks) context;
        addView(inflate(getContext(), R.layout.b, null));
        setOnTouchListener(this);
        musicControls = this.findViewById(R.id.musicControls);
        ui_bottom = this.findViewById(R.id.ui_bottom);
        CLICK_DRAG_TOLERANCE = 5 * dp;
        uiBottomH = getResources().getDimensionPixelSize(R.dimen.bottomDrawerHeight);
        musicControlH = getResources().getDimensionPixelSize(R.dimen.musicControlHeight);
        postDelayed(() -> {
            collapseAnimator.setFloatValues(0, 1);
            collapseAnimator.setDuration(8 * duration);
            collapseAnimator.addUpdateListener(valueAnimator -> {
                float f = 1 - valueAnimator.getAnimatedFraction();
                float f1 = f > 7 / 11f ? (11 * f - 3) / 4 : 1;
                final float scale = (float) (Math.pow(f, f1 * f1 * 12 / 4) * Math.pow(Math.sin(7 * Math.PI * f / 2), 2));
                setParams(getLayoutParams(), musicControlH + scale * uiBottomH);
            });
            collapseAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (((Activity) context).findViewById(R.id.fullscreen_player).getVisibility() == VISIBLE)
                        callBacks.showFullScreenPlayer();
                }
            });
        }, 800);
    }
//
//    public void showFeatureTip() {
//        featureDisplayAnimator = new ValueAnimator();
//        featureDisplayAnimator.setFloatValues(0, 1);
//        featureDisplayAnimator.addUpdateListener(valueAnimator -> {
//            float f = 1 - valueAnimator.getAnimatedFraction();
//            f = f < .5 ? 2 * f : 0;
//            final float scale = (float) (f * f * f * Math.pow(Math.sin(5 * Math.PI * f), 2));
//            setParams(getLayoutParams(), musicControlH + scale * musicControlH);
//        });
//        featureDisplayAnimator.setDuration(40 * duration);
//        featureDisplayAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        featureDisplayAnimator.start();
////        postDelayed(() -> callBacks.display(getContext().getString(R.string.swipe_up_to_expand), 0, (v1) -> callBacks.hideDisplay(), getContext().getString(R.string.ok)), 20 * duration);
//    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (this.findViewById(R.id.blurredArte).getAlpha() == 0)
            return false;
        ViewGroup.LayoutParams params = getLayoutParams();
        if (action == MotionEvent.ACTION_DOWN) {
            downRawY = motionEvent.getRawY();
            tDown = (new Date()).getTime();
            h = params.height + 20 * dp;
            setParams(params, h);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float hh = h + downRawY - motionEvent.getRawY();
            setParams(params, hh);
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            float upRawY = motionEvent.getRawY();
            if (((new Date()).getTime() - tDown > 300 && getMeasuredHeight() <= musicControlH + 20 * dp) || upRawY - downRawY > CLICK_DRAG_TOLERANCE) {
                collapse();
            } else if (downRawY - upRawY > CLICK_DRAG_TOLERANCE) {
                expand();
            } else {
                if (this.findViewById(R.id.blurredArte).getAlpha() != 0)
                    callBacks.showFullScreenPlayer();
            }
            return true;
        }
        return false;
    }

    private void setParams(ViewGroup.LayoutParams params, float hh) {
        hh = hh < uiBottomH ? hh > musicControlH ? hh : musicControlH : uiBottomH;
        params.height = (int) hh;
        setLayoutParams(params);
        ui_bottom.animate().alpha((hh - musicControlH) / (uiBottomH - musicControlH)).setDuration(0).start();
        musicControls.animate().alpha((uiBottomH - hh) / (uiBottomH - musicControlH)).setDuration(0).start();
    }

    public void collapse() {
        musicControls.findViewById(R.id.playPause1).setVisibility(VISIBLE);
        if (getMeasuredHeight() < .98 * uiBottomH)
            setArteAnimation(musicControlH);
        else
            collapseAnimator.start();
        if (findViewById(R.id.blurredArte).getAlpha() == 0) {
            findViewById(R.id.blurredArte).animate().alpha(1).start();
            findViewById(R.id.blurredArte_).animate().alpha(1).start();
            findViewById(R.id.blurredArte).setVisibility(VISIBLE);
            findViewById(R.id.blurredArte_).setVisibility(VISIBLE);
        }
    }

    public void expand() {
        setArteAnimation(uiBottomH);
        getHandler().postDelayed(() -> musicControls.findViewById(R.id.playPause1).setVisibility(GONE), duration);
        if (featureDisplayAnimator != null) {
            featureDisplayAnimator.cancel();
//            setFeatureDone(featureId);
//            callBacks.display("Well done! You can swipe up to expand controls.", 2000, (v) -> callBacks.hideDisplay(), getContext().getString(R.string.ok));
        }
    }

    CallBacks callBacks;

    public interface CallBacks {
        void showFullScreenPlayer();
//
//        void display(String s, int duration, OnClickListener clickListener, String btnText);
//
//        void hideDisplay();
    }

    private void setArteAnimation(int h) {
        ValueAnimator animator = new ValueAnimator();
        animator.setFloatValues(getMeasuredHeight(), h);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        final ViewGroup.LayoutParams params = getLayoutParams();
        animator.addUpdateListener(animation -> {
            float hh = (float) animation.getAnimatedValue();
            setParams(params, hh);
        });
        animator.start();
    }
}