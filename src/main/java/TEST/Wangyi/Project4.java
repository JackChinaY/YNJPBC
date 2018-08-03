package TEST.Wangyi;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 题目描述
 * 有 n 个学生站成一排，每个学生有一个能力值，牛牛想从这 n 个学生中按照顺序选取 k 名学生，要求相邻两个学生的位置编号的差不超过 d，使得这 k 个学生的能力值的乘积最大，你能返回最大的乘积吗？
 * 输入描述:
 * 每个输入包含 1 个测试用例。每个测试数据的第一行包含一个整数 n (1 <= n <= 50)，表示学生的个数，接下来的一行，包含 n 个整数，按顺序表示每个学生的能力值 ai（-50 <= ai <= 50）。接下来的一行包含两个整数，k 和 d (1 <= k <= 10, 1 <= d <= 50)。
 * 输出描述:
 * 输出一行表示最大的乘积。
 * 示例1
 * 输入
 * 复制
 * 3
 * 7 4 7
 * 2 50
 * 输出
 * 复制
 * 49
 */
public class Project4 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = br.readLine()) != null) {
            int n = Integer.parseInt(line.trim());
            line = br.readLine();
            int[] nums = new int[n];
            String[] s = line.trim().split(" ");
            for (int i = 0; i < n; i++) {
                nums[i] = Integer.parseInt(s[i]);  //a[i]表示学生的能力值
            }
            line = br.readLine();
            String[] s1 = line.trim().split(" ");
            int k = Integer.parseInt(s1[0]);  //选取k个学生
            int d = Integer.parseInt(s1[1]);  //编号之差不超过d
            long[][] max = new long[k][n];
            long[][] min = new long[k][n];
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < n; j++) {
                    max[i][j] = 1;
                    min[i][j] = 1;
                    if (i == 0) {
                        min[i][j] = nums[j];
                        max[i][j] = nums[j];
                    }
                }
            }
            for (int i = 1; i < k; i++) {
                for (int j = 0; j < n; j++) {
                    for (int m = 1; m <= d; m++) {
                        if (j - m >= 0) {
                            if (nums[j] > 0) {
                                min[i][j] = Math.min(min[i][j], min[i - 1][j - m] * nums[j]);
                                max[i][j] = Math.max(max[i][j], max[i - 1][j - m] * nums[j]);
                            } else {
                                min[i][j] = Math.min(min[i][j], max[i - 1][j - m] * nums[j]);
                                max[i][j] = Math.max(max[i][j], min[i - 1][j - m] * nums[j]);
                            }
                        }
                    }
                }
            }
            long res = 0;
            for (int i = 0; i < n; i++) {
                if (i >= k - 1) {
                    res = Math.max(res, max[k - 1][i]);
                }
            }
            System.out.println(res);
        }
    }
}
