package TEST.Alibaba;
/**
 * 在阿里巴巴的在线智能客服场景中，会出现服务转交的情况，在转交的时候，我们会进行智能摘要，但是在摘要之前需要对用户说过的话进行去重的处理，字符串去重的规则是：
 * 1：相邻的最大重复串需要去掉； 比如说： 【刚才我说了我要退款我要退款我要退款，我都说了我要退款】  去重之后：【刚才我说了我要退款，我都说了我要退款】
 * 2：重复的字数必须大于1，所以 ：【阿里旺旺】 ，不需要去重
 * 3：数字不能进行处理，所以：【10000】，不需要去重处理
 * 现在需要大家编写一段程序进行去重处理，输出去重之后的字符串
 * 编译器版本: Java 1.8.0_66
 * 请使用标准输入输出(System.in, System.out)；已禁用图形、文件、网络、系统相关的操作，如java.lang.Process , javax.swing.JFrame , Runtime.getRuntime；不要自定义包名称，否则会报错，即不要添加package answer之类的语句；您可以写很多个类，但是必须有一个类名为Main，并且为public属性，并且Main为唯一的public class，Main类的里面必须包含一个名字为'main'的静态方法（函数），这个方法是程序的入口
 * 时间限制: 3S (C/C++以外的语言为: 5 S)   内存限制: 128M (C/C++以外的语言为: 640 M)
 * 输入:
 * 刚才我说了我要退款我要退款我要退款我都说了我要退款
 * 刚才我说了我要退款我要退款我要退款，我都说了我要退款我要退款我要退款
 * 输出:
 * 阿里旺旺
 * 输入范例:
 * 刚才我说了我要退款我都说了我要退款
 * 输出范例:
 * 阿里旺旺
 */

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        char[] temp = line.toCharArray();
        int Di = 0;
        int Pi = 0;
        for (int i = 0; i < temp.length - 2; i++) {
            for (int j = i + 2; j < temp.length; j++) {
                if (temp[i] != '&' && temp[i] == temp[j]) {
                    Di = j - i;
                    for (int k = i; k < i + Di; k++) {
                        if ((k + Di) < temp.length && temp[k] == temp[k + Di]) {
                            Pi++;
                        } else {
                            break;
                        }
                    }
                    if (Di == Pi) {
                        for (int k = i; k < i + Di; k++) {
                            temp[k] = '&';
                        }
                        Di = 0;
                        Pi = 0;
                    }
                    Di = 0;
                    Pi = 0;
                }
            }
        }
        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < temp.length; i++) {
            stringBuilder1.append(temp[i]);
            if (temp[i] != '&') {
                stringBuilder.append(temp[i]);
            }
        }
//        System.out.println(stringBuilder1);
        System.out.println(stringBuilder);
    }
}
