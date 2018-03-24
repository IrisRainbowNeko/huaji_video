package com.huajivideo.tool;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import java.io.*;
import android.media.*;
import com.imagetovideo.tool.*;
import android.util.*;
import android.content.res.*;
import java.util.*;
import java.nio.*;
import java.text.*;

public class MainActivity extends Activity 
{
	EditText et,et_head,et_info;
	TextView tx;
	BitmapToHuaji bth;
	String inputpath;
	int videolen,framecount,fps,bitrate=5000;
	int video_w,video_h,item_size=50;
	float angle,dangle=1;
	
	Bitmap cover=null;
	boolean doext,domix;
	
	String dir=Environment.getExternalStorageDirectory().toString();
	ArrayList<Bitmap> frame_temp=new ArrayList<Bitmap>();
	
	DecimalFormat df = new DecimalFormat("#.##");
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		et=(EditText)findViewById(R.id.mainEditText1);
		et_head=(EditText)findViewById(R.id.et_head);
		et_info=(EditText)findViewById(R.id.et_info);
		tx=(TextView)findViewById(R.id.mainTextView1);
		
    }
	public void on1(View v){
		if(et.length()<=0)return;
		inputpath=et.getText().toString();
		
		try
		{
			cover=BitmapFactory.decodeStream(getAssets().open("hj.png"));
			bth = new BitmapToHuaji(cover,item_size,item_size);
		}
		catch (IOException e)
		{}
		
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(inputpath);
		Bitmap bm =mmr.getFrameAtTime();
		video_w = bm.getWidth();
		video_h = bm.getHeight();
		MediaPlayer mp=new MediaPlayer();
		try
		{
			mp.setDataSource(inputpath);
			mp.prepare();
			videolen=mp.getDuration();
		}catch (Exception e){e.printStackTrace();}
		try{
			MediaExtractor mex=new MediaExtractor();
			mex.setDataSource(inputpath);
			fps=mex.getTrackFormat(selectViedoTrack(mex)).getInteger(MediaFormat.KEY_FRAME_RATE);
			if(framecount==0)framecount=((videolen/1000)*fps);
		}catch(Exception e){e.printStackTrace();}
		mp.release();
		
		new Thread(render).start();
		new Thread(videomixer).start();
	}
	public void oninfo(View v){
		String info=et_info.getText().toString();
		switch(et_head.getText().toString()){
			case "dsize":
				item_size=Integer.parseInt(info);
				break;
			case "bitrate":
				bitrate=Integer.parseInt(info);
				break;
			case "fcount":
				framecount=Integer.parseInt(info);
				break;
			case "dangle":
				dangle=Float.parseFloat(info);
				break;
		}
	}
	
	private int selectViedoTrack(MediaExtractor extractor) {
        // Select the first video track we find, ignore the rest.
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                return i;
            }
        }

        return -1;
    }
	
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			tx.setText(msg.obj.toString());
		}
	};
	
	Runnable render=new Runnable(){

		@Override
		public void run()
		{
			MediaFramesExtracter exframe=new MediaFramesExtracter();
			exframe.setInputFile(inputpath);
			exframe.setstartTime(0);
			exframe.setendTime(videolen);
			exframe.setframecount(framecount);
			exframe.setwh(video_w,video_h);
			exframe.fex = new MediaFramesExtracter.FrameExtracter(){

				@Override
				public void frameavailable(Bitmap frame, int index)
				{
					synchronized(frame_temp){
						//handler.obtainMessage(0,"正在解析"+index+"/"+framecount).sendToTarget();
						bth.settexture(BitmapToHuaji.rotateToFit(cover,angle));
						angle+=dangle;
						frame_temp.add(bth.toHuaji(frame));
					}
				}

				@Override
				public void finished()
				{
					
				}

			};
			try{
				exframe.ExtractMpegFrames();
			}catch (Throwable e){e.printStackTrace();}
		}
	};
	
	Runnable videomixer=new Runnable(){
		@Override
		public void run()
		{
			final String str=dir+"/hjsp";
			int count=0;
			MediaEncoder encoder=new MediaEncoder(getAssets());
			File gifout;
			while((gifout=new File(str+count+".mp4")).exists())count++;
			final String outfile=gifout.toString();

			//设置参数
			encoder.setFPS(fps);
			encoder.setBitRate(bitrate*1000);
			encoder.setFrameCount(framecount);
			encoder.setSize(video_w,video_h);
			encoder.setaudiolen((int)(1000f*framecount/fps));
			encoder.setOutFile(outfile);
			encoder.setAudioPath(inputpath);

			//开始转换
			encoder.setFrameRander(new MediaEncoder.FrameRander(){

					@Override
					public Bitmap getFrame(int frameindex)
					{
						
						while(frame_temp.size()<=0){}
						synchronized(frame_temp){
							handler.obtainMessage(0,"正在合成"+frameindex+"/"+framecount).sendToTarget();
							Bitmap temp=frame_temp.get(0);
							//frame_temp=null;
							frame_temp.remove(0);
							return temp;
						}
					}

					@Override
					public void videofinish()
					{
						handler.obtainMessage(0,"开始混合音频").sendToTarget();
					}

					@Override
					public void audioProgress(double progress)
					{
						handler.obtainMessage(0,"音频编码进度:"+df.format(progress)+"%").sendToTarget();
					}

					
				});
			try{
				encoder.testEncodeVideoToMp4();
			}catch(Throwable e){e.printStackTrace();}
			handler.obtainMessage(0,"合成完毕").sendToTarget();
		}
	};
}
