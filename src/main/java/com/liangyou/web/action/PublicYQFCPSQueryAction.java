package com.liangyou.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.liangyou.dao.BorrowTenderDao;
import com.liangyou.domain.BorrowTender;
import com.liangyou.util.DateUtils;
import com.liangyou.yiqifa.api.advertiser.Order;
import com.liangyou.yiqifa.api.advertiser.OrderStatus;
import com.liangyou.yiqifa.api.advertiser.Product;
import com.liangyou.yiqifa.api.advertiser.QueryOrderServlet;

@Namespace("/public/yqf/cps")
@ParentPackage("p2p-default")
public class PublicYQFCPSQueryAction extends BaseAction {
	private static final Logger logger = Logger.getLogger(PublicYQFCPSQueryAction.class);

	@Autowired
	private BorrowTenderDao borrowTenderDao;

	QueryOrderServlet qos = new QueryOrderServlet() {

		private static final long serialVersionUID = 1L;

		@Override
		public List<Order> queryDatas(String cid, Date startDate, Date endDate) {

			logger.info("startDate:" + DateUtils.dateStr4(startDate));
			logger.info("endDate:" + DateUtils.dateStr4(endDate));

			List<Order> orderList = new ArrayList<Order>();
			List<BorrowTender> btList = borrowTenderDao.getBorrowTenderList(cid, startDate, endDate);

			for (BorrowTender bt : btList) {
				
				String category = "", commissionType = "";
				if (bt.getBorrow().getTimeLimit() == 1) {
					category = "1";
					commissionType = "A";
				}
				if (bt.getBorrow().getTimeLimit() == 3) {
					category = "3";
					commissionType = "B";
				}
				if (bt.getBorrow().getTimeLimit() == 6) {
					category = "6";
					commissionType = "C";
				}
				
				logger.info("亿起发数据查询：BID：" + bt.getBorrow().getId() + ", category：" + category + ", commissionType：" + commissionType);

				Order order = new Order();
				Product product = new Product();
				OrderStatus orderStatus = new OrderStatus();

				order.setOrderNo(bt.getSubOrdId());
				order.setOrderTime(bt.getAddtime());
				order.setCampaignId(Long.parseLong(cid));
				order.setFeedback(bt.getYqfCpsFeedback());
				
				product.setProductNo(String.valueOf(bt.getBorrow().getId()));
				product.setName(bt.getBorrow().getName());
				product.setAmount("1");
				product.setPrice(String.valueOf(bt.getMoney()));
				product.setCategory(category);
				product.setCommissionType(commissionType);

				order.setOrderNo(bt.getSubOrdId());
				order.setOrderTime(bt.getAddtime());
				order.setOrderStatus(orderStatus);
				order.addProduct(product);

				orderList.add(order);
			}

			return orderList;
		}
	};

	@Action(value = "queryOrder")
	public void queryOrder() {
		try {
			qos.service(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
