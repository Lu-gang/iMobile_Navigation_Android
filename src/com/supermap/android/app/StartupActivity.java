package com.supermap.android.app;


import com.supermap.android.configuration.DefaultDataConfiguration;
import com.supermap.android.navi.MainActivity;
import com.supermap.data.Environment;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

/**
 * �������棬��������ʱ������ݼ��غã���Ȼ̫���ˣ�������ʱ�򻹿��Լӵ�ÿ��Ķ���
 * @author Congle
 *
 */
public class StartupActivity extends Activity {
	
	private final String LicPath = DefaultDataConfiguration.LicensePath;
	
	private MyApplication mApp = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Environment.setLicensePath(LicPath);
		Environment.setWebCacheDirectory(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/SuperMap/WebCahe/");
		Environment.initialization(this);
		
		mApp = (MyApplication) getApplication();
		mApp.registerActivity(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		// Initialize NaviData and License when they are contained in the package
		initData();                     

	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
	}
	
	/**
	 * ��ʼ������
	 */
	    public void initData()
	    {  	
	    	final ProgressDialog dialog = new ProgressDialog(this);
	    	dialog.setCancelable(false);
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.setMessage("���ڳ�ʼ�����ݡ�����");
	    	dialog.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				// Initialize NaviData and License when they are contained in
				// the package

				new DefaultDataConfiguration().autoConfig();

				dialog.dismiss();

				// Start MainActivity after initialize data
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Intent intent = new Intent(StartupActivity.this,
								MainActivity.class);
						StartupActivity.this.startActivity(intent);

					}
				});
			};

		}).start();
	    	
	    }
}
