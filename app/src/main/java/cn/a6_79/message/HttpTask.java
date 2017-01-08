package cn.a6_79.message;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

class HttpTask extends AsyncTask<Void, Integer, String> {
    private String transValue;
    private int status;
    private OnAsyncTaskListener listener;
    private ProgressDialog progressDialog = null;
    HttpTask(Context context, String transValue, int status, OnAsyncTaskListener listener) {
        this.transValue = transValue;
        this.status = status;
        this.listener = listener;
        if (context != null) {
            this.progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
        }
        else
            progressDialog = null;
    }

    @Override
    protected void onPreExecute() {
        String show = "";
        if (status == WebConnect.API_LOGIN)                 show = "正在登录";
        else if (status == WebConnect.API_GET_NOTICE)       show = "正在获取权限";
        else if (status == WebConnect.API_SEND_MESSAGE)     show = "正在发送短信";
        else if (status == WebConnect.API_GET_LIST)         show = "正在获取历史";
        else if (status == WebConnect.API_GET_CONTACT)      show = "正在获取联系人";
        else if (status == WebConnect.API_ADD_CONTACT)      show = "正在添加联系人";
        else if (status == WebConnect.API_MODIFY_CONTACT)   show = "正在修改联系人";
        else if (status == WebConnect.API_DELETE_CONTACT)   show = "正在删除联系人";
        if (progressDialog != null) {
            progressDialog.setMessage(show);
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        return WebConnect.postGetJson(transValue, status);
    }

    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);
        if (progressDialog != null)
            progressDialog.dismiss();
        listener.onSuccess(str);
    }
}
