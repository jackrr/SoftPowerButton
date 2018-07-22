package com.example.jack.softsleep;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.util.Log;

import android.accessibilityservice.FingerprintGestureController;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class FingerprintGestureService extends AccessibilityService {
    private static final String TAG = FingerprintGestureService.class.getSimpleName();
    enum Gesture { LEFT, RIGHT; }

    private FingerprintGestureController contr;
    private FingerprintGestureController.FingerprintGestureCallback gestureCallback;
    private boolean isAvailable;
    private Gesture lastGesture;

    @Override
    public void onCreate() {
        Log.d(TAG, "Created fingerprint service");
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
        return;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG,
                "accessibility event!");
        return;
    }

    private void lockScreen() {
        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDPM.lockNow();
    }

    private void scheduleResetGestureState() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        lastGesture = null;
                    }
                },
                250
        );
    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "Service Connected!");
        contr = getFingerprintGestureController();
        isAvailable =
                contr.isGestureDetectionAvailable();

        if (gestureCallback != null) {
            Log.d(TAG, "Gesture callback already registered");
            return;
        }

        if (!isAvailable) {
            Log.d(TAG, "Gesture unavailable");
            return;
        }

        gestureCallback =
                new FingerprintGestureController.FingerprintGestureCallback() {
                    @Override
                    public void onGestureDetected(int gesture) {
                        switch (gesture) {
                            case FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN:
                                Log.i(TAG,
                                        "DOWN");
                                break;
                            case FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT:
                                lastGesture = Gesture.LEFT;
                                scheduleResetGestureState();
                                Log.i(TAG,
                                        "LEFT");
                                break;
                            case FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT:
                                if (lastGesture == Gesture.LEFT) {
                                    Log.i(TAG, "WOULD SLEEP NOW");
                                    lockScreen();
                                }
                                Log.i(TAG,
                                        "RIGHT");
                                break;
                            case FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP:
                                Log.i(TAG,
                                        "UP");
                                break;
                            default:
                                Log.e(TAG,
                                        "Error: Unknown gesture type detected!");
                                break;
                        }
                    }

                    @Override
                    public void onGestureDetectionAvailabilityChanged(boolean available) {
                        Log.d(TAG, "Gesture detection availability changed");
                        isAvailable = available;
                    }
                };

        if (gestureCallback != null) {
            Log.d(TAG, "Gesture callback registered");
            contr.registerFingerprintGestureCallback(
                    gestureCallback, null);
        }
    }
}
