package com.example.systemdialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private AlertDialog alertDialog;
    private int dialogWidth = WindowManager.LayoutParams.WRAP_CONTENT;
    private int dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT;

    private Button button1;
    private Button button2;
    private Button button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 显示自定义 Dialog
        if (savedInstanceState != null) {
            // 恢复之前的 Dialog
            showSystemAlertDialog(savedInstanceState.getInt("dialog_width"), savedInstanceState.getInt("dialog_height"));
        } else {
            showSystemAlertDialog(dialogWidth, dialogHeight); // 使用默认值
        }
    }

    private void showSystemAlertDialog(int width, int height ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setTitle("系统级对话框")
                .setMessage("这是一个系统级别的AlertDialog")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 点击确定的操作
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                    dialog = null;
                });
        // 1. 使用 LayoutInflater 加载自定义布局
        View customView = getLayoutInflater().inflate(R.layout.button, null);
        builder.setView(customView);

        alertDialog = builder.create();
        button1 = customView.findViewById(R.id.button1);
        button2 = customView.findViewById(R.id.button2);
        button3 = customView.findViewById(R.id.button3);
        Button buttonok = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button buttoncancle = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (button1 != null && button2!=null && button3 != null) {
            // 判断系统是否处于暗模式
            if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                // 暗模式
                button1.setBackgroundResource(R.drawable.button_dialogx_ios_bottom_night);
                button2.setBackgroundResource(R.drawable.button_dialogx_ios_bottom_night);
//                button3.setBackgroundResource(R.drawable.button_dialogx_ios_bottom_night);

            } else {
                // 亮模式
                button1.setBackgroundResource(R.drawable.button_dialogx_ios_bottom_light);
                button2.setBackgroundResource(R.drawable.button_dialogx_ios_bottom_light);
//                button3.setBackgroundResource(R.drawable.button_dialogx_ios_bottom_light);
            }
        }

        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(dialogInterface -> {
            Window window = alertDialog.getWindow();
            if (window != null) {
                // 设置对话框的窗口属性
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                // 设置底部距离（例如，设置为导航栏高度 + 16dp）
                layoutParams.y = getNavigationBarHeight(getApplicationContext()) + dpToPx(16);;
                ((Window) window).setAttributes(layoutParams);
            }
        });


        // 设置系统对话框的类型
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        alertDialog.show();

        // 使用 ViewTreeObserver 获取 Dialog 的实际宽高
        alertDialog.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 移除监听器，防止重复调用
                alertDialog.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // 在竖屏时获取 Dialog 的实际宽高
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    dialogWidth = alertDialog.getWindow().getDecorView().getWidth();
                    dialogHeight = alertDialog.getWindow().getDecorView().getHeight();
                }

                // 如果是横屏，使用竖屏时的宽高
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
                    layoutParams.width = dialogWidth; // 使用竖屏时的宽度
                    layoutParams.height = dialogHeight; // 使用竖屏时的高度
                    alertDialog.getWindow().setAttributes(layoutParams);
                }
            }
        });
    }

    // dp 转换为 px
    public int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 在横竖屏切换时重新设置 Dialog 的宽高
        if (alertDialog != null && alertDialog.isShowing()) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // 在横屏时，使用竖屏时保存的宽高
                layoutParams.width = dialogWidth;
                layoutParams.height = dialogHeight;
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                // 竖屏时正常显示
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }

            alertDialog.getWindow().setAttributes(layoutParams);
        }
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // 获取导航栏高度
    private int getNavigationBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (alertDialog != null && alertDialog.isShowing()) {
            // 保存 Dialog 的宽高
            WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
            outState.putInt("dialog_width", layoutParams.width);
            outState.putInt("dialog_height", layoutParams.height);
        }
    }
}
