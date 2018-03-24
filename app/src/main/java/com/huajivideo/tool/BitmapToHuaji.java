package com.huajivideo.tool;
import android.graphics.*;

public class BitmapToHuaji
{
	int dw,dh,w,h,texw,texh;
	Bitmap texture;
	Paint pai=new Paint();
	
	public BitmapToHuaji(Bitmap tex,int dw,int dh){//纹理图片，单位宽度，单位高度
		this.dw=dw;this.dh=dh;
		settexture(tex);
	}
	public void settexture(Bitmap tex){//设置纹理图片
		texture=tex;
		texw=tex.getWidth();texh=tex.getHeight();
	}
	
	public Bitmap toHuaji(Bitmap src){//图片滑稽化
		src=toGrayscale(src);//转灰度图
		w=src.getWidth();
		h=src.getHeight();
		//创建一张新图片，用于绘制滑稽
		Bitmap result=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		Canvas c=new Canvas(result);//创建画布
		c.drawColor(Color.WHITE);//背景色为白色
		int[] pixs=new int[w*h];//储存图片像素的数组
		src.getPixels(pixs,0,w,0,0,w,h);//获取图片所有像素
		src.recycle();//释放图片，节省内存
		for(int u=0;u<h;u+=dh)//遍历像素点
		for(int i=0;i<w;i+=dw)
		{
			//获取当前区域平均灰度
			double gray=getAverageGray(pixs,i,u,Math.min(w-i,dw),Math.min(h-u,dh),w);
			//如果不是白色区域，根据灰度计算图片绘制比例，并绘制图片
			if(gray!=0)c.drawBitmap(texture,new Rect(0,0,texw,texh),new RectF(i+dw/2-dw/2*(float)gray,u+dh/2-dh/2*(float)gray,i+dw/2+dw/2*(float)gray,u+dh/2+dh/2*(float)gray),pai);
		}
		return result;
	} 
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		//利用颜色矩阵将图片灰度化
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}
	public static double getAverageGray(int[] pixs,int x,int y,int w,int h,int len){
		//获取平均灰度
		double gray=0;
		for(int u=y;u<y+h;u++)
		for(int i=x;i<x+w;i++)
		{
			gray+=(pixs[u*len+i]&0xFF);
		}
		return 1-(gray/(w*h))/0xFF;
	}
	public static Bitmap rotateToFit(Bitmap bm,float degrees){
		//旋转图片
		int width = bm.getWidth(); 
		int height = bm.getHeight();
		Bitmap bmResult = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
		Canvas c=new Canvas(bmResult);
		c.rotate(degrees,width/2,height/2);
		
		c.drawBitmap(bm,0,0,new Paint());
		return bmResult;
	}
}
