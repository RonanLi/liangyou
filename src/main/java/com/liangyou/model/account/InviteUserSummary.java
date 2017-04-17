package com.liangyou.model.account;

import com.liangyou.domain.InviteUser;

/**
 * v1.8.0.4 TGPROJECT-267 lx start
 * @author Administrator
 *
 */
public class InviteUserSummary {
		/**
	 * 
	 */
	private static final long serialVersionUID = -2647709029501473511L;
		private int inviteUserId;
		private String tenderedMoney;
		private String username;
		private String inviteUsername;
		private String addtime;
		public String getTenderedMoney() {
			return tenderedMoney;
		}

		public void setTenderedMoney(String tenderedMoney) {
			this.tenderedMoney = tenderedMoney;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getInviteUsername() {
			return inviteUsername;
		}

		public void setInviteUsername(String inviteUsername) {
			this.inviteUsername = inviteUsername;
		}
		public int getInviteUserId() {
			return inviteUserId;
		}

		public void setInviteUserId(int inviteUserId) {
			this.inviteUserId = inviteUserId;
		}

		public String getAddtime() {
			return addtime;
		}

		public void setAddtime(String addtime) {
			this.addtime = addtime;
		}
		
}
