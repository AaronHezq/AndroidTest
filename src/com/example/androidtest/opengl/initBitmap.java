package com.example.androidtest.opengl;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.androidtest.R;

public class initBitmap {
	public static Bitmap bitmap;  
    
    public static void init(Resources res){  
        bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_beer) ;  
    }  
}
