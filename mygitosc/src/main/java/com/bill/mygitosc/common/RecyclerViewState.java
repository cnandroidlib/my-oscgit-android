package com.bill.mygitosc.common;

/**
 * Created by liaobb on 2015/7/31.
 */
public class RecyclerViewState {
    public static final int STATE_ERROR = -1;
    public static final int STATE_EMPTY = 0;
    public static final int STATE_MORE = 1;
    public static final int STATE_FULL = 2;

    private int state;

    public RecyclerViewState(int result) {
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
