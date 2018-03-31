package TEST.Microsoft;

import java.util.Arrays;

public class Arrangement {
    public static int Arrange(String[] arr) {
        Arrays.sort(arr);
        int flag = -1;
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            count = 0;
            char a = arr[i].charAt(arr[i].length() - 1);
            for (int j = 0; j < arr.length; j++) {
                if (i == j) continue;
                char b = arr[j].charAt(0);
                if (a == b) {
                    a = arr[j].charAt(arr[j].length() - 1);
                    count++;
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
        String[] arr1 = {"abc", "cde", "efg"};
        String[] arr2 = {"abc", "efg", "cde"};
        System.out.println(Arrangement.Arrange(arr1));
        System.out.println(Arrangement.Arrange(arr2));
    }
}
