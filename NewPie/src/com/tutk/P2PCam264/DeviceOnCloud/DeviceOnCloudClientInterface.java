package com.tutk.P2PCam264.DeviceOnCloud;

public interface DeviceOnCloudClientInterface {
	public final int UPLOADOK=0;
	public  final int DOWNLOADOK=1;
	public  final int ERROR=-1;
	
	public void uploadok(int Status);
	public void downloadok(int Status);
	public void error(int Status);
	
	
}

