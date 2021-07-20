package cn.demomaster.wuyou;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.demomaster.huan.quickdeveloplibrary.base.activity.QDActivity;
import cn.demomaster.huan.quickdeveloplibrary.util.QDFileUtil;
import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.qdrouter_library.actionbar.ACTIONBAR_TYPE;
import cn.demomaster.quickjs_library.ajs.AjsControllerInterface;
import cn.demomaster.quickjs_library.ajs.AjsEngine;
import cn.demomaster.quickjs_library.fragment.AppletsFragment;

public class JsActivity extends QDActivity implements AjsControllerInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarTool().setActionBarType(ACTIONBAR_TYPE.NO_ACTION_BAR_NO_STATUS);
        setContentView(R.layout.activity_js);
        ViewGroup viewGroup = findViewById(R.id.container_content);
        String controller = "page/index.js";
        AjsEngine ajsEngine = new AjsEngine(this);
        String scriptStr = QDFileUtil.getFromAssets(this, controller);
        //QDLogger.e("文本内容========"+scriptStr);
        ajsEngine.startControll( this,viewGroup,scriptStr);
        /*LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Button button = new Button(this);
        button.setText("123456");
        linearLayout.addView(button);
        linearLayout.setBackgroundColor(Color.RED);
        ((ViewGroup)findViewById(android.R.id.content)).addView(linearLayout,layoutParams);*/
    }

    @Override
    public void startAjsActivity() {

    }

    @Override
    public void startAjsFragment(Bundle bundle) {
//getFragmentHelper().startFragment(new AppletsFragment(),bundle);
       //.getChildAt(0)；
        getFragmentHelper().build(this, AppletsFragment.class.getName()).putExtras(bundle).setContainerViewId(android.R.id.content)
                .putExtra("password", 666666).navigation();
    }

}