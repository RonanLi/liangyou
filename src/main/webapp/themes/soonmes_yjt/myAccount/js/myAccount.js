/* Created by liruonan on 2016/11/17.*/
function circleProgress(progress,value,total){
    var canvas = document.getElementById('progress');
    var context = canvas.getContext('2d');
    var _this = $(canvas),
        value= Number(value),// 当前百分比,数值
        color="#cccc99",// 进度条、文字样式
        maxpercent = 100,//最大百分比，可设置
        c_width = _this.width(),// canvas，宽度
        c_height =_this.height();// canvas,高度

    // 清空画布
    context.clearRect(0, 0, c_width, c_height);
    // 画底圆
    context.beginPath();
    // 将起始点移到canvas中心
    context.moveTo(c_width/2, c_height/2);
    // 绘制一个中心点为（c_width/2, c_height/2），半径为c_height/2，起始点0，终止点为Math.PI * 2的 整圆
    context.arc(c_width/2, c_height/2, c_height/2, 0, Math.PI * 2, false);
    context.closePath();
    context.fillStyle = '#e79770'; //填充颜色
    context.fill();

    function draw(cur){
        // 画内部空白圆饼
        context.beginPath();
        context.moveTo(24, 24);
        context.arc(c_width/2, c_height/2, c_height/2-10, 0, Math.PI * 2, true);
        context.closePath();
        context.fillStyle ='rgba(255,255,255,1)';  // 填充内部颜色
        context.fill();
        // 画进度圆环
        context.beginPath();
        // 绘制一个中心点为（c_width/2, c_height/2），半径为c_height/2-5不与外圆重叠，
        // 起始点-(Math.PI/2)，终止点为((Math.PI*2)*cur)-Math.PI/2的整圆,cur为每一次绘制的距离
        context.arc(c_width/2, c_height/2, c_height/2-5, -(Math.PI / 2), ((Math.PI * 2) * cur ) - Math.PI / 2, false);
        context.strokeStyle = color;
        context.lineWidth =10.0;
        context.stroke();
        // 绘制内圆
        //在中间写字
        context.font = " 16px Microsoft YaHei ";  // 字体大小，样式
        context.fillStyle = "#555";  // 颜色
        context.textAlign = 'center';  // 位置
        context.textBaseline = 'middle';
        context.moveTo(c_width/2, c_height/2);  // 文字填充位置
        context.fillText("账户总额", c_width/2, c_height/2-15);

        context.font = "bold 14px Microsoft YaHei";  // 字体大小，样式
        context.fillStyle = "#a6671f";  // 颜色
        context.textAlign = 'center';  // 位置
        context.textBaseline = 'middle';
        context.fillText(total+"元", c_width/2, c_height/2+10);
    }
    // 调用定时器实现动态效果
    var timer=null,n=0;
    function loadCanvas(nowT){
        timer = setInterval(function(){
            if(n>nowT){
                clearInterval(timer);
            }else{
                draw(n);
                n += 0.01;
            }
        },15);
    }
    loadCanvas(value/total);
    timer=null;
};

