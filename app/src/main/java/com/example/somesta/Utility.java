package com.example.somesta;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

public class Utility {
    public static class ColumnQty {
        private int width, height, remaining;
        private DisplayMetrics displayMetrics;

        public ColumnQty(Context context, int viewId) {
            View view = View.inflate(context, viewId, null);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            width = view.getMeasuredWidth();
            height = view.getMeasuredHeight();
            displayMetrics = context.getResources().getDisplayMetrics();
        }
        public int calculateNoOfColumns() {
            int numberOfColumns = displayMetrics.widthPixels / width;
            remaining = displayMetrics.widthPixels - (numberOfColumns * width);

            if (remaining / (2 * numberOfColumns) < 15) {
                numberOfColumns--;
                remaining = displayMetrics.widthPixels - (numberOfColumns * width);
            }
            return numberOfColumns;
        }
        public int calculateSpacing() {
            int numberOfColumns = calculateNoOfColumns();
            return remaining/(10*numberOfColumns);
        }
    }
}
