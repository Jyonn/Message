package cn.a6_79.message;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import java.net.URLEncoder;

import android.widget.Spinner;
import android.widget.Toast;
import org.json.*;

public class LoginActivity extends AppCompatActivity{

    private EditText username, password;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        Button btnLogin = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    private void userLogin() {
        try {
            Info.username = username.getText().toString();
            Info.password = password.getText().toString();
            String transValue = URLEncoder.encode("username", "UTF-8") + "=" +
                    URLEncoder.encode(Info.username, "UTF-8") + "&" +
                    URLEncoder.encode("password", "UTF-8") + "=" +
                    URLEncoder.encode(Info.password, "UTF-8");
            HttpTask httpTask = new HttpTask(this, transValue, WebConnect.API_LOGIN, loginListener);
            httpTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNotice() {
        HttpTask httpTask = new HttpTask(null, "", WebConnect.API_GET_NOTICE, getNoticeListener);
        httpTask.execute();
    }

    private void getContact() {
        HttpTask httpTask = new HttpTask(null, "", WebConnect.API_GET_CONTACT, getContactListener);
        httpTask.execute();
    }

    private OnAsyncTaskListener loginListener = new OnAsyncTaskListener() {
        @Override
        public void onSuccess(String string) {
            try {
                JSONTokener jsonTokener = new JSONTokener(string);
                JSONObject jsonRet = (JSONObject) jsonTokener.nextValue();
                int code = jsonRet.getInt("code");
                String msg = jsonRet.getString("msg");
                if (code != 0) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "获取信息失败", Toast.LENGTH_SHORT).show();
            }
            getNotice();
        }
    };

    private OnAsyncTaskListener getNoticeListener = new OnAsyncTaskListener() {
        @Override
        public void onSuccess(String string) {
            try {
                JSONTokener jsonTokener = new JSONTokener(string);
                JSONObject jsonRet = (JSONObject) jsonTokener.nextValue();
                int code = jsonRet.getInt("code");
                String msg = jsonRet.getString("msg");
                if (code != 0) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    JSONObject jsonBody = jsonRet.optJSONObject("body");
                    Info.remaining = jsonBody.getInt("remaining");
                    Info.total = jsonBody.getInt("total");
                    JSONArray jsonCapability = jsonBody.optJSONArray("capability");
                    Info.messages.clear();
                    for (int i = 0; i < jsonCapability.length(); i++) {
                        JSONObject t = (JSONObject) jsonCapability.get(i);
                        Info.messages.add(new Message(
                                t.getString("notice"),
                                t.getInt("message_id")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "获取信息失败", Toast.LENGTH_SHORT).show();
            }
            getContact();
        }
    };

    private OnAsyncTaskListener getContactListener = new OnAsyncTaskListener() {
        @Override
        public void onSuccess(String string) {
            try {
                JSONTokener jsonTokener = new JSONTokener(string);
                JSONObject jsonRet = (JSONObject) jsonTokener.nextValue();
                int code = jsonRet.getInt("code");
                String msg = jsonRet.getString("msg");
                if (code != 0) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    JSONObject jsonBody = jsonRet.optJSONObject("body");
                    JSONArray jsonContacts = jsonBody.optJSONArray("contacts");
                    Info.contacts.clear();
                    for (int i = 0; i < jsonContacts.length(); i++) {
                        JSONObject t = (JSONObject) jsonContacts.get(i);
                        Info.contacts.add(new Contact(
                                t.getString("name"),
                                t.getString("phone"),
                                t.getString("email"),
                                t.getInt("id")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "获取信息失败", Toast.LENGTH_SHORT).show();
            }
            startActivityForResult(new Intent(LoginActivity.this, WriteTextActivity.class), 1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }
}
