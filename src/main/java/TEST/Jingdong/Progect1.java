package TEST.Jingdong;

import java.util.Scanner;

public class Progect1 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
//        String line = "ABA";
        int count = 0;
        if (isHuiwen(line)) {
            count++;
        }

        for (int i = 0; i < line.length(); i++) {
            if (isHuiwen(replaceIndex(i, line))) {
                count++;
            }
        }
        count = count + line.length();
        System.out.println(count);
    }

    public static boolean isHuiwen(String string) {
        int length = string.length();
        for (int i = 0; i < length / 2; i++) {
            if (string.toCharArray()[i] != string.toCharArray()[length - i - 1]) {
                return false;
            }
        }
        return true;
    }

    public static String replaceIndex(int i, String res) {
        return res.substring(0, i) + res.substring(i + 1);
    }
}