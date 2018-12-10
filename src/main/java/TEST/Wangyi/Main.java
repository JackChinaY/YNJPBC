package TEST.Wangyi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = br.readLine()) != null) {
            String[] a = line.trim().split(" ");
            int[] ds = new int[3];
            for (int i = 0; i < 3; i++) {
                ds[i] = Integer.parseInt(a[i]);
            }

            System.out.println(ds[2] - 2);
//            for (int i = 0; i < m; i++) {
//                for (int j = 0; j < n; j++) {
//                    if (xw[i] <= temp[j]) {
//                        System.out.println(j + 1);
//                        break;
//                    }
//                }
//            }
//            System.out.println(n + " " + m);
//            System.out.println(Arrays.toString(ds));
//            System.out.println(Arrays.toString(xw));
//            System.out.println(Arrays.toString(temp));
        }
    }
}
