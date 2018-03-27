package TEST.Wangyi;

import java.util.Scanner;

public class Progect2 {
    public class Work {
        int Di;
        int Pi;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        int n = Integer.parseInt(line.split(" ")[0]);
        int k = Integer.parseInt(line.split(" ")[1]);

        int count = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if ((i % j) >= k) {
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
//