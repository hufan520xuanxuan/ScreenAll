package com.wuba.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * java图片压缩工具
 * Created by FanSir on 2017-07-31.
 */

public class PictureChangeSize {

    //按照宽高压缩图片
    public synchronized static BufferedImage disposeImage(BufferedImage image, int new_w, int new_h) {
        int old_w = image.getWidth();
        // 得到源图宽
        int old_h = image.getHeight();
        // 得到源图长
        BufferedImage newImg = null;
        // 判断输入图片的类型
        switch (image.getType()) {
            case 13:
                // png,gifnewImg = new BufferedImage(new_w, new_h,
                // BufferedImage.TYPE_4BYTE_ABGR);
                break;
            default:
                newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
                break;
        }
        Graphics2D g = newImg.createGraphics();
        // 从原图上取颜色绘制新图
        g.drawImage(image, 0, 0, old_w, old_h, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸
        newImg.getGraphics().drawImage(
                image.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0,
                null);
        return newImg;
    }

    //指定长或宽的最大值
    public static BufferedImage compressImage(BufferedImage image, int maxLength) {
        if (null != image) {
            int old_w = image.getWidth();
            // 得到源图宽
            int old_h = image.getHeight();
            // 得到源图长
            int new_w = 0;
            // 新图的宽
            int new_h = 0;
            // 新图的长
            // 根据图片尺寸压缩比得到新图的尺寸
            if (old_w > old_h) {
                // 图片要缩放的比例
                new_w = maxLength;
                new_h = (int) Math.round(old_h * ((float) maxLength / old_w));
            } else {
                new_w = (int) Math.round(old_w * ((float) maxLength / old_h));
                new_h = maxLength;
            }
            BufferedImage im = disposeImage(image, new_w, new_h);
            return im;
        }
        return image;
    }
}
