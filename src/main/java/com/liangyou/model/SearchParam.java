package com.liangyou.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.mapping.Array;

import com.google.common.collect.Lists;
import com.liangyou.model.OrderFilter.OrderType;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.tool.Page;
import com.liangyou.util.StringUtils;

public class SearchParam {
	List <SearchFilter> filters = new ArrayList<SearchFilter>();
	List <OrderFilter> orders = new ArrayList<OrderFilter>();
	List<GroupByFilter> groups = new ArrayList<GroupByFilter>();
	Map <String,Class> joins = new HashMap<String,Class>();
	public Map<String, Object> map = new HashMap<String, Object>();
	public Page page;

	public SearchParam() {
		super();
	}
	/**
	 * 初始化实列
	 * @return
	 */
	public static SearchParam getInstance(){ 
		return new SearchParam();
	}
	public SearchParam addParam(String key,Object value){
		map.put(key, value.toString());
		SearchFilter filter = new SearchFilter(key, Operator.EQ, value);
		filters.add(filter);
		return this;
	}
	public SearchParam addParam(String key,Operator op,Object value){
		if(op!= Operator.NOTEQ){
			map.put(key, value.toString());
		}
		SearchFilter filter = new SearchFilter(key, op, value);
		filters.add(filter);
		return this;
	}
	/**
	 * 添加时间比较
	 * @param key
	 * @param op
	 * @param value
	 * @param times 秒数
	 * @return
	 */
	public SearchParam addParam(String key,Operator op,Object value,long times){
		if(op!= Operator.NOTEQ){
			map.put(key, value.toString());
		}
		SearchFilter filter = new SearchFilter(key, op, value,times);
		filters.add(filter);
		return this;
	}
	
	public SearchParam addJoin(String name,Class clazz){
		joins.put(name,clazz);
		return this;
	}
	public SearchParam addOrder(String name){
		orders.add(new OrderFilter(name));
		return this;
	}
	public SearchParam addOrder(OrderType type,String name){
		orders.add(new OrderFilter(type,name));
		return this;
	}
	//v1.8.0.4_u2 TGPROJECT-310 lx 2014-05-26 start
	public SearchParam addOrder(int order){
		switch (order) {
		case 0:
			orders.add(new OrderFilter(OrderType.DESC, "id"));
			break;
		case 1:
			orders.add(new OrderFilter(OrderType.ASC, "account"));
			break;
		case -1:
			orders.add(new OrderFilter(OrderType.DESC, "account"));
			break;
		case 2:
			orders.add(new OrderFilter(OrderType.ASC, "apr"));
			break;
		case -2:
			orders.add(new OrderFilter(OrderType.DESC, "apr"));
			break;
		case 3:
			orders.add(new OrderFilter(OrderType.ASC, "scales"));
			break;
		case -3:
			orders.add(new OrderFilter(OrderType.DESC, "scales"));
			break;
		case 4:
			orders.add(new OrderFilter(OrderType.ASC, "user.credit.value"));
			break;
		case -4:
			orders.add(new OrderFilter(OrderType.DESC, "user.credit.value"));
			break;
		default:
			orders.add(new OrderFilter(OrderType.DESC, "id"));
		}
		return this;
	}
	//v1.8.0.4_u2 TGPROJECT-310 lx 2014-05-26 end
			
	public SearchParam addGroupBy(String name){
		groups.add(new GroupByFilter(name));
		return this;
	}
	
	public SearchParam addAddFilter(String lkey,Operator lo,Object lvalue
			,String rkey,Operator ro,Object rvalue){
		map.put(lkey+"_Arr", new Object[]{lvalue,rvalue});
		SearchFilter leftFilter = new SearchFilter(lkey, lo, lvalue);
		SearchFilter rightFilter = new SearchFilter(rkey, ro, rvalue);
		SearchFilter expression=new SearchFilter(leftFilter,Operator.AND,rightFilter);
		filters.add(expression);
		return this;
	}
	public SearchParam addAddFilter(String key,Object lvalue,Object rvalue){
		this.addAddFilter(key, Operator.EQ, lvalue, key, Operator.EQ, rvalue);
		return this;
	}
	/*
	public SearchParam addOrFilter(String lkey,Operator lo,Object lvalue
			,String rkey,Operator ro,Object rvalue){
		map.put(lkey+"_Arr", new Object[]{lvalue,rvalue}); //命名规定，数组改成加上_Arr
		SearchFilter leftFilter = new SearchFilter(lkey, lo, lvalue);
		SearchFilter rightFilter = new SearchFilter(rkey, ro, rvalue);
		SearchFilter expression=new SearchFilter(leftFilter,Operator.OR,rightFilter);
		filters.add(expression);
		return this;
	}
	public SearchParam addOrFilter(String key,Object lvalue,Object rvalue){
		this.addOrFilter(key, Operator.EQ, lvalue, key, Operator.EQ, rvalue);
		return this;
	}*/
	
