package com.andretietz.android.controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.content.res.AppCompatResources;


/**
 * This view can be used for action inputs. it has 4 Button, as it is usual
 * for common game controllers such as PS,XBox and similar ones
 */
public class ActionView extends InputView {

    private static final int BUTTON_COUNT = 4;
    private Drawable[][] drawables = new Drawable[BUTTON_COUNT][2];
    private int[][] resources = new int[BUTTON_COUNT][2];

    public ActionView(Context context) {
        super(context);
    }

    public ActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ActionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        // read the attributes from xml
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ActionView, 0, R.style.default_actionview);
        super.init(context, attrs, R.style.default_actionview);
        try {
            resources[0][0] = a.getResourceId(R.styleable.ActionView_button1_enabled, R.drawable.action_usual_blue);
//            resources[0][1] = a.getResourceId(R.styleable.ActionView_button1_pressed, R.drawable.action_pressed_blue);
            resources[1][0] = a.getResourceId(R.styleable.ActionView_button2_enabled, R.drawable.action_usual_yellow);
            resources[1][1] = a.getResourceId(R.styleable.ActionView_button2_pressed, R.drawable.action_pressed_yellow);
            resources[2][0] = a.getResourceId(R.styleable.ActionView_button3_enabled, R.drawable.action_usual_green);
            resources[2][1] = a.getResourceId(R.styleable.ActionView_button3_pressed, R.drawable.action_pressed_green);
            resources[3][0] = a.getResourceId(R.styleable.ActionView_button4_enabled, R.drawable.action_usual_red);
            resources[3][1] = a.getResourceId(R.styleable.ActionView_button4_pressed, R.drawable.action_pressed_red);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected Drawable getStateDrawable(int buttonIndex, ButtonState state) {
        if (null == drawables[buttonIndex][state.ordinal()]) {
            drawables[buttonIndex][state.ordinal()] = AppCompatResources
                    .getDrawable(getContext(), resources[buttonIndex][state.ordinal()]);
        }
        return drawables[buttonIndex][state.ordinal()];
    }

    @Override
    protected int getButtonCount() {
        return BUTTON_COUNT;
    }
}
