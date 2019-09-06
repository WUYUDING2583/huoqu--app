package com.yuyi.family.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.yuyi.family.R;
import com.yuyi.family.test.service.TestAccessibilityService;

public class TestAccessbilityServiceActivity extends AppCompatActivity {

    private static final String TAG = "TestAccessbilityServiceActivity";
    private Intent intent;
    private int count=0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessbility_service);

        Button btn=(Button)findViewById(R.id.autoBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                System.out.println("我被点击了"+count+"次");
            }
        });

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAccessibilitySettingsOn(TestAccessbilityServiceActivity.this, TestAccessibilityService.class.getCanonicalName())) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                } else {
                    intent = new Intent(TestAccessbilityServiceActivity.this, TestAccessibilityService.class);
                    startService(intent);
                }
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent != null) {
                    stopService(intent);
                }
            }
        });
    }

    /**
     * 检测辅助功能是否开启
     *
     * @param mContext
     * @return boolean
     */
    private boolean isAccessibilitySettingsOn(Context mContext, String serviceName) {
        int accessibilityEnabled = 0;
        // 对应的服务
        final String service = getPackageName() + "/" + serviceName;
        //Log.i(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            System.out.println(TAG+" accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            System.out.println(TAG+" Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            System.out.println(TAG+" ***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    System.out.println(TAG+" -------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        System.out.println(TAG+" We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            System.out.println(TAG+" ***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
}
