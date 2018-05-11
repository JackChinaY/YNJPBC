package TEST.Alibaba;

import java.util.Scanner;

/**
 * 最短路径问题，走格子
 */
public class Test3 {
    public static class Point {
        public int x;
        public int y;
        public String direction = "";

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", direction='" + direction + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        Point beginPoint = new Point();
        Point endPoint = new Point();
        Scanner sc = new Scanner(System.in);

        String line = sc.nextLine();
        String[] begin = line.split(" ");
        beginPoint.x = Integer.parseInt(begin[0]);
        beginPoint.y = Integer.parseInt(begin[1]);
        beginPoint.direction = begin[2];

        line = sc.nextLine();
        String[] end = line.split(" ");
        endPoint.x = Integer.parseInt(end[0]);
        endPoint.y = Integer.parseInt(end[1]);
        endPoint.direction = end[2];
        line = sc.nextLine();
        String[] rc = line.split(" ");
        int r, c;
        r = Integer.parseInt(rc[0]);
        c = Integer.parseInt(rc[1]);
        int[][] map = new int[r][c];

        for (int i = 0; i < r; i++) {
            line = sc.nextLine();
            String[] temp = line.split(" ");
            for (int j = 0; j < c; j++) {
                map[i][j] = Integer.parseInt(temp[j]);
            }
        }

        int count = 0;
        count += endPoint.x - beginPoint.x;
        count += endPoint.y - beginPoint.y;
        boolean flag = true;
//        for (int i = 0; i < r; i++) {
//            for (int j = 0; j < c; j++) {
//                System.out.println(map[i][j]);
//            }
//        }
//        System.out.println(beginPoint.toString());
//        System.out.println(endPoint.toString());
//        System.out.println(map[endPoint.x][endPoint.y]);
        if (map[endPoint.x][endPoint.y] == 1) {
            count = 65535;
        } else if (endPoint.direction != beginPoint.direction) {
            if (endPoint.direction.equals("NORTH") && beginPoint.direction.equals("SOUTH")) {
                count++;
                count++;
            } else if (endPoint.direction.equals("SOUTH") && beginPoint.direction.equals("NORTH")) {
                count++;
                count++;
            } else if (endPoint.direction.equals("EAST") && beginPoint.direction.equals("WEST")) {
                count++;
                count++;
            } else if (endPoint.direction.equals("WEST") && beginPoint.direction.equals("EAST")) {
                count++;
                count++;
            } else {
                count++;
            }
        }
        System.out.println(count);
    }
}
