jQuery(function(){
	// function loginFun(){
	// 	var value = "";
 //        var userBox = $("#username");
 //        var pwdBox = $("#password");
	// 	var yzmBox = $("#yzm");
 //        userBox.focus(function(){
 //            var icon = $(this).next("span.formicon");
	// 		value = $(this).val();
 //            icon.hide();
	// 		if(value=="账户 / Email / 手机")
	// 		{
	// 			$(this).val("");
	// 		}
 //        }).blur(function(){
 //                var icon = $(this).next("span.formicon");
 //                icon.show();
 //                value = $(this).val();
 //                if(value=="")
 //                {
 //                    icon.addClass("erricon");
 //                    icon.removeClass("righticon");
	// 				$(this).val("账户 / Email / 手机");
 //                }
 //                else{
 //                    icon.removeClass("erricon");
 //                    icon.addClass("righticon");
 //                }
 //            });
	// 	 yzmBox.focus(function(){
 //            var icon = $(this).next("span.formicon");
 //            icon.hide();
	// 		value = $(this).val();
	// 		if(value=="验证码")
	// 		{
	// 			$(this).val("");
	// 		}
 //         }).blur(function(){
 //                var icon = $(this).next("span.formicon");
 //                icon.show();
 //                value = $(this).val();
 //                if(value=="")
 //                {
	// 				$(this).val("验证码");
 //                    icon.addClass("erricon");
 //                    icon.removeClass("righticon");
 //                }
 //                else{
 //                    icon.removeClass("erricon");
 //                    icon.addClass("righticon");
 //                }
 //            });	
 //        pwdBox.focus(function(){
 //            var icon = $(this).next("span.formicon");
	// 		var text_ = $(this).siblings("label");
	// 		var textVal = text_.text();
	// 		value = $(this).val();
			
 //            icon.hide();
	// 		if(textVal=="登录密码")
	// 		{
	// 			console.log(textVal)
	// 			text_.text("")
	// 		}
 //            }).blur(function(){
	// 			value = $(this).val();
	// 			var text_ = $(this).siblings("label");
	// 			var textVal = text_.text();
 //                var icon = $(this).next("span.formicon");
 //                icon.show();
 //                if(value=="")
 //                {
	// 				text_.text("登录密码");
 //                    icon.addClass("erricon");
 //                    icon.removeClass("righticon");
 //                }
 //                else{
 //                    icon.removeClass("erricon");
 //                    icon.addClass("righticon");
 //                }
 //            });
 //    	}
 //    loginFun();
		
	//表单的提示
		
		
		function choose(){
			var oBox = $(".chooseJS");
			oBox.each(function(){
				change(this);
				})
			function change(obj){
				var box = $(obj);
				var oLi = $("li",box);
                $(oLi[0]).addClass("clicks");
				oLi.click(function(){
					var index = oLi.index(this);
					$(oLi[index]).addClass("clicks").siblings().removeClass("clicks");

					var url=window.location.href;
					var index = url.indexOf("?");
					if(index>0){
						url=url.substring(0,index);
					}
					//url=url.match(/(.*?)\?/)[1];
					url=url+"?"+getSearchString()+"search=union";
					window.location.href=url;
				})
			}
		}
		choose();

		function getQueryString(name){
			var reg=new  RegExp("(^|&)"+name+"=([^&]*)(&|$)");
		    var r =window.location.search.substr(1).match(reg);
		    if(r!=null)
		    	return  unescape(r[2]);
		    return  null;
		}

		var searchNames=["sType","sApr","sLimit","sAccount"];
		for(var item in searchNames){
			searchDisplay(searchNames[item]);
		}

		function searchDisplay(searchName){
			var sTypeValue=getQueryString(searchName);
			if(sTypeValue==null) sTypeValue="all";
			var sTypeLi=$("#"+searchName).children();
			sTypeLi.each(function(i){
				$(this).removeClass("clicks");
				var value=$(this).attr("data-value");
				if(value==sTypeValue) $(this).addClass("clicks");
			});
		}

		function getSearchString(){
			var searchStr="";
			for(var item in searchNames){
				var searchName=searchNames[item];
				var sTypeLi=$("#"+searchName).children();
				sTypeLi.each(function(i){
					if($(sTypeLi[i]).hasClass("clicks")){
						searchStr+=searchName+"="+$(sTypeLi[i]).attr("data-value")+"&";
					}

				})
			}
			return searchStr;
		}

		function Tab(obj,childLi,childBox){
        var box = $(obj);
        var oLi = $(childLi,box);
        var oDiv = $(childBox , box);
        oLi.mouseenter(function(){
            var index = oLi.index(this);
            $(oLi[index]).addClass("hover").siblings().removeClass("hover");
            oDiv.hide();
            $(oDiv[index]).show();
        })
    }
   	 Tab(".lz-tab","li",".lz-tabtxt");
	 
	 //tab切换 鼠标移动
	 
   	function Tab2(obj,childLi,childBox){
        var box = $(obj);
        var oLi = $(childLi,box);
        var oDiv = $(childBox , box);
        oLi.click(function(){
            var index = oLi.index(this);
            $(oLi[index]).addClass("active").siblings().removeClass("active");
            oDiv.hide();
            $(oDiv[index]).show();
        })
    }
   	 Tab2(".fb-tab","#tab li","#myTabContent1 .list_1 ");
	 Tab2(".investdetail","#tab li","#myTabContent1 .tab-pane");
	 Tab2("#detail-tab","#tab li","#myTabContent .tab-pane");
	 Tab2(".safe-tab",".tab-ul li",".tab-content");
	 Tab2("#fancyboxs","#tab li","#myTabContent .list-tab-con");
	 Tab2(".main-right-jsq",".jsqul li",".jsqbox");
	 //tab切换 点击
	 
	 $(".s-content .details").each(function(){
			$(this).mouseenter(function(){
				$(this).addClass("detailhover").siblings().removeClass("detailhover");	
			}).mouseleave(function(){
				$(".s-content .details").removeClass("detailhover");
				})	 	
	 })

});	
	
	
	
	jQuery(function(){ 
		var $ = jQuery;
		$(window).scroll(function (){
			var offsetTop = $(window).scrollTop() + 180 +"px";
			$("#floatBtn").animate({top : offsetTop},{duration:500 , queue:false});
			});
	});	
	
	(function($){
		$.fn.extend({
			fadeSlide:function(options){
				var defaults = {
					slideTime:"3000"
					};
				var options = $.extend(defaults,options);
				this.each(function(){
					var o = options;
					var obj = $(this);
					var oLi = $(".fade-ul li",obj);
					var oDiv = $(".fade-content div",obj);
					var Timer;
					var index = 1;
					var leng = (oLi.length);
					$(oDiv[0]).css({"display":"block"})
					oLi.mouseover(function(){
						index = oLi.index(this);
						showImg(index);
						}).eq(0).mouseover();
					obj.hover(function(){
							clearInterval(Timer);
						},function(){
						Timer = setInterval(function(){
								showImg(index);
								index++;
								if(index==leng)
								{
									index=0;	
								}
							},o.slideTime)	
					}).trigger("mouseleave");
					function showImg(index){
						$(oDiv[index]).fadeIn("slow").show().siblings().fadeOut().hide();
						$(oLi).removeClass("hover").eq(index).addClass("hover");
					}	
				});
			}
		});
	})(jQuery);	
	/*(function($){
        $.fn.extend({
            ajaxDate:function(options){
                var defaults = {
					J_url:"/article/list.html?",
                    retType:"json",
					code:"web-notice",
					page:"1",
					pageRow:"6"
                };
                 var options = $.extend(defaults,options);
                 this.each(function(){
                    var o = options;
                    var obj = $(this);
                    var jSon_url = o.J_url+"retType"+"="+o.retType+"&"+"code"+"="+o.code+"&"+"page"+"="+o.page+"&"+"pageRow"+"="+o.pageRow;
					$.ajax({url:jSon_url,dataType:"json",
						error:function(result){
							console.log(1);
							},
						success:function(result){
							articleAjax(result,obj);
						}
					});
					function articleAjax(result,obj){
						console.log(result);
						var box = $(obj);
						var str="";
						var jsonDate = (result.data.list);
						
						var jsonLen = jsonDate.length;
						for(var i = 0;i<jsonLen; i++)
						{
							str += "<li><a target='_blank' href="+'article/detail.html?code='+o.code+jsonDate[i].jumpurl+'&id='+jsonDate[i].id+">"+jsonDate[i].name+"</li>";	
						}
						 box.html(str);	
						
					};
                });
            }
        })
    })(jQuery);*/
	//ajax加载数据的代码
	//若是没有内容 则显示为空
	/*var xmlHttp;
	function create(){
		if (window.ActiveXObject) {    
       		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");    
    	}    
    	else if (window.XMLHttpRequest) {    
        	xmlHttp = new XMLHttpRequest();    
   		}    
	}
	function runAjax(id,page,pageRow){
	 create();
	 var URL = "http://yjf.wdaics.com/article/list.html?retType=json&code="+id+"&page="+page+"&pageRow="+pageRow+"";
	 xmlHttp.open("POST",URL,true);
	 xmlHttp.onreadystatechange=callback;
	 xmlHttp.send(null);
	}
	function callback(){
	 if(xmlHttp.readyState == 4){
	  if(xmlHttp.status == 200){
	   var val = xmlHttp.responseText;
	   var json = eval('('+val+')');
	   var jsonDate = json.data.list
	   var jsonLen = jsonDate.length;
	   var str="";
		for(var i = 0;i<jsonLen; i++)
		{
			str += "<li><a target='_blank' href="+'article/detail.html?code=bbstxt'+jsonDate[i].jumpurl+'&id='+jsonDate[i].id+">"+jsonDate[i].name+"</li>";	
		}
		
		 $("#investtxt").html(str);
	  }
	 }
	}
	runAjax();*/
	(function($){
        $.fn.extend({
            ajaxDate:function(options){
                var defaults = {
					J_url:"/article/list.html?",
                    retType:"json",
					code:"web-notice",
					page:"1",
					pageRow:"6"
                };
                 var options = $.extend(defaults,options);
                 this.each(function(){
                    var o = options;
                    var obj = $(this);
					var xmlHttp;
					function create(){
						if (window.ActiveXObject) {    
							xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");    
						}    
						else if (window.XMLHttpRequest) {    
							xmlHttp = new XMLHttpRequest();    
						}    
					}
					function runAjax(){
					 create();
					 var URL = o.J_url+"retType"+"="+o.retType+"&"+"code"+"="+o.code+"&"+"page"+"="+o.page+"&"+"pageRow"+"="+o.pageRow;
					 xmlHttp.open("POST",URL,true);
					 xmlHttp.onreadystatechange=callback;
					 xmlHttp.send(null);
					}
					function callback(){
					 if(xmlHttp.readyState == 4){
					 if(xmlHttp.status == 200){
					   	var val = xmlHttp.responseText;
					   	var json = eval('('+val+')');
					   	var jsonDate = json.data.list
					   	var jsonLen = jsonDate.length;
					   	var str="";
							for(var i = 0;i<jsonLen; i++)
							{
								str += "<li><a target='_blank' href="+'article/detail.html?code='+o.code+jsonDate[i].jumpurl+'&id='+jsonDate[i].id+">"+jsonDate[i].name+"</li>";	
							}
						 $(obj).html(str);
					  }
					 }
					}
					runAjax();
                });
            }
        })
    })(jQuery);
	
	$(function(){
		function articleNone(obj){
			var Timer;
			Timer = setTimeout(function(){
				var box = $(obj);
				var text = ($.trim(box.text()));
				if(text=="")
				{
					str="<li>无文章内容</li>";
					box.html(str)	
				}
			},800)
			
		}
		articleNone("#web-notice");	
		articleNone("#successtxt");
		articleNone("#investtxt");
		articleNone("#questiontxt");
		articleNone("#bbstxt");
		articleNone("#hyxw");
	});
	(function($){
        $.fn.extend({
            slideFn:function(options){
                var defaults = {
                    isTop:true,
                    slideTimer:"3000"
                };
                var options = $.extend(defaults,options);
                this.each(function(){
                    var o = options;
                    var obj = $(this);
                    var oUl = obj.find("ul:first");
                    var oLi = $("li",oUl);
                    var Timer;
                    obj.hover(function(){
                        clearInterval(Timer);
                    },function(){
                        Timer = setInterval(function(){
                            if(o.isTop==true){
                                slideTop(oUl);
                            }else{
                                slideLeft(oUl);
                            }
                        }, o.slideTimer )
                    }).trigger("mouseleave");

                    var slideTop = function(box){
						/*if(oLi.length>2)
						{
							obj.css("height","194px");	
						}
						else if(oLi.length>0&&oLi.lenght<3){
							var boxHeight = (oLi.length)*(oLi.height());
							obj.css("height",boxHeight+"px");
						}
						
						else{
							obj.css("height","31px");
							}*/
                        var oLiHeight = box.find("li:first").height();
                        box.animate({"marginTop":-oLiHeight+"px"},800,function(){
                            box.css("marginTop",0).find("li:first").appendTo(box);
                        })
                    };//上滚
                    var slideLeft = function(box2){
                        box2.css("width",((oLi.width())*(oLi.length))+"px");
                        var oLiWidth = box2.find("li:first").width();
                        box2.animate({"marginLeft":-oLiWidth+"px"},800,function(){
                            box2.css("marginLeft",0).find("li:first").appendTo(box2);
                        })
                    };//左滚
                })
            }

        })
    })(jQuery);//上下滚动
	
	$(function(){
	   $("[onclick='change_picktime()']").each(function(){
        this.className = "js-datetime";
		})
		
	jQuery(function(){  
		$.datepicker.regional['zh-CN'] = {  
		  clearText: '清除',  
		  clearStatus: '清除已选日期',  
		  closeText: '关闭',  
		  closeStatus: '不改变当前选择',  
		  prevText: '<上月',  
		  prevStatus: '显示上月',  
		  prevBigText: '<<',  
		  prevBigStatus: '显示上一年',  
		  nextText: '下月>',  
		  nextStatus: '显示下月',  
		  nextBigText: '>>',  
		  nextBigStatus: '显示下一年',  
		  currentText: '今天',  
		  currentStatus: '显示本月',  
		  monthNames: ['一月','二月','三月','四月','五月','六月', '七月','八月','九月','十月','十一月','十二月'],  
		  monthNamesShort: ['一','二','三','四','五','六', '七','八','九','十','十一','十二'],  
		  monthStatus: '选择月份',  
		  yearStatus: '选择年份',  
		  weekHeader: '周',  
		  weekStatus: '年内周次',  
		  dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],  
		  dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],  
		  dayNamesMin: ['日','一','二','三','四','五','六'],  
		  dayStatus: '设置 DD 为一周起始',  
		  dateStatus: '选择 m月 d日, DD',  
		  dateFormat: 'yy-mm-dd',  
		  firstDay: 1,  
		  initStatus: '请选择日期',  
		  isRTL: false  
		};  
    $.datepicker.setDefaults($.datepicker.regional['zh-CN']);  
    $('#datepicker').datepicker({changeMonth:true,changeYear:true});  
  });  
		$('.js-datetime').datepicker({yearRange:"1900:2090",timeFormat: 'yy-mm-dd',changeYear:true,changeMonth:true});
	
	})
	
	//显示年月日时分秒
	$(function(){
	   $("[onclick='change_pickdate_time()']").each(function(){
	    this.className = "js-datetime";
		})
		
		jQuery(function(){  
			$.datepicker.regional['zh-CN'] = {  
			  clearText: '清除',  
			  clearStatus: '清除已选日期',  
			  closeText: '关闭',  
			  closeStatus: '不改变当前选择',  
			  prevText: '<上月',  
			  prevStatus: '显示上月',  
			  prevBigText: '<<',  
			  prevBigStatus: '显示上一年',  
			  nextText: '下月>',  
			  nextStatus: '显示下月',  
			  nextBigText: '>>',  
			  nextBigStatus: '显示下一年',  
			  currentText: '今天',  
			  currentStatus: '显示本月',  
			  monthNames: ['一月','二月','三月','四月','五月','六月', '七月','八月','九月','十月','十一月','十二月'],  
			  monthNamesShort: ['一','二','三','四','五','六', '七','八','九','十','十一','十二'],  
			  monthStatus: '选择月份',  
			  yearStatus: '选择年份',  
			  weekHeader: '周',  
			  weekStatus: '年内周次',  
			  dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],  
			  dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],  
			  dayNamesMin: ['日','一','二','三','四','五','六'],  
			  dayStatus: '设置 DD 为一周起始',  
			  dateStatus: '选择 m月 d日, DD',  
			  dateFormat: 'yy-mm-dd',  
			  firstDay: 1,  
			  initStatus: '请选择日期',  
			  isRTL: false  
			};  
	    $.datepicker.setDefaults($.datepicker.regional['zh-CN']);  
	    $('#datepicker').datepicker({changeMonth:true,changeYear:true});  
	  });  
			$('.js-datetime').datetimepicker({yearRange:"1900:2090",showSecond: true,timeFormat: 'hh:mm:ss'});
		
	})
	
	
	$(function(){
	function personNum(){ 
		var test=function(idcard){ 
			var Errors=new Array("验证通过!","身份证号码位数不对!","身份证号码出生日期超出范围或含有非法字符!","身份证号码校验错误!","身份证地区非法!"); 
			var area={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"xinjiang",71:"台湾",81:"香港",82:"澳门",91:"国外"} 
			var idcard,Y,JYM; 
			var S,M; 
			var idcard_array = new Array(); 
			var idcard_array = idcard.split("");
			
			if(area[parseInt(idcard.substr(0,2))]==null) return Errors[4]; 
			switch(idcard.length){ 
			case 15: 
			if ((parseInt(idcard.substr(6,2))+1900) % 4 == 0 || ((parseInt(idcard.substr(6,2))+1900) % 100 == 0 && (parseInt(idcard.substr(6,2))+1900) % 4 == 0 )){ 
			ereg = /^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$/;//测试出生日期的合法性 
			} 
			else{ 
			ereg = /^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$/;//测试出生日期的合法性 
			} 
			if(ereg.test(idcard)) 
				return Errors[0]; 
			else 
				return Errors[2]; 
			break; 
			case 18: 
			if( parseInt(idcard.substr(6,4)) % 4 == 0 || ( parseInt(idcard.substr(6,4)) % 100 == 0 && parseInt(idcard.substr(6,4))%4 == 0 )){ 
			ereg = /^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$/;//闰年出生日期的合法性正则表达式 
			} 
			else{ 
			ereg = /^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$/; //平年出生日期的合法性正则表达式 
			} 
			if(ereg.test(idcard)){ 
			S = (parseInt(idcard_array[0]) + parseInt(idcard_array[10])) * 7 + (parseInt(idcard_array[1]) + parseInt(idcard_array[11])) * 9 + (parseInt(idcard_array[2]) + parseInt(idcard_array[12])) * 10 + (parseInt(idcard_array[3]) + parseInt(idcard_array[13])) * 5 + (parseInt(idcard_array[4]) + parseInt(idcard_array[14])) * 8 + (parseInt(idcard_array[5]) + parseInt(idcard_array[15])) * 4 + (parseInt(idcard_array[6]) + parseInt(idcard_array[16])) * 2 + parseInt(idcard_array[7]) * 1 + parseInt(idcard_array[8]) * 6 + parseInt(idcard_array[9]) * 3 ; 
			Y = S % 11; 
			M = "F"; 
			JYM = "10X98765432"; 
			M = JYM.substr(Y,1); 
			if(M == idcard_array[17]) 
			return Errors[0]; 
			else 
			return Errors[3]; 
			} 
			else 
			return Errors[2]; 
			break; 
			default: 
			return Errors[1]; 
			break; 
			} 
		} 
		//测试代码 
		var value = $(".sfzbox").val();
		test(value) 
		$(".sfzbox").text(test(value)); 
	}
	$(".sfzbox").blur(function(){
		personNum();	
	})
});
$(function(){
		var Timers;
		var box = document.getElementById("notice-content"),
				btnUp=$(".up-arrow"),
				btnDown=$(".down-arrow");
		function doMove(speeds){
			var node=box.getElementsByTagName('li'),
					temp = node[0].cloneNode(true),
					temp2=node[node.length-1].cloneNode(true),
					i=0;
			function GoUp(){
				/*speeds>0?i++:i--;*/
				-box.offsetTop>=node[0].offsetHeight?(clearInterval(time),/*node[0].remove()*/box.removeChild(node[0]),box.appendChild(temp),box.style.top=0):(box.style.top=box.offsetTop + speeds + i + 'px');
			}
			function GoDowm(){
				box.offsetTop>=node[0].offsetHeight?(clearInterval(time),/*node[node.length-1].remove()*/box.removeChild(node[node.length-1]),box.insertBefore(temp2,node[0]),box.style.top=0):(box.style.top=box.offsetTop + speeds + i + 'px');
			}
			var time=setInterval(function(){
				speeds<0?GoUp():GoDowm();
			},100);
		}
		btnUp.on('click',function(){
			doMove(-5);
		});
		btnDown.on('click',function(){
			doMove(5);
		})
		$(".noticeOutContent").hover(function(){
				clearInterval(Timers);
			},function(){
				Timers = setInterval(function(){
					doMove(-5);	
				},3000)
			}).trigger("mouseleave");
	});	


	(function($){
        $.fn.extend({
            nav:function(options){
                var defaults = {
                    isTab: true,
                    childLi:".navul",
                    childContent:".navList",
                    hoverClassName:"hover",
                    hasClassName:"hover2"
                };
                var options = $.extend(defaults,options);
                 this.each(function(){
                    var o = options;
                    var obj = $(this);
                    var items = $(o.childLi,obj);
                    var oDiv = $(o.childContent,obj);
                     if(o.isTab){
                         oDiv.eq(0).show(); // tab切换可以使用这句;
                     }
                     var showDiv = function(){
                         items.each(function(i){
                            if($(items[i]).hasClass(o.hasClassName))
                            {
                                $(items).removeClass(o.hoverClassName);
                                $(items[i]).addClass(o.hoverClassName);
                                oDiv.hide();
                                $(oDiv[i]).show();
                            }
                         });
                     };
                     if(o.isTab==true){
                        showDiv();
                     }
                    items.hover(function(){
                        var index = items.index(this);
                        $(items).removeClass(o.hoverClassName);
                        $(items[index]).addClass(o.hoverClassName);
                        $(oDiv).hide();
                        $(oDiv[index]).show();
                    });
                     $(this).mouseleave(function(){
                        if(o.isTab==true){
                            showDiv();
                        }else{
                            oDiv.hide();
                            $(items).removeClass(o.hoverClassName);
                        }
                    });

                })
            }
        })
    })(jQuery);
    $(function(){
        $("#nav").nav({isTab:true,childLi:".navul li",childContent:".navlist"});
      })	

    //标的详情显示----------------------------------------------------------------------------------------------------------------------------
