package com.example.haojia;
//餐廳搜尋
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;




public class SearchActivity extends Activity{	
	//透過bundle傳送至下頁
	EditText search_editText; //關鍵字
	String location_county; //縣市
	String location_area; //鄉鎮市區
	String type_group; //種類群組
	String type_class; //群組類別
	
	//類別
	private ArrayAdapter<String> group_adapter;
	private ArrayAdapter<String> class_adapter;
	private Spinner group_spinner = null; //群組Spinner
	private Spinner class_spinner = null; //類別Spinner
	private String[] group = new String[]{"全部","早餐","中式","西式","日式","火鍋","點心","其他"};
	private String[] type_set = new String[]{"全部"}; //存放預設值
	private String[][] group_class = new String[][]{{"全部"},
													{"全部"}, //早餐
													{"全部","麵食","飯食","熱炒","其他"}, //中式
													{"全部","牛排","義大利麵","套餐","速食","其他"}, //西式
													{"全部"}, //日式
													{"全部","小火鍋","涮涮鍋"}, //火鍋
													{"全部","甜點","咖啡廳","其他"}, //點心
													{"全部"}};//其他 
	int group_pos; //存放group spinner選取索引
	
	//區域
	private ArrayAdapter<String> county_adapter;
	private ArrayAdapter<String> area_adapter;
	private Spinner county_spinner = null; //縣市Spinner
	private Spinner area_spinner = null; //鄉鎮區Spinner
	//county_spineer裡的內容 各縣市 
	private String[] county = new String[]{"全區","臺北市","新北市","臺中市","臺南市","高雄市","基隆市",
										"宜蘭縣","花蓮縣","臺東縣","桃園縣","新竹市","新竹縣","苗栗縣",
										"彰化縣","南投縣","雲林縣","嘉義市","嘉義縣","屏東縣","金門縣",
										"澎湖縣","連江縣"}; 
	private String[] location_set = new String[]{"全區"}; //存放預設值
	private String[][] area = new String[][]{{"全區"}, //全區
	//臺北市		
	{"全區","中正區","大同區","中山區","松山區","大安區","萬華區","信義區","士林區","北投區","內湖區","南港區","文山區"}, 
	//新北市
	{"全區","萬里區","金山區","板橋區","汐止區","深坑區","石碇區","瑞芳區","平溪區","雙溪區","貢寮區","新店區","坪林區","烏來區","永和區",
	"中和區","土城區","三峽區","樹林區","鶯歌區","三重區","新莊區","泰山區","林口區","蘆洲區","五股區","八里區","淡水區","三芝區","石門區"},
	//臺中市 
	{"全區","中區","東區","南區","西區","北區","北屯區","西屯區","南屯區"}, 
	//臺南市
	{"全區","中西區","東區","南區","北區","安平區","安南區","永康區","歸仁區","新化區","左鎮區","玉井區","楠西區","南化區","仁德區","關廟區","龍崎區",
	"官田區","麻豆區","佳里區","西港區","七股區","將軍區","學甲區","北門區","新營區","後壁區","白河區","東山區","六甲區","下營區","柳營區","鹽水區",
	"善化區","大內區","山上區","新市區"," 安定區"},
	//高雄市
	{"全區","新興區","前金區","苓雅區","鹽埕區","鼓山區","旗津區","前鎮區","三民區","楠梓區","小港區","左營區","仁武區","大社區","岡山區","路竹區","阿蓮區",
	"田寮區","燕巢區","橋頭區","梓官區","彌陀區","永安區","湖內區","鳳山區","大寮區","林園區","鳥松區","大樹區","旗山區","美濃區","六龜區","內門區",
	"杉林區","甲仙區","桃源區","那瑪夏","茂林區","茄萣區"},
	//基隆市
	{"全區","仁愛區","信義區","中正區","中山區","安樂區","暖暖區","七堵區"},
	//宜蘭縣
	{"全區","宜蘭市","頭城鎮","礁溪鄉","壯圍鄉","員山鄉","羅東鎮","三星鄉","大同鄉","五結鄉","冬山鄉","蘇澳鎮","南澳鄉"},
	//花蓮縣
	{"全區","花蓮市","新城鄉","秀林鄉","吉安鄉","壽豐鄉","鳳林鎮","光復鄉","豐濱鄉","瑞穗鄉","萬榮鄉","玉里鎮","卓溪鄉","富里鄉"},
	//臺東縣
	{"全區","臺東市","綠島鄉","蘭嶼鄉","延平鄉","卑南鄉","鹿野鄉","關山鎮","海端鄉","池上鄉","東河鄉","成功鎮","長濱鄉","太麻里","金峰鄉","大武鄉","達仁鄉"},
	//桃園縣
	{"全區","中壢市","平鎮市","龍潭鄉","楊梅鎮","新屋鄉","觀音鄉","桃園市","龜山鄉","八德市","大溪鎮","復興鄉","大園鄉","蘆竹鄉"},
	//新竹市
	{"全區","東區","北區","香山區"},	
	//新竹縣
	{"全區","竹北市","湖口鄉","新豐鄉","新埔鎮","關西鎮","芎林鄉","寶山鄉","竹東鎮","五峰鄉","橫山鄉","尖石鄉","北埔鄉","峨眉鄉"},
	//苗栗縣
	{"全區","竹南鎮","頭份鎮","三灣鄉","南庄鄉","獅潭鄉","後龍鎮","通霄鎮","苑裡鎮","苗栗市","造橋鄉","頭屋鄉","公館鄉","大湖鄉","泰安鄉","銅鑼鄉","三義鄉","西湖鄉","卓蘭鎮"},
	//彰化縣
	{"全區","彰化市","芬園鄉","花壇鄉","秀水鄉","鹿港鎮","福興鄉","線西鄉","和美鎮","伸港鄉","員林鎮","社頭鄉","永靖鄉","埔心鄉","溪湖鎮","大村鄉","埔鹽鄉","田中鎮",
	"北斗鎮","田尾鄉","埤頭鄉","溪州鄉","竹塘鄉","二林鎮","大城鄉","芳苑鄉","二水鄉"},
	//南投縣
	{"全區",	"南投市","中寮鄉","草屯鎮","國姓鄉","埔里鎮","仁愛鄉","名間鄉","集集鎮","水里鄉","魚池鄉","信義鄉","竹山鎮","鹿谷鄉"},
	//雲林縣
	{"全區","斗南鎮","大埤鄉","虎尾鎮","土庫鎮","褒忠鄉","東勢鄉","台西鄉","崙背鄉","麥寮鄉","斗六市","林內鄉","古坑鄉","莿桐鄉",
	"西螺鎮","二崙鄉","北港鎮","水林鄉","口湖鄉","四湖鄉","元長鄉"},
	//嘉義市
	{"全區","東區","西區"},
	//嘉義縣
	{"全區","番路鄉","梅山鄉","竹崎鄉","阿里山鄉","中埔鄉","大埔鄉","水上鄉","鹿草鄉","太保市","朴子市","東石鄉","六腳鄉","新港鄉","民雄鄉","大林鎮","溪口鄉","義竹鄉","布袋鎮"},
	//屏東縣
	{"全區",	"屏東市","三地門","霧台鄉","瑪家鄉","九如鄉","里港鄉","高樹鄉","鹽埔鄉","長治鄉","麟洛鄉","竹田鄉","內埔鄉","萬丹鄉","潮州鎮","泰武鄉","來義鄉",
	"萬巒鄉","崁頂鄉","新埤鄉","南州鄉","林邊鄉","東港鎮","琉球鄉","佳冬鄉","新園鄉","枋寮鄉","枋山鄉","春日鄉","獅子鄉","車城鄉","牡丹鄉","恆春鎮","滿州鄉"},
	//金門縣 
	{"全區",	"金沙鎮","金湖鎮","金寧鄉","金城鎮","烈嶼鄉","烏坵鄉"},
	//澎湖縣 
	{"全區",	"馬公市","西嶼鄉","望安鄉","七美鄉","白沙鄉","湖西鄉"},
	//連江縣
	{"全區",	"南竿鄉","北竿鄉","莒光鄉","東引鄉"}};
	int county_pos; //存放county spinner選取索引
	
