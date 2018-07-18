package TEST.Alibaba;

import java.util.*;

public class Main {

/** 请完成下面这个函数，实现题目要求的功能 **/
    /**
     * 当然，你也可以不按照这个模板来作答，完全按照自己的想法来 ^-^
     * 输入:
     * 4
     * 2,2
     * 2,8
     * 4,4
     * 7,2
     * 输出:
     * 30
     **/
    static int calculate(String[] locations) {
        int[][] location = new int[locations.length][2];
        for (int i = 0; i < locations.length; i++) {
            location[i][0] = Integer.parseInt(locations[i].split(",")[0]);
            location[i][1] = Integer.parseInt(locations[i].split(",")[1]);
//            System.out.println(location[i][0]);
//            System.out.println(location[i][1]);
        }
        int count = 0;
        count += location[0][0];
        count += location[0][1];
        for (int i = 1; i < locations.length; i++) {
            int x = Math.abs(location[i][0] - location[i - 1][0]);
            int y = Math.abs(location[i][1] - location[i - 1][1]);
            count += x;
            count += y;

        }
        count += location[locations.length - 1][0];
        count += location[locations.length - 1][1];
//        System.out.println(count);
//        System.out.println(locations.length);
//        System.out.println(Arrays.toString(locations));
        return count;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = Integer.parseInt(scanner.nextLine().trim());
        int index = 0;
        String[] locations = new String[num];
        while (num-- > 0) {
            locations[index++] = scanner.nextLine();
        }
        System.out.println(calculate(locations));
    }
}