	public SearchParam addOrFilter(List<SearchFilter> list){
		for (SearchFilter searchFilter : list) {
			map.put(searchFilter.fieldName+"", searchFilter.value);
		}
		SearchFilter filter = new SearchFilter(null, Operator.OR, null);
		filter.setListFilters(list);
		filters.add(filter);
		return this;
	}
	
	public SearchParam addOrFilter(String key, Object ...value){
		List<SearchFilter> list = new ArrayList<SearchFilter>();
		for (Object object : value) {
			list.add(new SearchFilter(key, Operator.EQ, object));
		}
		addOrFilter(list);
		return this;
	}
	
	/*
	 * 分页情况
	 */
	public SearchParam addPage(int current){
		addPage(0,current,Page.ROWS);
		return this;
	}
	public SearchParam addPage(int total, int currentPage, int pernum){
		if(currentPage<1) currentPage=1;
		this.page=new Page(total,currentPage,pernum);
		return this;
	}
	public SearchParam addPage(int currentPage, int pernum){
		addPage(0,currentPage,pernum);
		return this;
	}
	public Page getPage() {
		return page;
	}
	
	/**
	 * 将所有的参数信息封装成Map
	 * 将所有的map中封装时key带对象的字段都将对象过滤掉
	 * 例如：user.username转换成username
	 * @return
	 */
	public Map toMap(){
		Map<String, Object> maps= new HashMap<String, Object>();
		 Set<String> keySet = map.keySet();    
		 for (String key : keySet) {
			if(key.contains(".")){
				String[] strArray = key.split("\\.");
				String newKey = strArray[strArray.length -1];
				maps.put(newKey, map.get(key).toString() );
			}else{
				maps.put(key, map.get(key).toString());
			}
		}
		return maps;
	}
	private StringBuffer parseSql(SearchFilter filter){
		StringBuffer sql=new StringBuffer(" and ");
		switch (filter.operator) {
		case EQ:
			if(filter.value instanceof String){
				sql.append(StringUtils.isNull(filter.fieldName)).append("='").append(filter.value+"'");
			}else{
				sql.append(StringUtils.isNull(filter.fieldName)).append("=").append(filter.value);
			}				
			break;
		case NOTEQ:
			if(filter.value instanceof String){
				sql.append(StringUtils.isNull(filter.fieldName)).append("<>'").append(filter.value+"'");
			}else{
				sql.append(StringUtils.isNull(filter.fieldName)).append("<>").append(filter.value);
			}			
			break;
		case LIKE:
			if(filter.value instanceof String){
				sql.append(StringUtils.isNull(filter.fieldName)).append(" like ").append("'%" + filter.value + "%'");
			}else{
				sql.append(StringUtils.isNull(filter.fieldName)).append(" like ").append("%" + filter.value + "%");
			}
			
			break;
		case GT:
			if(filter.value instanceof String){
				sql.append(StringUtils.isNull(filter.fieldName)).append(">'").append(filter.value+"'");
			}else{
				sql.append(StringUtils.isNull(filter.fieldName)).append(">").append(filter.value);
			}
			break;
		case LT:
			if(filter.value instanceof String){
				sql.append(StringUtils.isNull(filter.fieldName)).append("<'").append(filter.value+"'");
			}else{
				sql.append(StringUtils.isNull(filter.fieldName)).append("<").append(filter.value);
			}			
			break;
		case GTE:
			if(filter.value instanceof String){
				sql.append(StringUtils.isNull(filter.fieldName)).append(">='").append(filter.value+"'");
			}else{
				sql.append(StringUtils.isNull(filter.fieldName)).append(">=").append(filter.value);
			}
			
			break;
		case LTE:
			if(filter.value instanceof String){
				sql.append(StringUtils.isNull(filter.fieldName)).append("<='").append(filter.value+"'");
			}else{
				sql.append(StringUtils.isNull(filter.fieldName)).append("<=").append(filter.value);
			}			
			break;
		case AND:
			SearchFilter lfilter=(SearchFilter)filter.fieldName;
			SearchFilter rfilter=(SearchFilter)filter.value;
			StringBuffer lp=parseSql(lfilter);
			StringBuffer rp=parseSql(rfilter);
			sql.append("((").append(lp).append(") and (").append(rp).append("))");
			break;
		case OR:
			lfilter=(SearchFilter)filter.fieldName;
			rfilter=(SearchFilter)filter.value;
			if (lfilter == null || lfilter == null) {
				List<SearchFilter> list = filter.getListFilters();
				sql.append("(");
				for (SearchFilter sf : list) {
					sql.append("(").append(StringUtils.isNull(sf.fieldName)).append(" = ").append(sf.value).append(") or ");
				}
				sql = new StringBuffer(sql.substring(0, sql.length() - 4));
				sql.append(")");
			} else {
				lp=parseSql(lfilter);
				rp=parseSql(rfilter);
				sql.append("((").append(lp).append(") or (").append(rp).append("))");
			}
			break;
		case PROPERTY_EQ:
			sql.append(StringUtils.isNull(filter.fieldName)).append("=").append(filter.value);
			break;
		case PROPERTY_NOTEQ:
			sql.append(StringUtils.isNull(filter.fieldName)).append("<>").append(filter.value);
			break;
		case PROPERTY_GT:
			sql.append(StringUtils.isNull(filter.fieldName)).append(">").append(filter.value);
			break;
		case PROPERTY_LT:
			sql.append(StringUtils.isNull(filter.fieldName)).append("<").append(filter.value);
			break;
		}
		return sql.append(" ");
	}
	private Expression parseExpression(String name,Root root){
		Expression expression=null;
		String property=StringUtils.isNull(name);
		String[] props=property.split("\\.");
		switch(props.length){
			case 1:
				expression=root.get(name);
				break;
			case 2:
				Join join=root.join(props[0],JoinType.LEFT);
				expression=join.get(props[1]);
				break;
			case 3:
				Join join1=root.join(props[0],JoinType.LEFT);
				Join join2=join1.join(props[1],JoinType.LEFT);
				expression=join2.get(props[2]);
				break;
			default:
				throw new IllegalArgumentException("外连接参数异常，长度为"+props.length);
		}
		return expression;
	}
	private Predicate parse(SearchFilter filter,CriteriaBuilder builder,Root root){
		Predicate p=null;
		switch (filter.operator) {
		case EQ:
			Expression expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			p=builder.equal(expression, filter.value);
			break;
		case NOTEQ:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			p=builder.notEqual(expression, filter.value);
			break;
		case LIKE:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			p=builder.like(expression, "%" + filter.value + "%");
			break;
		case GT:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			if(filter.value instanceof Date){
			    p=builder.greaterThanOrEqualTo((Path<Date>)expression, (Date)filter.value);
			}else{
				p=builder.gt(expression, (Number) filter.value);
			}
			break;
		case LT:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			if(filter.value instanceof Date){
			    p=builder.lessThanOrEqualTo((Path<Date>)expression, (Date)filter.value);
			}else{
				p=builder.lt(expression, (Number) filter.value);
			}
			break;
		case GTE:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			if(filter.value instanceof Date){
			    p=builder.greaterThanOrEqualTo((Path<Date>)expression, (Date)filter.value);
			}else{
				p=builder.ge(expression, (Number) filter.value);
			}
			break;
		case LTE:
			expression = parseExpression(StringUtils.isNull(filter.fieldName), root);
			if(filter.value instanceof Date){
			    p=builder.lessThanOrEqualTo((Path<Date>)expression, (Date)filter.value);
			}else{
				p=builder.le(expression, (Number) filter.value);
			}
			break;
		case AND:
			SearchFilter lfilter=(SearchFilter)filter.fieldName;
			SearchFilter rfilter=(SearchFilter)filter.value;
			Expression lp=parse(lfilter, builder, root);
			Expression rp=parse(rfilter, builder, root);
			p=builder.and(lp, rp);
			break;
		case OR:
			List<SearchFilter> list = filter.getListFilters();
			List<Predicate> prediates = new ArrayList<Predicate>();
			for (SearchFilter sf : list) {
				prediates.add(parse(sf, builder, root));
			}
			p=builder.or(prediates.toArray(new Predicate[0]));
			break;
		case PROPERTY_EQ:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			Expression valueExpresssion=root.get(StringUtils.isNull(filter.value));
			p=builder.equal(expression, valueExpresssion);
			break;
		case PROPERTY_NOTEQ:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			valueExpresssion=root.get(StringUtils.isNull(filter.value));
			p=builder.notEqual(expression, valueExpresssion);
			break;
		case PROPERTY_GT:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			valueExpresssion=root.get(StringUtils.isNull(filter.value));
			p=builder.greaterThan(expression, valueExpresssion);
			break;
		case PROPERTY_LT:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			valueExpresssion=root.get(StringUtils.isNull(filter.value));
			p=builder.lessThan(expression, valueExpresssion);
			break;
		case DATE_GT_TIMES:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			Expression time1 = builder.function("UNIX_TIMESTAMP", Long.class, expression);
			valueExpresssion=root.get(StringUtils.isNull(filter.value));
			Expression time2 = builder.function("UNIX_TIMESTAMP", Long.class, valueExpresssion);
			p=builder.greaterThan(builder.diff(time1, time2),filter.times);
			break;
		case DATE_LTE_TIMES:
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
			time1 = builder.function("UNIX_TIMESTAMP", Long.class, expression);
			valueExpresssion=root.get(StringUtils.isNull(filter.value));
		    time2 = builder.function("UNIX_TIMESTAMP", Long.class, valueExpresssion);
			p=builder.lessThanOrEqualTo(builder.diff(time1, time2),filter.times);
			break;
		case ISNOT :
			expression = parseExpression(StringUtils.isNull(filter.fieldName),root);
		    p=builder.isNotNull(expression);
			break;
		}
		return p;
	}
	
