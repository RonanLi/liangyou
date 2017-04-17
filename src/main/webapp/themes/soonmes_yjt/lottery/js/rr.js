$(function () {

     var $goe = $('#goe > ul'),
         _h = parseInt(),
         len = $goe.find('li').length, cout = 2, f = false, prize = 3, vrl = '/themes/soonmes_yjt/lottery/img/';
     var im = function(){
            return im = {
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
                    return (im.Android() || im.BlackBerry() || im.iOS() || im.Opera() || im.Windows() || im.wechat());
               }
            }
        };

        
        function imgload(){
            
            if( !$.support.leadingWhitespace ){
                 $('html').show();
                 $('body').append('<div style="position: fixed; z-index: 9999999; background: #000; left: 0; top: 0; width: 100%; filter:alpha(opacity=90); height: 100%; text-align: center; color: #fff; font-size: 18px; filter: alpha(opacity=90);"><p style="font-size:26px; font-weight:bold; margin-top:10%;">您当前的浏览器版本太低，请立即更新您的<a style="color:orange;padding:0 8px;" href="http://sw.bos.baidu.com/sw-search-sp/software/a7958cfcdbd6e/ChromeStandalone_53.0.2785.116_Setup.exe">浏览器</a>以获得更好的体验</p></div>');
            }else{
                var image = new Array(), _len = imgload.arguments.length, x = 0;
                for( var i = 0; i < _len; i++ ){
                    image[i] = new Image();
                    image[i].src = vrl + imgload.arguments[i];
                    image[i].onload = function(){ 
                        if( x + 1 == _len ){
                            $('html').fadeIn();
                        }
                        x++;
                    }
                    
                }
            }

        }

    imgload('logoW.png','pointer.png','qrcodejhs.jpg','raffl.jpg','rafflein.jpg','titlebg.jpg','fuceng/awards.png','fuceng/sorry-min.png','fuceng/zlyc.png','fuceng/sharebtn.png','fuceng/mslq.png','fuceng/img0.png','fuceng/img46.png','fuceng/img88.png','fuceng/img132.png','fuceng/img177.png','fuceng/img224.png','fuceng/img270.png');

    document.documentElement.className = ((im().any() != null )?'touch':'notouch');
    $('.touch .toolt').html('微信分享可增加抽大奖概率，您当前共有两次免费抽奖机会,奖品有限,先到先得。');
    $('.touch .plate-container').css({'width': window.screen.availWidth,'height': window.screen.availWidth});

    var fRotateDuringReq = function(){
        $("#lotteryBtn").rotate({
            angle: 0,
            duration: 10000,
            animateTo: 1800,
            callback: function () {
                alert('服务器返回异常');
            }
        });
    }

    
    var fStartLottery = function (angle, text) {
        var mask = $('#maskin');
        $('#lotteryBtn').stopRotate();
        $("#lotteryBtn").rotate({
            angle: 0,
            duration: 5000,
            animateTo: angle + 1440,
            callback: function(){
                f = false;
                console.log(text);
                if( angle == 316 ){
                    mask.addClass('xxcy').find('a').attr('class','zlyc');
                }else{
                    mask.addClass('DB Fa' + angle ).find('a').attr('class','drew child' + angle);
                    mask.find('.status > img').attr('src','/themes/soonmes_yjt/lottery/img/fuceng/awards.png').after('<img class="rkr" src="/themes/soonmes_yjt/lottery/img/fuceng/img'+ angle +'.png"/><input type="tel" maxlength="11" class="insertphone" id="phone" placeholder="请输入手机号码" >').end().find('a[name="wei"]').find('img').attr('src','/themes/soonmes_yjt/lottery/img/fuceng/mslq.png');
                }

            }
        });
    };

    var awards = {
        "1": {name: "美的电饭煲", angle: 224},
        "2": {name: "50元京东卡", angle: 270},
        "3": {name: "谢谢参与", angle: 316},
        "4": {name: "360行车记录仪", angle: 0},
        "5": {name: "唱吧直播间VIP券", angle: 46},
        "6": {name: "不粘锅系列煎盘", angle: 88},
        "7": {name: "薰衣草庄园门票", angle: 132},
        "8": {name: "黑糖姜茶", angle:177}
    };

    /**
     * 抽奖按钮点击事件
     */

    $('body').on('click','.zlyc',function(){
        // var theEvent = window.event || arguments.callee.caller.arguments[0];
        // theEvent.preventDefault();
        // theEvent.stopPropagation();
        $('#maskin').attr('class','maskin');
        $('#lotteryBtn').css('transform','none');
        $('html,body').animate({scrollTop: $('.toolt').offset().top },300);
        if( $('html').hasClass('touch') ){
            $('.toolt').text('微信分享可增加抽大奖概率，您当前仅剩一次免费抽奖机会,奖品有限,先到先得。');
        }else{
            $('.toolt').text('您当前仅剩一次免费抽奖机会,奖品有限,先到先得。');
        }
        
    });

    $('body').on('click','.drew',function(){
        var phonenumber = $('#phone').val().trim();
        if( phonenumber.length < 11 || !/^1[0-9]{10}$/.test(phonenumber) ){
            alert('系统无法识别您的手机号码，请重新输入');
            return false;
        }else{
            $.ajax({
                url: '/lottery/receivePrize.html',
                type: 'POST',
                dataType: 'json',
                data: {phone:phonenumber,prizeId:prize},
                success:function(wr){
                    if( wr.success ){
                        window.location.assign(wr.obj.redirectURL);
                    }
                }
            });
        }
    });

    /*$("#lotteryBtn").click(function(){
        if(f){ return false };
        f = true;
        var that = $(this);
        fRotateDuringReq();
        if( cout == 2 ){ cout--; fStartLottery(awards['3'].angle,'谢谢参与');
        }else if( cout == 1 ){ $.get('/lottery/doLottery.html', function(data){
                var data = JSON.parse(data);
                if(data.success){
                    prize = data.obj.prizeId;
                    fStartLottery(awards[data.obj.prizeId].angle,awards[data.obj.prizeId]);
                }else{
                    fStartLottery(awards['3'].angle,'谢谢参与[返回请求]');
                }
            });
        }else{
            alert('你已经没有抽奖机会了!');
            return false;
        }

    });*/

    $('.wcx').on('click',function(){ $('.wcx').removeClass('show'); $(this).addClass('show');
    });


    function slidCop(e, g) {
        var timeout, ul = $('#goe ul'), li = ul.find('li:first')[0].offsetHeight;
        ul.hover(function(){
            window.clearInterval(timeout);
        },function(){
            timeout = setInterval(function(){
                ul.animate({
                    "margin-top": -li + "px"
                }, e, function() {
                    ul.css('margin-top', 0 + 'px').find('li:first').appendTo(ul);
                });
            }
            , g);
        }
        ).trigger('mouseleave');
    }
    
    /*setTimeout(function(){
        $('html').hasClass('touch')?slidCop(800, 780):slidCop(500,664);
    },800);*/

});