package com.supermap.android.navi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.Toast;

import com.supermap.data.Environment;
import com.supermap.data.Workspace;
import com.supermap.mapping.MapView;
import com.supermap.mapping.MapControl;


import com.supermap.android.app.MyApplication;
import com.supermap.android.configuration.DefaultDataConfiguration;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.TrackingLayer;
import com.supermap.navi.Navigation;
import com.supermap.navidemo.R;
import com.supermap.plugin.SpeakPlugin;
import com.supermap.plugin.Speaker;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;

/**
 * <p>
 * Title:����
 * </p>
 * 
 * <p>
 * Description:
 * ============================================================================>
 * ------------------------------��Ȩ����----------------------------
 * ���ļ�Ϊ SuperMap iMobile ��ʾDemo�Ĵ��� 
 * ��Ȩ���У�������ͼ����ɷ����޹�˾
 * ----------------------------------------------------------------
 * ----------------------------SuperMap iMobile ��ʾDemo˵��---------------------------
 * 
 * 1��Demo��飺
 *   չʾ������� iMobile �ĵ���ģ��ʵ��·��������
 * 2��Demo���ݣ���������Ŀ¼��"/SuperMap/Demos/Data/NaviData/"
 *           ��ͼ���ݣ�http://supermapcloud.com
 *           ���Ŀ¼��"/SuperMap/License/"
 * 3���ؼ�����/��Ա: 
 *	    Navigation.setStartPoint();          ����
 *      Navigation.setDestinationPoint();    ����
 *      Navigation.enablePanOnGuide();       ����
 *      Navigation.connectNaviData();        ����
 *      Navigation.isGuiding();              ����
 *      Navigation.cleanPath();              ����
 *      Navigation.stopGuide();              ����
 *      Navigation.startGuide();             ����
 *      Navigation.routeAnalyst();           ����
 *      
 * 4������չʾ
 *   (1)��������֮�����·����
 *   (2)�Բ��ҵ������·��������ʵ������
 *   (3)�Բ��ҵ������·������ģ�⵼����
 * 5��ע�⣺ 
 *	������б�����ʧ�ܣ�����ԭ����ȱ��������Դ��
 *  ����취���뽫��Ʒ����Resource�ļ����µ�voice�ļ��п���������Ŀ¼�е�assets�ļ����¡�
 * ------------------------------------------------------------------------------
 * ============================================================================>
 * </p> 
 * 
 * <p>
 * Company: ������ͼ����ɷ����޹�˾
 * </p>
 * 
 */

@SuppressLint("SdCardPath")
public class MainActivity extends Activity {
	
	// ���尴ť�ؼ�
    private ImageButton      btn_clean              = null;
    private ImageButton      btn_end_point          = null;
    private ImageButton      btn_location           = null;   
    private ImageButton      btn_start_point        = null;
    private ImageButton      btn_zoomIn             = null;
    private ImageButton      btn_zoomOut            = null;
    private ImageButton      btn_back               = null;
    private ImageButton      btn_navi_simulation    = null;
    private ImageButton      btn_navi_real          = null;
    private ImageButton      btn_pause_navigation   = null;
    private ImageButton      btn_path_analyst       = null;
    private ImageButton      btn_conti_navigation   = null;
    private ImageButton      btn_stop_navigation    = null;
    
	// �����ͼ�ؼ�
	private MapView        m_mapView        = null;
	private Workspace      m_workspace      = null;
	private MapControl     m_mapControl     = null;
	private Map            m_Map            = null;
	private Navigation     m_Navigation     = null;
	private TrackingLayer  m_TrackingLayer  = null;
	
	// ���弸�ζ���   	
	private Point2D        startPoint       = null;          // �������
	private Point2D        destPoint        = null;          // �����յ�
		
	// �����ַ�������
    private final String LicPath = DefaultDataConfiguration.LicensePath;
	
	
	// ���岼������
	private boolean isStartPoint       = false;
	private boolean isEndPoint         = false;
	private boolean isLongPressEnable  = false;
	private boolean bGuideEnable       = false;
    private boolean mExitEnable        = false;
    
