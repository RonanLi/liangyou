/* zhuolun 2016 */
var S = (function(){

    window.Szl = {
        ui:{
            tooltips:function(tex){
                if( !$('#tooltips').hasClass('leave') && $('#tooltips').hasClass('active') ){
                    return;
                }else{
                    $('#tooltips').attr('class','tooltips active').find('div').text(tex);
                    setTimeout(function(){
                        $('#tooltips').attr('class','tooltips active leave');
                    },3500);
                }

            },
            o0o : function (elem){
                var $elem = $(elem);
                var $window = $(window);
                var docViewTop = $window.scrollTop();
                var docViewBottom = docViewTop + $window.height();
                var elemTop = $elem.offset().top;
                var elemBottom = elemTop + $elem.height();

                return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
            },
            gUrlp:function(zl){
                return decodeURIComponent((new RegExp('[?|&]' + zl + '=' + '([^&;]+?)(&|#|;|$)', "ig").exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || null;
            },
            isEmpty:function(str){
                if(typeof(str) == 'number' || typeof(str) == 'boolean')
                  {
                    return false;
                  }
                  if(typeof(str) == 'undefined' || str === null)
                  {
                    return true;
                  }
                  if(typeof(str.length) != 'undefined')
                  {
                    return str.length == 0;
                  }
                  var count = 0;
                  for(var i in str)
                  {
                    if(str.hasOwnProperty(i))
                    {
                      count ++;
                    }
                  }
                  return count == 0;
            },
            isMobile:function(){
                return ico = {
                    Android: function(){
                        return navigator.userAgent.match(/Android/i);
                    },
                    wechat: function(){
                        return navigator.userAgent.match(/MicroMessenger/i);
                    },
                    BlackBerry: function(){
                        return navigator.userAgent.match(/BlackBerry/i);
                    },
                    iOS: function(){
                        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
                    },
                    Opera: function(){
                        return navigator.userAgent.match(/Opera Mini/i);
                    },
                    Windows: function(){
                        return navigator.userAgent.match(/IEMobile/i);
                    },
                    any: function(){
                        return (ico.Android() || ico.BlackBerry() || ico.iOS() || ico.Opera() || ico.Windows() || ico.wechat());
                   }
                }
            },
            CK:{
                setcookie:function(name,value,tt){
                        var expdate = new Date(),
                            outms = tt;
                            expdate.setTime(expdate.getTime()+outms);
                        document.cookie = name +"="+ encodeURIComponent(value) +";path=/;domain="+ location.hostname +";expires=" + expdate.toUTCString();

                },
                readcookie:function(cookiename){
                    var value = document.cookie.match(new RegExp("(^| )" + cookiename + "=([^;]*)(;|$)"));
                    return null != value ? decodeURIComponent(value[2]) : null;
                }
            }
        }
    };

    NProgress.configure({ showSpinner: false });
    NProgress.configure({ minimum: 0.1 });

    document.onreadystatechange = function(){
        NProgress.start();
        if( document.readyState == "Uninitialized" ){
            NProgress.set(0);
        }
        if( document.readyState == "Interactive" ){
            NProgress.set(0.3);
        }
        if( document.readyState === "complete" ){

            NProgress.set(0.8);

            var percent = 0, mark = $('html').attr('data-area');
            function _loadimg(){
                var IMG = new Array(), Len = _loadimg.arguments.length, _plus = 100 / Len;

                for( var i = 0; i < Len; i++ ){
                    IMG[i] = new Image();
                    IMG[i].src = _loadimg.arguments[i];
                    IMG[i].onload = function(){
                        percent += _plus;
                        if( percent > 88 ){
                            //document.body.className = "";
                            NProgress.done();
                        }
                    }

                }
            }

            if( mark == 'log' ){
                _loadimg('/themes/soonmes_yjt/media/wap/img/bglogio.jpg','/themes/soonmes_yjt/media/wap/img/mountain.jpg','/themes/soonmes_yjt/media/wap/img/male_default.png','/themes/soonmes_yjt/media/wap/img/female_default.png','/themes/soonmes_yjt/media/wap/img/userpbg.png','/themes/soonmes_yjt/media/wap/img/xiao.png');

            }else if( mark == 'indexPage' ){
                _loadimg('/themes/soonmes_yjt/media/wap/img/activity_banners/banners/1_bans.jpg','/themes/soonmes_yjt/media/wap/img/activity_banners/banners/2_bans.jpg','/themes/soonmes_yjt/media/wap/img/activity_banners/banners/3_bans.jpg','/themes/soonmes_yjt/media/wap/img/activity_banners/banners/4_bans.jpg','/themes/soonmes_yjt/media/wap/img/activity_banners/banners/5_bans.jpg');
            }else{
               // document.body.className = "";
                NProgress.done();
            }



        }
    }

    function runJHS(){

        var ip = '',
            eleClass = $('html').attr('data-area'),
            footnav = $('#navJHS'),
            hideKeyboard = function(){
                document.activeElement.blur();
                var inputs = document.querySelectorAll('input');
                for(var i = 0; i < inputs.length; i++){
                    inputs[i].blur();
                }
            }, ajax_count = 2;

        // programming start
        document.documentElement.style.fontSize = document.documentElement.clientWidth / 10 + 'px';

        if( Szl.ui.isMobile().any() === null || navigator.userAgent.indexOf('WindowsWechat') >= 0 ){
            $('html').attr({'data-terminal':'notouch','name':'notouch'});
        }else{
            $('html').attr({'data-terminal':Szl.ui.isMobile().any()});
        }

        //底部导航
        footnav.find('.'+ eleClass +' > a').addClass("index").parent().siblings("li").find('a').removeClass("index");

        $('.wDih').each(function(){
            var widt =  $(this).attr('data-wid');
            $(this).css('width',widt+'%').parents('.lineProject').next('em')[0].innerText ='项目进度'+widt+'%';
        });

        document.addEventListener('scroll',function(){ Throttle(throttle_scroll); },false);

        function Throttle( method,set_txt ){
            window.clearTimeout(method.hehe);
            method.hehe = window.setTimeout(function(){
                method.call(set_txt);
            },300);
        }

        // 标的进度整数制
        $('.Epcharts.non-after').each(function(){
            var xu = $(this).data('percent');
            $(this).attr('data-percent',Math.floor(xu));

        });

        //滚动操作
        function throttle_scroll(){

            // 返回顶部
            if( $(window).scrollTop() > 808 && $('#backtotop').length > 0){
                $('#backtotop').addClass('show');
            }else{
                $('#backtotop').removeClass('show');
            }

            // 首页svg 写入
            // if( $('#numberaniamte').length > 0 && Szl.ui.o0o('#o0o') && !$('.svgami').hasClass('show') ){
            //    $('.svgami').addClass('show');
            // }

            // 加载更多数据[DoTS]
            var _iwdt = $('#DoTS');
            if( _iwdt.length > 0 && _iwdt.css('display') == 'block' ){

                if( location.pathname == "/wap/listbids.html" && Szl.ui.o0o(_iwdt) ){
                    var _status_bidsl = $('.seledi').eq(0).find('span.index').attr('data-v'),
                        _deadlines_bidsl = $('.seledi').eq(1).find('span.index').attr('data-v');
                    aia('/interface/myAccount/borrowList.html?page='+ ajax_count+'&xo='+Math.random(),{ status:_status_bidsl, timeLimit:_deadlines_bidsl },function(){ _iwdt.show(); },function(data){
                        var xa = new Array(), D = data.obj.borrowList;
                        if( D.length > 0 ){

                            for( var i = 0; i < D.length; i++ ){

                                var fort = Date.parse( new Date(D[i].borrowAddTime.replace(' ', 'T')) ), sta = new Date("Mon Dec 12 2016 00:00:00 GMT+0800 (CST)").getTime(), eta =new Date("Sta Apr 01 2017 00:00:00 GMT+0800 (CST)").getTime(), io_ =  D[i].borrowApr + '%', repayType = '一次性还本付息';

                                if( D[i].borrowIsDay==1 && D[i].timeLimitDay==30 && fort >= sta && fort < eta ){
                                    io_ = ( D[i].borrowApr - 1 ).toFixed(1) + '%<i class="xep">+1%</i>';
                                }

                                if( D[i].borrowIsDay==0 && D[i].timeLimit==1 && fort >= sta && fort < eta ){
                                    io_ = ( D[i].borrowApr - 1 ) + '%<i class="xep">+1%</i>';
                                }

                                if( D[i].borrowIsDay==0 && D[i].timeLimit==2 && fort >= sta && fort < eta ){
                                    io_ = ( D[i].borrowApr - 1 ) + '%<i class="xep">+1%</i>';
                                }

                                if( D[i].borrowIsDay==0 && D[i].timeLimit==3 && fort >= sta && fort < eta ){
                                    io_ = ( D[i].borrowApr - 1 ) + '%<i class="xep">+1%</i>';
                                }
                                
                                if(D[i].borrowIsDay == 1) {
                                	repayType = "到期全额还款";
                                } 

                                xa.push('<article class="cards-bids"><a href="'+ D[i].borrowLink +'" class="d-b">'+
                                      '<div class="titleinfo F">'+
                                          '<span>'+ D[i].borrowName +'</span><span class="tittype">'+ repayType +'</span>'+
                                      '</div><div class="F info-Cards"><div class="inFO"><ul class="F">'+
                                    '<li>'+ io_ +'<em>预期年化收益率</em></li><li class="inside">'+
                                          D[i].borrowTimeLimit +'<em>项目期限</em>'+
                                      '</li><li class="linePercent"><div class="lineProject R"><span class="R"><i class="wDih" style="width:'+ D[i].borrowSchedule +'%"></i></span><div><em>项目进度'+ D[i].borrowSchedule.toFixed(2) +'%</em></li></ul></div></div></a></article>');
                            }
                            _iwdt.before(xa.join(''));
                            // _iwdt.show();

                            // $('.Epcharts.ax-' + ajax_count).each(function(index){

                            //     var chart = window.chart = new EasyPieChart($('.Epcharts.ax-' + ajax_count)[index], {
                            //         easing: 'easeOutElastic',
                            //         delay: 3000,
                            //         barColor: '#f39e03',
                            //         trackColor: 'rgba(245,245,245,.9)',
                            //         scaleColor: 'orange',
                            //         lineWidth: 2,
                            //         trackWidth: 4,
                            //         size:64,
                            //         lineCap: 'circle',
                            //         onStep: function(from, to, percent) {
                            //             this.el.children[0].innerHTML = Math.round(percent);
                            //         }
                            //     });
                            // });

                            ajax_count++;

                        }else{

                            _iwdt.hide();
                        }

                    });


                }




            }





        }

        // ajax pretension
        $.extend($.ajaxSettings,{
            headers: {
              'author': 'ZhuoLunS_jhfax'
            }
        });

       function aia(URL,para_obj,bfSend,succ){
            $.ajax({
            url: ip + URL,
            type: 'POST',
            dataType:'json',
            data: para_obj || {},
            beforeSend:bfSend,
            success:succ,
            error:function(xhr, errorType, error){
                alert('服务器请求异常\n\r错误信息：\n'+ errorType + ',' + error);
                //window.location.replace('/wap/aggregationPage.html?router=login');
            }
        });
       }


        // 更新图片验证码
        function ajaximg(){
            var x = $(this);
            $.get(ip+'/ajaxValidimg.html?t='+ Math.random(), function(data){
                var xurlbase = 'data:image/jpg;base64,' + JSON.parse(data).obj.valiCodeImg;
                if( x[0].className == 'updateimg' ){
                    x.attr('src',xurlbase);
                }else{
                    $('.updateimg').attr('src',xurlbase);
                }

            });
        };

        $(document).on('click', '.updateimg', ajaximg);

        //路由
        function pushstateUrl(url){

            var urlPushjson = {time:new Date().getTime()},
                ourl = Szl.ui.gUrlp('router');

            NProgress.start();
            history.replaceState(urlPushjson, url,'?router=' + url);
            NProgress.set(0.6);
            setTimeout(function(){ NProgress.done(); },1000);
            if( url == 'userinfo' ){
               window.location.replace('Innerpage.html');
            }

        }

        function _getFd(x){
            if( x.length > 0 ){
                var X = x.get(0).nodeName.toLowerCase(), Y, er;
                if( X == 'input' ){
                    Y = x.val();
                }else{
                    Y = x.text();
                }
                er = Y.replace(/[^\d\.]/g,'');
                /*by lrn 修改万元为元*/
               /* if( Y.indexOf('万') > 0 ){
                    x.attr('data-text',er*10000);
                }else{
                    x.attr('data-text',er );
                }*/
                x.attr('data-text',er );
            }

        }


        if( $('html').hasClass('staticPage') ){
            var $anbz = $('.aqbz');
            $anbz.on('click','.card.f',function(){
                if( $(this).hasClass('show') ){
                    $(this).removeClass('show');
                }else{
                    $(this).addClass('show');
                }
            });

        }



        $('#backtotop').on('click',function(){ $('html,body').scrollTop(0); });
        $('#goBack').on('click',function(){
            if( document.referrer == '' ){
                window.location.href = 'index.html';
            }else if( document.referrer.indexOf('aggregationPage') > 0 && $('#usersession').data('ssion') ){
                window.location.href = 'innerpage.html';
            }else{
                window.history.back();
            }

        });

        // history.pushState && window.addEventListener('popstate',function(e){
        //         console.log(e.state);
        //     },'false');
        document.body.className = '';
        hideKeyboard();


        var LOGIO = function(){

            var animating = false,
                submitphaseOne = 1100,
                submitphaseTwo = 400,
                $loginout = $('.loginout'),
                $personalPageMain = $('.personalDataPage'),
                $btnlogConvert = $('.btn-logConvert'),
                $sendPhoneIndenty = $('#sendPhoneIndenty'),
                _session = $('#usersession').data('ssion');

            router();

            // 注册成功 手机号自动注入
            if( $('.reg,.forget').length <=0 && Szl.ui.gUrlp('phone') && Szl.ui.gUrlp('phone').length > 0 ){
                $('.userphone').val(Szl.ui.gUrlp('phone'));
                $('.userpassword').focus();
            }

            $('input[type="tel"],input[type="password"]').each(function(){
                $(this).on({
                    'focus':function(){ $('.f-Copyright').attr('class','f-Copyright hif');  },
                    'blur':function(){ $('.f-Copyright').attr('class','f-Copyright showft hif');  }
                });
            });


            function JuRF(){

               var $this = $(this), dom_frag = '<div class="pAd F tbc"><div class="insertarea"><div class="lineouter F">'+
                        '<input type="tel" autocomplete="off" autocapitalize="off" autocorrect="off" spellcheck="false" class="picIdentifyingcode" placeholder="图形验证码" maxlength="4" style="width:60%">'+
                            '<span class="linefocus R" style="background: #fff;height:34px;padding:4px 0 0 4px;display: inline-block;float: right;width: 30%;margin-top: 4px;overflow:hidden;border-radius: 20px;box-shadow: 1px 2px 14px rgba(236, 74, 74, 0.77);"><img class="updateimg" src="" style="display: block; width: 100%;margin:0 0 0 3.4px"></span></div></div>'+ '<div class="icon username O svg">'+
                        '<svg class="svg-icon" viewBox="0 0 20 20">'+
                            '<path d="M0,20 20,20 20,8 0,8z M10,13 10,16z M4,8 a6,8 0 0,1 12,0"></path></svg></div></div><div class="pAd F tbc delaytbc"><div class="insertarea"><div class="lineouter F">'+
                        '<input type="tel" autocomplete="off" autocapitalize="off" autocorrect="off" spellcheck="false" class="phoneIdentifyingcode" placeholder="手机验证码" maxlength="6" style="width:50%">'+
                            '<span class="linefocus R" style="height:34px; padding:0; display: inline-block; float: right; width: 44%; margin-top: 2px;border-radius:20px;"><input type="button" value="发送验证码" style="background: #fff; border-radius:20px;border: 0; color: #666; height: 34px; line-height: 34px; display: block; font-size: .388rem; box-shadow: 1px 2px 4px rgba(241, 112, 112, 0.77);" id="sendPhoneIndenty"/></span></div></div>'+
                        '<div class="icon username O svg">'+
                        '<svg class="svg-icon R" viewBox="0 0 20 20">'+
                            '<path d="M0,20 20,20 20,8 0,8z M10,13 10,16z M4,8 a6,8 0 0,1 12,0"></path></svg></div></div>';


                if( $('#sendPhoneIndenty').hasClass('sending') && $this.text() != '马上登录' ){
                    event.preventDefault();
                    event.stopPropagation();
                    Szl.ui.tooltips('请等待当前操作完毕');
                    return false;
                }else{

                    $('.userphone,.userpassword,.picIdentifyingcode,.phoneIdentifyingcode').val('');
                    if( $this.text() == '立即注册' || arguments[0] == 'x' ){
                        pushstateUrl('register');
                        if( $('.pAd.tbc').length <= 0 ){
                            $('.info-submit').before(dom_frag);
                            ajaximg();
                        }
                        $this.next().next('a.lOg').text('重置密码');
                        $this.text('马上登录');
                        $loginout.addClass('reg').find('.BTNlogin').text('注册').addClass('tbc delaytbc2 REG');
                        $('.userphone').attr('placeholder','请输入手机号码');
                        $('.userpassword').addClass('fz-chas').attr('placeholder','请输入登录密码');
                        $('#sendPhoneIndenty').attr('class','regBtnForbid');

                    }else if( $this.text() == '重置密码' || arguments[0] == 'y' ){
                        pushstateUrl('forget');
                        if( $('.pAd.tbc').length <= 0 ){
                            $('.info-submit').before(dom_frag);
                            ajaximg();
                        }
                        $this.prev().prev('a.lOg').text('立即注册');
                        $this.text('马上登录');
                        $loginout.addClass('forget').find('.BTNlogin').text('重置密码').addClass('tbc delaytbc2 FOR');
                        $('.userphone').attr('placeholder','输入已注册的手机号码');
                        $('.userpassword').addClass('fz-chas').attr('placeholder','请输入新密码');
                        $('#sendPhoneIndenty').attr('class','forBtnForbid');
                    }else if( $this.text() == '马上登录' ){
                        window.location.href = window.location.pathname + '?router=login';
                    }


                }

                $('input[type="tel"],input[type="password"]').each(function(){
                    $(this).on({
                        'focus':function(){ $('.f-Copyright').attr('class','f-Copyright hif');  },
                        'blur':function(){ $('.f-Copyright').attr('class','f-Copyright showft hif');  }
                    });
                });
                ajaximg();
            }

            $('.log').on('click','.btn-logConvert', JuRF);

            function router(){

                // if( Szl.ui.gUrlp('router') == null || location.search == '' ){
                //     window.location.href = window.location.pathname + '?router=login';
                // }else

                if( Szl.ui.gUrlp('router') == "login" ){
                    if( $('#usersession').data('ssion') ){
                        $('html').attr({'class':'userinfo',"data-area":"userinfo"});
                        $(function(){
                            $('.personalDataPage').css('top');
                            $('.personalDataPage').addClass('active');
                        });
                        pushstateUrl('userinfo');
                    }else{
                        $('html').attr('class','log');
                    }

                }else if( Szl.ui.gUrlp('router') == "userinfo" ){
                    // if( $('#usersession').data('ssion') ){
                    //     $('html').attr({'class':'userinfo',"data-area":"userinfo"});
                    // }else{
                    //     pushstateUrl('login');
                    //     $('html').attr('class','log');
                    // }
                    window.location.href = 'Innerpage.html';
                }else if( Szl.ui.gUrlp('router') == "register" ){
                    $('html').attr('class','log');
                    JuRF('x');
                    $('.btn-logConvert').eq(0).text('马上登录');
                }else if( Szl.ui.gUrlp('router') == "forget" ){
                    $('html').attr('class','log');
                    JuRF('y');
                    $('.btn-logConvert').eq(1).text('马上登录');
                }else{
                    if( $('#usersession').data('ssion') ){
                        window.location.replace('./Innerpage.html');
                    }else{
                        window.location.replace('./aggregationPage.html?router=login');
                    }
                }

            }


            function ripple(ele,e){
                var e = window.event || arguments[1];
                $('.ripple').remove();
                var eleTop = ele.offset().top,
                    eleLeft = ele.offset().left,
                    x = e.pageX - eleLeft,
                    y = e.pageY - eleTop,
                    $ripple = $('<div/>',{class:'ripple',css:{top:y,left:x}});
            }

            function SendPhoneCode(pn,vc,xcode){
                var xCd = xcode, $that = $('#sendPhoneIndenty');

                function Valphone(){
                    var str, $btn = $that.attr('class');

                    if( typeof xCd == 'number' ){
                        str = xCd;
                    }else{
                        switch( $btn ){
                            case 'forBtnForbid': str = 0; break;
                            case 'regBtnForbid': str = 1; break;
                            default:;
                        }
                    }

                    return str;
                }

                $.ajax({
                    url: ip + '/interface/user/getPhoneCode.html',
                    type: 'POST',
                    dataType: 'json',
                    data: { phone:pn,
                            validCode:vc,
                            phoneType:Valphone()
                        },
                    beforeSend:function(){},
                    success:function(D){
                        function sendCountDown(){
                            var allowt = 60, tu = setInterval(function(){
                                if( allowt == 0 ){
                                    clearInterval(tu);
                                    $that.removeAttr('disabled').removeClass('sending').val('发送验证码');
                                }else{
                                    $that.addClass('sending').val(allowt+'秒后重发');
                                    allowt--;
                                }
                            },1000);
                        }
                        $('.updateimg').trigger('click');
                        //注册

                        if( !D.success ){
                            Szl.ui.tooltips(D.msg);
                            setTimeout(function(){ $that.removeAttr('disabled'); },2000);
                            return false;
                        }else if( Valphone() == 1 && !D.success ){
                            Szl.ui.tooltips('该手机号已经被注册');
                            setTimeout(function(){ $that.removeAttr('disabled'); },2000);
                            return false;
                        }else if(  Valphone() == 1 && D.success  ){
                            sendCountDown();
                            Szl.ui.tooltips('手机验证码已发送到您的手机('+ pn +')');
                            return false;
                        }else if(  Valphone() == 0 && !D.success ){
                            Szl.ui.tooltips('该手机号并没有被注册');
                            setTimeout(function(){ $that.removeAttr('disabled'); },2000);
                            return false;
                        }else if( Valphone() == 0 && D.success ){
                            sendCountDown();
                            Szl.ui.tooltips('手机验证码已发送到您的手机('+ pn +')');
                            return false;
                        }

                    },
                    error:function(xhr){}
                });
            }


            // 发送验证码
            $loginout.on('click','#sendPhoneIndenty',function(){

                var $that = $(this),
                    phonenumber = $.trim($('.userphone').val()),
                    picIdentifyingcode = $.trim($('.picIdentifyingcode').val());

                    $that.attr('disabled','disabled');
                if( phonenumber == '' ){
                    Szl.ui.tooltips('请输入手机号码');
                    setTimeout(function(){ $that.removeAttr('disabled'); },2000);
                    return false;
                }else if( picIdentifyingcode == '' ){
                    Szl.ui.tooltips('请输入图形验证码');
                    setTimeout(function(){ $that.removeAttr('disabled'); },2000);
                    return false;
                }else{
                    SendPhoneCode(phonenumber,picIdentifyingcode);
                }
            });

            $(document).keydown(function(event){
                if( event.which == 13 && $('.BTNlogin').css('display') == 'block' ){
                    $('.BTNlogin').trigger('click');
                };
            });

            // 登录等按钮触发
            $loginout.on('click','.BTNlogin',function(e){
                if(animating) return;
                var that = this,
                    phoneNumber = $.trim($('.userphone').val()),
                    password = $.trim($('.userpassword').val()),
                    piccode = $('.picIdentifyingcode'),
                    phonecode = $('.phoneIdentifyingcode');

                animating = true;
                //ripple($(that),e);


                $(that).addClass('processing');
                if( phoneNumber == ''){
                    Szl.ui.tooltips('请输入手机号码');
                    setTimeout(function(){ $(that).removeClass('processing'); animating = false; },2000);
                    return false;
                }else if( password == ''){
                    Szl.ui.tooltips('请输入登录密码');
                    setTimeout(function(){ $(that).removeClass('processing'); animating = false; },2000);
                    return false;
                }else if( !/^1[0-9]{10}$/.test(phoneNumber) ){
                    Szl.ui.tooltips('请输入数字1开头的11位手机号码');
                    setTimeout(function(){ $(that).removeClass('processing');animating = false; },2000);
                    return false;
                }else if( !$(that).hasClass('REG') && !$(that).hasClass('FOR') ){

                    $.ajax({
                        url: ip + '/interface/user/doLogin.html',
                        type: 'POST',
                        dataType: 'json',
                        data: {username: phoneNumber,password:password},
                        beforeSend:function(){},
                        success:function(D){
                            if( !D.success ){
                                Szl.ui.tooltips(D.msg);
                                setTimeout(function(){$(that).removeClass('processing'); animating = false; },2000);
                                return false;
                            }else{

                                setTimeout(function(){
                                  $('.f-Copyright').hide();
                                  $(that).addClass("success");
                                      setTimeout(function(){
                                        $personalPageMain.show();
                                        $personalPageMain.css("top");
                                        $personalPageMain.addClass('active');

                                        animating = false;
                                        if( !Szl.ui.isEmpty(Szl.ui.gUrlp('bidDetails_backurl')) && Szl.ui.gUrlp('bidDetails_backurl').length > 0 ){
                                            window.location.href = Szl.ui.gUrlp('bidDetails_backurl');
                                        }else{
                                            pushstateUrl('userinfo');
                                        }
                                        //$('html').attr({'class':'userinfo',"data-area":"userinfo"});
                                      },submitphaseTwo - 50);

                                      setTimeout(function(){
                                        $loginout.hide().addClass('inactive');
                                        $(that).removeClass("success processing");
                                      },submitphaseTwo);

                                },submitphaseOne);


                            }
                        },
                        error:function(xhr){}
                    });

                }else{
                        if( piccode.length > 0 && $.trim(piccode.val()) == '' ){
                            Szl.ui.tooltips('请输入图形验证码');
                            setTimeout(function(){ $(that).removeClass('processing'); animating = false; },2000);
                            return false;
                        }else if( phonecode.length > 0 && $.trim(phonecode.val()) == '' ){
                            Szl.ui.tooltips('请输入手机验证码');
                            setTimeout(function(){ $(that).removeClass('processing');animating = false; },2000);
                            return false;
                        }else if( $(that).hasClass('REG') && !/^[a-zA-Z]\w{5,17}$/.test(password)  || $(that).hasClass('FOR') && !/^[a-zA-Z]\w{5,17}$/.test(password)){
                            Szl.ui.tooltips('密码为字母开头,6到18位数字和字母的组合');
                            setTimeout(function(){ $(that).removeClass('processing'); animating = false; },2000);
                            return false;
                        }else{

                            function ajaxEar(url,w){
                                $.ajax({
                                    url: ip + url,
                                    type: 'POST',
                                    dataType: 'json',
                                    data: {phone:phoneNumber, password:password,repeatPassword:password, validCode:piccode.val(), phoneCode:phonecode.val()},
                                    beforeSend:function(){},
                                    success:function(D){

                                        if( !D.success ){
                                            Szl.ui.tooltips(D.msg);
                                            setTimeout(function(){ $(that).removeClass('processing'); animating = false; },2000);
                                            return false;
                                        }else{
                                            setTimeout(function(){
                                                $(that).text(' ').removeClass('processing');
                                                var xtime = 5,
                                                    xtarget = setInterval(function(){
                                                        if( xtime == 0 ){
                                                            animating = false;
                                                            window.clearInterval(xtarget);
                                                            window.location.href = './aggregationPage.html?router=login&phone=' + phoneNumber;
                                                        }else{
                                                            $(that).text(w + '成功,'+ xtime +'秒后跳转').css({"font-size":".44rem","letter-spacing":0,"text-indent":0});
                                                            xtime --;
                                                        }
                                                    },1000);
                                            },2000);
                                            return false;
                                        }
                                    }
                                });

                            }

                            if( $(that).text() == '注册'  ){
                                ajaxEar('/interface/user/doRegister.html','注册');
                            }else if( $(that).text() == '重置密码' ){
                                ajaxEar('/interface/user/getpwdNew.html','重置');
                            }



                        }

                    }


               // 登录入册等页面按钮结束
            });

            //return { validImgRandom : ajaximg(), SPC:function(a,b,c){ SendPhoneCode(a,b,c); } }

            // log end
        };

        var INdEX = function(){

            var SlidElem = document.querySelector('.carousel'),
                _session = $('#usersession').data('ssion'),
                flY = new Flickity(SlidElem,{
                    accessibility:false,
                    autoPlay: 2500,
                    cellAlign: 'center',
                    contain: true,
                    draggable: true,
                    prevNextButtons: false,
                    pageDots: true

                });

            $('.clo_img_banad').on('click',function(){
                $('._ad_banner').hide();
                $('body').css('overflow-y','auto');
            });


            /*    首页 banners 目前隐藏 */
                setTimeout(function(){
                    var x = $('.carousel-cell img').css('height');
                    $('.index-banners').css('height',x);
                    $('#loading_money').hide();
                    // xpt();  之前的首页文字轮播
                    // if( Szl.ui.CK.readcookie('ib') == null ){
                    //     Szl.ui.CK.setcookie('ib',1,1000*60*60*24);
                    //     $('._ad_banner').addClass('show0ani');
                    //     $('body').css('overflow-y','hidden');
                    // }
                },2600);

            setTimeout(function(){ if( !$('.svgami').hasClass('show') ) $('.svgami').addClass('show');  },3000);
            function xpt(){
                var $welcomeWords = $('#uipintro');
                 var xword = ['金和所强监管下再升级<br>账户安全保障金突破6000万元','金和所荣获2016年度<br>供应链金融创新合规奖','金和所获得<br>京朋集团一亿元投资控股'], _wx = 0;
                var st = setInterval(function(){
                    if( _wx > xword.length - 1 ){
                        _wx = 0;
                        window.clearInterval(st);
                        xpt();
                    }else{
                        $welcomeWords.html(xword[_wx]);
                        _wx++;
                    }
                },3000);

            }

            /**  首页右上角导航
                $('#circleLines').on('click',function(){
                    var orMas = $('.orimasNav ul li a');

                    if( $(this).hasClass('show') ){
                        orMas.removeClass('in');
                        $(this).removeClass('show');
                        $('html,body').css({"overflow-x":'hidden','overflow-y':'auto'});
                    }else{
                        $(this).addClass('show');
                        $('html,body').css('overflow','hidden');
                        setTimeout(function(){
                            orMas.addClass('in');
                        },605);

                    }


                });
            */

            $('.carousel-cell').on('click','.transForm',function(){

                var that = $(this), ep_ele = that.find('.Epcharts')[0];
                that.hasClass('disable')?true:that.toggleClass('flipped');

                if( that.hasClass('flipped') ){
                    var chart = window.chart = new EasyPieChart(ep_ele, {
                        easing: 'easeOutElastic',
                        delay: 3000,
                        barColor: '#d65c6b',
                        trackColor: 'rgba(255,255,255,.5)',
                        scaleColor: '#c7a480',
                        lineWidth: 2,
                        trackWidth: 3,
                        size:64,
                        lineCap: 'circle',
                        onStep: function(from, to, percent) {
                            this.el.children[0].innerHTML = Math.round(percent);
                        }
                    });
                }else{
                    setTimeout(function(){ that.find('canvas').remove();},300);
                }
            });

            $('.btnLjtb').on('click',function(){ event.stopPropagation(); });
            $('.transForm.disable').on('click',function(){ event.stopPropagation(); window.location.href = ''; });

            $('#loadmorelists').on('click',function(){
                event.stopPropagation();
                event.preventDefault();
                window.location.href='listbids.html';
            });


            if( _session ){ $('#LoGIN').hide(); }else{ $('#MyACCOUNT').hide(); }


        // 首页结束
        }

        var ListBids = function () {

            var echarts = $('.Epcharts'), spanc = false;

             $('.wDih').each(function(){
             var widt =  $(this).attr('data-wid');
             $(this).css('width',widt+'%');
             });

            $('.seledi').on('click', 'span', function () {
                event.stopPropagation();

                $(this).addClass('index').siblings('span').removeClass('index');
                $('#conDiNode').remove();
                var that = $(this),
                    //_i = that.find('i'),
                    _status = $('.seledi').eq(0).find('span.index').attr('data-v'),
                    _date = $('.seledi').eq(1).find('span.index').attr('data-v');

                if (!spanc) {
                    $('html, body').scrollTop(0);
                    $('.cards-bids').remove();
                    $('.loadDataCircles').show();
                    $('#DoTS').hide();
                    aia('/interface/myAccount/borrowList.html?page=1', {
                        status: _status,
                        timeLimit: _date
                    }, function () {
                        //that.removeClass('focus');
                        //_i.text('筛选');
                        spanc = true;
                        $('#DoTS').show();
                    }, function (data) {

                        var xa = new Array(), D = data.obj.borrowList;

                        if (D.length > 0) {
                            $('.conDiNode').remove();
                            for (var i = 0; i < D.length; i++) {
                                var fort = Date.parse(new Date(D[i].borrowAddTime.replace(' ', 'T'))), sta = new Date("Mon Dec 12 2016 00:00:00 GMT+0800 (CST)").getTime(), eta = new Date("Sta Apr 01 2017 00:00:00 GMT+0800 (CST)").getTime(), io_  =  D[i].borrowApr + '%', repayType = '一次性还本付息';
                                if (D[i].borrowIsDay == 1 && D[i].timeLimitDay == 30 && fort >= sta && fort < eta) {
                                    io_ = ( D[i].borrowApr- 1 ).toFixed(1) + '%<i class="xep">+1%</i>';
                                }

                                if (D[i].borrowIsDay == 0 && D[i].timeLimit == 1 && fort >= sta && fort < eta) {
                                    io_ = ( D[i].borrowApr- 1 ) + '%<i class="xep">+1%</i>';
                                }

                                if (D[i].borrowIsDay == 0 && D[i].timeLimit == 2 && fort >= sta && fort < eta) {
                                    io_ = ( D[i].borrowApr - 1 ) + '%<i class="xep">+1%</i>';
                                }

                                if (D[i].borrowIsDay == 0 && D[i].timeLimit == 3 && fort >= sta && fort < eta) {
                                    io_ = ( D[i].borrowApr - 1 ) + '%<i class="xep">+1%</i>';
                                }
                                
                                if(D[i].borrowIsDay == 1) {
                                	repayType = "到期全额还款";
                                } 
                                
                                xa.push('<article class="cards-bids"><a href="' + D[i].borrowLink + '" class="d-b">' +
                                    '<div class="titleinfo F">' +
                                    '<span>' + D[i].borrowName + '</span><span class="tittype">' + repayType + '</span>' +
                                    '</div><div class="F info-Cards"><div class="inFO"><ul class="F">' +
                                    '<li>'+ io_ +'<em>预期年化收益率</em></li><li class="inside">' +
                                    D[i].borrowTimeLimit + '<em>项目期限</em>' +
                                    '</li><li class="linePercent"><div class="lineProject R"><span class="R"><i class="wDih" style="width:' + D[i].borrowSchedule + '%"></i></span><div><em>项目进度'+D[i].borrowSchedule.toFixed(2)+'%</em></li></ul></div></div></a></article>');

                            }
                            //$('.listBids').prepend(xa.join(''));

                            $('.conditionsBid').after(xa.join(''));
                            $('.loadDataCircles').hide();

                            $('#DoTS').hide();
                        } else {
                            $('#DoTS').hide();
                            $('.loadDataCircles').hide();
                            $('.conDiNode').remove();
                            $('.listBids').append('<div class="conDiNode">当前暂无数据,请修改检索条件 :( </div>');
                        }

                        spanc = false;

                    });
                } else {
                    //that.addClass('focus');
                    //_i.text('确认修改');
                    alert('您的操作太频繁！');
                    return false;
                }
            });


            setTimeout(function () {
                $('.loadDataCircles').hide();
                $('.f-Copyright,.listBids').css('opacity', 1);
            }, 1000);


            // 投标列表详情页结束
        };

        var BidDetails = function(){

            setTimeout(function(){
                $('.loadDataCircles').hide();
                $('.biddetails .listBids.details,.biddetails .f-Copyright.ftb').css('opacity',1);
                var chart = window.chart = new EasyPieChart(document.querySelector('.Epcharts'),{
                    easing: 'easeOutElastic',
                    delay: 3000,
                    barColor: '#f39e03',
                    trackColor: 'rgba(245,245,245,.9)',
                    scaleColor: 'orange',
                    lineWidth: 2,
                    trackWidth: 4,
                    size:64,
                    lineCap: 'circle',
                    onStep: function(from, to, percent) {
                        this.el.children[0].innerHTML = Math.round(percent);
                    }
                });
            }, 1000);

            var $anbz = $('.aqbz'), $makemONey = $('#makemONey'), cabackurl = $('#callbackurl_biddetails');

            cabackurl.attr('href',cabackurl.attr('href') + '&bidDetails_backurl='+ location.href );

            $anbz.on('click','.card.f',function(){
                if( $(this).hasClass('show') ){
                    $(this).removeClass('show');
                }else{
                    $(this).addClass('show');
                }
            });

            $('.nliav').each(function(){
                var ta = $(this), name = ta.data('name');
                ta.click(function(){
                    $(this).addClass('focus').siblings('li').removeClass('focus');
                  $('.' + name).show().siblings('div').hide();
                });
            });

            $('#monYk').on({'focus':function(){
                $('.info-Cards,.fixheader,.OneinThree').hide();
                $('.listBids').css('padding-top',0);
                setTimeout( function(){ $('html,body').scrollTop(0);},50);
            }, 'blur':function(){
                $('.listBids').css('padding-top','50px');
                $('.info-Cards,.fixheader,.OneinThree').show();
                if( !Szl.ui.isEmpty($('#monYk').val()) ) $('#makemONey').trigger('click');
            } });
            var lowM = $('#lowMoney'), inM = $('#inMoney'), NxM = $('#maxMoney');

            _getFd(lowM);
            _getFd(inM);
            _getFd(NxM);

            if( $('#pass_dxb').length < 1 ){
                $('#n_pwdf').remove();
            }

            $('#monYk').parent().append('<div class="tyb_infos d-n">'+ $('#monYk').data('rq') +'</div>');


            if( $('#_mark_0089').text() == 0 || $('#monYk').attr('disabled') == 'disabled' ){
                $('.tyb_infos').hide();
            }

            $('#monYk').keyup(function(){
                var va = 1*$(this).val();
                if( va > 0 && 1*$('#_mark_0089').text() > 0 && va > 1*$('#_mark_0089').text() ){
                    var i9x = va - $('#_mark_0089').text();
                    $('.tyb_infos i').text(i9x);
                    $('.tyb_infos').show();
                }else{
                    $('.tyb_infos').hide();
                }
            });

            $('#makemONey').on('click',function(e){
                var e = e || event;
                e.stopPropagation();
                e.preventDefault();
                var money = $.trim($('#monYk').val()),
                    pass_dxb = $('#pass_dxb').val(),
                    isdisabled = $('#monYk').attr('disabled');


                    $('.info-Cards,.fixheader,.OneinThree').show();
                    hideKeyboard();

                    if( isdisabled == 'disabled' ){
                        if( money == 0 ){
                            alert('抱歉，您当前没有资格享受体验标');
                            window.location.href= '/wap/listbids.html';
                            return false;
                        }else{
                            $.ajax({
                                url: ip + '/borrow/tender.html',
                                type: 'POST',
                                dataType:'html',
                                data: { money:money, id:$('#makemONey').data('id') },
                                beforeSend:function(){},
                                success:function(dada){
                                    if( !Szl.ui.isEmpty( JSON.parse(dada).obj.returnURL ) ){
                                        window.location.replace(JSON.parse(dada).obj.returnURL);
                                    }else if( !Szl.ui.isEmpty( $(dada).find('#error_zl') )){
                                        if( $(dada).find('#error_zl').text().indexOf('授权再进行') > 0 ){
                                            if( confirm('现在将立即跳入授权页面，请确认？') ){
                                                window.location.href = '/member/loanAuthorize.html?borrowId=' + $('#makemONey').data('id');
                                            }else{
                                                window.location.href = '/member/loanAuthorize.html?borrowId=' + $('#makemONey').data('id');
                                            }


                                        }else{
                                            Szl.ui.tooltips( $(dada).find('#error_zl').text() );
                                            return false;
                                        }

                                    }

                                }
                            });
                        }

                    }else if( money.length < 1 ){
                        Szl.ui.tooltips('请输入投标金额(数字)');
                        return false;
                    }else if( isNaN(money) ){
                        Szl.ui.tooltips('金额请输入数字');
                        return false;
                    }else if( $('#pass_dxb').length > 0 && pass_dxb.length < 1 ){
                        Szl.ui.tooltips('请输入定向标密码');
                        return false;
                    }else if( $('#maxMoney').data('text') > money ){
                        Szl.ui.tooltips('投标金额必须大于最低出借金额');
                        return false;
                    }else if( $('#lowMoney').data('text') < money ){
                        Szl.ui.tooltips('投标金额必须小于剩余出借金额');
                        return false;
                    }else if( $('#inMoney').data('text') < ( money - $('#_mark_0089').text()) ){
                        Szl.ui.tooltips('账户余额不足以投标，请充值');
                        return false;
                    }else if( typeof money.toString().split('.')[1] != 'undefined' && money.toString().split('.')[1].length > 2 ){
                        Szl.ui.tooltips('金额只允许两位小数');
                        return false;
                    }else if( confirm("您将出借"+money+"元，请确认？") ){
                        if( $('#pass_dxb').length > 0 && pass_dxb.length > 0 ){
                            $('#n_pwdf').val(pass_dxb);
                        }
                        //$('#form123').submit();
                        var da_ = {};
                        if( $('#pass_dxb').length < 1 ){
                            da_ = { money:money, id:$('#makemONey').data('id') };
                        }else{
                            da_ = { money:money, id:$('#makemONey').data('id'), pwd:pass_dxb };
                        }


                        $.ajax({
                            url: ip + '/borrow/tender.html',
                            type: 'POST',
                            dataType:'html',
                            data: da_,
                            beforeSend:function(){},
                            success:function(dada){
                                if( !Szl.ui.isEmpty( $(dada).find('#error_zl') )){
                                    if( $(dada).find('#error_zl').text().indexOf('授权再进行') > 0 ){
                                        if( confirm('现在将立即跳入授权页面，请确认？') ){
                                            window.location.href = '/member/loanAuthorize.html?borrowId=' + $('#makemONey').data('id');
                                        }else{
                                            window.location.href = '/member/loanAuthorize.html?borrowId=' + $('#makemONey').data('id');
                                        }


                                    }else{
                                        Szl.ui.tooltips( $(dada).find('#error_zl').text() );
                                        return false;
                                    }

                                }else{
                                    $('body').append($(dada));
                                }

                            }
                        });
                    }

            });

            var ajax_loadrecordbids = $('#ajax_loadrecordbids'), borrowid = ajax_loadrecordbids.attr('name');
            aia('/interface/myAccount/wapAppDetailTenderForJson.html?borrowid='+borrowid,{},function(){},function(wd){
                var dara = wd.obj.detailTender, len = dara.length;

                if( len > 0 ){
                    for(var i = 0; i < len; i++){
                       // 万 ajax_loadrecordbids.append('<tr><td>'+ dara[i].userName.slice(-6) +'</td><td>'+ dara[i].borrowApr +'%</td><td>'+ ( dara[i].tenderMoney < 10000 ? (dara[i].tenderMoney + '元'):(dara[i].tenderMoney/10000 + '万元') )+'</td><td>'+ dara[i].tenderDate +'</td></tr>');
                       ajax_loadrecordbids.append('<tr><td>'+ dara[i].userName.slice(-6) +'</td><td>'+ dara[i].borrowApr +'%</td><td>'+ dara[i].tenderMoney +'</td><td>'+ dara[i].tenderDate +'</td></tr>');
                    };
                }else{
                    ajax_loadrecordbids.empty().text('暂无数据');
                }

            });

            if( $('#usersession').data('ssion') ){
                $('.m_U.logn').show();
                $('.m_U.logN').hide();
                $('.logm_U.qrtb.d-n').show();
            }else{
                $('.m_U.d-n.logN').show();
                $('.m_U.logn').hide();
                $('.logm_U.qrtb.d-n').hide();
            }

            $('#log_biddetais_determine').on('click',function(){
                if( $('#usersession').data('ssion') ){
                    window.location.href = 'Innerpage.html';
                }else{
                    window.location.href = 'aggregationPage.html?router=login';
                }
            });

            if( $('.wDih').data('wid') == 100 ){
                $('.qUote').find('.logm_U.qrtb').hide();
            }


            $('.xmxq').find('table').each(function(){
                var $t_table = $(this);
                    $(this).after($t_table.find('img'));
                    $(this).remove();
            });

            // 投标详情页结束
        };

        var USERINFO = function(){
             //LOGIO().validImgRandom;  //图像验证码
             //LOGIO().SPC(null,null,2); 1注册0重置密码  【 发送手机验证码 】

            setTimeout(function(){
                $('.loadDataCircles').hide();
                $('.f-Copyright,.listBids').css('opacity',1);

                if( $('body').data('exp') && location.search == '' ){
                    $('body').append('<div class="tempmask" ><a href="javascript:void(0);" class="oldtea O" id="closeTMsk"></a><img src="/themes/soonmes_yjt/media/wap/img/userpbg.png"><a href="javascript:void(0);" id="_uwctar">查看我的体验金</a></div>');
                }

            }, 1000);

            var $header_nav = $('#NAVPersonalPage'),
                _infOPage = $('.infOPage'),
                loadING = $('.loadDataCircles'),
                Back_ = $('.inerbacklistener'),
                _url = Szl.ui.gUrlp('router'),
                ArrFun = ["iNdexpage","rEcharge","cAshOut","rEcodequery","fBmanager","mYawards","oPtions","noTice"],
                $pagetitle = $('#pagetitle'),
                $rowsradio = $('.rows-info.radio'),
                $bankcard_edit_nav = $('#bankcard_edit_nav'),
                $cardlists = $('.bindcardlists'),
                $confirm_aUthreal = $('#confirm_aUthreal'),
                $safeC_edit_nav = $('#safeC_edit_nav'),
                $borrowmoney_edit_nav = $('#borrowmoney_edit_nav'),
                $invest_edit_nav = $('#invest_edit_nav')
                $_visi_balance = $('.visi_balance');

                _getFd($_visi_balance);
                $(window).resize(function () {
                    // document.activeElement.scrollIntoView(true);

                    document.querySelector("#pre_paid,#sele_cashout_byq").scrollIntoView(false);
                    document.querySelector("#sele_cashout_byq").scrollIntoView(false);
                });
                $('input[type="tel"],input[type="text"]').each(function(){
                    $(this).on({
                        'focus':function(){ $('.nav-GA').attr('class','nav-GA hif');  },
                        'blur':function(){ $('.nav-GA').attr('class','nav-GA');  }
                    });
                });

                if( $('#noTice').parent().attr('data-messages') == 0 ){
                    $('.status-top-right .message').hide();
                    $('#noTice').parent().addClass('disappear_after');
                }

                $('.userinfo').on('click','#closeTMsk',function(){
                    event.stopPropagation();
                    event.preventDefault();
                    $('.tempmask').remove();
                    $.get('/member/receiveExperienceMoney.html', function(data){});
                });

                $('.userinfo').on('click','#_uwctar',function(){
                    // 个人主页 体验标
                    $.get('/member/receiveExperienceMoney.html', function(data){
                        window.location.href = $('body').attr('data-urls');
                        return false;
                    });

                });

                $('.userinfo').on('click','#syxz',function(){
                    $('.masknav').addClass('showrules');
                    $('#NAVPersonalPage').hide();

                });


                $('.userinfo').on('click','.cloIxq',function(){
                    $('.masknav').removeClass('showrules');
                    $('#NAVPersonalPage').show();

                });

                if( $('.Ioxqe').data('use') ){
                    $('#Gexp').text('已');
                    $('.tyjlx').show();
                    $('#ij2').hide();

                }else{
                    $('#ij2').show();
                    $('#Gexp').text('未');
                    $('.tyjlx').hide();
                }

                $('.userinfo').on('click','#hurrY',function(){
                    event.stopPropagation();
                    event.preventDefault();
                    var url = $(this).attr('data-href');
                    if( !$(this).hasClass('disabled') ){
                        if( $('#Gexp').text() == '已' ){
                            window.location.href= 'listbids.html';
                            return false;
                        }else if( $('#Gexp').text() == '未' ){
                            window.location.href= url;
                            return false;
                        }
                    }

                });


                function c_o(t,isnotback){

                    if( t != 'iNdexpage' ){
                        _infOPage.find('.d-n').hide();
                        $('body').removeClass('nav-is-shown');
                        $header_nav.removeClass('show');
                        t != 'iNdexpage'?Back_.show():Back_.hide();
                        $pagetitle.text( _infOPage.find('._0' + t).attr('name') );

                        if( Szl.ui.gUrlp('subRouter') != null && isnotback ){
                            _infOPage.find('._0' + t).show().find('.subOne').attr('class','subOne pop-out-T moment-pop-in-leave').next('.subInfos').attr('class','subInfos pop-in-T d-n moment-pop-in-enter').show().find('[name='+ Szl.ui.gUrlp('subRouter') +']').show().addClass('show').siblings('div').removeClass('show').hide();
                            ajai();
                                //pushstateUrl(t+'&subRouter='+);
                        }else{
                            loadING.show();
                            setTimeout(function(){
                                loadING.hide();
                                _infOPage.find('._0' + t).show();
                                pushstateUrl(t);
                            },800);

                        }
                        $('._0'+ t).find('.updateimg').trigger('click');
                        if( t == 'noTice' ){
                            // ajax for system-messages
                            var messContainer = $('#mess_AGES');
                            aia('/interface/myAccount/wapAppMsg.html',{},function(){},function(data){
                                var getD = data.obj.magList, UNr = new Array(), r = new Array();
                                if( getD.length > 0 ){
                                    for( var i = 0; i < getD.length; i++ ){
                                        if( getD[i].isRead == 1 ){
                                            // 已读
                                            r.push('<div class="listdoMESS"><p class="O time">'+ getD[i].addTime +'</p><p class="O content">'+getD[i].content  +'</p></div>');
                                        }else{
                                            // 未读
                                            UNr.push('<div class="listdoMESS"><p class="O time">'+ getD[i].addTime +'</p><p class="O content">'+ getD[i].content +'</p></div>');
                                        }
                                    }

                                    if( r.length > 0  ){
                                        messContainer.append('<h2 class="line-messages R styleTitle"><span class="messinfo">已读消息</span></h2>'+ r.join(''));
                                    }

                                    if( UNr.length > 0 ){
                                        messContainer.prepend('<div class="unreadMessages"><h2 class="line-messages R styleTitle"><span class="messinfo unread">未读消息</span></h2>'+ UNr.join('') +'</div>');
                                    }


                                }else{
                                    messContainer.append('<h2 class="line-messages R styleTitle"><span class="messinfo">暂无消息</span></h2>');
                                }



                            });
                        }


                    }else{
                        return false;
                    }


                }

                function lFoor(ele){

                    $.each(ele,function(index,t){
                       if( t == _url ){
                            if( Szl.ui.gUrlp('subRouter') == null ){
                                c_o(t,false);
                                return;
                            }else{
                                c_o(t,true);
                                return;
                            }
                        }

                    });

                    $('.onCL').on('click',function(){

                        $('.subOne').attr('class','subOne pop-out-T');
                        $('.subInfos').attr('class','subInfos pop-in-T d-n');

                        $(this).parent().addClass('active').siblings('li').removeClass('active');
                        if( $(this).attr('id') == Szl.ui.gUrlp('router') && Szl.ui.gUrlp('subRouter') == null ){
                            $('#NAVPersonalPage').removeClass('show');
                            $('body').removeClass('nav-is-shown');
                        }else if( $(this).attr('id') == Szl.ui.gUrlp('router') && Szl.ui.gUrlp('subRouter') != null ){
                            $('#NAVPersonalPage').removeClass('show');
                            $('body').removeClass('nav-is-shown');
                            $('.inerbacklistener').trigger('click');
                        }else{
                            c_o($(this).attr('id'),false);
                        }

                        $bankcard_edit_nav.find('a').eq(0).trigger('click');
                        $invest_edit_nav.find('a').eq(0).trigger('click');
                        $borrowmoney_edit_nav.find('a').eq(0).trigger('click');
                        $safeC_edit_nav.find('a').eq(0).trigger('click');

                    });

                    if( Szl.ui.gUrlp('router') != null ){
                        $('#'+Szl.ui.gUrlp('router')).parent().addClass('active').siblings('li').removeClass('active');
                    }


                }

                Back_.hide();
                lFoor(ArrFun);

                setTimeout(function(){
                    if( !Szl.ui.isEmpty($('body').data('messages')) ){
                         $.post('/public/getResult.html', {tenderFlag:$('body').data('messages') }, function(rr) {
                                if( !Szl.ui.isEmpty(rr.msg_data) ){
                                    Szl.ui.tooltips(rr.msg_data);
                                }
                                $('body').attr('data-messages','');
                                return false;
                            });
                     }
                },1300);


                if( !$('#confirm_aUthreal').data('status') ){
                    pushstateUrl('oPtions&subRouter=realnameauth');
                    c_o('oPtions',true);
                    $('.goback,#NAVPersonalPage').hide();
                    $('#pagetitle').text('实名认证');
                    $('#NAVPersonalPage').attr('href','logout.html').html('退出').css({"font-size":".36rem","width":"inherit"}).show();
                    $('.goback').attr({"name":"auth",'href':'/wap/index.html'}).html('返回').show();
                }

                $header_nav.on('click',function(){
                    $('body').removeClass('unbind');
                    $(this).toggleClass('show');
                    $('body').toggleClass('nav-is-shown');
                });

                $('#ack_nav_go').click(function(){
                    $('body').removeClass('unbind');
                    $('body').removeClass('nav-is-shown');
                    $header_nav.removeClass('show');
                });

                Back_.on('click',function(){

                   if( $(this).attr('name') == 'auth' ){ return; }
                   var uRL = Szl.ui.gUrlp('subRouter');
                   $('#NAVPersonalPage').removeClass('show');
                   $('body').removeClass('nav-is-shown unbind');

                   if( uRL == null && Szl.ui.gUrlp('router').length > 0 ){
                        window.location.replace('Innerpage.html');
                   }else if( uRL.length > 0 ){
                        // $.each(ArrFun,function(index,t){
                        //    if( t == _url ){c_o(t,true); return false; }
                        // });
                        var zge = $('.subInfos > .layrows-child.show'), _id = zge.index();
                        $('.subInfos').attr('class','subInfos pop-in-T d-n moment-pop-out-leave').prev('.subOne').show().attr('class','subOne pop-out-T moment-pop-out-enter');
                        $('.layrows-child').hide();
                        pushstateUrl(Szl.ui.gUrlp('router'));
                   }

                    $bankcard_edit_nav.find('a').eq(0).trigger('click');
                    $invest_edit_nav.find('a').eq(0).trigger('click');
                    $borrowmoney_edit_nav.find('a').eq(0).trigger('click');
                    $safeC_edit_nav.find('a').eq(0).trigger('click');
                });


                $rowsradio.on('click','label',function(){
                    var that = $(this), txt = that.find('.radiodiv > p').text(), nid = that.find('.radiosel').val();
                    that.addClass('check').siblings('label').removeClass('check').parent('.radio').attr('data-d','{"text":"'+ txt +'","nid":"'+ nid +'"}');
                });

                _infOPage.on('click','.lay-rows',function(){
                    var Zge = $(this), id = Zge.index(), $fath = Zge.parent('.subOne'), parent_Url = $fath.parent(), sib_ = parent_Url.siblings('section'), subname = parent_Url.find('.layrows-child').eq(id).attr('name');
                    $fath.attr('class','subOne pop-out-T moment-pop-in-leave').next('.subInfos').show().attr('class','subInfos pop-in-T d-n moment-pop-in-enter').find('.layrows-child').eq(id).show().addClass('show').siblings('div').removeClass('show').hide();

                    sib_.find('.subOne').attr('class','subOne pop-out-T');
                    sib_.find('.subInfos').attr('class','subInfos pop-in-T d-n');

                    if( parent_Url.hasClass('_0oPtions') ){
                        pushstateUrl('oPtions&subRouter='+subname);
                    }else if( parent_Url.hasClass('_0rEcodequery') ){
                        pushstateUrl('rEcodequery&subRouter='+subname);
                    }else if( parent_Url.hasClass('_0fBmanager') ){
                        pushstateUrl('fBmanager&subRouter='+subname);
                    }

                    $('.layrows-child.show').parents('section').css('height',$('.layrows-child.show').height() );

                    ajai();
                });

                function tab_select(master){
                    master.find('a').each(function(){
                        var ind = $(this).index();
                        $(this).on('click',function(){
                            $(this).addClass('show').siblings('a').removeClass('show');
                            master.next().find('.bankfun').eq(ind).show().siblings().hide();
                            master.parents('section.d-n').css('height', master.next().find('.bankfun').eq(ind).height() );
                            $('.layrows-child.show').parents('section').css('height',$('.layrows-child.show').height() );
                        });
                    });

                }

                tab_select($bankcard_edit_nav);
                tab_select($safeC_edit_nav);
                tab_select($borrowmoney_edit_nav);
                tab_select($invest_edit_nav);


                function aja_mybankcard(){
                    $.post('/interface/myAccount/bankcancel.html', {}, function(U) {
                        var qt = JSON.parse(U), xd = qt.obj.accountBanks, atr = new Array();
                        window.se = qt;
                        if( xd.length > 0 ){
                            for( var i = 0; i < xd.length; i++ ){
                                atr.push("<div class='bindcardlists R' data-str='"+ JSON.stringify(xd[i]) +"'></div>");
                            }
                            $('.bindcardlists').remove();
                            $('.nocardnotice').after(atr.join(''));
                            $('.nocardnotice').css('display','none !important');
                            $('#numcaRds').html('我的银行卡('+ xd.length +')');

                            $('.bindcardlists').each(function(index,s){

                                var that = $(this), data = that.data('str');
                                window.ser = data;
                                that.html('<span>'+ data.bankname.slice(0,-3) +'<i>借记卡</i></span><mark>****&nbsp;****&nbsp;****&nbsp;'+ data.cardNumber.slice(-4) +'</mark><button class="unbindbutton O cardINFO"></button><button class="unbindbutton O uNbind"></button><div class="bindfocus T"><div class="F bf-Xg"><input type="tel" placeholder="请输入图形验证码" name="coo'+ index +'" maxlength="4"/><img src="" class="unbind_vcodeimg updateimg"/></div><div class="F bf-Xg"><input type="tel" placeholder="请输入手机验证码" maxlength="6"/><input type="button" value="获取验证码" class="unbind_vcodephone getphoneCd" name="coo'+ index +'"/></div><a href="javascript:void(0);" class="confirm_unbind">确认解绑</a></div>');

                                $(this).on('click','.cardINFO',function(){ event.stopPropagation();
                                    $('body').addClass('unbind');
                                    $('.innermaskbind .innerTxt').html('<p class="unbindconditions"><strong>'+ data.bankname +'</strong></p><p class="unbindconditions"><strong>银行卡号：</strong>'+ data.cardNumber +'</p><p class="unbindconditions"><strong>开户行所在地：</strong>'+ data.addressCreate +'</p><p class="unbindconditions"><strong>银行卡绑定时间：</strong>'+ data.timeCreate +'</p><p class="unbindconditions"><strong>网站绑定手机号：</strong>'+ data.phonenumber +'</p>');

                                });

                                $(this).on('click','.uNbind',function(){
                                    event.stopPropagation();
                                    $(this).parent('.bindcardlists').find('.updateimg').trigger('click');
                                    $(this).toggleClass('close').next().toggleClass('show');
                                });

                                $('._0oPtions').css('height',$('.layrows-child[name="bankcard"]').height() );
                            });

                        }else{
                            $('.nocardnotice').css('display','block !important');
                            $('.bindcardlists').remove();
                            $('#numcaRds').html('我的银行卡');
                        }
                    });
                }

                aja_mybankcard();

                $('#close_unbindcard').on('click',function(){ $('body').removeClass('unbind'); });

                $('.userinfo').on('click','.confirm_unbind',function(){
                    var ths = $(this), P = ths.parent('.bindfocus'), picode = P.find('[placeholder="请输入图形验证码"]').val(), phonecode = P.find('[placeholder="请输入手机验证码"]').val();

                    if( Szl.ui.isEmpty(picode) ){
                        Szl.ui.tooltips('请输入图片验证码');
                        return false;
                    }else if( Szl.ui.isEmpty(phonecode) ){
                        Szl.ui.tooltips('请输入手机验证码');
                        return false;
                    }else{
                        $.post('/member/account/bankcancel.html', { valicode:picode,validCodeee:phonecode, codeUniqueId:'',bankid:P.parents('.bindcardlists').data('str').bankid, type:'bankcancel',isPhoneRequest:1 }, function(U){
                            if( $(U).find('#error_zl').length > 0 ){
                                    Szl.ui.tooltips( $(U).find('#error_zl').text() );
                                    aja_mybankcard();
                                    return false;
                                }else{
                                    //$('body').append($(U));
                                }
                        });
                    }

                });

                if( $confirm_aUthreal.data('status')){
                    $confirm_aUthreal.text('√ 已通过实名认证').addClass('ready');
                    $confirm_aUthreal.parent('.layout-sub').find('input').attr('disabled','disabled');
                    $confirm_aUthreal.prev('.v-x-code').hide();
                    $confirm_aUthreal.parent('.layout-sub').find('.rows-title').hide();
                    $('.sub-rows-title.auth').next().next().addClass('Ready R');
                }


                if( Szl.ui.gUrlp('subRouter') != null && Szl.ui.gUrlp('subRouter').length > 0 ){
                    $('.layrows-child.show').parents('section').css('height',$('.layrows-child.show').height() );
                }


                function ajai(){

                   var corr_url = Szl.ui.gUrlp('subRouter');
                   $('.dataTablerecord').find('td').parent('tr').remove();
                   if( !$('#usersession').attr('data-ssion') ){
                        Szl.ui.tooltips('请先登录');
                        return false;
                   }else if( corr_url == 'm_tz' ){
                        //console.log('出借管理 ||  招标中的项目，未收款明细，已收款明细');
                        var conD = 'm_tz', tabLe = $('div[name='+ conD +']').find('.dataTablerecord');

                        tabLe.eq(0).addClass('Opc');
                        loadING.show();
                        aia('/interface/myAccount/collecticonDetails.html',{},function(){},function(data){

                            if( data.success ){

                                var D = data.obj;
                                if( typeof D == 'undefined' ){
                                    window.location.replace('/aggregationPage.html?router=login');
                                    return false;
                                }else if( D instanceof Object ){
                                    tabLe.each(function(index){
                                        var ax = new Array(), xa = '';
                                        if( index == 0 ){
                                            xa = 'tendering';
                                            for(var i = 0; i < D[xa].length; i++){
                                                ax.push('<tr><td><a href="'+ D[xa][i].borrowLink +'">'+ D[xa][i].borrowName +'</a></td>'+
                                                    //万元 '<td>'+ D[xa][i].borrower +'</td><td>'+( D[xa][i].tenderMoney >= 10000?D[xa][i].tenderMoney/10000 + '万':D[xa][i].tenderMoney ) +'元</td><td>'+ D[xa][i].tenderDate +'</td><td>'+ D[xa][i].tenderStatus +'</td></tr>');
                                                    '<td>'+ D[xa][i].borrower +'</td><td>'+D[xa][i].tenderMoney +'元</td><td>'+ D[xa][i].tenderDate +'</td><td>'+ D[xa][i].tenderStatus +'</td></tr>');
                                            }

                                            if( ax.length > 0 ){
                                                $(tabLe[index]).find('tbody').append(ax.join(''));
                                            }else{
                                                $(tabLe[index]).html('<caption>暂无数据</caption>');
                                            }

                                        }else if( index == 1 ){
                                            xa ='noCollect';
                                            for(var i = 0; i < D[xa].length; i++){
                                            ax.push('<tr><td><a href="'+ D[xa][i].borrowLink +'">'+ D[xa][i].borrowName +'</a></td>'+
                                          '<td>'+ D[xa][i].collectTime +'</td><td>'+ D[xa][i].borrower +'</td>'+
                                          //万元 '<td>'+ ( D[xa][i].capital >= 10000?D[xa][i].capital/10000 + '万':D[xa][i].capital ) +'元</td><td>'+ D[xa][i].interest +'元</td></tr>');
                                          '<td>'+ D[xa][i].capital +'元</td><td>'+ D[xa][i].interest.toFixed(2) +'元</td></tr>');
                                                }
                                            if( ax.length > 0 ){
                                                $(tabLe[index]).find('tbody').append(ax.join(''));
                                            }else{
                                                $(tabLe[index]).html('<caption>暂无数据</caption>');
                                            }
                                        }else if( index == 2 ){
                                            xa ='hasCollect';
                                            for(var i = 0; i < D[xa].length; i++){
                                                //万元 ax.push('<tr><td><a href="'+ D[xa][i].borrowLink +'">'+ D[xa][i].borrowName +'</a></td><td>'+ D[xa][i].collectTime +'</td><td>'+ D[xa][i].borrower +'</td><td>'+ ( D[xa][i].capital >= 10000?D[xa][i].capital/10000 + '万':D[xa][i].capital ) +'元</td><td>'+ D[xa][i].interest +'元</td><td>'+ D[xa][i].collectStatus +'</td></tr>');
                                                ax.push('<tr><td><a href="'+ D[xa][i].borrowLink +'">'+ D[xa][i].borrowName +'</a></td><td>'+ D[xa][i].collectTime +'</td><td>'+ D[xa][i].borrower +'</td><td>'+ D[xa][i].capital +'元</td><td>'+ D[xa][i].interest.toFixed(2) +'元</td><td>'+ D[xa][i].collectStatus +'</td></tr>');
                                            }
                                            if( ax.length > 0 ){
                                                $(tabLe[index]).find('tbody').append(ax.join(''));
                                            }else{
                                                $(tabLe[index]).html('<caption>暂无数据</caption>');
                                            }
                                        }
                                    });

                                }
                                setTimeout(function(){
                                    $('.layrows-child.show').parents('section').css('height',$('.layrows-child.show').height() );
                                    tabLe.eq(0).removeClass('Opc');
                                    loadING.hide();
                                },800);
                            }else{
                                alert('请求失败');
                                return false;
                            }

                        });
                   }else if( corr_url == 'm_jk' ){
                       // console.log('收款明细  正在招标的借款，正在还款的借款');
                        var conD = 'm_jk', tabLe = $('div[name='+ conD +']').find('.dataTablerecord');
                        tabLe.eq(0).addClass('Opc');
                        loadING.show();
                        aia('/interface/myAccount/borrowingOrTendering.html',{},function(){},function(data){

                            if( data.success ){

                                var D = data.obj;
                                if( typeof D == 'undefined' ){
                                    window.location.replace('/aggregationPage.html?router=login');
                                    return false;
                                }else if( D instanceof Object ){
                                    tabLe.each(function(index){
                                        var ax = new Array(), xa = '';
                                        if( index == 0 ){
                                            xa = 'borrowing';
                                            for(var i = 0; i < D[xa].length; i++){
                                                //万元 ax.push('<tr><td><a href="'+ D[xa][i].borrowLink +'">'+ D[xa][i].borrowName +'</a></td><td>'+ ( D[xa][i].borrowAccount >= 10000?D[xa][i].borrowAccount/10000 + '万':D[xa][i].borrowAccount ) +'元</td><td>'+ D[xa][i].borrowApr +'%</td><td>'+ D[xa][i].timeLimit +'</td><td>'+ D[xa][i].status +'</td></tr>');
                                                ax.push('<tr><td><a href="'+ D[xa][i].borrowLink +'">'+ D[xa][i].borrowName +'</a></td><td>'+ D[xa][i].borrowAccount +'元</td><td>'+ D[xa][i].borrowApr +'%</td><td>'+ D[xa][i].timeLimit +'</td><td>'+ D[xa][i].status +'</td></tr>');
                                            }
                                            if( ax.length > 0 ){
                                                $(tabLe[index]).find('tbody').append(ax.join(''));
                                            }else{
                                                $(tabLe[index]).html('<caption>暂无数据</caption>');
                                            }

                                        }else if( index == 1 ){
                                            xa ='repaying';
                                            for(var i = 0; i < D[xa].length; i++){
                                            //万元 ax.push('<tr><td><a href="'+ D[xa][i].borrowLink +'">'+D[xa][i].borrowName  +'</a></td><td>'+ ( D[xa][i].borrowAccount >= 10000?D[xa][i].borrowAccount/10000 + '万':D[xa][i].borrowAccount ) +'元</td><td>'+ D[xa][i].borrowApr +'%</td><td>'+ D[xa][i].timeLimit +'</td><td>'+ D[xa][i].repayTime +'</td><td>'+ D[xa][i].status +'</td></tr>');
                                            ax.push('<tr><td><a href="'+ D[xa][i].borrowLink +'">'+D[xa][i].borrowName  +'</a></td><td>'+ D[xa][i].borrowAccount +'元</td><td>'+ D[xa][i].borrowApr +'%</td><td>'+ D[xa][i].timeLimit +'</td><td>'+ D[xa][i].repayTime +'</td><td>'+ D[xa][i].status +'</td></tr>');
                                                }
                                            if( ax.length > 0 ){
                                                $(tabLe[index]).find('tbody').append(ax.join(''));
                                            }else{
                                                $(tabLe[index]).html('<caption>暂无数据</caption>');
                                            }
                                        }
                                    });

                                }
                                setTimeout(function(){
                                    $('.layrows-child.show').parents('section').css('height',$('.layrows-child.show').height() );
                                    tabLe.eq(0).removeClass('Opc');
                                    loadING.hide();
                                },800);
                            }else{
                                alert('请求失败');
                                return false;
                            }

                        });


                   }else if( corr_url == 're_charge' ){
                        //console.log('充值记录查询');
                        var conD = 're_charge', tabLe = $('div[name='+ conD +']').find('.dataTablerecord');
                        tabLe.eq(0).addClass('Opc');
                        loadING.show();
                        aia('/interface/myAccount/rechargeRecords.html',{},function(){},function(data){
                            if( data.success ){
                                var D = data.obj;
                                if( typeof D == 'undefined' ){
                                    window.location.replace('/aggregationPage.html?router=login');
                                    return false;
                                }else if( D instanceof Object ){
                                    var dd = D.rechargeRecords, ix = new Array();
                                    for( var i = 0; i < dd.length; i++ ){
                                        ix.push('<tr><td>*'+ dd[i].orderNo.slice(-4) +'</td><td class="'+ (function(){
                                            var oq = dd[i].rechargeType; if( oq == 1 ){ return 'qcp'; }else if( oq == 8 ){ return 'qcp tra'; }else{ return 'qcp trat'; }
                                        //万元 }).call(this) +'"></td><td>'+ ( dd[i].rechargeAmount >= 10000?dd[i].rechargeAmount/10000 + '万':dd[i].rechargeAmount ) +'元</td><td>'+ dd[i].rechargeTime +'</td><td class="O">'+ dd[i].rechargeStatus +'</td></tr>');
                                        }).call(this) +'"></td><td>'+ dd[i].rechargeAmount +'元</td><td>'+ dd[i].rechargeTime +'</td><td class="O">'+ dd[i].rechargeStatus +'</td></tr>');
                                    }
                                    if( ix.length > 0 ){
                                            $(tabLe).find('tbody').append(ix.join(''));
                                        }else{
                                            $(tabLe).html('<caption>暂无数据</caption>');
                                        }

                                }
                                setTimeout(function(){
                                    $('.layrows-child.show').parents('section').css('height',$('.layrows-child.show').height() );
                                    tabLe.eq(0).removeClass('Opc');
                                    loadING.hide();
                                },800);
                            }else{
                                alert('请求失败');
                                return false;
                            }

                        });

                   }else if( corr_url == 're_cashout' ){
                        //console.log('提现记录查询');
                        var conD = 're_cashout', tabLe = $('div[name='+ conD +']').find('.dataTablerecord');
                        tabLe.eq(0).addClass('Opc');
                        loadING.show();
                        aia('/interface/myAccount/cashRecords.html',{},function(){},function(data){
                            if( data.success ){
                                var D = data.obj;
                                if( typeof D == 'undefined' ){
                                    window.location.replace('/aggregationPage.html?router=login');
                                    return false;
                                }else if( D instanceof Object ){
                                    var dd = D.cashRecords, ix = new Array();
                                    if( dd.length > 0 ){
                                        for( var i = 0; i < dd.length; i++ ){
                                            var x = dd[i].cashCardNo, _x = x.substr(0,4),x_ = x.slice(-4);
                                            //万元 ix.push('<tr><td>'+ _x + '****' + x_ +'</td><td>'+  ( dd[i].cashAmount < 10000 ? (dd[i].cashAmount + '元'):(dd[i].cashAmount/10000 + '万元') )+'</td><td>'+ dd[i].cashTime +'</td><td class="O">'+ dd[i].cashStatus +'</td></tr>');
                                            ix.push('<tr><td>'+ _x + '****' + x_ +'</td><td>'+ dd[i].cashAmount + '元</td><td>'+ dd[i].cashTime +'</td><td class="O">'+ dd[i].cashStatus +'</td></tr>');
                                        }

                                        if( ix.length > 0 ){
                                            $(tabLe).find('tbody').append(ix.join(''));
                                        }else{
                                            $(tabLe).html('<caption>暂无数据</caption>');
                                        }
                                    }

                                }
                                setTimeout(function(){
                                    $('.layrows-child.show').parents('section').css('height',$('.layrows-child.show').height() );
                                    tabLe.eq(0).removeClass('Opc');
                                    loadING.hide();
                                },800);
                            }else{
                                alert('请求失败');
                                return false;
                            }

                        });
                   }else if( corr_url == 're_money' ){
                        //console.log('资金记录查询');
                        var conD = 're_money', tabLe = $('div[name='+ conD +']').find('.dataTablerecord');
                        tabLe.eq(0).addClass('Opc');
                        loadING.show();
                        aia('/interface/myAccount/accountLog.html',{},function(){},function(data){
                            if( data.success ){
                                var D = data.obj;
                                if( typeof D == 'undefined' ){
                                    window.location.replace('/aggregationPage.html?router=login');
                                    return false;
                                }else if( D instanceof Object ){
                                    var dd = D.accountLogs, ix = new Array();
                                    if( dd.length > 0 ){
                                        for( var i = 0; i < dd.length; i++ ){
                                            //万元 ix.push('<tr><td>'+ dd[i].type +'</td><td>'+ ( dd[i].account >= 10000?dd[i].account/10000 + '万':dd[i].account )  +'元</td><td>'+ dd[i].addTime +'</td><td>'+ dd[i].userName.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') +'</td></tr>');
                                            ix.push('<tr><td>'+ dd[i].type +'</td><td>'+ dd[i].account +'元</td><td>'+ dd[i].addTime +'</td><td>'+ dd[i].userName.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') +'</td></tr>');
                                        }

                                         if( ix.length > 0 ){
                                            $(tabLe).find('tbody').append(ix.join(''));
                                        }else{
                                            $(tabLe).html('<caption>暂无数据</caption>');
                                        }
                                    }

                                }
                                setTimeout(function(){
                                    $('.layrows-child.show').parents('section').css('height',$('.layrows-child.show').height() );
                                    tabLe.eq(0).removeClass('Opc');
                                    loadING.hide();
                                },800);
                            }else{
                                alert('请求失败');
                                return false;
                            }

                        });
                   }



                }

                $('.userinfo').on('click','.getphoneCd', function(){
                    var $this = $(this), name = $this.attr('name'), picsx =$.trim($('input[name="'+ name +'"]').val()), phoneNumber = $('#real_reg_phone').val();

                    if( $this.parent().hasClass('bf-Xg') ){
                        // 解绑卡
                        if( picsx.length < 1 ){
                            Szl.ui.tooltips('请输入图形验证码');
                            return false;
                        }else{
                            Sa(phoneNumber,picsx,name,2); // 3不确定
                        }

                    }else{
                        // 变更手机号
                    }


                    function Sa(phonenumber,piccode,ele,sx){
                        var $that = $('[type="button"][name="'+ ele +'"]');
                        $.ajax({
                            url: ip + '/interface/user/getPhoneCode.html',
                            type: 'POST',
                            dataType: 'json',
                            data: { phone:phonenumber,
                                    validCode:piccode,
                                    phoneType:sx
                                },
                            beforeSend:function(){  },
                            success:function(D){
                                if( !D.success ){
                                    Szl.ui.tooltips(D.msg);
                                    $that.parents('.bindfocus').find('.updateimg').trigger('click');
                                    return false;
                                }else{
                                    //alert('即将发送验证短信给手机号'+ phonenumber);
                                    var allowt = 60, tu = setInterval(function(){
                                        if( allowt == 0 ){
                                            clearInterval(tu);
                                            $that.removeAttr('disabled').removeClass('sending').val('发送验证码');
                                        }else{
                                            $that.attr('disabled','disable').addClass('sending').val(allowt+'秒后重发');
                                            allowt--;
                                        }
                                    },1000);
                                }



                            }
                        });
                    }




                    // 发送手机验证码
                });

                $('.userinfo').on('click','#confirm_recharge', function(){
                    // 确认充值
                    var P = $(this).parent('.layout-sub'), prepaid = $('#pre_paid').data('d'), piccode = P.find('.v-x-code .sub-rows-data').find('[type="tel"]').val(), rechar_b_desq = $('.rechar_b_desq').val();

                    if( Szl.ui.isEmpty(rechar_b_desq) ){
                        Szl.ui.tooltips('请输入充值金额(数字)');
                        return false;
                    }else if(isNaN(rechar_b_desq)){
                        Szl.ui.tooltips('充值金额必须为数字');
                        return false;
                    }else if( Szl.ui.isEmpty(piccode) ){
                        Szl.ui.tooltips('请输入图形验证码');
                        return false;
                    }else if( typeof rechar_b_desq.toString().split('.')[1] != 'undefined' && rechar_b_desq.toString().split('.')[1].length > 2 ){
                        Szl.ui.tooltips('金额只允许两位小数');
                        return false;
                    }else if( rechar_b_desq < 10 ){
                        Szl.ui.tooltips('充值金额必须大于10元');
                        return false;
                    }else{

                       $.ajax({
                            url: '/member/account/newrecharge.html',
                            type: 'POST',
                            dataType: 'html',
                            data: { type:prepaid.nid, money:rechar_b_desq, valicode:piccode,isPhoneRequest:1 },
                            success:function(U){
                                //( confirm('您将为账户充值'+ rechar_b_desq +'元,请确认？') )

                                if( $(U).find('#error_zl').length > 0 ){
                                    Szl.ui.tooltips( $(U).find('#error_zl').text() );
                                    $(P).find('.updateimg').trigger('click');
                                    return false;
                                }else{
                                    $('body').append($(U));
                                }
                            }
                        });
                    }



                });


                $('#sele_cashout_byq').on('click',function(){
                    if( typeof $('#sele_cashout_byq')[0].selectedOptions[0] == 'undefined' ){
                        if( confirm('请先绑定银行卡，再执行提现操作！') ){
                            window.location.href = '/wap/Innerpage.html?router=oPtions&subRouter=bankcard';
                        }
                        return false;
                    }
                });

                $('.userinfo').on('click','#confirm_cashout', function(){
                    // 确认提现
                    var P = $(this).parent('.layout-sub'), piccode = P.find('.v-x-code .sub-rows-data').find('[type="tel"]').val(), bankcardValue = $('#sele_cashout_byq')[0], money = P.find('.o_txje_').val();
                    if( typeof bankcardValue.selectedOptions[0] == 'undefined' ){
                        if( confirm('请先绑定银行卡，再执行提现操作！') ){
                            window.location.href = '/wap/Innerpage.html?router=oPtions&subRouter=bankcard';
                        }
                        return false;
                    }else if( Szl.ui.isEmpty(money) ){
                        Szl.ui.tooltips('请输入提现金额(数字)');
                        return false;
                    }else if(isNaN(money)){
                        Szl.ui.tooltips('提现金额必须为数字');
                        return false;
                    }else if( Szl.ui.isEmpty(piccode) ){
                        Szl.ui.tooltips('请输入图形验证码');
                        return false;
                    }else if( typeof money.toString().split('.')[1] != 'undefined' && money.toString().split('.')[1].length > 2 ){
                        Szl.ui.tooltips('金额只允许两位小数');
                        return false;
                    }else if( money < 100 ){
                        Szl.ui.tooltips('提现金额必须大于100元');
                        return false;
                    }else if( money > $_visi_balance.data('text') ){
                        Szl.ui.tooltips('提现金额不能大于账户可用余额');
                        return false;
                    }else{
                           $.ajax({
                                url: '/member/account/doNewcash.html',
                                type: 'POST',
                                dataType: 'html',
                                data: { accountBank:bankcardValue.selectedOptions[0].value,money:money,valicode:piccode,type:'newcash',isPhoneRequest:1 },
                                success:function(U){

                                    if( $(U).find('#error_zl').length > 0 ){
                                        Szl.ui.tooltips( $(U).find('#error_zl').text() );
                                        $(P).find('.updateimg').trigger('click');
                                        return false;
                                    }else{
                                        $('body').append($(U));
                                    }
                                }
                            });
                        }

                });

                $('.userinfo').on('change','#bind_bankcard_provice',function(){
                    var x = $(this)[0].selectedOptions[0].value,
                        x_child = $('#bind_bankcard_city');

                        if( x != 0 ){
                            aia('/member/account/showmmmbank.html?pid='+x,{},function(){ $('.loadDataCircles').show(); },function(address){
                                var addressArr = new Array();
                                for( var i =0; i < address.length; i++ ){
                                    addressArr.push('<option value="'+ address[i].id +'">'+ address[i].name +'</option>');
                                }
                                x_child.find('option').remove();
                                x_child.append(addressArr.join('')).removeClass('d-n').show();
                                $('.loadDataCircles').hide();
                            });
                        }else{
                            Szl.ui.tooltips('请选择地理位置');
                            x_child.find('option').remove();
                            x_child.hide();
                            return false;
                        }
                });

                $('.userinfo').on('click','#bind_bankcard', function(){
                    // 绑定银行卡
                    var P = $(this).parent('.layout-sub'), bankcardCreate = $('#bind_Xsel_bankcard')[0].selectedOptions[0].value, number_bankcard = P.find('.bind_cardNumB_er').val().trim(), Provice = $('#bind_bankcard_provice')[0].selectedOptions[0].value, city = $('#bind_bankcard_city')[0].selectedOptions[0];
                    if( bankcardCreate == 'select' ){
                        Szl.ui.tooltips('请选择开户银行');
                        return false;
                    }else if( Szl.ui.isEmpty(number_bankcard) ){
                        Szl.ui.tooltips('请输入银行卡号');
                        return false;
                    }else if( number_bankcard.length != number_bankcard.replace(/[^0-9]/ig,"").length ){
                        Szl.ui.tooltips('银行卡号必须全部是数字');
                        return false;
                    }else if( typeof city == 'undefined' ){
                        Szl.ui.tooltips('请选择开户行所在地理位置');
                        return false;
                    }else{
                        city = city.value;
                        $.ajax({
                            url: '/member/account/bank.html',
                            type: 'POST',
                            dataType: 'html',
                            data: { drawBank:bankcardCreate, account:number_bankcard,province:Provice, city:city, type:'add',codeUniqueId:'',isPhoneRequest:1 },
                            success:function(U){
                                if( $(U).find('#error_zl').length > 0 ){
                                    Szl.ui.tooltips( $(U).find('#error_zl').text() );
                                    $('#numcaRds').trigger('click');
                                    aja_mybankcard();
                                    return false;
                                }else{
                                    $('body').append($(U));
                                }
                            }
                        });
                    }




                });


                $('.userinfo').on('click','#confirm_aUthreal[data-status="false"]', function(){
                    // 实名认证
                    var P = $(this).parent('.layout-sub'), realname= $('._realOpsname').val(), realNumber = $('._realOpsnumber').val(), piccode = $('._realOppicco').val();

                    if( Szl.ui.isEmpty(realname) ){
                        Szl.ui.tooltips('请输入真实姓名');
                        return false;
                    }else if( Szl.ui.isEmpty(realNumber) ){
                        Szl.ui.tooltips('请输入身份证号码');
                        return false;
                    }else if( Szl.ui.isEmpty(piccode) ){
                        Szl.ui.tooltips('请输入图形验证码');
                        return false;
                    }else if(realNumber.length != 18){
                        Szl.ui.tooltips('请输入正确的身份证号码');
                        return false;
                    }else{
                        $.ajax({
                            url: '/member/identify/apiRealname.html',
                            type: 'POST',
                            dataType: 'html',
                            data: { realname:realname, cardType:1616, cardId:realNumber,validCode:piccode, isPhoneRequest:1 },
                            success:function(U){
                                if( $(U).find('#error_zl').length > 0 ){
                                    Szl.ui.tooltips( $(U).find('#error_zl').text() );
                                    $('[name="realnameauth"] .updateimg').trigger('click');
                                    return false;
                                }else{
                                    $('body').append($(U));
                                }
                            }
                        });
                    }

                });

                $('.userinfo').on('click','#password_edit', function(){
                    // 密码修改
                    var P = $(this).parent('.layout-sub'), originalPass= $('._originalpass').val(), newpass = $('._newpass').val(), renewpass = $('._renewpass').val();
                    if( Szl.ui.isEmpty(originalPass) ){
                        Szl.ui.tooltips('请输入原始密码');
                        return false;
                    }else if( Szl.ui.isEmpty(newpass) ){
                        Szl.ui.tooltips('请输入新密码');
                        return false;
                    }else if(!/^[a-zA-Z]\w{5,17}$/.test(newpass)  ){
                        Szl.ui.tooltips('密码为字母开头,6到18位数字和字母的组合');
                        return false;
                    }else if( Szl.ui.isEmpty(renewpass) ){
                        Szl.ui.tooltips('请重复输入新密码');
                        return false;
                    }else if( newpass == originalPass ){
                        Szl.ui.tooltips('原始密码与新密码相同！');
                        return false;
                    }else{
                        $.ajax({
                            url: '/member/security/userpwd.html',
                            type: 'POST',
                            dataType: 'html',
                            data: { oldpassword:originalPass,newpassword:newpass,newpassword1:renewpass,actionType:'chgpwd' },
                            success:function(U){
                                if( $(U).find('#sb_msg').length > 0 ){
                                    if( $(U).find('#sb_msg').find('p.error').length > 0 ){
                                        Szl.ui.tooltips( $(U).find('#sb_msg').find('p.error').text() );
                                    }else{
                                        Szl.ui.tooltips( $(U).find('#sb_msg').find('p.ok').text() );
                                        setTimeout(function(){ window.location.href = 'logout.html'; },3800);
                                    }

                                    return false;
                                }
                            }
                        });
                    }

                });

            $('.ica').each(function(){
                var numb = $(this),
                    _tmp = numb.text().replace(/[^\d\.]/g,''),
                    trannum;

                    if( _tmp != "" ){
                        trannum = ( _tmp*1).toFixed(3)*1;
                        if( trannum.toFixed(2) == trannum.toFixed(3) ){
                            trannum = trannum.toFixed(2);
                        }else if( trannum.toFixed(1) == trannum.toFixed(2)  ){
                            trannum = trannum.toFixed(1);
                        }else if( trannum.toFixed(0) == trannum.toFixed(1)  ){
                            trannum = trannum.toFixed(0);
                        }
                        numb.text(numb.text().replace(_tmp,trannum));
                    }

            });

            //个人主页完毕
        };


        if( $('html').data('area') == 'log' ) LOGIO();
        if( $('html').data('area') == 'userinfo' ) USERINFO();
        if( $('html').data('area') == 'indexPage' ) INdEX();
        if( $('html').data('area') == 'listbids' ) ListBids();
        if( $('html').data('area') == 'biddetails' ) BidDetails();


    //  结束部分
    }
    return { ZL:runJHS() }
})();
S.ZL;