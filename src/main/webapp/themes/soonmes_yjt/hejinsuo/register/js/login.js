
var loadingDialog = null;

$(function () {
    $('#phone').val("");
    $('#passWord').val("");
    $('#messCode').val("");
    $('#imgCode').val("");

    $("#btnSendMsg").click(function () {
    	
    	sendPhoneCode(phone)
        //sendMobileValidSMSCode();
    });
    $('#phone').focus(function () {
        ClickBox();
    });
    $('#passWord').blur(function () {
        BlurPwd(false, "#passWord", "#dvErrorDesc", "#div_errorbox");
    });
    $('#messCode').blur(function () {
        BlurCode(false, "#messCode", "#dvErrorDesc", "#div_errorbox");
    });
    $('#imgCode').blur(function () {
        BlurCode2(false, "#imgCode", "#dvErrorDesc", "#div_errorbox");
    });
    $('#phone').blur(function () {
        BlurTelNo(false, "#phone", "#dvErrorDesc", "#div_errorbox");
    });
    $("#btnReg").click(function () {
        RegSubmit($("#btnReg"), true);
    });


    $("#baiduReg").keypress(
        function (e) {
            if (e.keyCode == "13")
                RegSubmit($("#btnReg"), true);
        });
});

function reloadCode(urlString) {
    document.getElementById("imVcode").src = urlString + "?id=" + Math.random();
}

function ClickBox() {
    $.ajax({
        type: "post",
        async: false,
        url: "",

        data: "Cmd=cmessage",
        dataType: "json",
        timeout: 3000,
        success: function (jsondata) {

        }
    });
}

function isBeginWithNum(str) {
    var number = str.substring(0, 1);
    var reg = /^\d+$/;
    return number.match(reg);
}

function BlurTelNo(isSubmit, txt, td, errBox) {
    var result = false;
    var patTel = new RegExp("^(13|14|15|17|18)[0-9]{9}$", "i");
    var str = $(txt).val();
    if ($.trim(str) != "") {
        if (patTel.test(str)) {
            $(td).html(GetP("reg_info", "<i></i>正在检测手机号码……"));
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            var bool = phone();//验证手机号码 是否被占用
            result = bool;
           
            
            
           /* var from = "jinhesuo_TG";
            var regFrom = GetQueryString("tdfrom");
            if (regFrom == "" || regFrom == null) {
                regFrom = GetQueryString("from");
                if (regFrom == "" || regFrom == null) {
                    regFrom = GetQueryString("extra");
                    if (regFrom == "" || regFrom == null) {
                        regFrom = getCookie("tdfrom");
                        if (regFrom == "" || regFrom == null) {
                            regFrom = from;
                        }
                    }
                }
            }

            $.ajax({
                type: "post",
                async: false,
                url: "",
                data: "Cmd=CheckPhone&sPhone=" + str + "&dec=" + regFrom,
                dataType: "json",
                timeout: 3000,
                success: function (jsondata) {
                    var data = jsondata.result;
                    if (data == "False") {
                        $(td).hide();
                        $(errBox).attr("style", "height:10px; margin:20px 0px 5px 0px;");
                        $(txt).removeClass("error");
                        result = true;
                    }
                    else {
                        if (regFrom == "mzps-m1604-01") {
                            $(td).html("<i></i>手机号已被注册,3秒跳转到wifi链接").show();
                            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
                            $(txt).addClass("error");

                            result = false;
                        }
                        else {
                            $(td).html("<i></i>手机号已被注册").show();
                            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
                            $(txt).addClass("error");
                            result = false;
                        }
                    }
                }
            });*/
        }
        else {
            var errorStr = "请输入正确的手机号";
            $(td).html("<i></i>" + errorStr).show();
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            $(txt).addClass("error");
            result = false;
        }
        return result;
    }
    else {
        if (isSubmit == true) {
            $(td).html("<i></i>请输入手机号").show();
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            $(txt).addClass("error");
        }
        else {
            $(td).hide();
            $(errBox).attr("style", "height:10px; margin:20px 0px 5px 0px;");
            $(txt).removeClass("error");
        }
        return false;
    }
}

