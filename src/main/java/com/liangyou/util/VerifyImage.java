package com.liangyou.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 生成验证码工具类.
 * 
 * Copyright (c) 2005-2007
 * Commnet Software Tech  All Rights Reserved.
 * @author josson.jin
 */
public class VerifyImage {
	public String sRand = "";

	/**
	 * 给定范围获得随机颜色
	 * @param fc
	 * @param bc
	 * @return
	 */
	public Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)	fc = 255;
		if (bc > 255)	bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * 生成验证码图
	 * @return
	 */
	public BufferedImage creatImage() {

		// 在内存中创建图象
		int width = 60, height = 20;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		// 获取图形上下
		Graphics g = image.getGraphics();

		// 生成随机
		Random random = new Random();

		// 设定背景
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);

		// 设定字体
		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));

		// 画边
		// g.setColor(new Color());
		// g.drawRect(0,0,width-1,height-1);
		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		// 取随机产生的认证(4位数)
		// String rand = request.getParameter("rand");
		// rand = rand.substring(0,rand.indexOf("."));
		for (int i = 0; i < 4; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
			// 将认证码显示到图象中
			// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(rand, 13 * i + 6, 16);
		}
		// 图象生效
		g.dispose();
		return image;
	}
	
	/**
	 * 获取随机数
	 * @return
	 */
	public String getRandCode() {
		 return sRand;
	}

	public void setRandCode(String rand) {
		sRand = rand;
	}
}