package com.bill.mygitosc.common;

/**
 * Created by liaobb on 2015/7/31.
 */
public class FootViewState {
    public static final int STATE_ERROR = -1;
    public static final int STATE_EMPTY = 0;
    public static final int STATE_MORE = 1;
    public static final int STATE_FULL = 2;

    private int state;

    public static String showFootHint(int state) {
        String hint;
        switch (state) {
            case STATE_ERROR:
                hint = "STATE_ERROR";
                break;
            case STATE_EMPTY:
                hint = "STATE_EMPTY";
                break;
            case STATE_MORE:
                hint = "STATE_MORE";
                break;
            case STATE_FULL:
                hint = "STATE_FULL";
                break;
            default:
                hint = "unknow error";
                break;
        }
        return hint;
    }

    public FootViewState(int result) {
        if (result == -1) {
            state = STATE_ERROR;
        } else if (result < AppContext.PAGE_SIZE) {
            state = STATE_FULL;
        } else if (result == 0) {
            state = STATE_EMPTY;
        } else {
            state = STATE_MORE;
        }
    }
}