	public Predicate[] bySearchFilter(Class entityClass,
			CriteriaBuilder builder,CriteriaQuery cq,Root root) {
		List<Predicate> predicates = Lists.newArrayList();
		for (SearchFilter filter : filters) {
			predicates.add(parse(filter, builder, root));
		}
		return predicates.toArray(new Predicate[]{});
	}
	
	public StringBuffer bySearchSqlFilter() {
		StringBuffer sb=new StringBuffer();
		for (SearchFilter filter : filters) {
			sb.append(parseSql(filter));
		}
		return sb;
	}
	
	// add by gy 2017-01-18 12:02:10
	// 增加分组语句
	public Expression groupBy(CriteriaBuilder builder,Root root) {
		if (groups.size() < 1) return null;
		Expression exp = parseExpression(groups.get(0).getName(), root);
		return exp;
	}
	
	public Order[] orderBy(CriteriaBuilder builder,Root root){
		int size=orders.size();
		if(size<1) return null;
		Order[] orderArray=new Order[size];
		for(int i=0;i<size;i++){
			OrderFilter of=orders.get(i);
			Expression exp=parseExpression(of.getName(),root);
			if(of.getOrder()==OrderType.DESC){
				orderArray[i]=builder.desc(exp);
			}else{
				orderArray[i]=builder.asc(exp);
			}
		}
		return orderArray;
	}
	
	public StringBuffer byOrderSqlFilter() {
		StringBuffer sb=new StringBuffer();
		for (OrderFilter filter : orders) {
			sb.append(parseOrderSql(filter));
		}
		return sb;
	}
	
	private StringBuffer parseOrderSql(OrderFilter filter){
		StringBuffer sql=new StringBuffer(" order by ");
		switch (filter.order) {
			case DESC:
				sql.append(StringUtils.isNull(filter.name)+" desc ");
				break;
			case ASC:
				sql.append(StringUtils.isNull(filter.name)+" asc ");
				break;			
		}
		return sql;
	}
	
	public StringBuffer byGroupBySqlFilter() {
		StringBuffer sb=new StringBuffer();
		for (GroupByFilter filter : groups) {
			sb.append(parseGroupSql(filter));
		}
		return sb;
	}
	
	private StringBuffer parseGroupSql(GroupByFilter filter){
		StringBuffer sql=new StringBuffer(" group by ");
		sql.append(StringUtils.isNull(filter.name));
		return sql;
	}
}
