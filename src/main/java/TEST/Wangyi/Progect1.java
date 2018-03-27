package TEST.Wangyi;

import java.util.*;

public class Progect1 {

    public class Rectangle {
        int x1;
        int x2;
        int y1;
        int y2;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());
        Rectangle[] rectangle = new Rectangle[n];
        //给所有点赋值x1
        String line = sc.nextLine();
        String[] x1List = line.split(" ");
        for (int i = 0; i < n; i++) {
            rectangle[i].x1 = Integer.parseInt(x1List[i]);
        }
        //给所有点赋值x2
        line = sc.nextLine();
        String[] x2List = line.split(" ");
        for (int i = 0; i < n; i++) {
            rectangle[i].x2 = Integer.parseInt(x2List[i]);
        }
        //给所有点赋值y1
        line = sc.nextLine();
        String[] y1List = line.split(" ");
        for (int i = 0; i < n; i++) {
            rectangle[i].y1 = Integer.parseInt(y1List[i]);
        }
        //给所有点赋值y2
        line = sc.nextLine();
        String[] y2List = line.split(" ");
        for (int i = 0; i < n; i++) {
            rectangle[i].y2 = Integer.parseInt(y2List[i]);
        }
        //求相交个数
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 1 + i; j < n; j++) {
                if ((rectangle[j].x1 - rectangle[i].x2) > 0 && (rectangle[j].y1 - rectangle[i].y2) > 0) {
                    count++;
                }
            }
        }
        System.out.println(count);
    }
}

//        Scanner in = new Scanner(System.in);
//        String line = in.nextLine();
//        Integer n = Integer.parseInt(line.split(",")[0]);
//        Integer m = Integer.parseInt(line.split(",")[1]);
//        List<Integer> order = new ArrayList<Integer>();
//        line = in.nextLine();
//        String[] itemCnt = line.split(",");
//        for (int i = 0; i < n; i++) {
//            order.add(Integer.parseInt(itemCnt[i]));
//        }
//        Map<String, Integer> reslut = new HashMap<String, Integer>();
//        //temp为比较结果
//        int[][] temp = new int[2][3];
//        int j = 0;
//        in.close();
