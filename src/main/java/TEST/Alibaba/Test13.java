package TEST.Alibaba;

import java.util.*;

public class Test13 {
    /**
     * 请完成下面这个函数，实现题目要求的功能
     * 当然，你也可以不按照这个模板来作答，完全按照自己的想法来 ^-^
     **/
    public static void main(String[] args) {

        ArrayList<Integer> order = new ArrayList<Integer>();
        Map<String, ArrayList<Integer>> boms = new HashMap<String, ArrayList<Integer>>();
        //订单中商品的个数
        order.add(2);
        order.add(3);
        order.add(1);
        ArrayList<Integer> bomDetail = new ArrayList<Integer>();
        bomDetail.add(2);
        bomDetail.add(1);
        bomDetail.add(1);
        boms.put("bom1", bomDetail);
        ArrayList<Integer> bomDetail2 = new ArrayList<Integer>();
        bomDetail2.add(1);
        bomDetail2.add(1);
        bomDetail2.add(0);
        boms.put("bom2", bomDetail2);
        ArrayList<Integer> bomDetail3 = new ArrayList<Integer>();
        bomDetail3.add(0);
        bomDetail3.add(1);
        bomDetail3.add(1);
        boms.put("bom3", bomDetail3);
//        Scanner in = new Scanner(System.in);
//        String line = in.nextLine();

        //订单中商品的个数
//        Integer n = Integer.parseInt(line.split(",")[0]);
        //
//        Integer m = Integer.parseInt(line.split(",")[1]);

//        line = in.nextLine();
//        String[] itemCnt = line.split(",");
//        for (int i = 0; i < n; i++) {
//            order.add(Integer.parseInt(itemCnt[i]));
//        }

//        for (int i = 0; i < m; i++) {
//            line = in.nextLine();
//            String[] bomInput = line.split(",");
//            List<Integer> bomDetail = new ArrayList<Integer>();
//
//            for (int j = 1; j <= n; j++) {
//                bomDetail.add(Integer.parseInt(bomInput[j]));
//            }
//            boms.put(bomInput[0], bomDetail);
//        }
//        in.close();

        Map<String, Integer> res = resolve(order, boms);

        System.out.println("match result:");
        for (String key : res.keySet()) {
            System.out.println(key + "*" + res.get(key));
        }
    }