function BlurPwd(isSubmit, txt, td, errBox) {
    var str = $(txt).val();

    if (str.indexOf(" ") > -1) {
        var errorStr = "密码不允许含有空格";
        $(td).html("<i></i>" + errorStr).show();
        $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
        $(txt).addClass("error");
        return false;
    }

    if ($.trim(str) != "") {
        if (/^.*?[\d]+.*$/.test(str) && /^.*?[A-Za-z].*$/.test(str) && /^.{6,16}$/.test(str)) {
            //格式正确
            $(td).hide();
            $(errBox).attr("style", "height:10px; margin:20px 0px 5px 0px;");
            $(txt).removeClass("error");
            return true;
        }
        else {
            var errorStr = "密码由6-16位字符+数字组成";
            $(td).html("<i></i>" + errorStr).show();
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            $(txt).addClass("error");
            return false;
        }
    }
    else {
        if (isSubmit == true) {
            $(td).html("<i></i>请输入密码").show();
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            $(txt).addClass("error");
        }
        else {
            $(td).hide();
            $(errBox).attr("style", "height:10px; margin:20px 0px 5px 0px;");
            $(txt).removeClass("error");
        }
        return false;
    }
}

//检验 验证码
function BlurCode(isSubmit, txt, td, errBox) {
    var str = $(txt).val();
    var pat = new RegExp("^[0-9]{6}$", "i");

    if ($.trim(str) != "") {
        if (!pat.test(str)) {   //格式不正确
            var errorStr = "验证码格式错误";
            $(td).html("<i></i>" + errorStr).show();
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            $(txt).addClass("error");
            return false;
        }
        else {
            $(td).hide();
            $(errBox).attr("style", "height:10px; margin:20px 0px 5px 0px;");
            $(txt).removeClass("error");
            return true;
        }
    }
    else {
        if (isSubmit == true) {
            $(td).html("<i></i>请输入验证码").show();
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            $(txt).addClass("error");
        }
        else {
            $(td).hide();
            $(errBox).attr("style", "height:10px; margin:20px 0px 5px 0px;");
            $(txt).removeClass("error");
        }
        return false;
    }
}

function BlurCode2(isSubmit, txt, td, errBox) {
    var str = $(txt).val();
    var pat = new RegExp("^[\\da-z]{4}$", "i");

    if ($.trim(str) != "") {
        if (!pat.test(str)) {   //格式不正确
            var errorStr = "图形验证码格式错误";
            $(td).html("<i></i>" + errorStr).show();
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            $(txt).addClass("error");
            return false;
        }
        else {
            $(td).hide();
            $(errBox).attr("style", "height:10px; margin:20px 0px 5px 0px;");
            $(txt).removeClass("error");
            return true;
        }
    }
    else {
        if (isSubmit == true) {
            $(td).html("<i></i>请输入图形验证码").show();
            $(errBox).attr("style", "height:20px; margin:14px 0px 5px 0px;");
            $(txt).addClass("error");
        }
        else {
            $(td).hide();
            $(errBox).attr("style", "height:10px; margin:20px 0px 5px 0px;");
            $(txt).removeClass("error");
        }
        return false;
    }
}

function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

