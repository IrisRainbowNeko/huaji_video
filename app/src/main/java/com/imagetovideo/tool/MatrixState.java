package com.imagetovideo.tool;

import android.opengl.Matrix;

//�洢ϵͳ����״̬����
public class MatrixState 
{
	private static float[] mProjMatrix = new float[16];//4x4���� ͶӰ��
    private static float[] mVMatrix = new float[16];//�����λ�ó���9�������
    private static float[] mMVPMatrix;//��������õ��ܱ任����
    static float[] mMMatrix=new float[16] ;//����������ƶ���ת����
    
    
    
    public static void setInitStack()//��ȡ���任��ʼ����
    {
    	Matrix.setRotateM(mMMatrix, 0, 0, 1, 0, 0);
    }
    
    public static void transtate(float x,float y,float z)//������xyz���ƶ�
    {
    	Matrix.translateM(mMMatrix, 0, x, y, z);
    }
    
    public static void rotate(float angle,float x,float y,float z)//������xyz��ת��
    {
    	Matrix.rotateM(mMMatrix,0,angle,x,y,z);
    }
    
    
    //���������
    public static void setCamera
    (
    		float cx,	//�����λ��x
    		float cy,   //�����λ��y
    		float cz,   //�����λ��z
    		float tx,   //�����Ŀ���x
    		float ty,   //�����Ŀ���y
    		float tz,   //�����Ŀ���z
    		float upx,  //�����UP���X���
    		float upy,  //�����UP���Y���
    		float upz   //�����UP���Z���		
    )
    {
    	Matrix.setLookAtM
        (
        		mVMatrix, 
        		0, 
        		cx,
        		cy,
        		cz,
        		tx,
        		ty,
        		tz,
        		upx,
        		upy,
        		upz
        );
    }
    
    //����͸��ͶӰ����
    public static void setProject
    (
    	float left,		//near���left
    	float right,    //near���right
    	float bottom,   //near���bottom
    	float top,      //near���top
    	float near,		//near�����
    	float far       //far�����
    )
    {
    	Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }
   
    //��ȡ����������ܱ任����
    public static float[] getFinalMatrix()
    {
    	mMVPMatrix=new float[16];
    	Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);        
        return mMVPMatrix;
    }
}
