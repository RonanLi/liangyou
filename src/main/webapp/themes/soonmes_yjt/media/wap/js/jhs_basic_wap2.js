$(".line2Div>a").attr('href','#').addClass('btn');
$('.iptBox>input').focus(function () {
    $("#Error").hide();
});

/*手机验证码*/
$("#sendPhoneCode").click(function(){
    if( $('.phone').val().trim() == '' ){
        $("#Error").html("手机号不能为空").show();
        return false;
    }else if( $('.imgCode').val().trim() == '' ){
        $("#Error").html("图形验证码不能为空").show();
        return false;
    }else {
        sendPhoneCode($('.phone').val().trim(), $('.imgCode').val().trim());
    }

});
function sendPhoneCode(phone, imgCode){
    $("#Error").hide();
//        PhoneTest();
    var tim = 60, xt;
    $.ajax({
        "url": "/user/getPhoneCode.html",
        "type": "post",
        "cache": "false",
        "data": {'phone':phone, 'valicode' : imgCode},
        "dataType": "json",
        success: function(data){

            if (data.data != "") {
                $("#Error").html(data.data).show();
                $('.updateimg_1').trigger('click');
                return false;
            } else {
                $("#sendPhoneCode").attr('disabled',"true");

                xt = window.setInterval(function(){
                    if(tim < 1){
                        window.clearInterval(xt);
                        $('#sendPhoneCode').removeAttr('disabled',"true").val('获取验证码');
                        $("#Error").hide();
                    }else{
                        $('#sendPhoneCode').val(tim+'秒后重发');
                        tim--;
                    }
                },1000);
                $("#Error").html("验证码发送成功，请注意查收");
                $("#Error").show();
                return false;
            }
        }

    })
}

/*注册*/
$("#regBtn").click(function(){
    var phone = $(".phone").val();
    var pwd= $(".password").val();
    var pwd_len= pwd.length;
    var pwdtest = getResult(pwd);
    if( $('.phone').val()== '' ){
        $("#Error").html("手机号不能为空").show();
        return false;
    }
    else if( $('.passWord').val()== '' ){
        $("#Error").html("密码不能为空").show();
        return false;
    }
    else if(pwd_len<6||pwd_len>18){
        $("#Error").html("请输入6至18位，数字加字母的密码").show();
        return false;
    }
    else if( $('.imgCode').val()== '' ){
        $("#Error").html("图形验证码不能为空").show();
        return false;
    }
    else if( $('.phoneCode').val()== '' ){
        $("#Error").html("手机验证码不能为空").show();
        return false;
    }
    else if((pwd_len>=6)&&(pwdtest<2)){
        $("#Error").html("请输入6至18位，数字加字母的密码").show();
        return false;
    }
    else if((pwdtest<2)&&(pwd_len<=18)){
        $("#Error").html("请输入6至18位，数字加字母的密码").show();
        return false;
    }
    else {
        $("#Error").html("").hide();
        testForm();
    }
});

function testForm(){
    console.log("0000");
    $.ajax({
        type: "POST",
        url:"/user/doRegister.html",
        data:{
            "username":$('#username').val(),
            "password":$('#password').val(),
            "validCodee":$('#validCodee').val(),
            "channel":$('#channel').val(),
            "code":$('#code').val(),
            "test":$('#test').val()
        },
        beforeSend:function(){
            $('#regBtn').val('正在注册...');
            $("#regBtn").attr('disabled',"true");
        },
        success: function(msg) {
            var x = document.createElement('html');
            x.innerHTML = msg;
            var errorMSG = $(x).find('#error_zl').text();
            if( errorMSG.indexOf('注册成功') >= 0 ){
                var n = 3;
                var _xc = window.setInterval(function(){
                    if( n == 0 ){
                        window.clearInterval(_xc);
                        window.location.href='/wap/aggregationPage.html?router=login';
                    }else{
                        $('#regBtn').val('注册成功,'+ n +'秒后跳转');
                        n--;
                    }
                },1000);

            }else{
                $('#regBtn').removeAttr('disabled').val('注册');
                $("#Error").html(errorMSG).show();
                return false;
            }

        }
    });
}


/*密码判断*/
/*function pwdTest(){
 var pwd= $(".passWord").val();
 var pwd_len = pwd.length;
 var pwdtest = getResult(pwd);;
 if( $("#Error").text().indexOf('手机号已注册') >= 0 ){

 return false;
 }else if((pwd_len>=6)&&(pwd_len<=18)){
 if(pwdtest<2) {
 $("#Error").html("请输入6至18位，数字加字母的密码").show();
 return false;
 }
 else{
 $("#Error").html("").hide();
 return true;
 }
 }else{
 $("#Error").html("请输入6至18位，数字加字母的密码").show();
 return false;
 }
 }*/

/*检测*/
function getResult(value){
    var str_len = value.length;
    var i = 0;
    if(value.match(/[a-z]/ig)) {
        i++;
    }
    if(value.match(/[0-9]/ig)) {
        i++;
    }
    if(value.match(/(.[^a-z0-9])/ig)) {
        i++;
    }
    if(value.length <=5 && i > 0) {
        i--;
    }
    return i;
}

/*轮播*/
var swiper = new Swiper('.swiper-container', {
    pagination: '.swiper-pagination',
    nextButton: '.swiper-button-next',
    prevButton: '.swiper-button-prev',
    paginationClickable: true,
    spaceBetween: 30,
    centeredSlides: true,
    autoplay: 2500,
    autoplayDisableOnInteraction: false
});
