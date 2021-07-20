package cn.demomaster.wuyou.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.List;

import cn.demomaster.huan.quickdeveloplibrary.widget.popup.QDTipPopup;
import cn.demomaster.wuyou.AppConfig;
import cn.demomaster.wuyou.AppInfo;
import cn.demomaster.wuyou.R;


/**
 * Created by Squirrel桓 on 2018/11/11.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    List<AppConfig> appConfigs;
    private List<AppInfo> lists = null;
    private Context context;
    QDTipPopup qdTipPopup;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RecycleAdapter(Context context, List<AppInfo> lists, List<AppConfig> appConfigs) {
        this.appConfigs=appConfigs;
        this.context = context;
        this.lists = lists;
    }

    //创建View,被LayoutManager所用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.desktop_gridview_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AppInfo appInfo = lists.get(position);
        holder.tv_app_name.setText(lists.get(position).getName());
//        int id = getResId(appInfo.getName(), R.mipmap.class);
        holder.iv_icon.setImageDrawable(appInfo.getIco());

        //holder.itemView.setTag(departMentModel.getCode());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(v, p);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               // Toast.makeText(context,"1234335",Toast.LENGTH_LONG).show();
                /*qdTipPopup = new QDTipPopup.Builder(context).setBackgroundRadius(10).setBackgroundColor(Color.WHITE).setTextColor(Color.BLACK).setMessage("底部多行提示---------------------------------------------------------------------------------------提示。").create();
                qdTipPopup.showTip(view, GuiderView.Gravity.TOP);*/
                int p = (int) v.getTag();
                if (onItemClickListener != null) {
                    onItemClickListener.onLongClick(v, p);
                }
                return false;
            }
        });
        if (containsPKG(lists.get(position).getPackageName())){
            holder.tv_state.setVisibility(View.VISIBLE);
        }else {
            holder.tv_state.setVisibility(View.GONE);
        }
       /* holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i("CGQ","ACTION_STATE_SWIPE。。。。。。"+motionEvent.getAction()+","+motionEvent.getX()+","+motionEvent.getY());
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        if(qdTipPopup!=null){
                            qdTipPopup.dismiss();
                        }
                        break;
                }
                return false;
            }
        });*/
    }
    public boolean  containsPKG(String pkgName){
        for (AppConfig appConfig :appConfigs){
           if(appConfig.getPackageName().equals(pkgName)){
               return true;
           }
        }return false;
    }
    public void  onCanelMenu(){
        Log.i("CGQ","onCanelMenu---------------");
        if(qdTipPopup!=null){
            qdTipPopup.dismiss();
        }
    }

    public void updateList( List<AppInfo> data) {
        this.lists = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_app_name;
        private TextView tv_state;
        private ImageView iv_icon;
        //private ToggleButton toggleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_state = itemView.findViewById(R.id.tv_state);
        }
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static interface OnItemClickListener {
        void onClick(View v, int position);
        void onLongClick(View v, int position);
    }

}

