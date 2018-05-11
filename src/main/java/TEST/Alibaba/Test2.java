package TEST.Alibaba;

import java.util.Scanner;

/**
 * n表示n个小组，每个小组2个人
 * m表限制条件个数，然后每一行有两个数表示，如1,3，表示1、3客户不能在一组
 * 求组合数，如果有输出yes
 */
public class Test2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());
        int m = Integer.parseInt(sc.nextLine());
        int[][] map = new int[m][2];
//        System.out.println(n + " " + m);
        for (int i = 0; i < m; i++) {
            String line = sc.nextLine();
//            System.out.println(line);
            String[] temp = line.split(",");
            map[i][0] = Integer.parseInt(temp[0]);
            map[i][1] = Integer.parseInt(temp[1]);
        }
        System.out.println("yes");
    }
}
