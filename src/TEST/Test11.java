package TEST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Test11 {
    /** 请完成下面这个函数，实现题目要求的功能 **/
    /**
     * 当然，你也可以不按照这个模板来作答，完全按照自己的想法来 ^-^
     **/
    public static void main(String[] args) {

        List<Integer> order = new ArrayList<Integer>();
        Map<String, List<Integer>> boms = new HashMap<String, List<Integer>>();

        Scanner in = new Scanner(System.in);
        String line = in.nextLine();

        //订单中商品的个数
        Integer n = Integer.parseInt(line.split(",")[0]);
        //
        Integer m = Integer.parseInt(line.split(",")[1]);

        line = in.nextLine();
        String[] itemCnt = line.split(",");
        for (int i = 0; i < n; i++) {
            order.add(Integer.parseInt(itemCnt[i]));
        }

        for (int i = 0; i < m; i++) {
            line = in.nextLine();
            String[] bomInput = line.split(",");
            List<Integer> bomDetail = new ArrayList<Integer>();

            for (int j = 1; j <= n; j++) {
                bomDetail.add(Integer.parseInt(bomInput[j]));
            }
            boms.put(bomInput[0], bomDetail);
        }
        in.close();

        Map<String, Integer> res = resolve(order, boms);

        System.out.println("match result:");
        for (String key : res.keySet()) {
            System.out.println(key + "*" + res.get(key));
        }
    }

    // write your code here
    public static Map<String, Integer> resolve(List<Integer> order, Map<String, List<Integer>> boms) {
        Map<String, Integer> reslut = new HashMap<String, Integer>();
        //temp为比较结果
        int[][] temp = new int[boms.size()][order.size()];
        int j=0;
        for (Map.Entry<String, List<Integer>> entry : boms.entrySet()) {
            for (int i = 0; i < order.size(); i++) {
                if ((order.get(i) - entry.getValue().get(i)) < 0) {
                    break;
                } else {
                    temp[j][i] = order.get(i) - entry.getValue().get(i);
                }
            }
            j++;
        }
        return reslut;
    }
}
