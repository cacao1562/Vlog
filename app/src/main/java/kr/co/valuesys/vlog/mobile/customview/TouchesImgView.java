package kr.co.valuesys.vlog.mobile.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import kr.co.valuesys.vlog.mobile.application.MobileApplication;
import kr.co.valuesys.vlog.mobile.common.LogUtil;

public class TouchesImgView extends AppCompatImageView {
    public TouchesImgView(Context context) {
        super(context);
    }

    public TouchesImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchesImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float oldXvalue;
    private float oldYvalue;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        int parent_width = ((ViewGroup) this.getParent()).getWidth() - (this.getWidth());
        int parent_height = ((ViewGroup) this.getParent()).getHeight() - (this.getHeight());


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            oldXvalue = event.getX();
            oldYvalue = event.getY();
            //  Log.i("Tag1", "Action Down X" + event.getX() + "," + event.getY());
//            Log.i("Tag1", "Action Down rX " + event.getRawX() + "," + event.getRawY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            oldXvalue = event.getX();
            oldYvalue = event.getY();
            this.setX( (event.getX()) - (this.getWidth()/2));
            this.setY( (event.getY()) - (this.getHeight()/2));
            //  Log.i("Tag2", "Action Down " + me.getRawX() + "," + me.getRawY());

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            LogUtil.d("www", "getX = " + this.getX() + " getY = " + this.getY() );
            LogUtil.d("www", "parent_width = " + parent_width + " parent_height = " + parent_height );
            // 우 하단
            if (this.getX() > parent_width && this.getY() > parent_height) {
                LogUtil.d("www", " 우 하단 ");
                this.setX(parent_width);
                this.setY(parent_height);
            // 좌 하단
            } else if (this.getX() < 0 && this.getY() > parent_height) {
                LogUtil.d("www", " 좌 하단 ");
                this.setX(0) ;
                this.setY(parent_height);

            // 우 상단
            } else if (this.getX() > parent_width && this.getY() < 0) {
                LogUtil.d("www", " 우 상단  ");
                this.setX(parent_width);
                this.setY(0);
            // 좌 상단
            } else if (this.getX() < 0 && this.getY() < 0) {
                LogUtil.d("www", " 좌 상단  ");
                this.setX(0);
                this.setY(0);
            } else if (this.getX() < 0) {
                this.setX(0);
            } else if (this.getY() < 0) {
                this.setY(0);
            } else if (this.getX() > parent_width) {
                this.setX(parent_width);
            } else if (this.getY() > parent_height) {
                this.setY(parent_height);
            }

        }
        return true;
    }

}
