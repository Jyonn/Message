package cn.a6_79.message;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import cn.a6_79.message.wheel.StericWheelAdapter;
import cn.a6_79.message.wheel.WheelView;

public class WriteTextActivity extends AppCompatActivity{
    private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minuteWheel;
    public static String[] yearContent = null;
    public static String[] monthContent = null;
    public static String[] dayContent = null;
    public static String[] hourContent = null;
    public static String[] minuteContent = null;
    public static String[] secondContent = null;
    // time params
    Calendar calendar;
    int curYear, curMonth, curDay, curHour, curMinute;
    String strSendTime;

    private TextView time_has_set;
    private EditText editText;
    private Spinner spinner;
    long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_text);

        init();
        drawCircle();
    }

    private void drawCircle() {
        int total = Info.total;
        int occupied = Info.total - Info.remaining;
        CircleBar circleBar = (CircleBar) findViewById(R.id.circle);
        circleBar.setSweepAngle((float)(occupied*360.0/total));
        circleBar.setTotal(total);
        circleBar.setText(""+occupied);
        circleBar.setEndText("还可以发送"+Info.remaining+"条短信");
        circleBar.start();
    }

    private void init() {
        time_has_set = (TextView) findViewById(R.id.time_has_set);
        time_has_set.setText("");

        ImageButton imageButton = (ImageButton) findViewById(R.id.set_clock);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeClick();
            }
        });

        editText = (EditText) findViewById(R.id.write_text);
        Button button = (Button) findViewById(R.id.text_end);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Info.content = editText.getText().toString();
                Info.mid = Info.messages.get((int)spinner.getSelectedItemId()).message_id;
                startActivityForResult(new Intent(WriteTextActivity.this, SendActivity.class), 1);
            }
        });

        // spinner init
        spinner = (Spinner) findViewById(R.id.spinner_title);
        ArrayAdapter<Message> messageAdapter = new ArrayAdapter<>
                (WriteTextActivity.this, android.R.layout.simple_spinner_item, Info.messages);
        messageAdapter.setDropDownViewResource(R.layout.spinner);
        spinner.setAdapter(messageAdapter);
        spinner.setVisibility(View.VISIBLE);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
            }
        });

        // date time init
        calendar = Calendar.getInstance();
        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH) + 1;
        curDay = calendar.get(Calendar.DAY_OF_MONTH);
        curHour = calendar.get(Calendar.HOUR_OF_DAY);
        curMinute = calendar.get(Calendar.MINUTE);
        strSendTime = composeTime(curYear, curMonth, curDay, curHour, curMinute, 0);
        initDateTimeContent();
    }

    void initDateTimeContent() {
        yearContent = new String[20];
        for(int i = 0; i < 20; i++)
            yearContent[i] = String.valueOf(i + calendar.get(Calendar.YEAR));

        monthContent = new String[12];
        for(int i = 0; i < 12; i++) {
            monthContent[i]= String.valueOf(i + 1);
            if(monthContent[i].length() < 2)
                monthContent[i] = "0" + monthContent[i];
        }

        dayContent = new String[31];
        for(int i = 0; i < 31; i++) {
            dayContent[i] = String.valueOf(i + 1);
            if(dayContent[i].length() < 2)
                dayContent[i] = "0" + dayContent[i];
        }

        hourContent = new String[24];
        for(int i = 0; i < 24; i++) {
            hourContent[i] = String.valueOf(i);
            if(hourContent[i].length() < 2)
                hourContent[i] = "0" + hourContent[i];
        }

        minuteContent = new String[60];
        for(int i = 0; i < 60; i++) {
            minuteContent[i] = String.valueOf(i);
            if(minuteContent[i].length() < 2)
                minuteContent[i] = "0" + minuteContent[i];
        }

        secondContent = new String[60];
        for(int i = 0; i < 60; i++) {
            secondContent[i] = String.valueOf(i);
            if(secondContent[i].length() < 2)
                secondContent[i] = "0" + secondContent[i];
        }
    }

    static String composeTime(
            int iYear, int iMonth, int iDay, int iHour, int iMinute, int iSecond) {
        String temp = iYear + "-";
        temp = putInt(temp, iMonth, "-");
        temp = putInt(temp, iDay, " ");
        temp = putInt(temp, iHour, ":");
        temp = putInt(temp, iMinute, ":");
        temp = putInt(temp, iSecond, "");
        return temp;
    }

    private static String putInt(String s, int i, String append) {
        if (i < 10)
            return s + "0" + i + append;
        return s + i + append;
    }

    private void setTimeClick() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.time_picker, null);
        yearWheel = (WheelView)view.findViewById(R.id.yearWheel);
        monthWheel = (WheelView)view.findViewById(R.id.monthWheel);
        dayWheel = (WheelView)view.findViewById(R.id.dayWheel);
        hourWheel = (WheelView)view.findViewById(R.id.hourWheel);
        minuteWheel = (WheelView)view.findViewById(R.id.minuteWheel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        builder.setView(view);
        setFinishOnTouchOutside(false);

        yearWheel.setAdapter(new StericWheelAdapter(yearContent));
        yearWheel.setCurrentItem(curYear - calendar.get(Calendar.YEAR));
        yearWheel.setCyclic(true);
        yearWheel.setInterpolator(new AnticipateOvershootInterpolator());

        monthWheel.setAdapter(new StericWheelAdapter(monthContent));
        monthWheel.setCurrentItem(curMonth - 1);
        monthWheel.setCyclic(true);
        monthWheel.setInterpolator(new AnticipateOvershootInterpolator());

        dayWheel.setAdapter(new StericWheelAdapter(dayContent));
        dayWheel.setCurrentItem(curDay - 1);
        dayWheel.setCyclic(true);
        dayWheel.setInterpolator(new AnticipateOvershootInterpolator());

        hourWheel.setAdapter(new StericWheelAdapter(hourContent));
        hourWheel.setCurrentItem(curHour);
        hourWheel.setCyclic(true);
        hourWheel.setInterpolator(new AnticipateOvershootInterpolator());

        minuteWheel.setAdapter(new StericWheelAdapter(minuteContent));
        minuteWheel.setCurrentItem(curMinute);
        minuteWheel.setCyclic(true);
        minuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

        LayoutInflater layoutInflater = LayoutInflater.from(WriteTextActivity.this);
        View mTitleView = layoutInflater.inflate(R.layout.title, null);
        builder.setCustomTitle(mTitleView);
        builder.setNegativeButton("取  消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                time_has_set.setText("");
                Info.clock_set = false;
                dialog.cancel();
            }
        });
        builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                StringBuffer sb = new StringBuffer();
                sb.append(yearWheel.getCurrentItemValue()).append("-")
                        .append(monthWheel.getCurrentItemValue()).append("-")
                        .append(dayWheel.getCurrentItemValue());

                sb.append(" ");
                sb.append(hourWheel.getCurrentItemValue())
                        .append(":").append(minuteWheel.getCurrentItemValue());
                time_has_set.setText(sb);
                Info.clock_set = true;
                Info.clock_time = sb.toString();
                dialog.cancel();

                curYear = Integer.valueOf(yearWheel.getCurrentItemValue());
                curMonth = Integer.valueOf(monthWheel.getCurrentItemValue());
                curDay = Integer.valueOf(dayWheel.getCurrentItemValue());
                curHour = Integer.valueOf(hourWheel.getCurrentItemValue());
                curMinute = Integer.valueOf(minuteWheel.getCurrentItemValue());
                strSendTime = composeTime(curYear, curMonth, curDay, curHour, curMinute, 0);
            }
        });

        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        drawCircle();
    }
}
