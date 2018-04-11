package TEST.Jingdong;

import java.util.Scanner;
import java.util.Stack;

public class Progect3 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        int N = Integer.parseInt(line);
        String strList[] = new String[N];

        for (int i = 0; i < N; i++) {
            strList[i] = sc.nextLine();
        }
        for (int i = 0; i < strList.length; i++) {
            char[] a = strList[i].toCharArray();
            boolean flag=false;
            for (int j = 0; j < strList[i].length(); j++) {
                for (int k = j + 1; k < strList[i].length(); k++) {
                    char[] b = a.clone();
                    char temp;
                    temp = b[j];
                    b[j] = b[k];
                    b[k] = temp;
                    if (Ismatch(new String(b))) {
                        System.out.println("Yes");
                        flag=true;
                        break;
                    }
                    if (i == strList[i].length() - 1 && i == strList[i].length() - 1) {
                        System.out.println("No");
                    }
                }
                if (flag==true){
                    break;
                }
            }
        }
    }

    public static boolean Ismatch(String str) {
        Stack<String> left = new Stack<String>();
        while (!str.isEmpty()) {
            //取首字母
            String character = str.substring(0, 1);
            //剩余的字符串
            str = str.substring(1);
            if (character.equals("(")) {
                //如果是左括号，则压入栈
                left.push(character);
            } else if (character.equals(")")) {
                //首先检查栈是否为空
                if (left.isEmpty())
                    return false;
                //弹出最后的左括号
                String leftChar = left.pop();
                //检查左右括号是否匹配
                if (character.equals(")")) {
                    if (!leftChar.equals("("))
                        return false;
                }
            }
        }
        return left.isEmpty();
    }
}