	// �������ͱ���
	private int    steps            = 0;
	private int    routeAnalystMode = 0;                    // 0:�Ƽ�ģʽ; 1:ʱ�����ģʽ; 2:�������ģʽ; 3:�����շ�ģʽ
	private int    naviMode         = 1;                    // 0:��ʵ����; 1:ģ�⵼��; 2:����Ѳ��
    private int    startStep        = 0;
    private int    destStep         = 0;

	private LocationTencent m_MyLocation = null;
	private MyApplication mApp   = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// �������·��������ʼ��
		Environment.setLicensePath(LicPath);
		Environment.setWebCacheDirectory(MyApplication.SDCARD+"/SuperMap/WebCahe/");
		Environment.setTemporaryPath(MyApplication.SDCARD + "/SuperMap/temp/");
		Environment.initialization(this);
		
		mApp   = (MyApplication) getApplication();
		mApp.registerActivity(this);
		m_MyLocation = new LocationTencent(this);
		
		initView();                                           // ��ʼ������ؼ��������ļ��а�����ͼ��ʾ�ؼ���������ɳ�ʼ��������ʾ����
        
		boolean  isOpenMap = false;
		isOpenMap = initMap();                                // �򿪹����ռ䣬�򿪵�ͼ 
		if(isOpenMap){
		    initNaviData();                                   // ��ʼ����������
		}else{
			showInfo("Initialize Map failed.");
		}
	} 

	/**
	 *  ��ʼ����������ʾ��������ؼ�����
	 */
	public void initView() {
		// ��ʾ����
		setContentView(R.layout.activity_main);
		// ��ȡ��ť�ؼ��������ü���
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new ImageButtonOnClickListener());
		
		btn_start_point = (ImageButton) findViewById(R.id.btn_start_point);
		btn_start_point.setOnClickListener(new ImageButtonOnClickListener());

		btn_end_point = (ImageButton) findViewById(R.id.btn_end_point);
		btn_end_point.setOnClickListener(new ImageButtonOnClickListener());

		btn_clean = (ImageButton) findViewById(R.id.btn_clear);
		btn_clean.setOnClickListener(new ImageButtonOnClickListener());

		btn_location = (ImageButton) findViewById(R.id.btn_location);
		btn_location.setOnClickListener(new ImageButtonOnClickListener());

		btn_zoomOut = (ImageButton) findViewById(R.id.btn_zoomOut);
		btn_zoomOut.setOnClickListener(new ImageButtonOnClickListener());

		btn_zoomIn = (ImageButton) findViewById(R.id.btn_zoomIn);
		btn_zoomIn.setOnClickListener(new ImageButtonOnClickListener());

		btn_path_analyst = (ImageButton) findViewById(R.id.btn_path_analyst);
		btn_path_analyst.setOnClickListener(new ImageButtonOnClickListener());
		
		btn_navi_simulation = (ImageButton) findViewById(R.id.btn_navi_simulation);
		btn_navi_simulation.setOnClickListener(new ImageButtonOnClickListener());
		
		btn_navi_real = (ImageButton) findViewById(R.id.btn_navi_real);
		btn_navi_real.setOnClickListener(new ImageButtonOnClickListener());
		
		btn_pause_navigation = (ImageButton) findViewById(R.id.btn_pause_navigation);
		btn_pause_navigation.setOnClickListener(new ImageButtonOnClickListener());
		
		btn_conti_navigation = (ImageButton) findViewById(R.id.btn_continue_navigation);
		btn_conti_navigation.setOnClickListener(new ImageButtonOnClickListener());
		
		btn_stop_navigation = (ImageButton) findViewById(R.id.btn_stop_navigation);
		btn_stop_navigation.setOnClickListener(new ImageButtonOnClickListener());
		
		listenNaviStatus();
	}

	// ��ť����¼�����
	private class ImageButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.btn_back:
				// ��ʼ���������յ㣬��δ������������һ״̬,ֻ���޸İ�ť״̬����
				if(isStartPoint) { 
					btn_start_point.setEnabled(true);
					isStartPoint = false;
					break;
				}
				if(isEndPoint) { 
					btn_end_point.setEnabled(true);
					isEndPoint = false;
					break;
				}  
				// ���ص�����ģ�⵼������ʵ��������
				boolean naviStatus = m_Navigation.isGuiding() || (btn_conti_navigation.getVisibility() == View.VISIBLE) || (m_Navigation.isGuiding() == false && btn_pause_navigation.getVisibility() == View.VISIBLE);
				if(naviStatus)
				{
					if(m_Navigation.isGuiding()){
					    m_Navigation.stopGuide();                    // ֹͣ����
					    m_Navigation.enablePanOnGuide(true);
					}
					btn_stop_navigation.setVisibility(View.GONE);
					btn_pause_navigation.setVisibility(View.GONE);
					btn_conti_navigation.setVisibility(View.GONE);
					btn_back.setVisibility(View.GONE);
					btn_navi_real.setVisibility(View.VISIBLE);
					btn_navi_simulation.setVisibility(View.VISIBLE);
				}else{
				    // ������һ�����õ������յ�
					deleteStartOrDestPoint();
				}
				break;
				
			case R.id.btn_start_point:
				// �����յ����õĻ����жϣ�����������������յ�ǰ�������Կ�ʼ������һ����
				if (isLongPressEnable) {
					showInfo("��������յ������");
				} else {
					m_mapView.removeCallOut("startPoint");
					btn_start_point.setEnabled(false);
					isStartPoint      = true;
					isLongPressEnable = true;
					showInfo("�볤���������");                 // �������ȡ���
				}
				break;

			case R.id.btn_end_point:
				// �����յ����õĻ����жϣ�����������������յ�ǰ�������Կ�ʼ������һ����
				if (isLongPressEnable) {
					showInfo("���������������");
				} else {
					btn_end_point.setEnabled(false);
					isEndPoint = true;
					isLongPressEnable = true;
					showInfo("�볤�������յ�");                  // �������ȡ�յ�
				}
				break;

			case R.id.btn_clear:
				// ֹͣ�������������·�������õ����
				clear();
				break;
				
			case R.id.btn_path_analyst:
				// ��������յ����ú���ܽ���·������
				if((startPoint == null) || (destPoint == null))
				{
					showInfo("�����������յ�");
				}else{
					startNaviRouteAnalyst(startPoint, destPoint);            // ��ʼ·������
				}
				m_Map.refresh();
				break;
				
            case R.id.btn_navi_simulation:
            	naviMode = 1;
            	startNavigation();
            	btn_back.setVisibility(View.VISIBLE);
            	btn_navi_simulation.setVisibility(View.GONE);
            	btn_navi_real.setVisibility(View.GONE);
            	btn_pause_navigation.setVisibility(View.VISIBLE);
            	btn_stop_navigation.setVisibility(View.VISIBLE);
				break;
				
            case R.id.btn_navi_real:
            	naviMode = 0;
      
				TencentMapLBSApiResult location = m_MyLocation.getLocationInfo();
				if(location == null ){
					showInfo("�����޷���λ�����Ժ�����");
					break;
				}
            	m_mapView.removeCallOut("startPoint");
            	startPoint = getPoint(m_MyLocation.getLocationInfo(), "startPoint", R.drawable.start_point);
                if((startPoint != null) && (destPoint != null))
				    startNaviRouteAnalyst(startPoint, destPoint);
				break;
				
            case R.id.btn_pause_navigation:
				m_Navigation.enablePanOnGuide(true);
				btn_pause_navigation.setVisibility(View.GONE);
				btn_conti_navigation.setVisibility(View.VISIBLE);
				btn_stop_navigation.setVisibility(View.GONE);
				break;
				
            case R.id.btn_stop_navigation:
				m_Navigation.enablePanOnGuide(true);
				btn_pause_navigation.setVisibility(View.GONE);
				btn_stop_navigation.setVisibility(View.GONE);
				if(m_Navigation.isGuiding()){
					m_Navigation.stopGuide();
				}
				btn_navi_real.setVisibility(View.VISIBLE);
				btn_navi_simulation.setVisibility(View.VISIBLE);
				btn_back.setVisibility(View.GONE);
				break;
				
            case R.id.btn_continue_navigation:
            	m_Navigation.enablePanOnGuide(false);
            	btn_pause_navigation.setVisibility(View.VISIBLE);
				btn_conti_navigation.setVisibility(View.GONE);
				btn_stop_navigation.setVisibility(View.VISIBLE);
            	break;
            	
            	
			case R.id.btn_location:
				if(m_Navigation.isGuiding()){
					showInfo("���Ȱ����ؼ����˳�����");
					break;
				}
				TencentMapLBSApiResult location1 = m_MyLocation.getLocationInfo();
				if(location1 == null ){
					showInfo("�����޷���λ�����Ժ�����");
					break;
				}
				m_mapView.removeCallOut("location");
				if(startPoint == null){
					steps++;
					startStep = destStep +1;
				    startPoint = getPoint(location1, "startPoint", R.drawable.start_point);
				    btn_back.setVisibility(View.VISIBLE);
				}else{
					getPoint(location1, "location", R.drawable.location);
				}
				// ��ʼ������㣬����δ����ʱ
				if (isStartPoint ){
					isStartPoint      = false; 
			        isLongPressEnable = false;
			        btn_start_point.setEnabled(true);
				}
				m_Map.refresh();
				break;

			case R.id.btn_zoomOut:
				m_mapControl.getMap().zoom(0.5);
				m_mapControl.getMap().refresh();
				break;

			case R.id.btn_zoomIn:
				m_mapControl.getMap().zoom(2);
				m_mapControl.getMap().refresh();
				break;

			default:
				break;
			}
		}

		/**
		 *  ������һ�����õ������յ�
		 */
		private void deleteStartOrDestPoint() {
			if ((startPoint != null) && startStep == steps && !bGuideEnable) {

				steps--;
				startStep = 0;
				m_mapView.removeCallOut("startPoint"); // ������
				startPoint = null;

				btn_start_point.setEnabled(true);
				isLongPressEnable = false;
				if (steps == 0)
					btn_back.setVisibility(View.GONE);

			} else {

				if ((destPoint != null) && (destStep == steps) && !bGuideEnable) {

					steps--;
					destStep = 0;
					m_mapView.removeCallOut("destPoint"); // ����յ�
					destPoint = null;

					btn_end_point.setEnabled(true);
					isLongPressEnable = false;
					if (steps == 0)
						btn_back.setVisibility(View.GONE);

				}
			}
		}
		
		/**
		 *  ֹͣ�������������·�������õ����
		 */
		private void clear() {
			if (m_Navigation.isGuiding())
				m_Navigation.stopGuide();
			m_Navigation.cleanPath();        // ���·��
			m_mapView.removeAllCallOut();    // ��������յ���ʾ

			steps = 0;
			startStep = 0;
			destStep = 0;
			naviMode = 1;
			startPoint = null;
			destPoint = null;
			m_Map.refresh();

			// ���ð�������
			btn_start_point.setEnabled(true);
			btn_end_point.setEnabled(true);

			btn_back.setVisibility(View.VISIBLE);
			btn_start_point.setVisibility(View.VISIBLE);
			btn_end_point.setVisibility(View.VISIBLE);
			btn_path_analyst.setVisibility(View.VISIBLE);
			btn_navi_simulation.setVisibility(View.GONE);
			btn_navi_real.setVisibility(View.GONE);
			btn_stop_navigation.setVisibility(View.GONE);
			btn_pause_navigation.setVisibility(View.GONE);
			btn_conti_navigation.setVisibility(View.GONE);
			btn_back.setVisibility(View.GONE);

			isLongPressEnable = false;
			bGuideEnable = false;
		}
	}

	/**
	 * �򿪹����ռ䣬��ʾ��ͼ
	 * @return
	 */
	public boolean initMap() {
        // ��ȡ��ͼ�ؼ�
		m_mapView    = (MapView) findViewById(R.id.mapView);
		m_mapControl = m_mapView.getMapControl();
		// ���Ʒ����ͼ
		m_workspace = new Workspace();
		DatasourceConnectionInfo info = new DatasourceConnectionInfo();
    	String dataServer = "http://supermapcloud.com";
		info.setEngineType(EngineType.SuperMapCloud);
		info.setAlias("SuperMapCloud");
		info.setServer(dataServer);
		// ���½��Ĺ����ռ��д�����Դ
		Datasource datasource = m_workspace.getDatasources().open(info);
	    if(datasource == null){
	    	return false;
	    }
		m_Map = m_mapControl.getMap();                                   // ��ȡ��ͼ�ؼ�
		m_Map.setWorkspace(m_workspace);                                 // ������ͼ�͹����ռ�
		m_Map.getLayers().add(datasource.getDatasets().get(0),false);    // �������ռ��е����ݼ���ӵ���ͼ���ϲ�
    	m_TrackingLayer = m_Map.getTrackingLayer();                      // ��ȡ��ͼ�ĸ��ٲ�    
    	
    	//���õ�ͼ��ʼ����ʾ��Χ���ŵ�ͼ��ͼʱ����ʾ���Ǳ���
		m_mapControl.getMap().setScale(1/458984.375);
		m_mapControl.getMap().setCenter(new Point2D(12953693.6950684, 4858067.04711915));
		m_mapControl.getMap().refresh();
		
		m_mapControl.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					m_Navigation.enablePanOnGuide(true);
				}
				if(m_Navigation.isGuiding()){
					btn_pause_navigation.setVisibility(View.GONE);
					btn_conti_navigation.setVisibility(View.VISIBLE);
				}
				return m_mapControl.onMultiTouch(event);
			}
		});
    	
		// ���ó����¼�����
	   m_mapControl.setGestureDetector(new GestureDetector(longTouchListener));;
	   return true;
	}
	
	// �����¼�����
	SimpleOnGestureListener longTouchListener = new SimpleOnGestureListener() {

		public void onLongPress(MotionEvent event) {
			if (isLongPressEnable) {              // �����ȡ�����¼����ʱ����Ӧ
				isLongPressEnable = false;        // ����һ�����ʹ������Ӧ��Ч
				
                steps ++;
				// �ж���������㻹���յ�
				if (isStartPoint ){
					startStep = destStep +1;
					isStartPoint = false; 
					setStartPoint(event);
					btn_back.setVisibility(View.VISIBLE);
				}
				if (isEndPoint) {
					destStep = startStep +1;
					isEndPoint = false;
					setDestinationPoint(event);
					btn_back.setVisibility(View.VISIBLE);
				}
			}
		}
	};

	/**
	 * ��ȡ����ʾ���
	 * @param event
	 */
	public void setStartPoint(MotionEvent event) {
		startPoint = getPoint(event, "startPoint", R.drawable.start_point);
	}
	
	/**
	 * ��ȡ����ʾ�յ�
	 * @param event
	 */
	public void setDestinationPoint(MotionEvent event) {
		destPoint = getPoint(event, "destPoint", R.drawable.end_point);
	}
	
	/**
	 * ��ʾ��ע��
	 * @param point2D
	 * @param pointName
	 * @param idDrawable
	 */
	public void showPointByCallOut(final Point2D point2D,
			final String pointName, int idDrawable) {
		// ���ñ�ע
		CallOut callOut = new CallOut(MainActivity.this);
		callOut.setStyle(CalloutAlignment.BOTTOM); // ���ñ�ע��Ķ��뷽ʽ���·�����
		callOut.setCustomize(true); // �����Զ��屳��
		callOut.setLocation(point2D.getX(), point2D.getY()); // ���ñ�ע������
		ImageView imageView = new ImageView(MainActivity.this);

		// ��ʾ���
		imageView.setBackgroundResource(idDrawable);
		callOut.setContentView(imageView);
		m_mapView.addCallout(callOut, pointName); // �ڵ�ͼ����ʾCallOut��ע�㣬����������

	}
	
	/**
	 * ��ȡ��Ļ�ϵĵ㣬��ת���ɵ�ͼ��γ����㣬�����ؾ�γ�����
	 * @param event
	 * @param pointName
	 * @param idDrawable
	 * @return
	 */
	public Point2D getPoint(MotionEvent event, final String pointName, final int idDrawable) {

		// ��ȡ��Ļ�ϵĵ�����ĵ�����(x, y)
		int x = (int) event.getX();
		int y = (int) event.getY();

		// ת��Ϊ��ͼ��ά��
		Point2D point2D = m_Map.pixelToMap(new Point(x, y));
		showPointByCallOut(point2D, pointName, idDrawable);

		// ͶӰת����ת��Ϊ��γ����ϵ
		if (m_Map.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			Point2Ds point2Ds = new Point2Ds();
			point2Ds.add(point2D);
			PrjCoordSys destPrjCoordSys = new PrjCoordSys();
			
			// ����Ŀ������ϵ����
			destPrjCoordSys
					.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			
			// ��ȡ��ǰ��ͼ����ϵ
			PrjCoordSys sourPrjCoordSys = m_Map.getPrjCoordSys();
			// ת��ͶӰ����
			CoordSysTranslator.convert(point2Ds, sourPrjCoordSys,
					destPrjCoordSys, new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);

			point2D = point2Ds.getItem(0);
		}

		return point2D;                  // ���ؾ�γ�����
	}

	/**
	 * ����õ�λ����Ϣ����γ���꣩ת���ɵ�ͼ����ϵ�ϵĵ㣬����ʾ�������ؾ�γ�����
	 * @param location
	 * @param pointName
	 * @param idDrawable
	 * @return
	 */
	public Point2D getPoint(TencentMapLBSApiResult location,
			final String pointName, final int idDrawable) {

		if (location == null) {
			showInfo("�����޷���λ�����Ժ�����");
			return null;
		}
		Point2D point2D = new Point2D(location.Longitude, location.Latitude);
		PrjCoordSys Prj = m_Map.getPrjCoordSys();

		// ��ͶӰ���Ǿ�γ����ϵʱ����Ե����ͶӰת��
		if (Prj.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			Point2Ds points = new Point2Ds();
			points.add(point2D);
			PrjCoordSys desPrjCoorSys = new PrjCoordSys();
			desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			boolean b1 = CoordSysTranslator.convert(points, desPrjCoorSys, Prj,
					new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			// �ڵ�ͼ����ʾ�õ�
			showPointByCallOut(points.getItem(0), pointName, idDrawable);
			m_Map.setCenter(points.getItem(0));
			m_Map.setScale(1 / 57373.046875);
			m_Map.refresh();
		}
		return point2D; 
	}

	/**
	 *  ��ʼ����������
	 */
	public void initNaviData() {
		m_Navigation = m_mapControl.getNavigation();
		
		// ���õ�������, 
		m_Navigation.connectNaviData(DefaultDataConfiguration.MapDataPath);

		SpeakPlugin.getInstance().setSpeaker(Speaker.CONGLE);

	}
	
    /**
     * ��ʼ·������
     * @param startpoint2D
     * @param destpoint2D
     */
 	public void startNaviRouteAnalyst(Point2D startpoint2D, Point2D destpoint2D) {
 		
 		if((startpoint2D == null ) || (destpoint2D == null) ){
 			showInfo("�������������յ�");
 			return;
 		}
 		
 		// ���õ����������յ�
 		m_Navigation.setStartPoint(startpoint2D.getX(), startpoint2D.getY());
 		m_Navigation.setDestinationPoint(destpoint2D.getX(), destpoint2D.getY());
 		
 		final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
 		dialog.setCancelable(false);
 		dialog.setCanceledOnTouchOutside(false);
 		dialog.setMessage("·��������....");
 		dialog.show();
 		new Thread(new Runnable() {

 			@Override
 			public void run() {
 				// TODO Auto-generated method stub
 			    // ����·������				
 		    	int analystResult = m_Navigation.routeAnalyst(routeAnalystMode);
 				dialog.dismiss();
                if(analystResult == 0){
                    System.out.println("·������ʧ��");
                    Runnable action = new Runnable() {

     					@Override
     					public void run() {
     						// TODO Auto-generated method stub
     						showInfo("·������ʧ��");
     					}
     				};
     				MainActivity.this.runOnUiThread(action);
                	return;
                }
 				// �����������޸�������
 				Runnable action = new Runnable() {

 					@Override
 					public void run() {
 						// TODO Auto-generated method stub
 						btn_start_point.setVisibility(View.GONE);
 						btn_end_point.setVisibility(View.GONE);
                        btn_path_analyst.setVisibility(View.GONE);
                        btn_back.setVisibility(View.GONE);
 						btn_navi_simulation.setVisibility(View.VISIBLE);
 						btn_navi_real.setVisibility(View.VISIBLE);
 						bGuideEnable = true;
 						if(naviMode == 0){
                            btn_back.setVisibility(View.VISIBLE);
 			            	btn_navi_simulation.setVisibility(View.GONE);
 			            	btn_navi_real.setVisibility(View.GONE);
 			            	btn_stop_navigation.setVisibility(View.VISIBLE);
 							m_Navigation.startGuide(naviMode);
 							m_Map.refresh();
 						}
 						m_Map.refresh();                               // ·��������֮��ˢ�²�����ʾ·��
 					}
 				};
 				MainActivity.this.runOnUiThread(action);
 			}
 		}).start();

 	}
 	
	/**
	 * ��ʼģ�⵼��
	 */
    public void startNavigation() {
    	
    	if(naviMode == 0  && destPoint != null)
    		bGuideEnable = true;
    	if(!bGuideEnable ){
			SpeakPlugin.getInstance().playSound("���Ƚ���·������!");
			Toast.makeText(this, "���Ƚ���·������!", Toast.LENGTH_SHORT).show();
			return;
		}
    	m_Navigation.enablePanOnGuide(false);
		// ��ʼ����
		m_Navigation.startGuide(naviMode);
    }
   /**
    * ֹͣ����
    */
 public void stoptNavigationSimulation() {
    	
    	//������Ѿ��������ٴε����ֹͣ����
		if(m_Navigation.isGuiding()){
			m_Navigation.stopGuide();
			m_Navigation.enablePanOnGuide(true);
			btn_pause_navigation.setVisibility(View.GONE);
			btn_navi_simulation.setVisibility(View.VISIBLE);
			btn_navi_real.setVisibility(View.VISIBLE);
		}
    }

	/**
	 * Toast message
	 * @param mesg
	 */
	public void showInfo(String mesg) {
		Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!mExitEnable){
				Toast.makeText(this, "�ٰ�һ���˳�����", 1500).show();
				mExitEnable = true;
			}else{
				if(m_Navigation.isGuiding()){
					m_Navigation.stopGuide();									
				}
				m_Navigation.enablePanOnGuide(true);
				mApp.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * ��������״̬�������������󣬷���ѡ�񵼺�ģʽ״̬
	 */
	public void listenNaviStatus() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					if(m_Navigation != null){
						if(m_Navigation.isGuiding() == false  && btn_pause_navigation.getVisibility() == View.VISIBLE){
							Runnable action = new Runnable() {

		     					@Override
		     					public void run() {
		     						// TODO Auto-generated method stub
		     						if(btn_back != null){
		     						   btn_back.performClick();
		     						}
		     					}
		     				};
		     				MainActivity.this.runOnUiThread(action);
						}
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}