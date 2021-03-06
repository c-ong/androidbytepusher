package org.starlo.bytepusher;

import coder36.BytePusherIODriver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class AndroidBytePusherIODriverImpl implements BytePusherIODriver, OnTouchListener {

	private static final int SCREEN_DIMENSION = 256;
	private SurfaceHolder mHolder;
	private Bitmap mBitmap;
	private Bitmap mScaledBitmap;
	private int mScreenWidth;
	private short mKeyState;
	
	AndroidBytePusherIODriverImpl(SurfaceView surfaceView){
		Resources r = surfaceView.getResources();
		mHolder = surfaceView.getHolder();
		mKeyState = 0x0000;
		mBitmap = Bitmap.createBitmap(SCREEN_DIMENSION, SCREEN_DIMENSION, Config.ARGB_8888);
		mScreenWidth = 
			Float.valueOf(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getConfiguration().screenWidthDp, r.getDisplayMetrics())).intValue();
	}
	
	@Override
	public short getKeyPress() {
		return mKeyState;
	}

	@Override
	public void renderAudioFrame(char[] data) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void renderDisplayFrame(char[] data) {
		int[] colors = new int[data.length];
		for (int y=0; y < SCREEN_DIMENSION; y++) {
			for (int x=0; x < SCREEN_DIMENSION; x++) {
				int color = BytePusherColorResolver.getRGBAtCoordinate(x, y, data);
				colors[(y*SCREEN_DIMENSION)+x] = 0xFF << 24|color;
			}
		}
		if(mScaledBitmap != null)
			mScaledBitmap.recycle();
		Canvas canvas = mHolder.lockCanvas();
		if(canvas != null){
			canvas.drawColor(Color.BLACK);
			mBitmap.setPixels(colors, 0, SCREEN_DIMENSION, 0, 0, SCREEN_DIMENSION, SCREEN_DIMENSION);
			mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, mScreenWidth, mScreenWidth, false);
			canvas.drawBitmap(mScaledBitmap, 0, 0, null);
			mHolder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int keyFlag = (1<<Integer.valueOf(((TextView)view).getText().toString(), 16));
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				mKeyState = (short)(mKeyState|keyFlag);	
				break;
			case MotionEvent.ACTION_UP:
				mKeyState = (short)(mKeyState^keyFlag);
				break;
		}
		return false;
	}
}