function RegSubmit(ctrl, isMain) {

    var phoneId = "#phone";
    var pwdId = "#passWord";
    var codeId = "#messCode";
    var codeId2 = "#imgCode";
    var errDescId = "#dvErrorDesc";
    var errBox = "#div_errorbox";

    if (isMain == false) {
        phoneId = "#phone_1";
        pwdId = "#passWord_1";
        codeId = "#messCode_1";
        errDescId = "#dvErrorDesc_1";
        errBox = "";
    }

    if (BlurPwd(true, pwdId, errDescId, errBox) && BlurTelNo(true, phoneId, errDescId, errBox) && BlurCode2(true, codeId2, errDescId, errBox) && BlurCode(true, codeId, errDescId, errBox)) {
        $(ctrl).unbind("click");

        loadingDialog = art.dialog({
            id: 'N1589' + Math.random(),
            title: '注册金和所',
            content: "<img src=\"../imgs/loading24.gif\"/>&nbsp;正在提交注册信息，请稍候......",
            opacity: 0.5,
            fixed: false,
            lock: true,
            zIndex: 9999999,
            drag: true,
            width: 600,
            padding: 0,
            background: '#fff'
        });

        var PhoneCode = $.trim($(codeId).val());
        var Mobile = $.trim($(phoneId).val());
        var Password = $.trim($(pwdId).val());

        var from = "jinhesuo_TG";
        var regFrom = GetQueryString("tdfrom");
        if (regFrom == "" || regFrom == null) {
            regFrom = GetQueryString("from");
            if (regFrom == "" || regFrom == null) {
                regFrom = GetQueryString("extra");
                if (regFrom == "" || regFrom == null) {
                    regFrom = getCookie("tdfrom");
                    if (regFrom == "" || regFrom == null) {
                        regFrom = from;
                    }
                }
            }
        }

        $.ajax({
            async: false,
            url: "",//地址
            dataType: "json",
            data: {
                Cmd: "User_Insert_Phone",
                UserTypeId: "1",
                phoneCode: PhoneCode,
                Phone: Mobile,
                Pwd: Password,
                dec: regFrom,
                tid: GetQueryString("tid"),
                sn: GetQueryString("sn"),
                wps_userid: GetQueryString("userid"),  //wps合作推广
                params: GetQueryString("params"),
                rtm_site: GetQueryString("rtm_site"),
                rtm_source: GetQueryString("rtm_source"),
                extendKey: GetQueryString("extendKey") == null ? GetQueryString("extendkey") : ""
            },
            success: function (json) {
                $(ctrl).click(function () {
                    RegSubmit(ctrl);
                });
                AsyncReg(json, ctrl);
            },
            error: function () {
                $(ctrl).click(function () {
                    RegSubmit(ctrl);
                });
            }
        });
    }


}