	//搜尋Button
	Button search_btn; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		
		//元件連接
		search_btn = (Button)findViewById(R.id.search_btn); //搜尋Button
		search_editText = (EditText)findViewById(R.id.search_editText); //關鍵字輸入欄
		county_spinner = (Spinner)findViewById(R.id.county_spinner); //縣市Spinner
		area_spinner = (Spinner)findViewById(R.id.area_spinner); //鄉鎮區Spinner
		group_spinner = (Spinner)findViewById(R.id.group_spinner); //類別群組Spinner
		class_spinner = (Spinner)findViewById(R.id.class_spinner); //種類spinner
		
		//區域
		county_adapter=new ArrayAdapter<String>(this,R.layout.spinner_item,county); //設定county_spinner顯示的字串內容 
		county_adapter.setDropDownViewResource(R.layout.drop_down_item); //設定下拉式選單顯示的版面
		county_spinner.setAdapter(county_adapter);
		county_spinner.setOnItemSelectedListener(county_spinnerlistener); //設定當county_spinner選取後的動作
		
		area_adapter=new ArrayAdapter<String>(this,R.layout.spinner_item,location_set); //預先載入第二個下拉選單
		area_adapter.setDropDownViewResource(R.layout.drop_down_item); 
		area_spinner.setAdapter(area_adapter);
		area_spinner.setOnItemSelectedListener(area_spinnerlistener);
		
