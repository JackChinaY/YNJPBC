package TEST.Microsoft;

import java.util.Arrays;

/**
 * 一个字符串数组中每个元素的首尾字母可以相连就输出1，否则输出-1
 */
public class Arrangement {
    public static int Arrange(String[] arr) {
//        Arrays.sort(arr);
        int flag = -1;
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            String[] arrary = arr.clone();
            count = 0;
            char a = arrary[i].charAt(arrary[i].length() - 1);
            for (int j = 0; j < arrary.length; j++) {
                if (i == j) continue;
                if (arrary[j].equals("")) continue;
                char b = arrary[j].charAt(0);
                if (a == b) {
                    a = arrary[j].charAt(arrary[j].length() - 1);
                    arrary[j] = "";
                    count++;
                    j = 0;
                }
            }
            if (count == arr.length - 1) {
                flag = 1;
                break;
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        String[] arr1 = {"abc", "cde", "efg", "ghi"};
        String[] arr2 = {"abc", "ghi", "efg", "cde"};
        String[] arr3 = {"abc", "efg", "gde", "ohi"};
        System.out.println(Arrangement.Arrange(arr1));
        System.out.println(Arrangement.Arrange(arr2));
        System.out.println(Arrangement.Arrange(arr3));
    }
}
