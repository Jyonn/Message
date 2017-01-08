package cn.a6_79.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;
import java.text.*;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SendActivity extends AppCompatActivity {

    // time params
    Calendar calendar;
    int curYear, curMonth, curDay, curHour, curMinute;

    // show params
    private EditText phones, emails;
    private TextView detail;
    private Context context;
    private Button chooseContacts, btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_send);
        init();
        refreshContact();
        setListener();
    }

    private void init() {
        // views init
        chooseContacts = (Button) findViewById(R.id.phone);
        detail = (TextView) findViewById(R.id.detail);
        phones = (EditText) findViewById(R.id.phoneList);
        emails = (EditText) findViewById(R.id.emailList);
        btnSend = (Button) findViewById(R.id.ok);
    }

    private void setListener() {
        chooseContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SendActivity.this, ContactActivity.class), 1);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        try {
            JSONObject postObject = new JSONObject();
            JSONArray contactArray = new JSONArray();
            for (Contact contact : Info.contacts) {
                if (contact.checked == 1)
                    contactArray.put(contact.id);
            }
            postObject.put("contacts", contactArray);
            postObject.put("message_id", Info.mid);
            postObject.put("content", Info.content);
            JSONArray otherList = new JSONArray();
            String[] phoneList = phones.getText().toString().split(",");
            String[] emailList = emails.getText().toString().split(",");
            for (String s : phoneList) {
                if (s.equals(""))
                    continue;
                otherList.put(new JSONObject().put("phone", s).put("email", null));
            }
            for (String s : emailList) {
                if (s.equals(""))
                    continue;
                otherList.put(new JSONObject().put("phone", null).put("email", s));
            }
            postObject.put("other_list", otherList);

            if (!Info.clock_set)
                postObject.put("send_time", 0);
            else {
                calendar = Calendar.getInstance();
                curYear = calendar.get(Calendar.YEAR);
                curMonth = calendar.get(Calendar.MONTH) + 1;
                curDay = calendar.get(Calendar.DAY_OF_MONTH);
                curHour = calendar.get(Calendar.HOUR_OF_DAY);
                curMinute = calendar.get(Calendar.MINUTE);
                long thisTime = dateToStamp(Info.clock_time);
                String strNowTime = WriteTextActivity.composeTime(
                        curYear, curMonth, curDay, curHour, curMinute, 0);
                long nowTime = dateToStamp(strNowTime);
                postObject.put("send_time", (thisTime - nowTime) / 60000);
            }
            Log.d("send", postObject.toString());
            HttpTask httpTask = new HttpTask(
                    this, postObject.toString(), WebConnect.API_SEND_MESSAGE, sendMessageListener);
            httpTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }

    private void refreshContact() {
        int count = 0;
        for (Contact contact : Info.contacts) {
            if (contact.checked == 1)
                count++;
        }
        if (count == 0)
            detail.setText("尚未选择常用联系人。");
        else {
            int showCount = 0;
            String showStr = "";
            for (Contact contact : Info.contacts) {
                if (contact.checked == 1) {
                    showStr += "、" + contact.name;
                    if (++showCount == 10)
                        break;
                }
            }
            showStr = showStr.substring(1);
            detail.setText("已选择 " + showStr + " 等" + count + "位常用联系人。");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshContact();
    }

    private OnAsyncTaskListener sendMessageListener = new OnAsyncTaskListener() {
        @Override
        public void onSuccess(String string) {
            try {
                JSONTokener jsonTokener = new JSONTokener(string);
                JSONObject jsonRet = (JSONObject) jsonTokener.nextValue();
                int code = jsonRet.getInt("code");
                String msg = jsonRet.getString("msg");
                if (code != 0)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                else {
                    JSONObject body = jsonRet.getJSONObject("body");
                    Info.remaining = body.getInt("remaining");
                    JSONArray failure = body.getJSONArray("failure_list");
                    if (failure.length() == 0)
                        Toast.makeText(context, "数据成功传送至服务器", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, ""+failure.length()+"条发送失败", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "获取信息失败", Toast.LENGTH_SHORT).show();
            }
        }
    };
}