package com.a363531807.curriculumdesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    public static final String HOST="http://registersystem.sinaapp.com/registersystem/";
    //public static final String HOST="http://10.10.164.200:8000/registersystem/";
    public static final String TAG ="stqbill";
    public static final String USER_TYPE  = "0"; //0代表学生；
    public static final int LOGIN_RESULT_CODE =11;
    private String mIMEI;
    private String mIMSI;
    private String mAccount;
    private  CourseListAdapter mListAdapter;
    private List mCourseList;
    private ListView mCourselv;
    private MyHandler mMyHandler;
    private SwipeRefreshLayout  mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAccount=getIntent().getStringExtra("account");
        mMyHandler = new MyHandler();
        Log.i(TAG, mAccount);
        TelephonyManager _te = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mIMEI = _te.getDeviceId();
        mIMSI = _te.getSubscriberId();
        if(mIMSI ==null){
            Toast.makeText(this,"请检查您的SIM卡是否插入",Toast.LENGTH_LONG).show();
            finish();
        }
        initView();
        getResoursefromInternet();
    }
    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView _user_name = (TextView)findViewById(R.id.tv_user_name);
        _user_name.setText("我的名字");
        TextView  _user_sign = (TextView)findViewById(R.id.tv_user_sign);
        _user_sign.setText("我的签名");
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_content_main);
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new CourseListRunable()).start();
            }
        });

        mCourselv = (ListView)findViewById(R.id.lv_course_view);
        mCourselv.setOnItemLongClickListener(new SignLvOnItemLongClickListener());

    }
    public void getResoursefromInternet(){
        if(!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(true);
        }
        new Thread(new CourseListRunable()).start();
    }

    public List getCourseList(){
        String _url = HOST+"getcourselist/";
        try {
            List _resultlist = new ArrayList();
            JSONObject _js = new JSONObject();
            _js.put("account", mAccount);
            String _result= HttpURLProtocol.postjson(_url, _js.toString().getBytes());
            JSONArray _jsarray = new JSONArray(_result);
            if(_jsarray.optString(0).equals("ok")){
                int length = _jsarray.length();
                for (int i=1;i<length;i++){
                    Map _map = new HashMap();
                    _js =   _jsarray.optJSONObject(i);
                    Iterator _it = _js.keys();
                    String _key;
                    while (_it.hasNext()){
                        _key = (String)_it.next();
                        _map.put(_key,_js.optString(_key));
                    }
                   if (!_map.isEmpty()){
                       _resultlist.add(_map);
                   }
                }
            }
            return _resultlist;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.course_list) {

        } else if (id == R.id.my_site) {

        } else if (id == R.id.myRecord) {

        } else if (id == R.id.about) {

        } else if (id == R.id.communciated) {

        } else if (id == R.id.share_some) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    class CourseListRunable implements Runnable{

        @Override
        public void run() {
            mCourseList = getCourseList();
            if(mCourseList!=null&&!mCourseList.isEmpty())
                mMyHandler.sendEmptyMessage(1);
            else mMyHandler.sendEmptyMessage(0);
        }
    }
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    showMsg("更新失败");
                    break;
                case 1:
                    if (mListAdapter==null){
                        mListAdapter=new CourseListAdapter(MainActivity.this,mCourseList);
                        mCourselv.setAdapter(mListAdapter);
                    }else {
                        mListAdapter.updateList(mCourseList);
                    }
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    break;
                case 3:
                    switch (msg.arg1){
                        case 0:
                            showMsg("现在还不能签到哦！");
                            break;
                        case 1:
                            showMsg("您的签到密码有误哦！待会重试吧！");
                            break;
                        case 2:
                            showMsg("您的签到密码已超过有效期！");
                            break;
                        case 3:
                            showMsg("该设备已被其他用户使用！");
                            break;
                        case 4:
                            showMsg("该SIM卡已被其他用户使用！");
                            break;
                        case 5:
                            showMsg("恭喜您签到成功！");
                            getResoursefromInternet();
                            break;
                    }
            }
        }
    }
    class SignLvOnItemLongClickListener implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Map _item = (Map) mCourseList.get(position);
            if (_item.containsKey("isSign")&&!_item.get("isSign").equals("0")){
                Toast.makeText(MainActivity.this,"您已签到!",Toast.LENGTH_SHORT).show();
                return false;
            }
            View _view = LayoutInflater.from(MainActivity.this).inflate(R.layout.sign_dialog_layout,null);
            final NumberPicker[] numberPickers = new NumberPicker[4];
            numberPickers[0]= (NumberPicker)_view.findViewById(R.id.numberPicker1);
            numberPickers[1]= (NumberPicker)_view.findViewById(R.id.numberPicker2);
            numberPickers[2]= (NumberPicker)_view.findViewById(R.id.numberPicker3);
            numberPickers[3]= (NumberPicker)_view.findViewById(R.id.numberPicker4);
            for (NumberPicker picker:numberPickers) {
                picker.setMaxValue(9);
                picker.setMinValue(0);
                picker.setValue(0);
            }
            final RadioGroup radioGroup = (RadioGroup)_view.findViewById(R.id.radiogroup_sign);
            final int _position = position;
            new AlertDialog.Builder(MainActivity.this)
                    .setView(_view)
                    .setTitle("请选择签到随机码")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuilder _radom =new StringBuilder();
                            for (NumberPicker picker:numberPickers
                                    ) {
                                _radom.append(picker.getValue());
                            }
                            int signtype;
                            if(radioGroup.getCheckedRadioButtonId()==R.id.radioButton_ontime){
                                signtype = 1;
                            }else signtype = 2;
                            new Thread(new SigninRunnable(_radom.toString(),signtype,_position)).start();

                        }
                    }).show();
            return true;
        }
    }
    void showMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    String signCourse(String radom,int signtype,int position) {
        String _url = HOST + "studentSign/";
        try {
            Map _item = (Map) mCourseList.get(position);
            JSONObject _js = new JSONObject();
            _js.put("sign_type", ""+signtype);
            _js.put("random_number", radom);
            _js.put("classing_id", _item.get("classing_id"));
            _js.put("account", mAccount);
            _js.put("imei", mIMEI);
            _js.put("imsi", mIMSI);
            if (_item.containsKey("afclass_id")) {
                _js.put("afclass_id", _item.get("afclass_id"));
            }
            return  HttpURLProtocol.postjson(_url, _js.toString().getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    class SigninRunnable implements Runnable{
        String mRadom;
        int mSigntype;
        int mPosition;
        public SigninRunnable(String radom,int signtype,int position) {
            mRadom=radom;
            mSigntype=signtype;
            mPosition=position;
        }

        @Override
        public void run() {
          String result =   signCourse(mRadom,mSigntype,mPosition);
            try{
                JSONArray _ja=new JSONArray(result);
                Message _ms =new Message();
                _ms.what=3;
                _ms.arg1=_ja.getInt(0);
                mMyHandler.sendMessage(_ms);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
