package com.purplespace.commoon;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-22-16:46
 */
@Component
public class RandomWords {

    /**
     * 随机生成常见的汉字
     *
     * @author xuliugen
     */
    public static List<String> getChineseList(Integer wordsNumber){
        List<String> stringsList = new ArrayList<>();
        for (Integer i = 0; i < wordsNumber; i++) {
             stringsList.add(String.valueOf(createRandomChar()));
        }
        return stringsList;
    }
    public static char createRandomChar() {
        String str = "";
        int hightPos; //
        int lowPos;
        Random random = new Random();
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();
        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("错误");
        }
        return str.charAt(0);
    }

    // 这里是种子字母 当然如果需要可以加上数字 已经把大小写区分不明显的去掉了
    public static Character[] words = new Character[]{'a', 'b', 'd', 'e', 'f', 'g', 'h', 'm', 'n', 'q', 'r', 't', 'y',
            'A', 'B', 'D', 'E', 'F', 'G', 'H', 'L', 'M', 'N', 'R', 'T', 'Y'};

    /**
     * 获取随机字母组合
     * begin 最小长度
     * offset 最小长度之后随机增加长度区间
     * @return
     */
    public static String getRandomWords(Integer begin, Integer offset) {
        //创建random 需要一个种子 同样的种子会出现固定顺序的random
        // 突发奇想用了时间戳
        Random random = new Random(System.currentTimeMillis());
        // 计算最终返回长度 这个方法是包左不包右的所以+1
        int i = random.nextInt(offset + 1) + begin;
        // 返回结果预存集合
        List<Character> results = new ArrayList<>();
        while(results.size() < i) {
            // 数组中取出一个随机索引 以及元素
            int index=(int)(Math.random()*words.length);
            results.add(words[index]);
        }
        // list 转character数组
        Character[] array = results.toArray(new Character[]{});
        // character 数组转char
        char[] chars = ArrayUtils.toPrimitive(array);
        // 返回结果
        return new String(chars);
    }

}
