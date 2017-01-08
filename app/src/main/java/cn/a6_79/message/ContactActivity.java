package cn.a6_79.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ContactActivity extends AppCompatActivity {
    private Context context;
    LinearLayout.LayoutParams layoutParams1, layoutParams2, layoutParams3, layoutParams4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        context = this;
        layoutParams1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(16, 2, 16, 2);
        layoutParams2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.setMargins(32, 8, 32, 8);
        layoutParams3 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.setMargins(16, 0, 16, 0);
        layoutParams4 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                3);
        layoutParams4.setMargins(60, 2, 50, 2);
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(context, ModifyContactActivity.class);
                intent.putExtra(Info.CONTACT_TYPE, Info.CONTACT_ADD);
                intent.putExtra(Info.CONTACT_INDEX, Info.CONTACT_NEW);
                startActivityForResult(intent, 1);
                return super.onOptionsItemSelected(item);
            case R.id.action_done:
                finish();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void refresh() {
        final LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();
        for (int i = 0; i < Info.contacts.size(); i++) {
            final int index = i;
            final Contact contact = Info.contacts.get(i);


            final LinearLayout singleContact = new LinearLayout(this);
            singleContact.setOrientation(LinearLayout.HORIZONTAL);
            singleContact.setLayoutParams(layoutParams2);
//            singleContact.setBackgroundResource(R.drawable.border);

            final LinearLayout linearLayoutLeft = new LinearLayout(this);
            linearLayoutLeft.addView(newCheckBox(contact));
            linearLayoutLeft.addView(newTextView(contact));

            final LinearLayout linearLayoutRight = new LinearLayout(this);
            linearLayoutRight.setLayoutParams(layoutParams3);
            linearLayoutRight.setHorizontalGravity(Gravity.END);
            linearLayoutRight.setVerticalGravity(Gravity.CENTER);
            linearLayoutRight.addView(newEditImageButton(index));
            linearLayoutRight.addView(newDeleteImageButton(index));

            singleContact.addView(linearLayoutLeft);
            singleContact.addView(linearLayoutRight);
            linearLayout.addView(singleContact);

            if (i < Info.contacts.size() - 1) {
                final ImageView imageView = new ImageView(this);
                imageView.setBackgroundColor(getResources().getColor(R.color.white));
                imageView.setLayoutParams(layoutParams4);
                linearLayout.addView(imageView);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refresh();
    }


    private CheckBox newCheckBox(final Contact contact) {
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setText(contact.name);
        checkBox.setChecked(contact.checked == 1);
        checkBox.setLayoutParams(layoutParams1);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                contact.checked = checkBox.isChecked() ? 1 : 0;
            }
        });
        return checkBox;
    }

    private TextView newTextView(final Contact contact) {
        final TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams1);
        if (contact.phone == null || contact.phone.equals(""))
            textView.setText("未填写手机号");
        else
            textView.setText(contact.phone);
        return textView;
    }

    private ImageButton newEditImageButton(final int index) {
        ImageButton imageButton = new ImageButton(this);
        imageButton.setLayoutParams(layoutParams1);
        imageButton.setImageResource(R.drawable.ic_menu_edit);
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ModifyContactActivity.class);
                intent.putExtra(Info.CONTACT_TYPE, Info.CONTACT_MODIFY);
                intent.putExtra(Info.CONTACT_INDEX, index);
                startActivityForResult(intent, 1);
            }
        });
        return imageButton;
    }

    private ImageButton newDeleteImageButton(final int index) {
        ImageButton imageButton = new ImageButton(this);
        imageButton.setImageResource(R.drawable.ic_menu_delete);
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", Info.contacts.get(index).id);
                    HttpTask httpTask = new HttpTask(
                            context, jsonObject.toString(), WebConnect.API_DELETE_CONTACT,
                            new OnAsyncTaskListener() {
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
                                            Info.contacts.remove(index);
                                            Toast.makeText(context, "成功删除", Toast.LENGTH_SHORT).show();
                                        }
                                        refresh();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "删除联系人失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    httpTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return imageButton;
    }

}
