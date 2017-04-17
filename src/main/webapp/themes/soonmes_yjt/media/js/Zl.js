var ZhuoLun = (function(){

    window.Szl = {
        ui:{
            isMobile:function(){
                return isMobile = {
                    Android: function() {
                        return navigator.userAgent.match(/Android/i);
                    },
                    wechat: function() {
                        return navigator.userAgent.match(/MicroMessenger/i);
                    },
                    BlackBerry: function() {
                        return navigator.userAgent.match(/BlackBerry/i);
                    },
                    iOS: function() {
                        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
                    },
                    Opera: function() {
                        return navigator.userAgent.match(/Opera Mini/i);
                    },
                    Windows: function() {
                        return navigator.userAgent.match(/IEMobile/i);
                    },
                    any: function() {
                        return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows() || isMobile.wechat());
                   }
                }
            },
            gUrlp:function(zl){
                return decodeURIComponent((new RegExp('[?|&]' + zl + '=' + '([^&;]+?)(&|#|;|$)', "ig").exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || null;
            }
        }
    };

    function better(){

        if( Szl.ui.isMobile().any() == null ){
            //$('body').addClass('notouch');
            
        }else if( navigator.userAgent.indexOf('WindowsWechat') >= 0 ){
            alert('请使用手机打开，获得更好的浏览体验！');
            return false;
        }else{
            $('html').addClass('i0i_Touch');
            $('body').addClass('touch Device-' + Szl.ui.isMobile().any());
            $('head').append('<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/><meta name="renderer" content="webkit"><meta name="format-detection" content="telephone=no, email=no"><link rel="stylesheet" type="text/css" href="/themes/soonmes_yjt/media/css/Zl.css" media="all">');
            /*隐藏适配手机端头部等*/
            /*GoWap();*/
            $('#Kioe,.lheader,.top').hide();
        }

        /* fun go */
function GoWap(){

            var topHeader = $('.i0i_Touch body > .top .topBox').find('span').html(),
                pn = window.location.pathname;
        $('.i0i_Touch body > .top .topBox').replaceWith('<p class="toppnu F" style="color: #999; font-size: 1.4rem; padding: 6px;"><a href="tel:4009005591" style=" color: #fff; text-decoration: underline;">客服:400-900-5591</a><span style="float: right; color: #999;display: block;">'+ topHeader +'</span></p>');
       $('.lheader').find('.head').addClass('F R').find('a.logo > img').attr('src','/themes/soonmes_yjt/media/imgs/logowap.png').parents('.head').append('<div id="Kioe"><span></span><span></span><span></span></div>');
       $('#Kioe').on('click',function(){ $('.i0i_Touch .book').toggle(); });
        if( !$('.type-content.bid').parent().hasClass('NewListM') ){
            $('.type-content.bid').wrap('<div class="NewListM"/>');
        }
       
       $('.NewListM .listwidth').find('.li2').append('<span class="HHre Nxli2">信用级别</span>').end().find('.li3').append('<span class="HHre Nxli3">预期年化</span>').end().find('.li4').append('<span class="HHre Nxli4">借款金额</span>').end().find('.li5').append('<span class="HHre Nxli5">借款期限</span>').end().find('.li6').append('<span class="HHre Nxli6">投资进度</span>');

       $('body').append('<div class="fooTWap">©2017 金和所, 版权所有</div>');
       $('.logo + .book').find('a:contains("首页")').css('margin','0');
       $('.logo + .book').find('a:contains("我要出借")').css('margin','0').next().remove();
       $('.logo + .book').find('a:contains("新手指导")').next().remove();
       $('.logo + .book').find('a:contains("关于我们")').next().remove();

       if( pn == '/invest/index.html?status=11' ){
            $('.logo + .book').find('li:eq(1)').find('a').addClass('current').parent().siblings('li').find('a').removeClass('current');
       }
       else if( pn == '/Safety.html' ){
            $('.logo + .book').find('li:eq(2)').find('a').addClass('current').parent().siblings('li').find('a').removeClass('current');
       }
       else if( pn == '/about/xszd.html?code=newGuide' ){
            $('.logo + .book').find('li:eq(3)').find('a').addClass('current').parent().siblings('li').find('a').removeClass('current');
       }
       else if( pn == '/about/gywm.html?code=introduce' ){
            $('.logo + .book').find('li:eq(4)').find('a').addClass('current').parent().siblings('li').find('a').removeClass('current');
       }
       else if( pn == '/member/index.html' ){
            $('.logo + .book').find('li:eq(5)').find('a').addClass('current').parent().siblings('li').find('a').removeClass('current');
       }

       
//       $('dl.attr-list').find('span:contains("项目类型"),span:contains("项目利率"),span:contains("项目类型") + dd,span:contains("项目利率") + dd').remove();

       if( !$('.investbody > .NewListM .type-content.bid').find('div:last').hasClass('moreVC')){  
            $('.investbody > .NewListM .type-content.bid').find('div:last').css({'visibility':'hidden','height':'20px'});
            $('.investbody').css('margin-bottom','12px');
       }

       if( $('img[src="/themes/soonmes_yjt/media/imgs/safety.jpg"]').length > 0 ){
            var ximgW_so = $('img[src="/themes/soonmes_yjt/media/imgs/safety.jpg"]').hide();
            ximgW_so.parent('body').css('background','#fff').addClass('imgtranSf');
            ximgW_so.after('<div class="tab-pane hide" style="display: block;"> <div class="container"> <img src="/themes/soonmes_yjt/liangyou/imgs/xp1.jpg" style="margin-bottom:10px;"> <div class="outer"> <div class="card f show"> <h1>1&nbsp;资金第三方托管</h1> <div> <p class="lef">金和所投资人的投资理财资金由业内领先的第三方支付公司<span class="sq">双乾</span>进行托管，保证资金流转不经过金和所的银行账户，避免资金流入平台的银行账户形成资金池，导致资金池内资金被挪用而给交易双方带来的风险。</p> <img src="/themes/soonmes_yjt/liangyou/imgs/xpd3.jpg"> </div> </div> <div class="card f re"> <h1>2&nbsp;账户安全保障金</h1> <div> <p class="lef">据招商银行出具的最新报告显示，截至2016年9月5日，金和所及上海众苑金融信息服务有限公司的账户安全保障金账户余额为人民币<span class="sq">53,867,603.84</span>元，账户安全保障金成功突破5000万元大关。继2016年7月突破2000万元大关之后，仅仅用了1个月时间增至5386万。</p> <img src="/themes/soonmes_yjt/liangyou/imgs/xpd2.jpg"> </div> </div> <div class="card f"> <h1>3&nbsp;徽商银行资金托管</h1> <div> <p class="lef">金和所客户资金账户将和平台资金账户“分立门户”，即平台将不参与交易过程中的资金流动，银行依法对用户资金进行监督和管理，客户资金安全得到有效保障。</p> <img src="/themes/soonmes_yjt/liangyou/imgs/xpd4.jpg"> </div> </div> <div class="card f re"> <h1>4&nbsp;全链条风控</h1> <div class="justimg"> <img src="/themes/soonmes_yjt/liangyou/imgs/xpq5.jpg"> </div> </div> <div class="card f re"> <h1>5&nbsp;项目保障</h1> <div class="justimg ep"> <p>所有项目均由金和所风控部和风审会进行严格审核。一个优质项目的诞生，要经过8道审核工序，37层严格筛选，层层剥离风险敞口。</p> <img src="/themes/soonmes_yjt/liangyou/imgs/xpq10.jpg"> </div> </div> <div class="card f re"> <h1>6&nbsp;机构保障</h1> <div class="justimg xrr"> <img src="/themes/soonmes_yjt/liangyou/imgs/xpdq29.jpg"> <h2 style="    font-size: 13px; padding-bottom: 5px; font-weight: bold; color: #888;">合作保障机构</h2> <p>金和所有一套完善的保障机构评估体系，从区域地位、体量、历史、股东背景、风控水平、处置能力等多方面对机构进行考察。目前金和所已与全国80多家保障机构达成战略合作。保障机构不仅从贷前会对项目进行调研及风险评估，也会与金和所一起进行贷后管理。</p> <h2 style="font-size: 13px; padding-bottom: 5px; font-weight: bold; padding-top:6px; color: #888;">本息保障制度</h2> <p>为了给投资人营造安全的投资环境，保证投资人的本息安全，保障机构会对投资人的本息提供保障，当投资项目出现企业无力偿还的情况，保障机构会进行全额赔付，保护投资人权益。</p> </div> </div> <div class="card f seven"> <h1>7&nbsp;合规保障</h1> <div class="topb"> <h3 style="    font-size: 13px; padding-bottom: 5px; font-weight: bold; color: #888;">信息披露真实合规</h3> <p class="lef">金和所建立了专业的风控团队对项目进行全方位尽职调查，确保信息的真实性和合法性。并且将项目信息、借款企业情况公开透明，投资人可根据项目信息进行风险判断，进行自主投资。</p> <img src="/themes/soonmes_yjt/liangyou/imgs/qp8.jpg" style="margin-top:4px;"> </div> <div class="rpox"> <p class="lef"><strong>资金流转安全保障</strong>为最大程度的保障投资人的账户资金安全，资金的流转并不经过金和所平台，资金由第三方资产公司进行托管，实现了平台与资金的真正隔离，确保交易的真实性和投资人的资金安全。</p> <img src="/themes/soonmes_yjt/liangyou/imgs/qp9.jpg"> </div> <div> <h3 style=" font-size: 13px; padding-bottom: 5px; font-weight: bold; color: #888;">CFCA安全认证</h3> <p class="lef">中国金融认证中心简称CFCA，是经中国人民银行和国家信息安全管理机构批准成立的国家级权威的安全认证机构。金和所作为业内首家通过CFCA安全认证的互联网金融平台，您的每一次投资都由CFCA对网络交易及电子合同安全上锁。</p> <img src="/themes/soonmes_yjt/liangyou/imgs/qp10.jpg"> </div> <div class="rpox"> <p class="lef"><strong>法律法规保障</strong>金和所与傲雪律师事务所进行合作，律所起草并审核金和所所有主营业务合同，制定充分保护投资人利益的条款；律所监督审核金和所对融资项目的尽职调查工作，确保尽调合规性、合法性、合理性。</p> <img src="/themes/soonmes_yjt/liangyou/imgs/qp11.jpg"> </div> </div> <div class="card f re"> <h1>8&nbsp;技术保障</h1> <div class="justimg ep"> <p>技术层面，我们秉承用户的数据就是我们生命线的原则，通过多种防护手段保障用户数据安全，包括但不限于：</p> <img src="/themes/soonmes_yjt/liangyou/imgs/sjjj.jpg" style="width:100%;"> </div> </div> <div class="card f re none"> <h1>9&nbsp;自我保障</h1> <div class="justimg ep"> <p>账户安全需要大家和金和所的共同努力。在此，我们倡导在网站使用过程中，注意以下几点：</p> <ul class="ulr" style="margin-left: 38px;"> <li><div class="pad-r"> 牢记金和所官方网址：www.jinhefax.com<div>不要点击来历不明的链接访问金和所，谨防网站钓鱼和欺诈。我们建议您将金和所官方网址加入浏览器收藏夹，以方便您的下次登录。</div> </div></li> <li><div class="pad-r"> 设置高强度的密码<div>在设置登录密码和安全密码时，密码不要有一定的规律,密码长度要在6位以上，最好是数字和字母结合。定期修改密码并且不要将密码分享给他人。</div> </div></li> <li><div class="pad-r"> 设置紧急联系人<div>在“我的账户 – 安全设置”中设置紧急联系人信息。当出现紧急情况，如果我们无法与您联系，我们将与您设置的紧急联系人联系。</div> </div></li> <li><div class="pad-r"> 不要在公共场所登录<div>不要在网吧、公司等公共场所登录您的金和所账号，如果一定要登录请记得退出。</div> </div></li> <li><div class="pad-r"> 注重电脑运行环境的安全<div>及时为您的电脑进行系统更新、安装安全补丁，以防系统漏洞被黑客利用。为您的电脑安装杀毒软件或防火墙，并定期为电脑进行查毒、杀毒。</div> </div></li> </ul> </div> </div> </div> </div> </div>')
       }

       if( $('input[value="登录"]').length > 0 || $('#btnReg:contains("注册")').length > 0 ){
            $('body').addClass('loginI');
       }

       if( $('#changeUslis').length > 0 && $('#changeUslis').find('span:contains("认证信息：")').length > 0 ){
             $('html').addClass('Alreay');
       }
       if( $('#tab a:contains("借款详细说明")').length > 0 ){
            $('html').addClass('cardDet');
       }
       if( $('#main').find('div.l:contains("真实姓名：")').length > 0 || $('a[href="/member/apiRealname.html"]').length > 0 ){
            $('html').addClass('cztx');
            $('#tab li').hide().find('a:contains("账户充值"),a:contains("账户提现")').parent('li').show();
            $('.user_right_border').find('select[name="accountBank"]').parent().css('float','none');
            $('[type="submit"]').css('margin-left',100);
       }
       $('.listwidth').click(function(){
            window.location.href= $(this).find('li.mli1 > a + a').attr('href');
       });
       $('[action="/member/identify/apiRealname.html"] font').hide();
       $('#web-notice').next('a').text('更多');
       $('#lxjs').find('span:contains("上一页")').parent().hide();

       if( $('[src="/themes/soonmes_yjt/liangyou/imgs/gift-01.jpg"]').length > 0 ){
            $('#tguo,#heIo,#heIo + div').hide();
            $('[src="/themes/soonmes_yjt/liangyou/imgs/gift-01.jpg"]').attr('src','/themes/soonmes_yjt/liangyou/imgs/giftWAp.jpg');
       }

       if( location.href.indexOf('/invest/index.html?status=11') > 0 ){ 
          $('.bg-invest').css({"height":"auto","background":'none'});
          $('.bg-invest .wrapper').css({'width':'100%',"margin":"0"}).append('<img src="/themes/soonmes_yjt/liangyou/imgs/sbanner8-min.jpg" width="100%"/>');
       }

       if( $('#error_zl').length > 0 ){
            var x = $('#error_zl').text();
            if( x == '充值成功' ){
                window.location.href = '/member/index.html?renewG';
            }
       }
       if( window.location.search.substr(1) == 'renewG' ){
            alert('您的充值已成功！');
       }

       $('img[src="themes/soonmes_yjt/liangyou/imgs/line1_new.png"]').parents('.investtop').hide();

      
       var xH = 350 / (750 / document.documentElement.clientWidth);
       document.querySelector('.Roll_visual').setAttribute('style','height:'+ xH +'px');
       
       return;

    }

}
  return { S:better };
})();
ZhuoLun.S();