package com.example.haojia;
//上傳美食
import java.io.IOException;
import java.util.Calendar;
import java.util.List;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class InsertActivity extends Activity{
	//-- 變數宣告 --//
	//必填欄位元件宣告
	EditText r_name_edittext; //名稱
	EditText r_address_edittext; //地址後段
	//區域
	private ArrayAdapter<String> county_adapter;
	private ArrayAdapter<String> area_adapter;
	private Spinner county_spinner = null; //縣市Spinner
	private Spinner area_spinner = null; //鄉鎮區Spinner
	//county_spineer裡的內容 各縣市 
	private String[] county = new String[]{"請選擇縣市","臺北市","新北市","臺中市","臺南市","高雄市","基隆市",
										"宜蘭縣","花蓮縣","臺東縣","桃園縣","新竹市","新竹縣","苗栗縣",
										"彰化縣","南投縣","雲林縣","嘉義市","嘉義縣","屏東縣","金門縣",
										"澎湖縣","連江縣"}; 
	private String[] location_set = new String[]{"請選擇鄉鎮區"}; //存放預設值
	private String[][] area = new String[][]{{"請選擇鄉鎮區"}, //全區
	//臺北市		
	{"請選擇鄉鎮區","中正區","大同區","中山區","松山區","大安區","萬華區","信義區","士林區","北投區","內湖區","南港區","文山區"}, 
	//新北市
	{"請選擇鄉鎮區","萬里區","金山區","板橋區","汐止區","深坑區","石碇區","瑞芳區","平溪區","雙溪區","貢寮區","新店區","坪林區","烏來區","永和區",
	"中和區","土城區","三峽區","樹林區","鶯歌區","三重區","新莊區","泰山區","林口區","蘆洲區","五股區","八里區","淡水區","三芝區","石門區"},
	//臺中市 
	{"請選擇鄉鎮區","中區","東區","南區","西區","北區","北屯區","西屯區","南屯區"}, 
	//臺南市
	{"請選擇鄉鎮區","中西區","東區","南區","北區","安平區","安南區","永康區","歸仁區","新化區","左鎮區","玉井區","楠西區","南化區","仁德區","關廟區","龍崎區",
	"官田區","麻豆區","佳里區","西港區","七股區","將軍區","學甲區","北門區","新營區","後壁區","白河區","東山區","六甲區","下營區","柳營區","鹽水區",
	"善化區","大內區","山上區","新市區"," 安定區"},
	//高雄市
	{"請選擇鄉鎮區","新興區","前金區","苓雅區","鹽埕區","鼓山區","旗津區","前鎮區","三民區","楠梓區","小港區","左營區","仁武區","大社區","岡山區","路竹區","阿蓮區",
	"田寮區","燕巢區","橋頭區","梓官區","彌陀區","永安區","湖內區","鳳山區","大寮區","林園區","鳥松區","大樹區","旗山區","美濃區","六龜區","內門區",
	"杉林區","甲仙區","桃源區","那瑪夏","茂林區","茄萣區"},
	//基隆市
	{"請選擇鄉鎮區","仁愛區","信義區","中正區","中山區","安樂區","暖暖區","七堵區"},
	//宜蘭縣
	{"請選擇鄉鎮區","宜蘭市","頭城鎮","礁溪鄉","壯圍鄉","員山鄉","羅東鎮","三星鄉","大同鄉","五結鄉","冬山鄉","蘇澳鎮","南澳鄉"},
	//花蓮縣
	{"請選擇鄉鎮區","花蓮市","新城鄉","秀林鄉","吉安鄉","壽豐鄉","鳳林鎮","光復鄉","豐濱鄉","瑞穗鄉","萬榮鄉","玉里鎮","卓溪鄉","富里鄉"},
	//臺東縣
	{"請選擇鄉鎮區","臺東市","綠島鄉","蘭嶼鄉","延平鄉","卑南鄉","鹿野鄉","關山鎮","海端鄉","池上鄉","東河鄉","成功鎮","長濱鄉","太麻里","金峰鄉","大武鄉","達仁鄉"},
	//桃園縣
	{"請選擇鄉鎮區","中壢市","平鎮市","龍潭鄉","楊梅鎮","新屋鄉","觀音鄉","桃園市","龜山鄉","八德市","大溪鎮","復興鄉","大園鄉","蘆竹鄉"},
	//新竹市
	{"請選擇鄉鎮區","東區","北區","香山區"},	
	//新竹縣
	{"請選擇鄉鎮區","竹北市","湖口鄉","新豐鄉","新埔鎮","關西鎮","芎林鄉","寶山鄉","竹東鎮","五峰鄉","橫山鄉","尖石鄉","北埔鄉","峨眉鄉"},
	//苗栗縣
	{"請選擇鄉鎮區","竹南鎮","頭份鎮","三灣鄉","南庄鄉","獅潭鄉","後龍鎮","通霄鎮","苑裡鎮","苗栗市","造橋鄉","頭屋鄉","公館鄉","大湖鄉","泰安鄉","銅鑼鄉","三義鄉","西湖鄉","卓蘭鎮"},
	//彰化縣
	{"請選擇鄉鎮區","彰化市","芬園鄉","花壇鄉","秀水鄉","鹿港鎮","福興鄉","線西鄉","和美鎮","伸港鄉","員林鎮","社頭鄉","永靖鄉","埔心鄉","溪湖鎮","大村鄉","埔鹽鄉","田中鎮",
	"北斗鎮","田尾鄉","埤頭鄉","溪州鄉","竹塘鄉","二林鎮","大城鄉","芳苑鄉","二水鄉"},
	//南投縣
	{"請選擇鄉鎮區",	"南投市","中寮鄉","草屯鎮","國姓鄉","埔里鎮","仁愛鄉","名間鄉","集集鎮","水里鄉","魚池鄉","信義鄉","竹山鎮","鹿谷鄉"},
	//雲林縣
	{"請選擇鄉鎮區","斗南鎮","大埤鄉","虎尾鎮","土庫鎮","褒忠鄉","東勢鄉","台西鄉","崙背鄉"	,"麥寮鄉","斗六市","林內鄉","古坑鄉","莿桐鄉",
	"西螺鎮","二崙鄉","北港鎮","水林鄉","口湖鄉","四湖鄉","元長鄉"},
	//嘉義市
	{"請選擇鄉鎮區","東區","西區"},
	//嘉義縣
	{"請選擇鄉鎮區","番路鄉","梅山鄉","竹崎鄉","阿里山鄉","中埔鄉","大埔鄉","水上鄉","鹿草鄉","太保市","朴子市","東石鄉","六腳鄉","新港鄉","民雄鄉","大林鎮","溪口鄉","義竹鄉","布袋鎮"},
	//屏東縣
	{"請選擇鄉鎮區",	"屏東市","三地門","霧台鄉","瑪家鄉","九如鄉","里港鄉","高樹鄉","鹽埔鄉","長治鄉","麟洛鄉","竹田鄉","內埔鄉","萬丹鄉","潮州鎮","泰武鄉","來義鄉",
	"萬巒鄉","崁頂鄉","新埤鄉","南州鄉","林邊鄉","東港鎮","琉球鄉","佳冬鄉","新園鄉","枋寮鄉","枋山鄉","春日鄉","獅子鄉","車城鄉","牡丹鄉","恆春鎮","滿州鄉"},
	//金門縣 
	{"請選擇鄉鎮區",	"金沙鎮","金湖鎮","金寧鄉","金城鎮","烈嶼鄉","烏坵鄉"},
	//澎湖縣 
	{"請選擇鄉鎮區",	"馬公市","西嶼鄉","望安鄉","七美鄉","白沙鄉","湖西鄉"},
	//連江縣
	{"請選擇鄉鎮區",	"南竿鄉","北竿鄉","莒光鄉","東引鄉"}};
	int county_pos; //存放county spinner選取索引
	
	//類別
	private ArrayAdapter<String> group_adapter;
	private ArrayAdapter<String> class_adapter;	
	private Spinner group_spinner; //類別
	private Spinner class_spinner; //種類
	private String[] group = new String[]{"請選擇","早餐","中式","西式","日式","火鍋","點心","其他"};
	private String[] type_set = new String[]{"請選擇"}; //存放預設值
	private String[][] group_class = new String[][]{{"請選擇"}, //請選擇
													{"早餐"}, //早餐
													{"請選擇","麵食","飯食","熱炒","其他"}, //中式
													{"請選擇","牛排","義大利麵","套餐","速食","其他"}, //西式
													{"日式"}, //日式
													{"請選擇","小火鍋","涮涮鍋"}, //火鍋
													{"請選擇","甜點","咖啡廳","其他"}, //點心
													{"其他"}};//其他 
	int group_pos; //存放group spinner選取索引
	
	EditText r_telephone_edittext; //電話
	EditText r_opening_hours_edittext; //開店時間
	EditText r_closing_hours_edittext; //打烊時間
	EditText r_note_edittext; //備註
	EditText r_suggest_edittext; //建議菜色
	Button insert_btn; //送出鈕
	private Calendar calendar = Calendar.getInstance();

	//String變數,存放使用者輸入值
	String r_name;
	String r_address; //地址完整(含縣市 鄉鎮市區)
	String location_county; //縣市
	String location_area; //鄉鎮市區
	String address; //地址 (不含縣市 鄉鎮市區)
	String type_group; //種類群組
	String type_class; //群組類別
	String r_telephone; //電話
	String r_opening_hours; //開店時間
	String r_closing_hours; //打烊時間
	String r_note; //備註
	String r_suggest; //建議菜色
	
	//轉換經緯度副程式全域變數
	String Lat; //存放轉換後的緯度
	String Lng; //存放轉換後的經度
	
	String Sql_string; //存放SQL指令變數
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//StrictMode嚴格模式，連線SQL必加，最新支援版本3.0
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
        .detectDiskReads()  
        .detectDiskWrites()  
        .detectNetwork()  
        .penaltyLog()  
        .build());  
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
        .detectLeakedSqlLiteObjects()   
        .penaltyLog()  
        .penaltyDeath()  
        .build()); 		
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_activity);
		
		//元件連接
		//必填項
		r_name_edittext = (EditText)findViewById(R.id.r_name); //名稱
		r_address_edittext = (EditText)findViewById(R.id.r_address); //地址後段
		county_spinner = (Spinner)findViewById(R.id.county_spinner); //縣市
		area_spinner = (Spinner)findViewById(R.id.area_spinner); //區域
		group_spinner = (Spinner)findViewById(R.id.group_spinner); //類別
		class_spinner = (Spinner)findViewById(R.id.class_spinner); //種類
		//非必填
		r_telephone_edittext = (EditText)findViewById(R.id.r_telephone); //電話
		r_opening_hours_edittext = (EditText)findViewById(R.id.opening_hours); //開店時間
		r_closing_hours_edittext = (EditText)findViewById(R.id.closing_hours); //打烊時間
		r_note_edittext = (EditText)findViewById(R.id.r_note); //備註
		r_suggest_edittext = (EditText)findViewById(R.id.r_suggest); //建議菜色
		insert_btn = (Button)findViewById(R.id.insert_btn); //送出鈕
		
		//隱藏軟鍵盤方法一 ，強制永遠不會開啟
		//r_open & close_hours EditText永遠不彈出鍵盤
		r_opening_hours_edittext.setInputType(InputType.TYPE_NULL);
		r_closing_hours_edittext.setInputType(InputType.TYPE_NULL);
		
		//類別
		group_adapter=new ArrayAdapter<String>(this,R.layout.spinner_item,group); //設定group_spinner顯示的字串內容
		group_adapter.setDropDownViewResource(R.layout.drop_down_item); //設定下拉式選單顯示的版面
		group_spinner.setAdapter(group_adapter);
		group_spinner.setOnItemSelectedListener(group_spinnerlistener); //設定當group_spinner選取後的動作
		
		class_adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, type_set); //預先載入第二個下拉選單
		class_adapter.setDropDownViewResource(R.layout.drop_down_item); 
        class_spinner.setAdapter(class_adapter);
        class_spinner.setOnItemSelectedListener(class_spinnerlistener);		
		
		//區域
		county_adapter=new ArrayAdapter<String>(this,R.layout.spinner_item,county); //設定county_spinner顯示的字串內容 
		county_adapter.setDropDownViewResource(R.layout.drop_down_item); //設定下拉式選單顯示的版面
		county_spinner.setAdapter(county_adapter);
		county_spinner.setOnItemSelectedListener(county_spinnerlistener); //設定當county_spinner選取後的動作
		
		area_adapter=new ArrayAdapter<String>(this,R.layout.spinner_item,location_set); //預先載入第二個下拉選單
		area_adapter.setDropDownViewResource(R.layout.drop_down_item); 
		area_spinner.setAdapter(area_adapter);
		area_spinner.setOnItemSelectedListener(area_spinnerlistener);
		
		//r_telephone_edittext軟鍵盤 ，按下Enter後隱藏
		r_telephone_edittext.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_DONE ||   
			              actionId == EditorInfo.IME_ACTION_NEXT)
				{
					//隱藏軟鍵盤方法二，非強制，還可再開啟
					InputMethodManager keyboard_hide =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	        		keyboard_hide.hideSoftInputFromWindow(r_telephone_edittext.getWindowToken(), 0); 
				}
				return false;
			}
		});
		
		//r_opening_hours_edittext開始時間監聽 
		r_opening_hours_edittext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				//calendar.setTimeInMillis(System.currentTimeMillis());
				int hour = calendar.get(Calendar.HOUR_OF_DAY); //取得系統目前時間
				int minute = calendar.get(Calendar.MINUTE); //取得系統目前時間
				//TimePickerDialog按鈕監聽
				TimePickerDialog TimeSetDialog;
				TimeSetDialog = new TimePickerDialog(InsertActivity.this,new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// TODO Auto-generated method stub
						//%02d 2表示寬度 整數不夠補上0
						r_opening_hours_edittext.setText(String.format("%02d:%02d",hourOfDay,minute));
					}
				},hour,minute,true); //true設定24小時制
				TimeSetDialog.setTitle("設定結束營業時間");
				TimeSetDialog.show();
			}
		});

		//r_closing_hours_edittext結束時間監聽 
		r_closing_hours_edittext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//calendar.setTimeInMillis(System.currentTimeMillis());
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				TimePickerDialog TimeSetDialog;
				//TimePickerDialog按鈕監聽
				TimeSetDialog = new TimePickerDialog(InsertActivity.this,new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// TODO Auto-generated method stub
						//%02d 2表示寬度 整數不夠補上0
						r_closing_hours_edittext.setText(String.format("%02d:%02d",hourOfDay,minute));
					}
				},hour,minute,true); //true設定24小時制
				TimeSetDialog.setTitle("設定結束營業時間");
				TimeSetDialog.show();
			}
		});
			
		//insert_btn監聽
		insert_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			// TODO Auto-generated method stub insert_btn
        		InputMethodManager keyboard_hide =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        		keyboard_hide.hideSoftInputFromWindow(insert_btn.getWindowToken(), 0); 
	
				AlertDialog.Builder insert_builder = new AlertDialog.Builder(InsertActivity.this);
				insert_builder.setTitle("美食上傳"); //標題
				insert_builder.setMessage("您確定要上傳此餐廳資料?") //下方內容 
				       	//設定右邊按鈕"否",及按鈕按下監聽
					   	.setPositiveButton("否", new DialogInterface.OnClickListener(){
					   		//按下後執行
					   		@Override 
							public void onClick(DialogInterface dialog,int which) {
								// TODO Auto-generated method stub
							}
					   	})
					   	//設定左邊按鈕"是",及按鈕按下監聽
						.setNegativeButton("是", new DialogInterface.OnClickListener(){
							//按下後執行
							@Override
							public void onClick(DialogInterface dialog,int which) {
								// TODO Auto-generated method stub	
								//將EditText值 放入變數
								r_telephone = r_telephone_edittext.getText().toString(); //電話
								r_opening_hours = r_opening_hours_edittext.getText().toString(); //開店時間
								r_closing_hours = r_closing_hours_edittext.getText().toString(); //打烊時間
								r_note = r_note_edittext.getText().toString(); //備註
								r_suggest = r_suggest_edittext.getText().toString(); //建議菜色
				
								//非必填欄位資料檢查-呼叫副程式
								r_telephone = EqualsData(r_telephone); //電話
								r_opening_hours = EqualsData(r_opening_hours); //開店時間
								r_closing_hours = EqualsData(r_closing_hours); //打烊時間
								r_note = EqualsData(r_note); //備註
								r_suggest = EqualsData(r_suggest); //建議菜色
								
								//必填欄位資料檢查
								r_name = r_name_edittext.getText().toString(); //名稱
								address = r_address_edittext.getText().toString(); //地址 (不含縣市 鄉鎮市區)
								r_address = location_county + location_area + address; //組合為完整地址
								turnLatLng(r_address); //呼叫地址轉經緯度副程式,將r_address傳入
									
								if("".equals(r_name) == true || //名稱錯誤
					   				location_county.equals("請選擇縣市") == true || //地址縣市Spinner錯誤
					   				location_area.equals("請選擇鄉鎮區") == true || //地址區域Spinner錯誤
					   				"".equals(address) == true || //地址editText錯誤
					   				"".equals(Lat) == true || "".equals(Lng) == true || //經緯度空值錯誤
					   				type_group.equals("請選擇") == true || type_class.equals("請選擇") == true)  //類別錯誤
								{
					   				//錯誤執行 AlertDialog訊息顯示資料有誤
					   				AlertDialog.Builder databug_builder = new AlertDialog.Builder(InsertActivity.this);
					   				databug_builder.setTitle("錯誤"); //標題
					   				databug_builder.setMessage("您輸入的資料有誤！") 
					   							.setPositiveButton("確定", new DialogInterface.OnClickListener(){
													@Override
													public void onClick(DialogInterface dialog,int which) {
														// TODO Auto-generated method stub
													}	
					   							});
					   				databug_builder.show();	
					   			}
					   			else{ //資料無誤 執行新增
					   				StringBuffer Latitude = new StringBuffer(Lat); //緯度多於字串刪除
					   				Latitude.delete(9, 10);
					   				StringBuffer Longitude = new StringBuffer(Lng); //經度多於字串刪除
					   				Longitude.delete(10, 11);
					   				//驗證經緯度字串整理 是否成功
									//Toast.makeText(InsertActivity.this,Lat+","+Lng+"\n"+Latitude+","+Longitude,Toast.LENGTH_SHORT).show(); 
					   				
					   				//顯示轉換及整理成功後的經緯度
					   				Toast.makeText(InsertActivity.this,"餐廳經緯度："+Latitude+","+Longitude,Toast.LENGTH_SHORT).show();
					   				
					   				//執行SQL Update語法進行新增
					   				Sql_string ="INSERT INTO restaurant_insert" +
    "(`R_Name`,`R_Address`,`R_Telephone`,`R_Type`,`R_Latitude`,`R_Longitude`,`R_OpeningHours`,`R_ClosingHours`,`R_Note`,`R_Suggest`)" +
	"VALUES('"+ r_name +"'," + //名稱 (必填)
	"'"+ r_address +"'," + //地址 (必填)
	r_telephone +"," + //電話 
	"(SELECT R_TypeID FROM `restaurant_type` WHERE `R_TypeGroup`='"+ type_group +"' AND `R_TypeClass` = '"+ type_class +"')," + //種類 (必填)
	"'"+ Latitude +"'," + //緯度 (必填)
	"'"+ Longitude +"'," + //經度 (必填)
	r_opening_hours +"," + //開店時間
	r_closing_hours +"," + //打烊時間
	r_note +"," + //備註
	r_suggest +")"; //建議菜色
	//--以上SQL Update語法結束--//
					   				String result = DBconnection.executeQuery(Sql_string); //將語法傳送
					   				Log.e("log_tag","JSON格式錯誤"+result.toString());	
					   				
					   				//顯示上傳完成訊息
					   				AlertDialog.Builder insertdata_success_builder = new AlertDialog.Builder(InsertActivity.this);
					   				insertdata_success_builder.setTitle("美食上傳"); //標題
					   				insertdata_success_builder.setMessage("資料上傳完成，請靜待審核！\n"+"確定後，跳轉回首頁。") 
					   							.setPositiveButton("確定", new DialogInterface.OnClickListener(){
													@Override
													public void onClick(DialogInterface dialog,int which) {
														// TODO Auto-generated method stub
														InsertActivity.this.finish(); //結束InsertActivity頁面
													}	
					   							});
					   				insertdata_success_builder.show();	
					   			}
							}
						});
				insert_builder.show();
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { //返回實體按鍵 監聽
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK )  
        {
			AlertDialog isExit_builder = new AlertDialog.Builder(InsertActivity.this).create();
	        isExit_builder.setTitle("美食上傳");  
	        isExit_builder.setMessage("資料尚未傳送，您確定要退出嗎");
	        isExit_builder.setButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
	        isExit_builder.setButton2("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					InsertActivity.this.finish(); //結束InsertActivity頁面
				}
			});        
	        isExit_builder.show();
        }  
		return false;
	}
	
	//種類
	//group_spinner監聽
	private OnItemSelectedListener group_spinnerlistener = new OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent, View v, int position,long id){
			type_group = group_spinner.getSelectedItem().toString(); //取得選取值
			group_pos = group_spinner.getSelectedItemPosition(); //讀取第一個下拉選單是選擇第幾個
            //重新產生新的Adapter，用的是二維陣列group_class[group_pos]
            set_class();
		}
		public void onNothingSelected(AdapterView<?> arg0){
		}
	};
    	
	private void set_class(){
		class_adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, group_class[group_pos]);//載入type第二個下拉選單Spinner
		class_adapter.setDropDownViewResource(R.layout.drop_down_item); 
        class_spinner.setAdapter(class_adapter);
	}
	//class_spinner監聽
	private OnItemSelectedListener class_spinnerlistener = new OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent, View v, int position,long id){
			type_class = class_spinner.getSelectedItem().toString(); //取得選取值
		}
		public void onNothingSelected(AdapterView<?> arg0){
		}
	};
	//區域
	//county_spinner監聽
	private OnItemSelectedListener county_spinnerlistener = new OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent, View v, int position,long id){
			location_county = county_spinner.getSelectedItem().toString(); //取得選取值
			county_pos = county_spinner.getSelectedItemPosition(); //讀取第一個下拉選單是選擇第幾個
            //重新產生新的Adapter，用的是二維陣列area[county_pos]
			set_area();
		}
		public void onNothingSelected(AdapterView<?> arg0){
		}
	};	
	private void set_area(){
		area_adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, area[county_pos]);
		area_adapter.setDropDownViewResource(R.layout.drop_down_item); 
        //載入location第二個下拉選單Spinner
		area_spinner.setAdapter(area_adapter);
	}
	//area_spinner監聽
	private OnItemSelectedListener area_spinnerlistener = new OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent, View v, int position,long id){
			location_area = area_spinner.getSelectedItem().toString(); //取得選取值
		}
		public void onNothingSelected(AdapterView<?> arg0){
		}
	};		
	//地址轉經緯度副程式
	private void turnLatLng(String locationAddress){
		Geocoder geocoder = new Geocoder(getBaseContext());
		List<Address> addressList = null;
		//預設經緯度空值，可用判斷轉換有無成功或錯誤
		Lat = ""; 
		Lng = ""; 
		
		try {
			// 解譯地名/地址後可能產生多筆位置資訊，所以回傳List<Address>
			addressList = geocoder.getFromLocationName(locationAddress,1);
		}
		// 如果無法連結到提供服務的伺服器，會拋出IOException
		catch (IOException e) {
			Log.e("GeocoderActivity", e.toString());
		}
		
		if (addressList == null || addressList.isEmpty()) {
			Toast.makeText(getBaseContext(), "經緯度轉換錯誤",Toast.LENGTH_LONG).show();
			//LatLngBUG = "bug";
		}
		else {
			// 因為當初限定只回傳1筆，所以只要取得第1個Address物件即可
			Address address = addressList.get(0);
			// Address取出緯經度,轉字串型態放至變數裡
			Lat = String.valueOf(address.getLatitude());
			Lng = String.valueOf(address.getLongitude());
		}
	}
	//非必填欄位資料檢查副程式
	private String EqualsData(String Data){
		
		String Reset_Data = "";
		try{
			if("".equals(Data) == true){
				Reset_Data = "NULL";
			}
			else{
				Reset_Data = "'"+ Data +"'";
			}
		}
		catch(Exception e) {
			Log.e("非必填欄位檢查錯誤",e.toString());//LogCatch
		}
		return Reset_Data;
		
	}
}
