package com.supermap.navigation.demo;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import com.supermap.android.configuration.DefaultDataConfiguration;
import com.supermap.data.Environment;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.mapping.MapControl;
import com.supermap.navi.Navigation;

public class NaviManager {

	static private NaviManager mNaviManager = null;
	
	private Context mContext = null;
	private Navigation mNavigation;
	private MapControl mMapControl;
	private int routeAnalystMode = 0;                    // 0:�Ƽ�ģʽ; 1:ʱ�����ģʽ; 2:�������ģʽ; 3:�����շ�ģʽ
	
	private double mDistanceToMe;
	private Point2D mPtFrom;
	private Point2D mPtTo;
	private Point2D mPtLocation;
	private Point2D mPtPoi;
	
	static public NaviManager getInstance(Context context) {
		if (mNaviManager == null) {
			mNaviManager = new NaviManager(context);
		}
		return mNaviManager;
	}
	
	private NaviManager(Context context) {
		mContext = context;
		mMapControl = MapManager.getInstance(mContext).getMapControl();
		mNavigation = mMapControl.getNavigation();
		
		init();
	}
	
	private void init() {
		mPtFrom = new Point2D();
		mPtTo = new Point2D();
		mPtLocation = new Point2D();
		mPtPoi = new Point2D();

		mPtLocation.setX(116.5052061584224);
		mPtLocation.setY(39.985749510080936);
	
		GeoStyle style = new GeoStyle();
		if (Environment.isOpenGLMode()) {
			style.setLineSymbolID(964882);
		} else {
			style.setLineSymbolID(964883);
		}
		
		mNavigation.setRouteStyle(style);
	}
	
	/**
	 * ��ȡ���
	 */
	public Point2D getStartPoint(){	
		return mPtFrom;
	}
	
	/**
	 * ��ȡĿ�ĵ�
	 */
	public Point2D getDestinationPoint(){		
		return mPtTo;
	}
	
	/**
	 * ��ȡ��ǰλ�õ�
	 */
	public Point2D getLocationPoint(){		
		return mPtLocation;
	}
	
	/**
	 * ��ȡPOI��
	 */
	public Point2D getPoiPoint(){		
		return mPtPoi;
	}
	
	/**
	 * �������
	 * @param x ���x����
	 * @param y ���y����
	 */
	public void setStartPoint(double x,double y){
		if(mNavigation != null){
			mNavigation.setStartPoint(x, y);
		}
		
		mPtFrom.setX(x);
		mPtFrom.setY(y);
	}
	
	/**
	 * ����Ŀ�ĵ�
	 * @param x �յ�x����
	 * @param y �յ�y����
	 */
	public void setDestinationPoint(double x,double y){
		if(mNavigation != null){
			mNavigation.setDestinationPoint(x, y);
		}
		
		mPtTo.setX(x);
		mPtTo.setY(y);
	}
	
	public void setLocationPoint(double x,double y){		
		mPtLocation.setX(x);
		mPtLocation.setY(y);
	}
	
	public void setPoiPoint(double x,double y){		
		mPtPoi.setX(x);
		mPtPoi.setY(y);
	}
	
	/**
	 * ���õ���ģʽ
	 * @param routeAnalystMode
	 */
	public void setRouteAnalystMode(int routeAnalystMode) {
		this.routeAnalystMode = routeAnalystMode;
	}
	
	/**
	 * ·������
	 */
	public int routeAnalyst(){
		int result = 0;
		if(mNavigation != null){
			result = mNavigation.routeAnalyst(routeAnalystMode);
			mMapControl.getMap().refresh();
		}
		return result;
	}
	
	/**
     * ��ʼ·������
     * @param startpoint2D
     * @param destpoint2D
     */
// 	public void routeAnalyst() {
// 		
// 		if((mPtFrom == null ) || (mPtTo == null) ){
// 			Toast.makeText(mContext, "�޷���ȡλ����Ϣ", Toast.LENGTH_SHORT).show();
// 			return;
// 		}
// 		
// 		final ProgressDialog dialog = new ProgressDialog(mContext);
// 		dialog.setCancelable(false);
// 		dialog.setCanceledOnTouchOutside(false);
// 		dialog.setMessage("·��������....");
// 		dialog.show();
// 		new Thread(new Runnable() {
//
// 			@Override
// 			public void run() {
// 				// TODO Auto-generated method stub
// 			    // ����·������				
// 		    	int analystResult = mNavigation.routeAnalyst(routeAnalystMode);
// 		    	mMapControl.getMap().refresh();
// 				dialog.dismiss();
//                if(analystResult == 0){
//                    System.out.println("·������ʧ��");
//                    Runnable action = new Runnable() {
//
//     					@Override
//     					public void run() {
//     						// TODO Auto-generated method stub
//     						Toast.makeText(mContext, "·������ʧ��", Toast.LENGTH_SHORT).show();
//     					}
//     				};
//     				((Activity)mContext).runOnUiThread(action);
//                }
// 			}
// 		}).start();
// 	}

//	public double getDistance(){
//		Point2Ds pts = new Point2Ds();
//		pts.add(mPtFrom);
//		pts.add(mPtTo);
//		
//		if (pts.getCount() < 2) {
//			return -1;
//		} 
//		mDistanceToMe = Geometrist.computeGeodesicDistance(pts, 6378137.0, 0.003352811);
//		return mDistanceToMe;
//	}
	
	// ��ʾֹͣ�����Ի���
	public void stopNaviDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("��ʾ");
		builder.setMessage("ȷ���˳�������");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				mNavigation.stopGuide();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}
	
	/**
	 *  ��ʼ����������
	 */
	public void initNaviData() {
		mNavigation = mMapControl.getNavigation();
		
		// ���õ�������, 
		mNavigation.connectNaviData(DefaultDataConfiguration.MapDataPath);
	}
}
