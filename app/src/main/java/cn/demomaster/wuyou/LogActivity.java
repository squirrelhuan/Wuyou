package cn.demomaster.wuyou;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.demomaster.huan.quickdeveloplibrary.base.activity.QDActivity;
import cn.demomaster.huan.quickdeveloplibrary.helper.QDSharedPreferences;
import cn.demomaster.wuyou.adapter.LogRecycleAdapter;

import static cn.demomaster.wuyou.MainActivity.accessibilityLog;

/**
 * 拦截记录
 */
public class LogActivity extends QDActivity {
    RecyclerView recyDrag;
    private LinearLayoutManager linearLayoutManager;
    private LogRecycleAdapter adapter;
    List<LogBean> logBeans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        recyDrag = findViewById(R.id.recy_log);
        //这里使用线性布局像listview那样展示列表,第二个参数可以改为 HORIZONTAL实现水平展示
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        //使用网格布局展示
        //recyDrag.setLayoutManager(new GridLayoutManager(this, 4));
        recyDrag.setLayoutManager(linearLayoutManager);
        adapter = new LogRecycleAdapter(this, logBeans);
        //设置分割线使用的divider
        recyDrag.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter.setOnItemClickListener(new LogRecycleAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                //startApp(appInfos.get(position).getPackageName());
            }

            @Override
            public void onLongClick(View v, int position) {
                // if (!appInfos.get(position).isSystem())
                //showMenu(v, appInfos.get(position).getPackageName());
            }
        });
        recyDrag.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logBeans = getLogs();
        adapter.updateList(logBeans);
    }

    private ArrayList<LogBean> getLogs() {
        String str =  QDSharedPreferences.getInstance().getString(accessibilityLog,null);
        if(TextUtils.isEmpty(str)){
            return new ArrayList<>();
        }else {
            return (ArrayList<LogBean>) JSON.parseArray(str,LogBean.class);
        }
    }

}