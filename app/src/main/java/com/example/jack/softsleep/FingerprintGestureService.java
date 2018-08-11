package com.example.jack.softsleep;

import android.util.Log;

import android.accessibilityservice.FingerprintGestureController;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class FingerprintGestureService extends AccessibilityService {
    private static final String TAG = FingerprintGestureService.class.getSimpleName();

    private GestureCallback gestureCallback;
    private boolean isAvailable;
    private int leftCount = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        return;
    }

    @Override
    public void onInterrupt() {
        return;
    }

    private void lockScreen() {
        Log.d(TAG, "Locking screen");
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
    }

    private void takeScreenshot() {
        Log.d(TAG, "Taking screenshot");
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);
    }

    private void onLeftSwipe() {
        leftCount++;

        if (leftCount > 2) {
            takeScreenshot();
            leftCount = 0;
        } else {
            scheduleResetGestureState();
        }
    }

    private void onRightSwipe() {
        if (leftCount == 1) {
            leftCount = 0;
            lockScreen();
        }
    }

    private void scheduleResetGestureState() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        leftCount = 0;
                    }
                },
                1000
        );
    }

    class GestureCallback extends FingerprintGestureController.FingerprintGestureCallback {
        public void onGestureDetected(int gesture) {
            switch (gesture) {
                case FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT:
                    onLeftSwipe();
                    break;
                case FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT:
                    onRightSwipe();
                    break;
            }
        }

        @Override
        public void onGestureDetectionAvailabilityChanged(boolean available) {
            Log.d(TAG, "Gesture detection availability changed");
            isAvailable = available;
        }
    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "Service Connected!");
        FingerprintGestureController contr = getFingerprintGestureController();
        isAvailable =
                contr.isGestureDetectionAvailable();

        if (gestureCallback != null) {
            // already watching for swipes
            return;
        }

        if (!isAvailable) {
            // fingerprint gestures currently unavailable
            return;
        }

        gestureCallback = new GestureCallback();

        if (gestureCallback != null) {
            Log.d(TAG, "Gesture callback registered");
            contr.registerFingerprintGestureCallback(gestureCallback, null);
        }
    }
}
