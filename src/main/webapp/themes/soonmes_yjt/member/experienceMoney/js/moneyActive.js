/**
 * liruonan20161014
 */
$(function () {
            var index_div_pro = [
               {sx: 0, sy: 0, mw: 3, mh: 1, bx: 8.4, by: 10.4, rx: -0.6},
               {sx: 700, sy:1150, mw: 3, mh: 0.1, bx: 6.4, by: 8.4, rx: -0.1},
               {sx: 35, sy: 90, mw: 0.3, mh: 0.1, bx: 6.5, by: 7.4, rx: -0.3},
               {sx: 700, sy:90, mw: 3, mh: 0.1, bx: 6.4, by: 8.4, rx: -0.1}];

            var ePageX = null;
            var ePageY = null;
            function getMousePos(expression) {
                if (ePageX == null || ePageY == null) return null;
                var _x = $(expression).position().left;
                _x += ePageX - $(expression).parent().position().left;
                var _y = $(expression).position().top;
                _y += ePageY - $(expression).parent().position().top;
                return {
                    x: _x,
                    y: _y
                }
            };

            var index_xh = setInterval(function () {
                        for (var i = 0; i < 4; i++) {
                            var mousepos = getMousePos("#indexg" + i);
                            if (mousepos != null) {
                                var self =$ ("#indexg" + i);
                                var left =(index_div_pro[i].sx + index_div_pro[i].mw - (mousepos.x - 100) / index_div_pro[i].bx * index_div_pro[i].rx ) * 0.4;
                                var top = (index_div_pro[i].sy + index_div_pro[i].mh - (mousepos.y - 100) / index_div_pro[i].by ) * 0.38;
                                $("#indexg" + i).css({
                                    left: left,
                                    top: top
                                })
                            }
                        }
                    },
                    15);

            $(".redTop").mousemove(function (event) {
                event = event || window.event;
                ePageX = event.pageX;
                ePageY = event.pageY;
            });
            
           //弹窗控制代码
        });

 //金币循环图片
    (genClips = function () {
        var red = $('.red');
        for(var j=1;j<=20;j++){
            for(var i=1;i<=5;i++){
                $('<img class="coin" src="/themes/soonmes_yjt/member/experienceMoney/imgs/gold_' + i + '.png" />').appendTo($('.gold'))

            }
        }
    })();
    //生成随机数
    function rand(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }
    var first = false, clicked = false;
    //点击触发
    $('.bag').hover( function () {
        //钱袋晃动
        var r = setInterval(function () {
            if (first === false) {
                $('.empty').addClass("shake");
                first = true;
            }
        });

        if (clicked === false) {
            $('.full').hide();
            $('.empty').show();
            clicked = true;
            $('.gold').css({ 'display': 'block'});

            //金币动效
            $('.gold img').each(function () {
                var v = rand(120, 90),
                    angle = rand(70, 89),
                    theta =(angle * Math.PI)/180,//角度
                    g = -9.8;



                var self = $(this);
                //随机调整金币形态
                var randDeg = rand(-5, 10),
                    randScale = rand(0.9, 1.1),
                    randDeg2 = rand(30, 5);
                $(this).css({ 'transform': 'scale(' + randScale + ') skew(' + randDeg + 'deg) rotateZ(' + randDeg2 + 'deg)'});




                var t = 0,totalt =10;

                var negate = [1, -1, 0],
                    direction = negate[Math.floor(Math.random() * negate.length)];
                var z = setInterval(function () {
                    var ux = (Math.cos(theta) * v) * direction;
                    var uy = (Math.sin(theta) * v) - ((-g) * t);
                    var nx = (ux * t);
                    var ny=(uy*t) + Math.pow(t, 2) * g+600;
                    if (ny < 0) {
                        ny = 0;
                    }
                    $(self).css({
                        'bottom': (ny) + 'px',
                        'left': (nx) + 'px'
                    });

                    t = t + 0.10;

                    if (t > totalt) {
                        clicked = false;
                       // first = false;
                        clearInterval(z);
                    }
                },20);
            });
        }
    });
/*moneyActive popUp*/
 var stste=$("#stste").val();
 if(stste==0){
	 $(".popup").show();
	 $(".bank").show();
 }
 else{}
 $(".popBtn").click(function(){
	 $(".popup").hide();
	 $(".bank").hide();
	 var url = "/member/receiveExperienceMoney.html";
	 $.ajax({
		 type: "POST",
		 url: url,
		 dataType: "json",
		 success: function (Obj) {}
	 });
  })
