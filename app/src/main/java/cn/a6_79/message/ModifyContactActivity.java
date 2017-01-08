package cn.a6_79.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ModifyContactActivity extends AppCompatActivity{
    private EditText nameEditText;
    private EditText phoneNumberEditText;
    private EditText emailIdEditText;
    private Button saveButton;
    private int modifyType;
    private Contact contact;
    private Context context;

    String nameText;
    String phoneNumberText;
    String emailIdText;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_contact);

        initFields();
        setListeners();
        context = this;
        intent = new Intent(ModifyContactActivity.this, ContactActivity.class);

        if (!getIntent().getExtras().isEmpty()) {
            modifyType = (int) getIntent().getExtras().get(Info.CONTACT_TYPE);
            int contactIndex = (int) getIntent().getExtras().get(Info.CONTACT_INDEX);
            if (contactIndex != Info.CONTACT_NEW)
                contact = Info.contacts.get(contactIndex);

            if (modifyType == Info.CONTACT_MODIFY) {
                nameEditText.setText(contact.name);
                phoneNumberEditText.setText(contact.phone);
                emailIdEditText.setText(contact.email);
                setTitle("编辑联系人");
            } else {
//                deleteButton.setVisibility(View.GONE);
                setTitle("添加联系人");
            }
        }
    }

    private OnAsyncTaskListener addContactListener = new OnAsyncTaskListener() {
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
                    int cid = jsonRet.getInt("body");
                    Info.contacts.add(new Contact(nameText, phoneNumberText, emailIdText, cid));
                    Toast.makeText(context, "成功添加", Toast.LENGTH_SHORT).show();
//                    setResult(-1, intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "添加联系人失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnAsyncTaskListener modifyContactListener = new OnAsyncTaskListener() {
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
                    contact.name = nameText;
                    contact.phone = phoneNumberText;
                    contact.email = emailIdText;
                    Toast.makeText(context, "成功修改", Toast.LENGTH_SHORT).show();
//                    setResult(-1, intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "修改联系人失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void initFields() {
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        emailIdEditText = (EditText) findViewById(R.id.emailIdEditText);
        saveButton = (Button) findViewById(R.id.saveButton);
    }

    private void setListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText = nameEditText.getText().toString();
                phoneNumberText = phoneNumberEditText.getText().toString();
                emailIdText = emailIdEditText.getText().toString();

                if (nameText.equals("") && phoneNumberText.equals("") && emailIdText.equals("")) {
                    Toast.makeText(getApplicationContext(), "不能全部为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (modifyType == Info.CONTACT_ADD) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", nameText);
                        jsonObject.put("phone", phoneNumberText);
                        jsonObject.put("email", emailIdText);
                        HttpTask httpTask = new HttpTask(
                                context, jsonObject.toString(), WebConnect.API_ADD_CONTACT, addContactListener);
                        httpTask.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", contact.id);
                        jsonObject.put("name", nameText);
                        jsonObject.put("phone", phoneNumberText);
                        jsonObject.put("email", emailIdText);
                        HttpTask httpTask = new HttpTask(
                                context, jsonObject.toString(), WebConnect.API_MODIFY_CONTACT, modifyContactListener);
                        httpTask.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