function AsyncReg(data, ctrl) {
    loadingDialog.close();
    if (data.result == "True" || data.result == true) {
        // AsyncReg_Back();
    }
    else if (data.result == "False" || data.result == false) {
        art.dialog({
            zIndex: 9999999,
            width: 400,
            time: 5,
            content: data.msg
        });
    }
    else {
        if (GetQueryString("rtm_site") != '' && GetQueryString("rtm_site") != null) {
            parent.window.location.href = "";//http://js.jinhesuo.com/View/NoticeMessage.aspx?status=2
        }
        else {
            window.location.href = "";//http://js.jinhesuo.com/View/NoticeMessage.aspx?status=2
        }

    }
}
///注册成功
function AsyncReg_Back() {
    var returnUrl = GetQueryString("ReturnUrl");
    if (returnUrl != "" && returnUrl != null) {
        if (GetQueryString("rtm_site") != '' && GetQueryString("rtm_site") != null) {
            parent.window.location.href = returnUrl;
        }
        else {
            window.location.href = returnUrl;
        }
    }
    else {
        returnUrl = "";//http://js.jinhesuo.com/user/Registered.aspx
        var from = GetQueryString("from") || GetQueryString("tdfrom");
        if (from != null) {
            //卖座网电影票
            if (from.toLowerCase().indexOf("mzw-") == 0) {
                //判断电影票是否已送完
                $.ajax({
                    async: true,
                    url: "",
                    dataType: "json",
                    data: {
                        Cmd: "HasMovieTicket"
                    },
                    success: function (data) {
                        //如果还有电影票
                        if (data.result == "1" && data.msg == "1") {
                            returnUrl += "?from=mzw-";//只有还有电影票的情况 才加from参数
                            if (GetQueryString("rtm_site") != '' && GetQueryString("rtm_site") != null) {
                                parent.window.location.href = returnUrl;
                            }
                            else {
                                window.location.href = returnUrl;
                            }
                        } else {
                            $(".m-mask").show();
                            $(".movie-end").show();
                        }
                    },
                    error: function () {
                        if (GetQueryString("rtm_site") != '' && GetQueryString("rtm_site") != null) {
                            parent.window.location.href = returnUrl;
                        }
                        else {
                            window.location.href = returnUrl;
                        }
                    }
                });
                return;
            }

            //注册送VIP活动-活动时间2016-04-19到2016-04-30结束,之后可以删除begin
            if (from.toLowerCase().indexOf("kuxin-") == 0) {
                //判断会员是是否已送完
                $.ajax({
                    async: true,
                    url: "",
                    dataType: "json",
                    data: {
                        Cmd: "HasYkVip"
                    },
                    success: function (data) {
                        //如果还有会员
                        if (data.result == "1" && data.msg == "1") {
                            returnUrl += "?from=kuxin-";//只有还有会员的情况 才加from参数
                            if (GetQueryString("rtm_site") != '' && GetQueryString("rtm_site") != null) {
                                parent.window.location.href = returnUrl;
                            }
                            else {
                                window.location.href = returnUrl;
                            }
                        } else {
                            $(".m-mask").show();
                            $(".movie-end").show();
                        }
                    },
                    error: function () {
                        if (GetQueryString("rtm_site") != '' && GetQueryString("rtm_site") != null) {
                            parent.window.location.href = returnUrl;
                        }
                        else {
                            window.location.href = returnUrl;
                        }
                    }
                });
                return;
            }
            //注册送VIP活动-活动时间2016-04-19到2016-04-30结束,之后可以删除end
        }

        if (GetQueryString("rtm_site") != '' && GetQueryString("rtm_site") != null) {
            parent.window.location.href = returnUrl;
        }
        else {
            window.location.href = returnUrl;
        }
    }
}

function GetP(clsName, c) {
    return c;
}

////刷新验证码
//function reload(urlString) {
//    $("#imVcode").attr("src", urlString + "?id=" + Math.random());
//}

