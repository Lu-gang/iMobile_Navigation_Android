package com.supermap.navigation.demo;

import java.text.DecimalFormat;
import com.supermap.android.app.MyApplication;
import com.supermap.android.tools.GeoToolkit;
import com.supermap.data.FieldInfos;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class PoiDetailsView extends BaseView {

	static private PoiDetailsView mPoiShowView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;
	
	private RelativeLayout mViewMainBottom = null;	
	private View mViewPoiDetails = null;
	private Button btnPoiNearbyPlace = null;
	private Button btnPoiOpenRoute = null;
	private ImageButton btnPoiDetailsBack = null;
	
	private TextView txtPoiDetailsTitle = null;
	private TextView txtPoiDetailsName = null;
	private TextView txtPoiDetailsDistance = null;
	private TextView txtPoiDetailsTel = null;
	private TextView txtPoiDetailsZipcode = null;
	
	private int mPoiId = -1;
	private String mDatasetName = "POI_All_new";
	private String strPoiShowName = "";
	
	static public PoiDetailsView getInstance(Context context, MapView mapView, View mainView) {
		if (mPoiShowView == null) {
			mPoiShowView = new PoiDetailsView(context, mapView, mainView);
		}
		return mPoiShowView;
	}
	
	private PoiDetailsView(Context context, MapView mapView, View mainView) {
		mContext = context;
		mMainView = mainView;
		mMapView = mapView;

		initView();
	}
	
	/**
	 *  ��ʼ����������ʾ��������ؼ�����
	 */
	private void initView() {	
		mViewMainBottom = (RelativeLayout)mMainView.findViewById(R.id.ly_main_bottom);
		mViewPoiDetails= LayoutInflater.from(mContext).inflate(R.layout.poi_details, null);
		
		btnPoiNearbyPlace = (Button) mViewPoiDetails.findViewById(R.id.btn_poi_details_nearby);
		btnPoiNearbyPlace.setOnClickListener(buttonOnClickListener);

		btnPoiOpenRoute = (Button) mViewPoiDetails.findViewById(R.id.btn_poi_details_open_route);
		btnPoiOpenRoute.setOnClickListener(buttonOnClickListener);
		
		btnPoiDetailsBack = (ImageButton) mViewPoiDetails.findViewById(R.id.btn_poi_details_back);
		btnPoiDetailsBack.setOnClickListener(buttonOnClickListener);
		
		txtPoiDetailsTitle = (TextView) mViewPoiDetails.findViewById(R.id.txt_poi_details_title);
		txtPoiDetailsName = (TextView) mViewPoiDetails.findViewById(R.id.txt_poi_details_name);
		txtPoiDetailsTel = (TextView) mViewPoiDetails.findViewById(R.id.txt_poi_details_tel);
		txtPoiDetailsZipcode = (TextView) mViewPoiDetails.findViewById(R.id.txt_poi_details_zipcode);
		txtPoiDetailsDistance = (TextView) mViewPoiDetails.findViewById(R.id.txt_poi_details_distance);
	}
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_poi_details_nearby:
				searchNearby();
				break;

			case R.id.btn_poi_details_open_route:			
				openRoute();
				break;
			case R.id.btn_poi_details_back:			
				ViewManager.getInstance().fallback();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * �������
	 */
	private void openRoute() {
//		int result = NaviManager.getInstance(mContext).routeAnalyst();
//		if (result == 0) {
//			Toast.makeText(mContext, "����ʧ��", Toast.LENGTH_SHORT).show();
//		} else {
//			RouteShowView view = RouteShowView.getInstance(mContext, mMapView, mMainView);
//			view.show();
//			ViewManager.getInstance().addView(view);
//			close();
//			
//			Point2D startPoint = NaviManager.getInstance(mContext).getStartPoint();
//			Point2D destinationPoint = NaviManager.getInstance(mContext).getDestinationPoint();
//			
//			showPointByCallOut(startPoint, "location", R.drawable.icon_track_navi_start, CalloutAlignment.BOTTOM);
//			showPointByCallOut(destinationPoint, "poiPoint", R.drawable.icon_track_navi_end, CalloutAlignment.BOTTOM);
//		}	
//		
		RouteSetView view = RouteSetView.getInstance(mContext, mMapView, mMainView);
		
		Point2D pt = NaviManager.getInstance(mContext).getLocationPoint();
		NaviManager.getInstance(mContext).setStartPoint(pt.getX(), pt.getY());
		
		Point2D ptPoi = NaviManager.getInstance(mContext).getPoiPoint();
		NaviManager.getInstance(mContext).setDestinationPoint(ptPoi.getX(), ptPoi.getY());
		
		view.setStartPtName("�ҵ�λ��");
		view.setEndPtName(strPoiShowName);
		view.show();
		ViewManager.getInstance().addView(view);
		close();
	}
	
	/**
	 * �ܱ�����
	 */
	private void searchNearby() {
		NearbySearchView view = NearbySearchView.getInstance(mContext, mMapView, mMainView);
		Point2D ptCurrent = NaviManager.getInstance(mContext).getPoiPoint();
		view.setPtCurrent(ptCurrent);
		view.show();
		ViewManager.getInstance().addView(view);
		
		close();
	}
	
	/**
	 * ��ʾPOI����
	 */
	private void showPoiDetail() {
		Recordset recordset = DataManager.getInstance(mContext).query(mDatasetName, mPoiId);
		if (recordset == null) {
			return;
		}
		
		FieldInfos fieldInfos = recordset.getFieldInfos();
		
		if (fieldInfos == null) {
			recordset.dispose();
			recordset = null;
			return;
		}
		
		if (fieldInfos.indexOf("Name") != -1) {
			strPoiShowName = recordset.getString("Name");
			
			if (strPoiShowName == null || strPoiShowName.isEmpty()) {
				strPoiShowName = "δ֪��";
			}
			
			
			txtPoiDetailsTitle.setText(strPoiShowName);
			txtPoiDetailsName.setText("��ַ��"+strPoiShowName);
		}
		
		if (fieldInfos.indexOf("Telephone") != -1) {
			String strTelephone = recordset.getString("Telephone");
			
			if (strTelephone == null || strTelephone.isEmpty()) {
				strTelephone = "δ֪";
			}
			
			txtPoiDetailsTel.setText("�绰��"+strTelephone);
		}
		
		if (fieldInfos.indexOf("ZipCode") != -1) {
			String strZipCode = recordset.getString("ZipCode");
			
			if (strZipCode == null || strZipCode.isEmpty()) {
				strZipCode = "δ֪";
			}
			
			txtPoiDetailsZipcode.setText("�ʱࣺ"+strZipCode);
		}
		
		Geometry geometry = recordset.getGeometry();
		if (geometry == null) {
			recordset.dispose();
			recordset = null;
			return;
		}
		
		Point2D pt = geometry.getInnerPoint();
		
		geometry.dispose();
		geometry = null;
		recordset.dispose();
		recordset = null;
		
		getDistance(pt);
	}
	
	public void close() {
		mViewMainBottom.removeView(mViewPoiDetails);
		MyApplication.getInstance().setLongPressEnable(true);
	}
	
	public void show() {
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mViewMainBottom.removeView(mViewPoiDetails);
		mViewMainBottom.addView(mViewPoiDetails, param);
		
		showPoiDetail();
		
		MyApplication.getInstance().setLongPressEnable(false);
	}
	
	public void setPoiID(int id) {	
		mPoiId = id;	
	}
	
	public void setDatasetName(String name) {	
		mDatasetName = name;	
	}
	
	//���δ֪�����
	public void setDistance(double distance) {	
		DecimalFormat df = new DecimalFormat("0.0");		
		String strDistance;
		
		if (distance < 0) {
			strDistance = 0 + "��";
		} else if (distance < 1000) {
			strDistance = (int)distance + "��";
		} else {
			strDistance = df.format(distance/1000.0) + "����";
		}
				
		txtPoiDetailsDistance.setText("���룺"+strDistance);	
	}
	
	private void getDistance(Point2D pt) {
		Point2D ptLocation = NaviManager.getInstance(mContext).getLocationPoint();
		
		double distance = GeoToolkit.getDistance(ptLocation, pt);
//		double distance = NaviManager.getInstance(mContext).getDistance();
		 
		DecimalFormat df = new DecimalFormat("0.0");		
		String strDistance;
		
		if (distance < 0) {
			strDistance = 0 + "��";
		} else if (distance < 1000) {
			strDistance = (int)distance + "��";
		} else {
			strDistance = df.format(distance/1000.0) + "����";
		}
				
		txtPoiDetailsDistance.setText("���룺"+strDistance);
	}

	/**
	 * ��ʾ��ע��
	 * @param point2D
	 * @param pointName
	 * @param idDrawable
	 */
	public void showPointByCallOut(Point2D point2D,
			String pointName, int idDrawable, CalloutAlignment alignment) {
		// ���ñ�ע
		CallOut callOut = new CallOut(mContext);
		callOut.setStyle(alignment); // ���ñ�ע��Ķ��뷽ʽ���·�����
		callOut.setCustomize(true); // �����Զ��屳��
		callOut.setLocation(point2D.getX(), point2D.getY()); // ���ñ�ע������
		ImageView imageView = new ImageView(mContext);

		imageView.setImageResource(idDrawable);
		// ��ʾ���
		callOut.setContentView(imageView);

		mMapView.removeCallOut(pointName);
		mMapView.addCallout(callOut, pointName); // �ڵ�ͼ����ʾCallOut��ע�㣬����������
	}
}
