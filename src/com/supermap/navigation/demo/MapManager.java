package com.supermap.navigation.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.navigation.demo.R;

public class MapManager {
	
	static private MapManager mMapManager = null;
	
	private Context		mContext = null;
	
	// �����ͼ�ؼ�
	private MapView			mMapView        = null;
	private Workspace		mWorkspace      = null;
	private MapControl		mMapControl     = null;
	private Map				mMap            = null;
	private DynamicView		mDynamicView	= null;;
	
	
	static public MapManager getInstance(Context context) {
		if (mMapManager == null) {
			mMapManager = new MapManager(context);
		}
		return mMapManager;
	}
	
	private MapManager(Context context) {
		mContext = context;
	}
	
	/**
	 * �򿪹����ռ䣬��ʾ��ͼ
	 * @return
	 */
	public boolean init(MapView mapView) {
        // ��ȡ��ͼ�ؼ�
		mMapView    = mapView;
		mMapControl = mMapView.getMapControl();
		mWorkspace = DataManager.getInstance(mContext).getWorkspace();

		mMap = mMapControl.getMap();                                   // ��ȡ��ͼ�ؼ�
		mMap.setWorkspace(mWorkspace);   
		
		String mapName = mWorkspace.getMaps().get(0);
		mMapControl.getMap().open(mapName);
        
		mMapControl.getMap().setFullScreenDrawModel(true);
	
    	//���õ�ͼ��ʼ����ʾ��Χ���ŵ�ͼ��ͼʱ����ʾ���Ǳ���
//		m_mapControl.getMap().setScale(1/458984.375);
//		m_mapControl.getMap().setCenter(new Point2D(12953693.6950684, 4858067.04711915));
		mMapControl.getMap().refresh();
		
		mDynamicView = new DynamicView(mContext, mMapControl.getMap());
//		mMapView.addDynamicView(mDynamicView);
		return true;
	}
	
	public MapControl getMapControl() {
		return mMapControl;
	}
	
	public Map getMap() {
		return mMap;
	}
	
	public MapView getMapView() {
		return mMapView;
	}
	
	public DynamicView getDynamicView() {
		return mDynamicView;
	}
	

	/**
	 * ��ʾ��ע��
	 * @param point2D
	 * @param pointName
	 * @param idDrawable
	 */
	public void showLocationPointByCallOut(Point2D point2D,
			String pointName, int idDrawable, CalloutAlignment alignment) {
		// ���ñ�ע
		CallOut callOut = new CallOut(mContext);
		callOut.setStyle(alignment); // ���ñ�ע��Ķ��뷽ʽ���·�����
		callOut.setCustomize(true); // �����Զ��屳��
		callOut.setLocation(point2D.getX(), point2D.getY()); // ���ñ�ע������
//		View view = LayoutInflater.from(mContext).inflate(R.layout.callout_location, null);
		
//		ImageButton imageView = (ImageButton)view.findViewById(R.id.btn_location_callout);
//		imageView.setBackgroundResource(idDrawable);
		
		ImageView imageView = new ImageView(mContext);
		imageView.setBackgroundResource(idDrawable);
		
		// ��ʾ���
		callOut.setContentView(imageView);

		mMapView.removeCallOut(pointName);
		mMapView.addCallout(callOut, pointName); // �ڵ�ͼ����ʾCallOut��ע�㣬����������
	}
	
	/**
	 * ��ʾPOI��
	 * @param point2D
	 * @param pointName
	 * @param idDrawable
	 */
	public void showPoiPointByCallOut(Point2D point2D,
			String pointName, int idDrawable, CalloutAlignment alignment) {
		// ���ñ�ע
		CallOut callOut = new CallOut(mContext);
		callOut.setStyle(alignment); // ���ñ�ע��Ķ��뷽ʽ���·�����
		callOut.setCustomize(true); // �����Զ��屳��
		callOut.setLocation(point2D.getX(), point2D.getY()); // ���ñ�ע������
//		View view = LayoutInflater.from(mContext).inflate(R.layout.callout_poi, null);
//		
//		ImageButton imageView = (ImageButton)view.findViewById(R.id.btn_poi_callout);
		
		ImageView imageView = new ImageView(mContext);
		imageView.setBackgroundResource(idDrawable);
		
		// ��ʾ���
		callOut.setContentView(imageView);

		mMapView.removeCallOut(pointName);
		mMapView.addCallout(callOut, pointName); // �ڵ�ͼ����ʾCallOut��ע�㣬����������
	}
}