    // write your code here
    public static Map<String, Integer> resolve(ArrayList<Integer> order, Map<String, ArrayList<Integer>> boms) {
        Map<String, Integer> reslut = new HashMap<String, Integer>();
        Map<String, ArrayList<Integer>> mid = new HashMap<String, ArrayList<Integer>>();
//        for (int i = 0; i < boms.size(); i++) {
        /**------------------------------------单个组合的情况-------------------------------**/
        //单个组合的情况
        int k = 0;//表示当前主组合的下标
        for (Map.Entry<String, ArrayList<Integer>> entry : boms.entrySet()) {
            int count = 0;//
            int[] boms_count = new int[boms.size()];
            boolean flag = true;
            ArrayList<Integer> order_copy = (ArrayList<Integer>) order.clone();
            while (flag) {
                for (int i = 0; i < order_copy.size(); i++) {
                    //判断订单中各个商品数量再减一次bomi中的数量，如果存在数量为负数，说明该bomi的的个数达最大值
                    if ((order_copy.get(i) - entry.getValue().get(i)) < 0) {
                        flag = false;
                        ArrayList<Integer> midDetail = new ArrayList<Integer>();
                        midDetail.add(count);
                        for (int j = 0; j < boms_count.length; j++) {
                            midDetail.add(boms_count[j]);
                        }
                        for (int j = 0; j < order_copy.size(); j++) {
                            midDetail.add(order_copy.get(j));
                            count += order_copy.get(j);
                        }
                        midDetail.set(0, count);
                        mid.put(UUID.randomUUID().toString(), midDetail);
//                        System.out.print("1第" + (k + 1) + " " + entry.getKey() + "个组合输出：");
                        System.out.print("1,组合输出：");
                        for (int j = 0; j < midDetail.size(); j++) {
                            System.out.print(midDetail.get(j) + ", ");
                        }
                        System.out.println();
                        break;
                    }
                }
                //上述判断不为负数，就进行订单中各个商品数量减一次bomi中各个商品的数量
                if (flag) {
                    for (int i = 0; i < order_copy.size(); i++) {
                        order_copy.set(i, order_copy.get(i) - entry.getValue().get(i));
                    }
                    count++;
                    boms_count[k] = count;
                }
            }
            k++;
        }
        /**------------------------------------两个组合的情况-------------------------------**/
        /**-------------------遍历第1个组合的情况--------------**/
        k = 0;
//        System.out.println();
        for (Map.Entry<String, ArrayList<Integer>> entry : boms.entrySet()) {
            boolean run_flag2 = true;//运行标志位
            int[] boms_count = new int[boms.size()];
            ArrayList<Integer> order_copy = (ArrayList<Integer>) order.clone();
            //对组合1的次数进行遍历
            while (run_flag2) {
                for (int i = 0; i < order_copy.size(); i++) {
                    //判断订单中各个商品数量再减一次bomi中的数量，如果存在数量为负数，说明该bomi的的个数达最大值
                    if ((order_copy.get(i) - entry.getValue().get(i)) < 0) {
//                        ArrayList<Integer> midDetail = new ArrayList<Integer>();
//                        int count = 0;
//                        midDetail.add(count);
//                        for (int j = 0; j < boms_count.length; j++) {
//                            midDetail.add(boms_count[j]);
//                            count += boms_count[j];
//                        }
//                        for (int j = 0; j < order_copy.size(); j++) {
//                            midDetail.add(order_copy.get(j));
//                            count += order_copy.get(j);
//                        }
//                        midDetail.set(0, count);
//                        mid.put(UUID.randomUUID().toString(), midDetail);
//                        System.out.print("2,组合输出：");
//                        for (int j = 0; j < midDetail.size(); j++) {
//                            System.out.print(midDetail.get(j) + ", ");
//                        }
//                        System.out.println();
                        run_flag2 = false;
                        break;
                    }
                }
                //上述判断不为负数，就进行订单中各个商品数量减一次bomi中各个商品的数量
                if (run_flag2) {
                    for (int i = 0; i < order_copy.size(); i++) {
                        order_copy.set(i, order_copy.get(i) - entry.getValue().get(i));
                    }
                    boms_count[k]++;
                    /**-------------------遍历第2个组合的情况--------------**/
                    int kk = k;
                    boolean run_flag3 = false;//用于只遍历自身以后的数据
                    //对组合2的次数进行遍历
                    for (Map.Entry<String, ArrayList<Integer>> entry2 : boms.entrySet()) {
                        if (entry.getKey().equals(entry2.getKey())) {
                            run_flag3 = true;
                        } else if (run_flag3) {
                            //只遍历自身以后的数据
                            kk++;
                            if (kk >= boms.size()) {
                                continue;
                            }
                            boolean run_flag4 = true;
                            ArrayList<Integer> order_copy2 = (ArrayList<Integer>) order_copy.clone();
                            while (run_flag4) {
                                for (int i = 0; i < order_copy2.size(); i++) {
                                    //判断订单中各个商品数量再减一次bomi中的数量，如果存在数量为负数，说明该bomi的的个数达最大值
                                    if ((order_copy2.get(i) - entry2.getValue().get(i)) < 0) {
                                        run_flag4 = false;
                                        ArrayList<Integer> midDetail = new ArrayList<Integer>();
                                        int count = 0;
                                        midDetail.add(count);
                                        //在midDetail添加每个组合的个数
                                        for (int j = 0; j < boms_count.length; j++) {
                                            midDetail.add(boms_count[j]);
                                            count += boms_count[j];
                                        }
                                        //在midDetail添加每个商品的剩余个数
                                        for (int j = 0; j < order_copy2.size(); j++) {
                                            midDetail.add(order_copy2.get(j));
                                            count += order_copy2.get(j);
                                        }
                                        midDetail.set(0, count);
                                        mid.put(UUID.randomUUID().toString(), midDetail);
//                                    System.out.print("3第" + (k + 1) + " " + entry2.getKey() + "个组合输出：");
//                                    System.out.println("order_copy1集合：" + order_copy);
//                                    System.out.println("order_copy2集合：" + order_copy2);
                                        System.out.print("3,组合输出：");
                                        for (int j = 0; j < midDetail.size(); j++) {
                                            System.out.print(midDetail.get(j) + ", ");
                                        }
                                        boms_count[kk] = 0;
                                        System.out.println();
                                        break;
                                    }
                                }
                                //上述判断不为负数，就进行订单中各个商品数量减一次bomi中各个商品的数量
                                if (run_flag4) {
                                    for (int i = 0; i < order_copy2.size(); i++) {
                                        order_copy2.set(i, order_copy2.get(i) - entry2.getValue().get(i));
                                    }
                                    boms_count[kk]++;
                                }
                            }
                        }
                    }
                }
            }
            k++;
        }
        /**------------------------------------三个组合的情况-------------------------------**/
        /**-------------------遍历第1个组合的情况--------------**/
        k = 0;
//        System.out.println();
        for (Map.Entry<String, ArrayList<Integer>> entry : boms.entrySet()) {
            boolean run_flag2 = true;//运行标志位
            int[] boms_count = new int[boms.size()];
            ArrayList<Integer> order_copy = (ArrayList<Integer>) order.clone();
            //对组合1的次数进行遍历
            while (run_flag2) {
//                boolean run_flag = true;
                for (int i = 0; i < order_copy.size(); i++) {
                    //判断订单中各个商品数量再减一次bomi中的数量，如果存在数量为负数，说明该bomi的的个数达最大值
                    if ((order_copy.get(i) - entry.getValue().get(i)) < 0) {
//                        run_flag = false;
                        run_flag2 = false;
                        break;
                    }
                }
                //上述判断不为负数，就进行订单中各个商品数量减一次bomi中各个商品的数量
                if (run_flag2) {
                    for (int i = 0; i < order_copy.size(); i++) {
                        order_copy.set(i, order_copy.get(i) - entry.getValue().get(i));
                    }
                    boms_count[k]++;
                    /**-------------------遍历第2个组合的情况--------------**/
                    int kk = k;
                    boolean run_flag3 = false;//用于只遍历自身以后的数据
                    //对组合2的次数进行遍历
                    for (Map.Entry<String, ArrayList<Integer>> entry2 : boms.entrySet()) {
                        if (entry.getKey().equals(entry2.getKey())) {
                            run_flag3 = true;
                        } else if (run_flag3) {
                            //只遍历自身以后的数据
                            kk++;
                            if (kk >= boms.size()) {
                                continue;
                            }
                            boolean run_flag4 = true;
                            ArrayList<Integer> order_copy2 = (ArrayList<Integer>) order_copy.clone();
                            while (run_flag4) {
                                for (int i = 0; i < order_copy2.size(); i++) {
                                    //判断订单中各个商品数量再减一次bomi中的数量，如果存在数量为负数，说明该bomi的的个数达最大值
                                    if ((order_copy2.get(i) - entry2.getValue().get(i)) < 0) {
                                        run_flag4 = false;
                                        boms_count[kk] = 0;
                                        break;
                                    }
                                }
                                //上述判断不为负数，就进行订单中各个商品数量减一次bomi中各个商品的数量
                                if (run_flag4) {
                                    for (int i = 0; i < order_copy2.size(); i++) {
                                        order_copy2.set(i, order_copy2.get(i) - entry2.getValue().get(i));
                                    }
                                    boms_count[kk]++;
                                    /**-------------------遍历第3个组合的情况--------------**/
                                    int kkk = kk;
                                    boolean run_flag5 = false;//用于只遍历自身以后的数据
                                    //对组合2的次数进行遍历
                                    for (Map.Entry<String, ArrayList<Integer>> entry3 : boms.entrySet()) {
                                        if (entry2.getKey().equals(entry3.getKey())) {
                                            run_flag5 = true;
                                        } else if (run_flag5) {
                                            //只遍历自身以后的数据
                                            kkk++;
                                            if (kkk >= boms.size()) {
                                                continue;
                                            }
                                            boolean run_flag6 = true;
                                            ArrayList<Integer> order_copy3 = (ArrayList<Integer>) order_copy2.clone();
                                            while (run_flag6) {
                                                for (int i = 0; i < order_copy3.size(); i++) {
                                                    //判断订单中各个商品数量再减一次bomi中的数量，如果存在数量为负数，说明该bomi的的个数达最大值
                                                    if ((order_copy3.get(i) - entry3.getValue().get(i)) < 0) {
                                                        run_flag6 = false;
                                                        ArrayList<Integer> midDetail = new ArrayList<Integer>();
                                                        int count = 0;
                                                        midDetail.add(count);
                                                        //在midDetail添加每个组合的个数
                                                        for (int j = 0; j < boms_count.length; j++) {
                                                            midDetail.add(boms_count[j]);
                                                            count += boms_count[j];
                                                        }
                                                        //在midDetail添加每个商品的剩余个数
                                                        for (int j = 0; j < order_copy3.size(); j++) {
                                                            midDetail.add(order_copy3.get(j));
                                                            count += order_copy3.get(j);
                                                        }
                                                        midDetail.set(0, count);
                                                        mid.put(UUID.randomUUID().toString(), midDetail);
                                                        System.out.print("4,组合输出：");
                                                        for (int j = 0; j < midDetail.size(); j++) {
                                                            System.out.print(midDetail.get(j) + ", ");
                                                        }
                                                        boms_count[kk] = 0;
                                                        boms_count[kkk] = 0;
                                                        System.out.println();
                                                        break;
                                                    }
                                                }
                                                //上述判断不为负数，就进行订单中各个商品数量减一次bomi中各个商品的数量
                                                if (run_flag6) {
                                                    for (int i = 0; i < order_copy3.size(); i++) {
                                                        order_copy3.set(i, order_copy3.get(i) - entry3.getValue().get(i));
                                                    }
                                                    boms_count[kkk]++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            k++;
        }

        /**--------------------------结果处理-----------------------------**/
        int count_min = 0;//总数最小的值
        boolean flag_temp = true;
        //找出总数最小的值
        for (Map.Entry<String, ArrayList<Integer>> entry : mid.entrySet()) {
            if (flag_temp) {
                count_min = entry.getValue().get(0);
                flag_temp = false;
                continue;
            }
            if (entry.getValue().get(0) < count_min) {
                count_min = entry.getValue().get(0);
            }
        }
        //挑出最小值所在的集合位置
        ArrayList<String> zuhe = new ArrayList<String>();
        for (Map.Entry<String, ArrayList<Integer>> entry : mid.entrySet()) {
            if (flag_temp) {
                count_min = entry.getValue().get(0);
                flag_temp = false;
                continue;
            }
            if (entry.getValue().get(0) == count_min) {
                zuhe.add(entry.getKey());
            }
        }
        //如果存在完全是组合的情况，即不剩下单个商品
        boolean flag_zuhe = false;
        String string_key = null;
        for (int i = 0; i < zuhe.size(); i++) {
            int temp = 0;
            for (int j = order.size() + 1; j < 2 * order.size(); j++) {
                temp += mid.get(zuhe.get(i)).get(j);
            }
            if (temp == 0) {
                string_key = zuhe.get(i);
                break;
            }
        }
        //如果是完全组合
        if (string_key != null) {
            int number = 1;
            for (Map.Entry<String, ArrayList<Integer>> entry : boms.entrySet()) {
                if (mid.get(string_key).get(number) == 0) {
                    continue;
                }
                reslut.put(entry.getKey(), mid.get(string_key).get(number));
                number++;
            }
        }
        return reslut;
    }
}
