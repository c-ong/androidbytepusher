package org.starlo.bytepusher;

import coder36.BytePusherIODriver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidBytePusherIODriverImpl implements BytePusherIODriver {

	private static final int SCREEN_DIMENSION = 256;
	private SurfaceHolder mHolder;
	private Bitmap mBitmap;
	private Bitmap mScaledBitmap;
	private int mScreenWidth;
	private int mScreenHeight;
	
	AndroidBytePusherIODriverImpl(SurfaceView surfaceView){
		Resources r = surfaceView.getResources();
		mHolder = surfaceView.getHolder();
		mBitmap = Bitmap.createBitmap(SCREEN_DIMENSION, SCREEN_DIMENSION, Config.ARGB_8888);
		mScreenWidth = 
			Float.valueOf(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getConfiguration().screenWidthDp, r.getDisplayMetrics())).intValue();
		mScreenHeight = 
			Float.valueOf(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getConfiguration().screenHeightDp, r.getDisplayMetrics())).intValue();
	}
	
	@Override
	public short getKeyPress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void renderAudioFrame(char[] data) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void renderDisplayFrame(char[] data) {
		int[] colors = new int[data.length];
		int i = 0;
		for (int y=0; y < 256; y++) {
			for (int x=0; x < 256; x++) 
			{
				int datum = data[i];
				if ( datum < 216 ) {
					int blue = datum % 6;
					int green = ((datum - blue) / 6) % 6;
					int red = ((datum - blue - (6 * green)) / 36) % 6;
					colors[i] = 0xFF << 24|red*0x33 << 16|green*0x33 << 8|blue*0x33;
				}
				i++;
			}
		}
		if(mScaledBitmap != null){
			mScaledBitmap.recycle();
			System.gc();
		}
		Canvas canvas = mHolder.lockCanvas();
		if(canvas != null){
			canvas.drawColor(Color.BLACK);
			mBitmap.setPixels(colors, 0, SCREEN_DIMENSION, 0, 0, SCREEN_DIMENSION, SCREEN_DIMENSION);
			mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, mScreenWidth, mScreenWidth, false);
			canvas.drawBitmap(mScaledBitmap, 0, (mScreenHeight-mScreenWidth)/2, null);
			mHolder.unlockCanvasAndPost(canvas);
		}
	}

}