/* ===========================================================
 * bootstrap-tooltip.js v2.3.0
 * http://twitter.github.com/bootstrap/javascript.html#tooltips
 * Inspired by the original jQuery.tipsy by Jason Frame
 * ===========================================================
 * Copyright 2012 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== */


!function ($) {

  "use strict"; // jshint ;_;


 /* TOOLTIP PUBLIC CLASS DEFINITION
  * =============================== */

  var Tooltip = function (element, options) {
    this.init('tooltip', element, options)
  }

  Tooltip.prototype = {

    constructor: Tooltip

  , init: function (type, element, options) {
      var eventIn
        , eventOut
        , triggers
        , trigger
        , i

      this.type = type
      this.$element = $(element)
      this.options = this.getOptions(options)
      this.enabled = true

      triggers = this.options.trigger.split(' ')

      for (i = triggers.length; i--;) {
        trigger = triggers[i]
        if (trigger == 'click') {
          this.$element.on('click.' + this.type, this.options.selector, $.proxy(this.toggle, this))
        } else if (trigger != 'manual') {
          eventIn = trigger == 'hover' ? 'mouseenter' : 'focus'
          eventOut = trigger == 'hover' ? 'mouseleave' : 'blur'
          this.$element.on(eventIn + '.' + this.type, this.options.selector, $.proxy(this.enter, this))
          this.$element.on(eventOut + '.' + this.type, this.options.selector, $.proxy(this.leave, this))
        }
      }

      this.options.selector ?
        (this._options = $.extend({}, this.options, { trigger: 'manual', selector: '' })) :
        this.fixTitle()
    }

  , getOptions: function (options) {
      options = $.extend({}, $.fn[this.type].defaults, this.$element.data(), options)

      if (options.delay && typeof options.delay == 'number') {
        options.delay = {
          show: options.delay
        , hide: options.delay
        }
      }

      return options
    }

  , enter: function (e) {
      var self = $(e.currentTarget)[this.type](this._options).data(this.type)

      if (!self.options.delay || !self.options.delay.show) return self.show()

      clearTimeout(this.timeout)
      self.hoverState = 'in'
      this.timeout = setTimeout(function() {
        if (self.hoverState == 'in') self.show()
      }, self.options.delay.show)
    }

  , leave: function (e) {
      var self = $(e.currentTarget)[this.type](this._options).data(this.type)

      if (this.timeout) clearTimeout(this.timeout)
      if (!self.options.delay || !self.options.delay.hide) return self.hide()

      self.hoverState = 'out'
      this.timeout = setTimeout(function() {
        if (self.hoverState == 'out') self.hide()
      }, self.options.delay.hide)
    }

  , show: function () {
      var $tip
        , pos
        , actualWidth
        , actualHeight
        , placement
        , tp
        , e = $.Event('show')

      if (this.hasContent() && this.enabled) {
        this.$element.trigger(e)
        if (e.isDefaultPrevented()) return
        $tip = this.tip()
        this.setContent()

        if (this.options.animation) {
          $tip.addClass('fade')
        }

        placement = typeof this.options.placement == 'function' ?
          this.options.placement.call(this, $tip[0], this.$element[0]) :
          this.options.placement

        $tip
          .detach()
          .css({ top: 0, left: 0, display: 'block' })

        this.options.container ? $tip.appendTo(this.options.container) : $tip.insertAfter(this.$element)

        pos = this.getPosition()

        actualWidth = $tip[0].offsetWidth
        actualHeight = $tip[0].offsetHeight

        switch (placement) {
          case 'bottom':
            tp = {top: pos.top + pos.height, left: pos.left + pos.width / 2 - actualWidth / 2}
            break
          case 'top':
            tp = {top: pos.top - actualHeight, left: pos.left + pos.width / 2 - actualWidth / 2}
            break
          case 'left':
            tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left - actualWidth}
            break
          case 'right':
            tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left + pos.width}
            break
        }

        this.applyPlacement(tp, placement)
        this.$element.trigger('shown')
      }
    }

  , applyPlacement: function(offset, placement){
      var $tip = this.tip()
        , width = $tip[0].offsetWidth
        , height = $tip[0].offsetHeight
        , actualWidth
        , actualHeight
        , delta
        , replace

      $tip
        .offset(offset)
        .addClass(placement)
        .addClass('in')

      actualWidth = $tip[0].offsetWidth
      actualHeight = $tip[0].offsetHeight

      if (placement == 'top' && actualHeight != height) {
        offset.top = offset.top + height - actualHeight
        replace = true
      }

      if (placement == 'bottom' || placement == 'top') {
        delta = 0

        if (offset.left < 0){
          delta = offset.left * -2
          offset.left = 0
          $tip.offset(offset)
          actualWidth = $tip[0].offsetWidth
          actualHeight = $tip[0].offsetHeight
        }

        this.replaceArrow(delta - width + actualWidth, actualWidth, 'left')
      } else {
        this.replaceArrow(actualHeight - height, actualHeight, 'top')
      }

      if (replace) $tip.offset(offset)
    }

  , replaceArrow: function(delta, dimension, position){
      this
        .arrow()
        .css(position, delta ? (50 * (1 - delta / dimension) + "%") : '')
    }

  , setContent: function () {
      var $tip = this.tip()
        , title = this.getTitle()

      $tip.find('.tooltip-inner')[this.options.html ? 'html' : 'text'](title)
      $tip.removeClass('fade in top bottom left right')
    }

  , hide: function () {
      var that = this
        , $tip = this.tip()
        , e = $.Event('hide')

      this.$element.trigger(e)
      if (e.isDefaultPrevented()) return

      $tip.removeClass('in')

      function removeWithAnimation() {
        var timeout = setTimeout(function () {
          $tip.off($.support.transition.end).detach()
        }, 500)

        $tip.one($.support.transition.end, function () {
          clearTimeout(timeout)
          $tip.detach()
        })
      }

      $.support.transition && this.$tip.hasClass('fade') ?
        removeWithAnimation() :
        $tip.detach()

      this.$element.trigger('hidden')

      return this
    }

  , fixTitle: function () {
      var $e = this.$element
      if ($e.attr('title') || typeof($e.attr('data-original-title')) != 'string') {
        $e.attr('data-original-title', $e.attr('title') || '').attr('title', '')
      }
    }

  , hasContent: function () {
      return this.getTitle()
    }

  , getPosition: function () {
      var el = this.$element[0]
      return $.extend({}, (typeof el.getBoundingClientRect == 'function') ? el.getBoundingClientRect() : {
        width: el.offsetWidth
      , height: el.offsetHeight
      }, this.$element.offset())
    }

  , getTitle: function () {
      var title
        , $e = this.$element
        , o = this.options

      title = $e.attr('data-original-title')
        || (typeof o.title == 'function' ? o.title.call($e[0]) :  o.title)

      return title
    }

  , tip: function () {
      return this.$tip = this.$tip || $(this.options.template)
    }

  , arrow: function(){
      return this.$arrow = this.$arrow || this.tip().find(".tooltip-arrow")
    }

  , validate: function () {
      if (!this.$element[0].parentNode) {
        this.hide()
        this.$element = null
        this.options = null
      }
    }

  , enable: function () {
      this.enabled = true
    }

  , disable: function () {
      this.enabled = false
    }

  , toggleEnabled: function () {
      this.enabled = !this.enabled
    }

  , toggle: function (e) {
      var self = e ? $(e.currentTarget)[this.type](this._options).data(this.type) : this
      self.tip().hasClass('in') ? self.hide() : self.show()
    }

  , destroy: function () {
      this.hide().$element.off('.' + this.type).removeData(this.type)
    }

  }


 /* TOOLTIP PLUGIN DEFINITION
  * ========================= */

  var old = $.fn.tooltip

  $.fn.tooltip = function ( option ) {
    return this.each(function () {
      var $this = $(this)
        , data = $this.data('tooltip')
        , options = typeof option == 'object' && option
      if (!data) $this.data('tooltip', (data = new Tooltip(this, options)))
      if (typeof option == 'string') data[option]()
    })
  }

  $.fn.tooltip.Constructor = Tooltip

  $.fn.tooltip.defaults = {
    animation: true
  , placement: 'top'
  , selector: false
  , template: '<div class="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>'
  , trigger: 'hover focus'
  , title: ''
  , delay: 0
  , html: false
  , container: false
  }


 /* TOOLTIP NO CONFLICT
  * =================== */

  $.fn.tooltip.noConflict = function () {
    $.fn.tooltip = old
    return this
  }

}(window.jQuery);
!function( $ ) {

    "use strict"

    var Popover = function ( element, options ) {
        this.init('popover', element, options)
    }

    /* NOTE: POPOVER EXTENDS BOOTSTRAP-TOOLTIP.js
     ========================================== */

    Popover.prototype = $.extend({}, $.fn.tooltip.Constructor.prototype, {

        constructor: Popover

        , setContent: function () {
            var $tip = this.tip()
                , title = this.getTitle()
                , content = this.getContent()

            $tip.find('.popover-title')[ $.type(title) == 'object' ? 'append' : 'html' ](title)
            $tip.find('.popover-content > *')[ $.type(content) == 'object' ? 'append' : 'html' ](content)

            $tip.removeClass('fade top bottom left right in')
        }

        , hasContent: function () {
            return this.getTitle() || this.getContent()
        }

        , getContent: function () {
            var content
                , $e = this.$element
                , o = this.options

            content = $e.attr('data-content')
                || (typeof o.content == 'function' ? o.content.call($e[0]) :  o.content)

            content = content.toString().replace(/(^\s*|\s*$)/, "")

            return content
        }

        , tip: function() {
            if (!this.$tip) {
                this.$tip = $(this.options.template)
            }
            return this.$tip
        }

    })


    /* POPOVER PLUGIN DEFINITION
     * ======================= */

    $.fn.popover = function ( option ) {
        return this.each(function () {
            var $this = $(this)
                , data = $this.data('popover')
                , options = typeof option == 'object' && option
            if (!data) $this.data('popover', (data = new Popover(this, options)))
            if (typeof option == 'string') data[option]()
        })
    }

    $.fn.popover.Constructor = Popover

    $.fn.popover.defaults = $.extend({} , $.fn.tooltip.defaults, {
        placement: 'right'
        , content: ''
        , template: '<div class="popover"><div class="arrow"></div><div class="popover-inner"><div class="popover-content"><p></p></div></div></div>'
    })
    $("[id^='info']").popover();
}( window.jQuery );

jQuery(function(){
    jQuery("[rel='tooltip']").tooltip();
});

function alertWithUrl(url, title,width,height){
	$.jBox.open("iframe:"+url, title,width,height,{ buttons: { '关闭': true},showScrolling:false,iframeScrolling:'no'});
}

function alertContent(title,content, width, height){
	$.jBox.info(content,title,width,height);
}


