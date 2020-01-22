package kr.co.valuesys.vlog.mobile.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public class TouchesView extends View {


        @Nullable
        private View someView;

        public TouchesView(Context context) {
            super(context);
        }

        public TouchesView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public TouchesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }


        public void setSomeView(View v) {
            this.someView = v;
        }


//
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (someView != null) {
                return someView.onTouchEvent(event);
            }
//            someView.setOnTouchListener(this);
            return super.onTouchEvent(event);


        }
//
        @Override
        public boolean performClick() {
            return super.performClick();
        }

}
