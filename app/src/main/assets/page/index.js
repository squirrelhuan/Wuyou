//ui.doclick();
var data = {
    title:"动态标题",
    message: 'Hello MINA!',
    buttons:[1,2,3,4,5,6],
    apps:[222,333,444],
}
function click1(params){
    //toast("点击了"+tag);
}
function oncheckchanged(params){
    log("js oncheckchanged===>"+JSON.stringify(params));
    var b = accessibility.hasAccessibility();
    if(!b){
        accessibility.startSettintActivity();
    }
}

function textChanged(tag){
    var view2 = findViewByTag("input1");
   // toast("文本改变了"+view2.text);
}

function alertDialog(tag){
    alert(tag);
}

function addViewTest(){
    var view = findViewByTag("layout_01");
    view.addView({name:"button",click:"toast()",text:"t"});
    //var view2 = findViewByTag("btn_01");
   // var v = convertToJava(view2);
    //log(""+JSON.stringify(v));
}

function requestPermission(){
    var p = ["android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"];
    log("权限"+JSON.stringify(p));
    requestPermission_java(JSON.stringify(p));
}

function newPage(){
    var page = {
        controller:"page/index.js",
        title:"新页面",
    };
    startPage(page);
}

function onCreatView(contentView){
    log("页面创建");
    setContentViewFromPath("page/index.xml");
    data.apps = accessibility.getAppInfo();
}