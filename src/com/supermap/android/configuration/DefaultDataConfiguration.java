package com.supermap.android.configuration;

import java.io.File;
import java.io.InputStream;


import com.supermap.android.app.MyApplication;
import com.supermap.android.filemanager.FileManager;
import com.supermap.android.filemanager.MyAssetManager;
import com.supermap.android.filemanager.Decompressor;

public class DefaultDataConfiguration {
	
	private final       String DataDir     = "NaviData";
	public static final String MapDataPath = MyApplication.SDCARD + "SuperMap/Demos/Data/NaviData/";
	
	private final       String LicenseName = "Trial License.slm";
	public static final String LicensePath = MyApplication.SDCARD + "SuperMap/License/" ;
	
	public DefaultDataConfiguration () 
	{
		
	}
	
	/**
	 * ��������
	 */
	public void autoConfig () 
	{
		
		File licenseDir = new File (LicensePath);
		File mapDataDir = new File (MapDataPath);
		
		if(!licenseDir.exists()){
			FileManager.getInstance().mkdirs(LicensePath);
			configLicense();
		}else {
			boolean isLicenseExists = FileManager.getInstance().isFileExist(LicensePath + LicenseName);
			if(isLicenseExists == false)
			{
				configLicense();
			}
		}
		
		if (!mapDataDir.exists())
		{
			FileManager.getInstance().mkdirs(MapDataPath);
			configMapData();
		}else {
			boolean isWorkspaceFileExists = FileManager.getInstance().isFileExist(MapDataPath + "NaviMap/RoadNameIndex.ndf");
			
			if(isWorkspaceFileExists == false)
			{
				configMapData();
			}
		}
	}
	
    /**
     * ��������ļ�
     */
	private void configLicense ()
	{
		InputStream is = MyAssetManager.getInstance().open(LicenseName);
		if(is != null){
		    FileManager.getInstance().copy(is, LicensePath + LicenseName);
		}
	}
	
	/**
	 * ���õ�ͼ����
	 */
	private void configMapData () 
	{
		String[] datas = MyAssetManager.getInstance().openDir(DataDir);
		for (String data : datas)
		{
			InputStream is = MyAssetManager.getInstance().open(DataDir + "/" + data);        // data is a zip file under DataDir
			if(is != null){
				String zip = MapDataPath + "/" + data;

				boolean result = FileManager.getInstance().copy(is, zip);
				if (result)
					Decompressor.UnZipFolder(zip, MapDataPath);

				File ziFile = new File(zip);
				ziFile.delete();
			}
		}
	}
}

