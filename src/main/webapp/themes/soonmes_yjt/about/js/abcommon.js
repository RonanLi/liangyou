/**
 * liruonan20161012
 */
$(".team li").hover(function () {
            $(".team li").eq($(this).index()).addClass("cur").siblings().removeClass('cur');
            $(".teamMask").show().eq($(this).index()).hide();
            $(".teamTxt").show().eq($(this).index()).hide();
            $(".introduce").hide().eq($(this).index()).show();
        })
        $(".insti>p>b").click(function(){
        	$(".instiFile").toggle();
        })
        
$(".webNews_tab > span").click(function () {
	$(".webNews_tab > span").eq($(this).index()).addClass('on').siblings().removeClass('on');
    $(".webNews_line").hide().eq($(this).index()).show();
    
})   

$(".conDdiv>h2>span").click(function () {
    $(".conDdiv>h2>span").eq($(this).index()).addClass("on").siblings().removeClass('on');

    $(".conDdiv>div").hide().eq($(this).index()).show();
})

 $('.point>a').click(function(){
	$('.floor').hide().eq($(this).index()).show();
});
$(".tab > span").click(function () {
    $(".tab > span").eq($(this).index()).addClass('cur').siblings().removeClass('cur');
    $(".hisLine").hide().eq($(this).index()).show();
})
$(".year>h2>a").click(function (e) {
    e.preventDefault();
    $(this).parents(".year").toggleClass("close");
});
$(".intro").click(function (e) {
    e.preventDefault();
    $(this).toggleClass("close").siblings(".more").toggle();
});
$(function(){
    $(".more>img").click(function(){
        var height = $(this).height();
        if(height<250)
        {
            $(this).height(300);
        }
        else
        {
            $(this).height(100);
        }
    });
});



