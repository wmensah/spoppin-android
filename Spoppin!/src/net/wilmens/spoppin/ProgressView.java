package net.wilmens.spoppin;

import net.wilmens.spoppin.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ProgressView extends FrameLayout{

	private String progressLabel;
	Context ctx;
	
	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
			    R.styleable.Progressview, 0, 0);
		progressLabel = a.getString(R.styleable.Progressview_progressLabel);
		ctx = context;
		initView(context);
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
	}

	private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_view, null);
        TextView lbl = (TextView)view.findViewById(R.id.lblProgressInfo);
        if (lbl != null && progressLabel != null)
        	lbl.setText(progressLabel);
        this.removeAllViews();

        addView(view);
    }
	
	public String getLabelText(){
	    return progressLabel;
	}
	
	public void setLabelText(String newLabel){
	    //update the instance variable
		progressLabel=newLabel;
	    //redraw the view
	    invalidate();
	    requestLayout();
		initView(ctx);
	}

}
