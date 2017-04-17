/**
 * Created by liruonan on 2016/12/29.
 */
$(".tab>span").click(function(){
    $(this).addClass('cur').siblings().removeClass('cur');
    var n=$(this).attr('date-val');
    $('.allActive li').removeClass();
    if(n==0){
      $(".hotActive").show();
    }
    else{
        $(".hotActive").hide();
        $('.allActive  li[data-val="'+n+'"]').addClass("S").siblings().addClass("H");
    }
})

