package TEST.Wangyi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = br.readLine()) != null) {
            int n = Integer.parseInt(line.trim());
            line = br.readLine();
            int[] nums = new int[n];
            String[] s = line.trim().split(" ");
        }
    }
}