		//類別
		group_adapter=new ArrayAdapter<String>(this,R.layout.spinner_item,group); //設定group_spinner顯示的字串內容
		group_adapter.setDropDownViewResource(R.layout.drop_down_item); //設定下拉式選單顯示的版面
		group_spinner.setAdapter(group_adapter);
		group_spinner.setOnItemSelectedListener(group_spinnerlistener); //設定當group_spinner選取後的動作
		
		class_adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, type_set); //預先載入第二個下拉選單
		class_adapter.setDropDownViewResource(R.layout.drop_down_item); 
        class_spinner.setAdapter(class_adapter);
        class_spinner.setOnItemSelectedListener(class_spinnerlistener);
        
        //按下虛擬鍵盤Enter執行搜尋
        search_editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);//設置虛擬鍵盤 右下角Enter功能
        search_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				//當actionId等於ACTION_SEARCH時觸發，功能等於按下enter直接進行搜尋
				if (actionId == EditorInfo.IME_ACTION_SEARCH)     
                {
					//如下程式碼 與search_btn功能相同
					//click search_btn後,隱藏鍵盤........有實用！！
	        		InputMethodManager keyboard_hide =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	        		keyboard_hide.hideSoftInputFromWindow(search_editText.getWindowToken(), 0); 
	        		
	        		//傳送字串至SeaechList.java
	        		Intent search_lsit_Intent =new Intent();
	        		search_lsit_Intent.setClass(SearchActivity.this,SearchList.class);
	        		//宣告要傳送的變數(並放入關鍵字 地點 類別) 
	        		Bundle search_bundle = new Bundle();
	        		search_bundle.putString("search_keyword",search_editText.getText().toString());
	        		search_bundle.putString("type_group",type_group); //群組 
	        		search_bundle.putString("type_class",type_class); //種類
	        		search_bundle.putString("location_county",location_county); //縣市
	        		search_bundle.putString("location_area",location_area); //鄉鎮市區
	        		search_lsit_Intent.putExtras(search_bundle);
	        		startActivity(search_lsit_Intent);
                }
				// TODO Auto-generated method stub
				return false;
			}
		});
        
		//搜尋Button監聽,並將值傳送至下一頁
		search_btn.setOnClickListener(new Button.OnClickListener()
        {	
        	public void onClick(View v){
        		//click search_btn後,隱藏鍵盤........有實用！！
        		InputMethodManager keyboard_hide =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        		keyboard_hide.hideSoftInputFromWindow(search_btn.getWindowToken(), 0); 
        		
        		//傳送字串至seaech_list
        		Intent search_lsit_Intent =new Intent();
        		search_lsit_Intent.setClass(SearchActivity.this,SearchList.class);
        		//宣告要傳送的變數(並放入關鍵字 地點 類別) 
        		Bundle search_bundle = new Bundle();
        		search_bundle.putString("search_keyword",search_editText.getText().toString());
        		search_bundle.putString("type_group",type_group); //群組 
        		search_bundle.putString("type_class",type_class); //種類
        		search_bundle.putString("location_county",location_county); //縣市
        		search_bundle.putString("location_area",location_area); //鄉鎮市區
        		search_lsit_Intent.putExtras(search_bundle);
        		startActivity(search_lsit_Intent);
        	}
        });	
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
	
	
}