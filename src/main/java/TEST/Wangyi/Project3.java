package TEST.Wangyi;

import java.util.Scanner;

public class Project3 {
    public class Work {
        int Di;//工作难度
        int Pi;//工作报酬
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        int N = Integer.parseInt(line.split(" ")[0]);
        int M = Integer.parseInt(line.split(" ")[1]);
        //存工作难度和报酬
        Work[] workList = new Work[N];
        for (int i = 0; i < N; i++) {
            line = sc.nextLine();
            workList[i].Di = Integer.parseInt(line.split(" ")[0]);
            workList[i].Pi = Integer.parseInt(line.split(" ")[1]);
        }
        //存小伙伴们的能力
        Integer[] abilityList = new Integer[M];
        line = sc.nextLine();
        for (int i = 0; i < M; i++) {
            abilityList[i] = Integer.parseInt(line.split(" ")[i]);
        }
        //存小伙伴们的最终报酬
        Integer[] salaryList = new Integer[M];
        for (int i = 0; i < M; i++) {
            int index = 0;
            int tempAbility = workList[0].Di;
            for (int j = 0; j < N; j++) {
                if (tempAbility <= abilityList[i] && tempAbility - workList[j].Di >= 0) {
                    index = j;
                    tempAbility = workList[j].Di;
                }
            }
            salaryList[i] = workList[index].Pi;
        }
        for (int i = 0; i < M; i++) {
            System.out.println(salaryList[i]);
        }
    }
}