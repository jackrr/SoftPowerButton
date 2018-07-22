package com.example.jack.softsleep;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Config extends AppCompatActivity {

    DevicePolicyManager mDPM;
    ComponentName ownerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ownerActivity = new ComponentName(this, AdminReceiver.class);

        if (!this.isActiveAdmin()) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ownerActivity);
            startActivity(intent);
        }
    }

    public void lockScreen(View view) {
        mDPM.lockNow();
    }

    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(ownerActivity);
    }

    public static class AdminReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            Toast.makeText(context, "Status", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            showToast(context, "Enabled");
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            showToast(context, "Disabled");
        }
    }
}