var timer = null;
var leftsecond = 60; //倒计时
var msg = "";
var mbTest = /^(13|14|15|17|18)[0-9]{9}$/;
//获取手机验证码
function sendMobileValidSMSCode() {
    $("#btnSendMsg").unbind("click").attr("class", "wait_but");
    var mobile = $("#phone").val();
    var imgcode = $("#imgCode").val();
    if (mobile == "" || mobile == "请输入手机号码") {
        $("#btnSendMsg").click(function () {
            sendMobileValidSMSCode();
        });
        $("#dvErrorDesc").html("<i></i>请输入手机号码").show();
        $("#btnSendMsg").attr("class", "messCode_but");
        $("#phone").val("");
        $("#phone").focus();
        return;
    }
    if (imgcode == "" || imgcode == null) {
        $("#btnSendMsg").click(function () {
            sendMobileValidSMSCode();
        });
        $("#dvErrorDesc").html("<i></i>请输入图形验证码").show();
        $("#btnSendMsg").attr("class", "messCode_but");
        $("#phone").focus();
        return;
    }
    if (mbTest.test(mobile)) {
        $("#btnSendMsg").html("短信发送中...");

        $.ajax({
            url: "",
            type: "post",
            dataType: "json",
            data: {Cmd: "getPhoneRegCode2", mobile: mobile, sVerCode: imgcode},
            success: function (json) {
                var result = json.result;
                leftsecond = 60;
                if (parseInt(result) == -100) {
                    window.location.href = "http://js.jinhesuo.com/View/NoticeMessage.aspx?status=2";
                }
                if (result == "true") {
                    msg = "发送成功，如未收到";
                    clearInterval(timer);
                    timer = setInterval(setLeftTime, 1000, "1");
                    $("#phone").attr("readonly", true);
                }
                else {
                    switch (result) {
                        case "-1":
                            $("#imgCode").val("");
                            reloadCode('');//http://js.jinhesuo.com/ValidateCode.ashx
                            $("#btnSendMsg").html("获取手机验证码");
                            msg = "验证码发送失败请刷新重试";
                            $("#div_errorbox").attr("style", "height:20px; margin:14px 0px 5px 0px;");
                            $("#dvErrorDesc").html("<i></i>" + msg).show();
                            $("#btnSendMsg").click(function () {
                                sendMobileValidSMSCode();
                            });
                            $("#btnSendMsg").attr("class", "messCode_but");
                            $("#phone").removeAttr("readonly");
                            $("#phone").val("");
                            $("#phone").focus();
                            break;
                        case "-2":
                            $("#imgCode").val("");
                            reloadCode('');//http://js.jinhesuo.com/ValidateCode.ashx
                            $("#btnSendMsg").html("获取手机验证码");
                            $("#div_errorbox").attr("style", "height:20px; margin:14px 0px 5px 0px;");
                            $("#dvErrorDesc").html("<i></i>手机号码已被其他用户使用").show();
                            $("#btnSendMsg").click(function () {
                                sendMobileValidSMSCode();
                            });
                            $("#btnSendMsg").attr("class", "messCode_but");
                            $("#phone").removeAttr("readonly");
                            // $("#phone").val("");
                            $("#phone").focus();
                            break;
                        case "-3":
                        case "-4":
                            $("#imgCode").val("");
                            reloadCode('');//http://js.jinhesuo.com/ValidateCode.ashx
                            $("#btnSendMsg").html("获取手机验证码");
                            $("#div_errorbox").attr("style", "height:20px; margin:14px 0px 5px 0px;");
                            $("#dvErrorDesc").html("<i></i>" + json.msg).show();
                            $("#btnSendMsg").click(function () {
                                sendMobileValidSMSCode();
                            });
                            $("#btnSendMsg").attr("class", "messCode_but");
                            $("#phone").removeAttr("readonly");
                            $("#imgCode").focus();
                            break;
                        case "-5":
                        case "-6":
                        case "-7":
                        case "-8":
                        case "-9":
                        case "-10":
                        case "-11":
                            $("#imgCode").val("");
                            reloadCode('');//http://js.jinhesuo.com/ValidateCode.ashx
                            $("#btnSendMsg").html("获取手机验证码");
                            $("#div_errorbox").attr("style", "height:20px; margin:14px 0px 5px 0px;");
                            $("#dvErrorDesc").html("<i></i>" + json.msg).show();
                            $("#btnSendMsg").click(function () {
                                sendMobileValidSMSCode();
                            });
                            $("#btnSendMsg").attr("class", "messCode_but");
                            $("#phone").removeAttr("readonly");
                            $("#phone").val("");
                            $("#phone").focus();
                            break;

                    }
                }
                //if (result == "-2") {
                //    $("#btnSendMsg").html("获取手机验证码");
                //    $("#div_errorbox").attr("style", "height:20px; margin:10px 0px 5px 0px;");
                //    $("#dvErrorDesc").html("<i></i>手机号码已被其他用户使用").show();
                //    $("#btnSendMsg").click(function () { sendMobileValidSMSCode(); });
                //    $("#btnSendMsg").attr("class", "messCode_but");
                //    $("#phone").removeAttr("readonly");
                //}
                //else if (result == "-3") {
                //    $("#btnSendMsg").html("获取手机验证码");
                //    $("#div_errorbox").attr("style", "height:20px; margin:10px 0px 5px 0px;");
                //    $("#dvErrorDesc").html("<i></i>" + json.msg).show();
                //    $("#btnSendMsg").click(function () { sendMobileValidSMSCode(); });
                //    $("#btnSendMsg").attr("class", "messCode_but");
                //    $("#phone").removeAttr("readonly");
                //}
                //else if (result == "-4") {
                //    reloadCode('http://js.jinhesuo.com/ValidateCode.ashx');
                //    $("#imgCode").val('');
                //    $("#btnSendMsg").html("获取手机验证码");
                //    $("#div_errorbox").attr("style", "height:20px; margin:10px 0px 5px 0px;");
                //    $("#dvErrorDesc").html("<i></i>" + json.msg).show();
                //    $("#btnSendMsg").click(function () { sendMobileValidSMSCode(); });
                //    $("#btnSendMsg").attr("class", "messCode_but");
                //    $("#phone").removeAttr("readonly");
                //}
                //else {
                //    $("#btnSendMsg").html("获取手机验证码");
                //    msg = "验证码发送失败请刷新重试";
                //    $("#div_errorbox").attr("style", "height:20px; margin:10px 0px 5px 0px;");
                //    $("#dvErrorDesc").html("<i></i>" + msg).show();
                //    //clearInterval(timer);
                //    //timer = setInterval(setLeftTime, 1000, "1");
                //    $("#btnSendMsg").click(function () { sendMobileValidSMSCode(); });
                //    $("#btnSendMsg").attr("class", "messCode_but");
                //    $("#phone").removeAttr("readonly");
                //}
            },
            error: function () {
                $("#btnSendMsg").html("获取手机验证码");
                msg = "验证码发送失败请刷新重试代码:001";
                $("#div_errorbox").attr("style", "height:20px; margin:14px 0px 5px 0px;");
                $("#dvErrorDesc").html("<i></i>" + msg).show();
                $("#btnSendMsg").click(function () {
                    sendMobileValidSMSCode();
                });
                $("#btnSendMsg").attr("class", "messCode_but");
                $("#phone").removeAttr("readonly");
                $("#phone").focus();
            }
        });
    }
    else {
        $("#div_errorbox").attr("style", "height:20px; margin:14px 5px 0px;");
        $("#dvErrorDesc").html("<i></i>手机号码不正确").show();
        $("#btnSendMsg").click(function () {
            sendMobileValidSMSCode();
        });
        $("#btnSendMsg").attr("class", "messCode_but");
        $("#phone").focus();
    }
}
//手机号码验证 是否存在

