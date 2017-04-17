package com.liangyou.tool;

import java.util.Arrays;

public class StringUtil {
	public static String[] StringSort(String[] str) {
		MyString mySs[] = new MyString[str.length];// 创建自定义排序的数组
		for (int i = 0; i < str.length; i++) {
			mySs[i] = new MyString(str[i]);
		}
		Arrays.sort(mySs);// 排序
		String[] str2 = new String[mySs.length];
		for (int i = 0; i < mySs.length; i++) {
			str2[i] = mySs[i].s;
		}
		return str2;
	}
}


class MyString implements Comparable<MyString> {
	public String s;// 包装String

	public MyString(String s) {
		this.s = s;
	}

	@Override
	public int compareTo(MyString o) {
		if (o == null || o.s == null)
			return 1;
		return s.compareTo(o.s);
	}
}