package com.yong.screensaver;

import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.content.*;
import android.widget.*;
import com.fsn.cauly.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements CaulyCloseAdListener
{
	private static final String APP_CODE = "f6g1Nu7e";
 	CaulyCloseAd mCloseAd ;
	SharedPreferences prefs;
	SharedPreferences.Editor ed;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		RadioGroup unlockMethodRadio = findViewById(R.id.unlockMethod);
		RadioGroup startMethodRadio = findViewById(R.id.startMethod);
		prefs = getApplicationContext().getSharedPreferences("androesPrefName", MODE_PRIVATE);
		ed = prefs.edit();
		ed.apply();
		switch(prefs.getInt("startMethod",0)){
			case 0:
				startMethodRadio.check(R.id.notiButton);
				break;
			case 1:
				startMethodRadio.check(R.id.floatingButton);
				break;
		}
		switch(prefs.getInt("unlockMethod",1)){
			case 0:
			case 1:
				unlockMethodRadio.check(R.id.doubleTap);
				break;
			case 2:
				unlockMethodRadio.check(R.id.longTap);
				break;
		}
		CaulyAdInfo closeAdInfo = new CaulyAdInfoBuilder(APP_CODE).build();
		mCloseAd = new CaulyCloseAd();
		mCloseAd.setButtonText("취소", "종료");
		mCloseAd.setDescriptionText("종료하시겠습니까?");
		mCloseAd.setAdInfo(closeAdInfo);
		mCloseAd.setCloseAdListener(this); 
		mCloseAd.disableBackKey();
		if(!prefs.getBoolean("ad_removed",false)){
			showBanner();
		}else{
			LinearLayout layout = findViewById(R.id.mainLayout);
			layout.setVisibility(View.GONE);
		}
    }
	
	private void showBanner(){
		LinearLayout layout = findViewById(R.id.mainLayout);
		CaulyAdInfo adInfo= new CaulyAdInfoBuilder("TOeplGZT").effect("FadeIn").reloadInterval(1).enableDefaultBannerAd(true).build();
		CaulyAdView adView = new CaulyAdView(this);
		adView.setAdInfo(adInfo);
		layout.addView(adView,0);
	}
	
	@Override
	protected void onResume() {
		super.onResume(); 
		if (mCloseAd != null)
 			mCloseAd.resume(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(!prefs.getBoolean("ad_removed",false)){
				if (mCloseAd.isModuleLoaded())
				{
					mCloseAd.show(this);
				} 
				else
				{
					showDefaultClosePopup();
				}
			}else{
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showDefaultClosePopup()
	{
		new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("종료 하시겠습니까?")
			.setPositiveButton("예", new DialogInterface.OnClickListener() {
 			    @Override
 			    public void onClick(DialogInterface dialog, int which) {
					finish();
 			    }
			})
			.setNegativeButton("아니요",null)
			.show();
 	}

	@Override
 	public void onFailedToReceiveCloseAd(CaulyCloseAd ad, int errCode,String errMsg) {
 	}
 	@Override
 	public void onLeaveCloseAd(CaulyCloseAd ad) {
 	}
 	@Override
 	public void onReceiveCloseAd(CaulyCloseAd ad, boolean isChargable) {

 	}	
 	@Override
 	public void onLeftClicked(CaulyCloseAd ad) {
 	}	
 	@Override
 	public void onRightClicked(CaulyCloseAd ad) {
 		finish();
 	}
 	@Override
 	public void onShowedCloseAd(CaulyCloseAd ad, boolean isChargable) {		
 	}
	
	public void start(View v){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, 0);
		}else{
			switch(prefs.getInt("startMethod",0)){
				case 0:
					startService(new Intent(this, NotiService.class));
					break;
				case 1:
					startService(new Intent(this, FloatingService.class));
					break;
			}
		}
	}
	
	public void stop(View v){
		switch(prefs.getInt("startMethod",0)){
			case 0:
				stopService(new Intent(this, NotiService.class));
				break;
			case 1:
				stopService(new Intent(this, FloatingService.class));
				break;
		}
	}
	
	public void donate(View v){
		startActivity(new Intent(this, BillingActivity.class));
	}
	
	public void info(View v){
		startActivity(new Intent(this, InfoActivity.class));
	}
	
	public void doubleTap(View v){
		ed.remove("unlockMethod");
		ed.apply();
		ed.putInt("unlockMethod", 1);
		ed.apply();
	}
	
	public void longTap(View v){
		ed.remove("unlockMethod");
		ed.apply();
		ed.putInt("unlockMethod", 2);
		ed.apply();
	}
	
	public void notiButton(View v){
		stopService(new Intent(this, FloatingService.class));
		stopService(new Intent(this, NotiService.class));
		ed.remove("startMethod");
		ed.apply();
		ed.putInt("startMethod", 0);
		ed.apply();
	}

	public void floatingButton(View v){
		stopService(new Intent(this, FloatingService.class));
		stopService(new Intent(this, NotiService.class));
		ed.remove("startMethod");
		ed.apply();
		ed.putInt("startMethod", 1);
		ed.apply();
	}
}
