package com.imagetovideo.tool;
import static com.imagetovideo.tool.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import android.content.*;
import android.content.res.*;

//���������
public class RectPicture 
{	
	int mProgram;//�Զ�����Ⱦ���߳���id
    int muMVPMatrixHandle;//�ܱ任��������id
    int maPositionHandle; //����λ����������id  
    int maTexCoorHandle; //�������������������id  
    String mVertexShader;//������ɫ��    	 
    String mFragmentShader;//ƬԪ��ɫ��
	
	FloatBuffer   mVertexBuffer;//���������ݻ���
	FloatBuffer   mTexCoorBuffer;//�������������ݻ���
    int vCount=0;   
    float xAngle=0;//��x����ת�ĽǶ�
    float yAngle=0;//��y����ת�ĽǶ�
    float zAngle=0;//��z����ת�ĽǶ�
	
	int w=0,h=0;
    
    public RectPicture(AssetManager assets,int w,int h)
    {    	
		this.w=w;this.h=h;
    	//��ʼ�������������ɫ���
    	initVertexData();
    	//��ʼ����ɫ��        
    	initShader(assets);
    }
    
    //��ʼ�������������ɫ��ݵķ���
    public void initVertexData()
    {
    	//���������ݵĳ�ʼ��================begin============================
        vCount=6;
        float vertices[]=new float[]
        {
        	0,0,0,
        	w,0,0,
        	0,h,0,
			w,0,0,
        	0,h,0,
			w,h,0
        };
		
        //�������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥��������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ��ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //���������ݵĳ�ʼ��================end============================
        
        //�������������ݵĳ�ʼ��================begin============================
        float texCoor[]=new float[]//������ɫֵ���飬ÿ���4��ɫ��ֵRGBA
        {
        		0,1, 
        		1,1, 
        		0,0,
				1,1, 
				0,0,
				1,0        		
        };        
        //�����������������ݻ���
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTexCoorBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTexCoorBuffer.put(texCoor);//�򻺳����з��붥����ɫ���
        mTexCoorBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ��ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //�������������ݵĳ�ʼ��================end============================

    }

    //��ʼ����ɫ��
    public void initShader(AssetManager assets)
    {
    	//���ض�����ɫ��Ľű�����
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", assets);
        //����ƬԪ��ɫ��Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", assets);  
        //���ڶ�����ɫ����ƬԪ��ɫ�������
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж������������������id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
	
    public void drawSelf(int texId)
    {        
    	 //�ƶ�ʹ��ĳ��shader����
    	 GLES20.glUseProgram(mProgram);        
    	 
    	 MatrixState.setInitStack();
    	 
         //������Z������λ��1
         MatrixState.transtate(0, 0,1);
         
         //������y����ת
         MatrixState.rotate(yAngle, 0, 1, 0);
         //������z����ת
         MatrixState.rotate(zAngle, 0, 0, 1);  
         //������x����ת
         MatrixState.rotate(xAngle, 1, 0, 0);
         //�����ձ任������shader����
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //Ϊ����ָ������λ�����
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //Ϊ����ָ����������������
         GLES20.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         //������λ���������
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         
         //������
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
         
         //�����������
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
}
