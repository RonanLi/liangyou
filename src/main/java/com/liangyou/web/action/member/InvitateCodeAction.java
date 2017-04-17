package com.liangyou.web.action.member;


import com.liangyou.json.Json;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.context.Global;
import com.liangyou.domain.User;
import com.liangyou.domain.UserInvitateCode;
import com.liangyou.model.SearchParam;
import com.liangyou.service.UserInvitateCodeService;
import com.liangyou.web.action.BaseAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lixiaomin
 * @date 2016-11-14 上午9:35:12
 */
@Namespace("/invitateCode")
@ParentPackage("p2p-default")
public class InvitateCodeAction extends BaseAction {

    @Autowired
    private UserInvitateCodeService userInvitateCodeService;

    /**
     * 我的邀请页面
     *
     * @return
     */
    @Action(value = "myInvitateCode", results = {
            @Result(name = "success", type = "ftl", location = "/gift4.html")
    })
    public String myInvitateCode() {
        User user = this.getSessionUser();
        if (user != null) {
            request.setAttribute("user_id", user.getUserId());
            UserInvitateCode ic = this.userInvitateCodeService.getInvitateCode(SearchParam.getInstance().addParam("user", user));
            if (ic != null) {
                request.setAttribute("invitateCodeURL", Global.getValue("weburl") + "/gift4_1.html?invitateCode=" + ic.getInvitateCode());
                request.setAttribute("realStatus", user.getRealStatus());
            } else {
                request.setAttribute("realStatus", 0);
            }
        }

        return SUCCESS;
    }


    /**
     * 获取我的邀请码
     *
     * @return
     * @throws Exception
     */
    @Action(value = "getMyInvitateCode")
    public String getMyInvitateCode() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        User user = this.getSessionUser();
        if (user != null) {
            dataMap.put("user_id", user.getUserId());
            UserInvitateCode ic = this.userInvitateCodeService.getInvitateCode(SearchParam.getInstance().addParam("user", user));
            if (ic != null) {
                dataMap.put("invitateCodeURL", Global.getValue("weburl") + "/activity/gift4_1.html?invitateCode=" + ic.getInvitateCode());
                dataMap.put("realStatus", user.getRealStatus());
            }else{
                dataMap.put("realStatus", 0);
            }
            Json json = new Json("Login!", dataMap, "");
            super.writeJson(json);
        } else {
            Json json = new Json(Json.CODE_FAILURE, false, "No Login!", dataMap, "");
            super.writeJson(json);
        }
        return null;
    }
}