var url = location.href;
$(".menuBottom>a").each(function () {
    if ((url + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
        $(this).addClass('active');
    } else {
        $(this).removeClass('active');
    }
});
$('.menuBottom .toggle a').each(function(){
    if ((url + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
        $(this).addClass('current').parents('ul').show();
        $(this).parents('.toggle').children('b').addClass('active');
        $(this).parents('.toggle').children('b').children('i').css("color","#e7bb70")
    } else {
        $(this).removeClass('current');
    }
});
$('.menuBottom .toggle b').click(function(){
    if($(this).parent().hasClass("toggle")&&$(this).hasClass("active")){
        $(this).parent().removeClass();
        $(this).removeClass("active");
        $(this).siblings("ul").hide();
        $(this).siblings("ul").toggle();
    }
    if($(this).parent().hasClass("toggle")&&!$(this).hasClass("active")){
        $(this).parent().removeClass("toggle");
        $(this).addClass("active");
        $(this).siblings("ul").toggle();
    }else{
        $(this).parent().addClass("toggle");
        $(this).removeClass("active");
        $(this).siblings("ul").toggle();
    }
});

/*还款弹窗*/
function opDialog(obj){
    var $obj = $(obj);
    var money = $obj.attr("money");
    var captial = $obj.attr("captial");
    var interest = $obj.attr("interest");
    var late_interest = $obj.attr("late_interest");
    var repayment_time = $obj.attr("repayment_time");
    //v1.8.0.3_u3 TGPROJECT -334  2014-06-12  qinjun start
    var compensation = $obj.attr("compensation");
    //v1.8.0.3_u3 TGPROJECT-334  2014-06-12  qinjun end
    var address = $obj.attr("address");
    var borrowId = $obj.attr("borrowId");
    if(/^\s*$/.test(borrowId)|| borrowId==undefined){ alert("参数错误，请联系客服"); return; }
    $("#money_").html("应还本息：" + money + " 元");
    $("#captial_").html("应还本金：" + captial + " 元");
    $("#interest_").html("应还利息：" + interest  + " 元");
    $("#repay_tips_").html("提示：您的应还款日期为<b>" + repayment_time + "</b>,请确认！一旦操作，无法回退").css({"color":"#ff0000","height":"55px"});
    $("#repay_tips_ b").css({"font-size":"18px","font-weight":"bold","color":"#ff0000"})
    $("#late_interest_").hide();
    //v1.8.0.3_u3 TGPROJECT-334  2014-06-12  qinjun start
    if(!isNaN(late_interest)&&late_interest!=0){
        $("#late_interest_").show().html("应还逾期利息：" + late_interest + " 元").css({"color":"#ff0000"});
    }else if(!isNaN(compensation)&&compensation!=0){
        $("#late_interest_").show().html("提前还款补偿金：" + compensation + " 元").css({"color":"#ff0000"});
    }
    //v1.8.0.3_u3 TGPROJECT-334  2014-06-12  qinjun end
    $("#form1_pay").attr("action",address);
    $("#id_pay").val(borrowId);
    jQuery( "#modal_dialog" ).dialog({ autoOpen: false , modal: true ,height:300,width:400 });
    jQuery( "#modal_dialog" ).dialog( "open" );
}
function check_form(){
    if(/^\s*$/.test($("#form1_pay").attr("action"))){
        alert("参数错误");
        return;
    }
    if( /^\s*$/.test($("#id_pay").val())){
        alert("参数错误");
        return;
    }
    $("#form1_pay").submit();
}

function clearNoNum(obj){
    var obj_Value = obj.value.replace(/^\s+|\s+$/g, '');
    var type=$('input:radio[name="type"]:checked').val();
    if(isNaN(Number(obj_Value)) ||!(/^\d+(\.\d{1,2})?$/.test(obj_Value))){
        obj.focus();
        $(".tip").html('请输入合理的充值金额');
        return false;
    }
    if(type==1){
        if(isNaN(Number(obj_Value)) || Number(obj_Value) < 50){
            obj.focus();
            $(".tip").html('充值金额不得小于50元');
            return false;
        }
        if(isNaN(Number(obj_Value)) ||Number(obj_Value)> 9999999999){
            obj.focus();
            $(".tip").html('充值金额不得大于9999999999元');
            return false;
        }

        else{$(".tip").html('');}
    }
    if(type==8){

        if(Number(obj_Value) < 5000){
            obj.focus();
            $(".tip").html('汇款充值金额不得小于5000元');
            return false;
        }
        if((isNaN(Number(obj_Value)) ||Number(money)> 9999999999)){
            obj.focus();
            $(".tip").html('汇款充值金额不得大于9999999999元');
            return false;
        }
        if(isNaN(Number(obj_Value)) ||!(/^\d+(\.\d{1,2})?$/.test(obj_Value))){
            obj.focus();
            $(".tip").html('请输入合理的汇款充值金额');
            return false;
        }
        else{ $(".tip").html("");}
    }else{
        if(isNaN(Number(obj_Value)) ||!(/^\d+(\.\d{1,2})?$/.test(obj_Value))){
            obj.focus();
            $(".tip").html('请输入合理的提现金额');
            return false;
        }
        if(isNaN(Number(obj_Value)) || Number(obj_Value) < 100){
            obj.focus();
            $(".tip").html('提现金额不得小于100元');
            return false;
        }
        if(isNaN(Number(obj_Value)) ||Number(obj_Value)> 9999999999){
            obj.focus();
            $(".tip").html('提现金额不得大于9999999999元');
            return false;
        }

        else{$(".tip").html('');}
    }

}
/*时间搜索*/
function sousuo(){
    var url = "";
    var _url = "cashWithdrawal.html?search=true";
    var dotime1 = jQuery("#dotime1").val();
    var dotime2 = jQuery("#dotime2").val();
    var d1 = new Date(dotime1.replace(/\-/g, "\/"));
    var d2 = new Date(dotime2.replace(/\-/g, "\/"));
    console.log(dotime1);
    console.log(dotime2);
    var account_type = jQuery("#account_type").val();
    var isFirst=true;
    if(dotime1!=""&&dotime2!=""&&d1>=d2){
        alert("开始时间不能大于结束时间！");
        return false;
    }
 /*   if(dotime1!=""&&dotime2!=""&&d2-d1>10){
        alert("开始时间不能大于结束时间10！");
        return false;
    }*/
    if(dotime1==""&&dotime2==""&&account_type!=""){
        _url += "&account_type="+account_type;
        location.href=url+_url
        return false;
    }
    else{
        _url += "&dotime1="+dotime1+"&dotime2="+dotime2+"&account_type="+account_type;
        location.href=url+_url
    }
}

function nowtime(){//将当前时间转换成yyyymmdd格式
    var mydate = new Date();
    var str = "" + mydate.getFullYear();
    var mm = mydate.getMonth()+1
    if(mydate.getMonth()>9){
        str += mm;
    }
    else{
        str += "0" + mm;
    }
    if(mydate.getDate()>9){
        str += mydate.getDate();
    }
    else{
        str += "0" + mydate.getDate();
    }
    return str;
}
var jjj=nowtime();
console.log(jjj);
var qqq = jQuery("#dotime2").val().replace(/\-/g, "");
console.log(qqq)
var end_time=["2017.01.12","2016.04.22","2017.01.20"];
for(var i=0;i<=end_time.length;i++){
    var kkk=end_time[i].replace(/\./g, "")
    if(kkk<jjj){
    }
    else{console.log(this)}
}
var end_TimeS=end_time.replace(/\./g, "");
console.log(end_TimeS)
