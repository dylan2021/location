package com.sfmap.route.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.sfmap.navi.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sfmap.route.model.QuestionBean;

import java.util.ArrayList;
import java.util.List;

public class QuestAdapter extends BaseQuickAdapter<QuestionBean, BaseViewHolder> {
    private List<QuestionBean> datas;
    private Context mContext;

    private InputCheckListener listener;

    public void setListener(InputCheckListener listener) {
        this.listener = listener;
    }

    public interface InputCheckListener {
        void inputOk();
    }

    public QuestAdapter(@Nullable ArrayList<QuestionBean> data, Context context) {
        super(R.layout.quest_item, data);
        datas = data;
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, QuestionBean item) {
        ((TextView) helper.getView(R.id.tv_question_title)).setText(item.getTitle());
        RadioGroup radioGroupOpt = ((RadioGroup) helper.getView(R.id.radio_group_opt));
        radioGroupOpt.removeAllViews();
        addView(item.getOptions(),radioGroupOpt);
        radioGroupOpt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG,"第几题："+helper.getAdapterPosition()+" 答案："+checkedId);
                datas.get(helper.getAdapterPosition()).setResult(getResult(checkedId));
                if(checkInput()){
                    if(null != listener){
                        listener.inputOk();
                    }
                }
            }
        });
    }

    private String getResult(int checkedId) {
        switch (checkedId){
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
        }
        return "";
    }

    private void addView(List<String> strings,RadioGroup radioGroup){
        for(int i=0;i<strings.size();i++){
            RadioButton radioButton = new RadioButton(mContext);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 0, 10, 0);
            radioButton.setLayoutParams(layoutParams);
            radioButton.setText(strings.get(i));
            radioButton.setTextSize(12);
//        radioButton.setButtonDrawable(android.R.color.transparent);//隐藏单选圆形按钮
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setPadding(10, 0, 10, 0);
            radioButton.setId(i);
//        radioButton.setTextColor(getResources().getColorStateList(R.color.selector_white_graydark_checked));//设置选中/未选中的文字颜色
//        radioButton.setBackground(getResources().getDrawable(R.drawable.selector_radiobtn_bg));//设置按钮选中/未选中的背景
            radioGroup.addView(radioButton);//将单选按钮添加到RadioGroup中
        }
    }

    private boolean checkInput(){
        for(QuestionBean questionBean : datas){
            if(TextUtils.isEmpty(questionBean.getResult())){
                return false;
            }
        }
        return true;
    }

}