function phone(){
	
    $.get("checkPhone.html?id="+Math.random(), {phone: $("#phone").val() },function (result){
        if(result==true){
        	$("#dvErrorDesc").hide();
             $("#div_errorbox").attr("style", "height:10px; margin:20px 0px 5px 0px;");
             $("#imgCode").removeClass("error");
            return true;
        } else {
        	 $("#dvErrorDesc").html("<i></i>手机号已被注册").show();
        	 $("#div_errorbox").attr("style", "height:20px; margin:14px 0px 5px 0px;");
        	 $("#imgCode").addClass("error");
        	//$("#dvErrorDesc").html("<i></i>手机号已被注册");
            return false;
        }
    });
}



// 发送手机验证码
function sendPhoneCode(phone){
	$.ajax({
		"url": "/user/getPhoneCode.html",
		"type": "post",
		"cache": "false",
		"data": {'phone':phone},
		"dataType": "json",
		"error": function(){
		},
		"success": function(data){
			alert("验证码发送成功，请注意查收");
		}
	   
	})
}


function setLeftTime() {
    var second = Math.floor(leftsecond);
    $("#btnSendMsg").html(second + "秒后可重发");
    leftsecond--;
    if (leftsecond < 1) {
        $("#btnSendMsg").html("获取手机验证码");
        $("#btnSendMsg").attr("class", "messCode_but");
        clearInterval(timer);
        try {
            $("#btnSendMsg").click(function () {
                sendMobileValidSMSCode();
            });
            $("#phone").removeAttr("readonly");
            $("#phone").focus();
        } catch (E) {
        }
        return;
    }
}
