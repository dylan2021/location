package com.sfmap.route;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sfmap.library.util.DateTimeUtil;
import com.sfmap.map.util.AppInfo;
import com.sfmap.navi.R;
import com.sfmap.navi.R2;
import com.sfmap.route.model.Anwers;
import com.sfmap.route.model.FeedBackParmas;
import com.sfmap.route.model.QuestionBean;
import com.sfmap.route.view.QuestAdapter;
import com.sfmap.tbt.DeviceIdManager;
import com.sfmap.tbt.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedBackActivity extends Activity implements View.OnClickListener {

    String fileName;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R2.id.img_title_close)
    ImageView imgTitleClose;
    @BindView(R2.id.recycler_question)
    RecyclerView recyclerQuestion;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;
    @BindView(R2.id.edit_others)
    EditText editOthers;

    private final String TAG = this.getClass().getSimpleName();

    private QuestAdapter questAdapter;
    ArrayList<QuestionBean> questionBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback1);
        ButterKnife.bind(this);
        initView();
        creatData();
        initData();
        fileName = (String) getIntent().getSerializableExtra("fileName");
    }

    private void initData() {
        questAdapter = new QuestAdapter(questionBeans, this);
        recyclerQuestion.setAdapter(questAdapter);
        questAdapter.setListener(new QuestAdapter.InputCheckListener() {
            @Override
            public void inputOk() {
                btnConfirm.setEnabled(true);
            }
        });
    }

    private void initView() {
        tvTitle.setText("问卷调查");
        btnConfirm.setOnClickListener(this);
        btnConfirm.setEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerQuestion.setLayoutManager(layoutManager);

    }

    private void creatData() {

        List<String> opt = new ArrayList<>();

        QuestionBean questionBean = new QuestionBean();
        questionBean.setTitle("1.导航过程中是否出现过闪退？");
        opt.add("有");
        opt.add("无");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        questionBean = new QuestionBean();
        opt = new ArrayList<>();
        questionBean.setTitle("2.导航过程中是否出现过卡死？");
        opt.add("有");
        opt.add("无");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        questionBean = new QuestionBean();
        opt = new ArrayList<>();
        questionBean.setTitle("3.规划路线是否合理？");
        opt.add("合理");
        opt.add("红绿灯多");
        opt.add("绕路");
        opt.add("走小路");
        opt.add("拥堵");
        opt.add("其他");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        questionBean = new QuestionBean();
        opt = new ArrayList<>();
        questionBean.setTitle("4.偏航判断是否准确？");
        opt.add("准确");
        opt.add("不准确");
        opt.add("无偏航");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        questionBean = new QuestionBean();
        opt = new ArrayList<>();
        questionBean.setTitle("5.路口播报是否准确及时，例如：在“直行、左转、 右转”提示是否及时和准确？");
        opt.add("准确");
        opt.add("不准确");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        questionBean = new QuestionBean();
        opt = new ArrayList<>();
        questionBean.setTitle("6.是否有准确的路口放大图出现？");
        opt.add("有");
        opt.add("无");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        questionBean = new QuestionBean();
        opt = new ArrayList<>();
        questionBean.setTitle("7.车道指引播报是否准确及时，例如\"请走中间X车道/右侧车道等语音提醒？");
        opt.add("准确");
        opt.add("不准确");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        questionBean = new QuestionBean();
        opt = new ArrayList<>();
        questionBean.setTitle("8.语音提示内容是否合理？");
        opt.add("合理");
        opt.add("播报太少");
        opt.add("播报太多");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        questionBean = new QuestionBean();
        opt = new ArrayList<>();
        questionBean.setTitle("9.导航过程中地图显示是否正常？");
        opt.add("正常");
        opt.add("不正常");
        questionBean.setOptions(opt);
        questionBeans.add(questionBean);

        LogUtil.d(TAG, new Gson().toJson(questionBeans));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(this);
    }

    public void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_title_back) {
            finish();
        } else if (id == R.id.img_title_close) {
            finish();
        } else if (id == R.id.btn_confirm) {
            Toast.makeText(this, "fadsaf", Toast.LENGTH_LONG).show();
            upload();
        }
    }

    private void upload() {

        String others = editOthers.getText().toString();
        FeedBackParmas parmas = new FeedBackParmas();
        parmas.setAk("0356b30071c440d992a4ddae624eb07f");
//                    parmas.setAccount(LoginUtil.getUserMoblie(MapApplication.getApplication()));
        parmas.setAccount(com.sfmap.tbt.util.AppInfo.getUserId());
        parmas.setDevice_id(DeviceIdManager.getDeviceID(getApplicationContext()));
        parmas.setDevice_type("android");
        parmas.setFile_name(fileName);
        parmas.setApp_version(AppInfo.getApplicationVersion(getApplicationContext()));
        parmas.setRecord_time(DateTimeUtil.getTimeStampDetail());
        List<Anwers> anwersList = new ArrayList<>();

        for(int i=0;i<questionBeans.size();i++){
            Anwers anwers = new Anwers();
            anwers.setQuestion_seq(String.valueOf(i+1));
            anwers.setTemplate_id("3");
            anwers.setAnswer_type("1");
            anwers.setAnswer_txt(questionBeans.get(i).getResult());
            anwersList.add(anwers);
        }
        Anwers anwers = new Anwers();
        anwers.setQuestion_seq("10");
        anwers.setTemplate_id("3");
        anwers.setAnswer_type("3");
        anwers.setAnswer_txt(others);
        anwersList.add(anwers);
        parmas.setAnwsers(anwersList);
        Log.i(TAG, new Gson().toJson(parmas));
        try {
            upload("https://gis.sf-express.com/navirec/qs/save", parmas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean upload(final String url, final FeedBackParmas parmas) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                Request request = new Request.Builder()
                        .header("Authorization", "Client-ID " + UUID.randomUUID())
                        .url(url)
                        .post(RequestBody.create(JSON, new Gson().toJson(parmas)))
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (response == null) {
                        Message message = new Message();
                        message.what = 2;
                        message.obj = "网络异常，上传失败";
                        handler.sendMessage(message);
                        return;
                    }
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        resultBean resultBean = new Gson().fromJson(res, resultBean.class);
                        if (resultBean.status == 0) {
                            handler.sendEmptyMessage(1);
                        } else {
                            Message message = new Message();
                            message.what = 2;
                            message.obj = resultBean.getResult().getMsg();
                            handler.sendMessage(message);
                        }
                        Log.i(TAG, "上传成功:" + res);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        return true;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "问卷提交成功，感谢您的反馈。", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), message.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
            return false;
        }
    });

    class resultBean {
        int status;
        resultDetail result;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public resultDetail getResult() {
            return result;
        }

        public void setResult(resultDetail result) {
            this.result = result;
        }
    }

    class resultDetail {
        String err;
        String msg;

        public String getErr() {
            return err;
        }

        public void setErr(String err) {
            this.err = err;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
