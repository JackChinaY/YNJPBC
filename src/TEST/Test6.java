package TEST;

import java.util.*;

public class Test6 {
    public static void main(String[] args) {
        int a = 9;
        int b = 9;
//        System.out.println((a == b));
//        Student c = new Student(1, "jack");
//        Student d = new Student(1, "jack");
//        Student d=new Student(2,"lili");
//        System.out.println((c == d));
//        System.out.println((c.equals(d)));
//        System.out.println(c.hashCode());
//        System.out.println(Integer.toHexString(c.hashCode()));
//        System.out.println(c);
        String e = "1234";
//        System.out.println(e.hashCode());
        System.out.println(1&2);
//        Arrays.sort();
//        LinkedList linkedList=new;
//        HashMap;
//        Collections
    }
}

class Student {
    private int no;
    private String name;

    public Student(int no, String name) {
        this.no = no;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        Student s = (Student) obj;
        return no == s.no && name.equals(s.name);
    }
}