package com.ustcinfo.nineunlockpathbyliaction;

import android.app.Activity;
import android.os.Bundle;

import com.liaction.utils.LiactionCommonUtils;
import com.ustcinfo.nineunlockpathbyliaction.widget.LiactionNinePath;


public class MainActivity extends Activity {
    private LiactionNinePath mLiactionNinePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLiactionNinePath = (LiactionNinePath) findViewById(R.id.ninePath);
        mLiactionNinePath.setOnpwdChangeLister(new LiactionNinePath.OnPwdChangeLister() {
            @Override
            public void pwdChange(String pwd) {
                LiactionCommonUtils.LiactionUtils.showTestLog("MainActivity中获得密码为 :  " + pwd);
            }
        });
    }


}
