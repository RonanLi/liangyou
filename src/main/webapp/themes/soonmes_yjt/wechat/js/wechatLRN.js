/**
 * Created by liruonan on 2017/2/17.
 */
$(document).ready(function () {
    $(".tab li").click(function () {
        $(".tab li").eq($(this).index()).addClass("cur").siblings().removeClass('cur');
        $("div").hide().eq($(this).index()).show();
        $("input").prop('checked',false);
    });
});

function Tab(elem1, elem2) {
    elem1.click(function () {
        elem1.eq($(this).index()).addClass("cur").siblings().removeClass('cur');
        elem2.hide().eq($(this).index()).show();
    });
}
Tab($('#replyType li'),$('.replyMsg'));


/*消息传值*/
function getMsgs() {
    $('#id_msgs').dialog({
        title: '选择消息',
        width: 600,
        height: 400,
        modal: true,
        buttons: {
            "确定": function () {
						var msgtype = $("#id_msgs_frame").contents().find('input[name="msgtype"]').val();
                        $("#id_msgs_frame").contents().find('input').each(function () {
                            if($(this).is(':checked')){
                                $("#id_msgIds").val($(this).val());
                            }
                        });
                        $(this).dialog('close');
            }
        }
    })
};