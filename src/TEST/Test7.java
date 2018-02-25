package TEST;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

/**
 * 排序
 */
public class Test7 {
    public static void main(String[] args) {
        int m, n;
        System.out.println("请输入m,n:");
        Scanner reader = new Scanner(System.in);
        m = reader.nextInt();
        n = reader.nextInt();
        Random r = new Random(m);
        Student1[] s1 = new Student1[n];
        for (int i = 0; i < n; i++) {
            s1[i] = new Student1();
            s1[i].id = i + 1;
            s1[i].grad1 = r.nextInt(101);
            s1[i].grad2 = r.nextInt(101);
            s1[i].grad3 = r.nextInt(101);
            s1[i].sum = s1[i].grad1 + s1[i].grad2 + s1[i].grad3;
        }
        Arrays.sort(s1);
        System.out.println("Comparable接口实现的排序：");
        for (int i = 0; i < s1.length; i++) {
            System.out.printf("%5d%5d%5d%5d%5d\n", s1[i].id, s1[i].grad1, s1[i].grad2, s1[i].grad3, s1[i].sum);
        }
        Student2[] s = new Student2[n];
        for (int i = 0; i < n; i++) {
            s[i] = new Student2();
            s[i].id = i + 1;
            s[i].grad1 = r.nextInt(101);
            s[i].grad2 = r.nextInt(101);
            s[i].grad3 = r.nextInt(101);
            s[i].sum = s[i].grad1 + s[i].grad2 + s[i].grad3;
        }
        Arrays.sort(s, new Student2());
        System.out.println("Comparator接口实现的排序：");
        for (int i = 0; i < s.length; i++) {
            System.out.printf("%5d%5d%5d%5d%5d\n", s[i].id, s[i].grad1, s[i].grad2, s[i].grad3, s[i].sum);
        }
    }
}

//实现Comparable接口
class Student1 implements Comparable<Student1> {
    public int id, grad1, grad2, grad3, sum;

    @Override
    public int compareTo(Student1 o) {
        if (this.sum > o.sum) {
            return 1;
        } else if (this.sum < o.sum)
            return -1;
        else
            return 0;
    }
}

//实现Comparator接口
class Student2 implements Comparator<Student2> {
    public int id, grad1, grad2, grad3, sum;

    @Override
    public int compare(Student2 o1, Student2 o2) {
//        return o1.sum - o2.sum;
        return o2.sum - o1.sum;
    }
}


