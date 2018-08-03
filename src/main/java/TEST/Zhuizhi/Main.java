package TEST.Zhuizhi;

import java.util.*;

/**
 * 主函数
 */
public class Main {
    /**
     * 输入：
     * 5 5
     * 1 2 N
     * LMLMLMLMM
     * 3 3 E
     * MMRMMRMRRM
     * 预期产出：
     * 1 3 N
     * 5 1 E
     **/
    static void calculate(List<Car> carList) {
        for (int i = 0; i < carList.size(); i++) {
            for (int j = 0; j < carList.get(i).orders.length; j++) {
                if (carList.get(i).orders[j] == 'L' || carList.get(i).orders[j] == 'R') {
                    carList.get(i).changeDirection(carList.get(i).orders[j]);
                } else if (carList.get(i).orders[j] == 'M') {
                    carList.get(i).move();
                }
            }
            System.out.println(carList.get(i).toString());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String top = scanner.nextLine().trim();
        int max_x = Integer.parseInt(top.split(" ")[0]);
        int max_y = Integer.parseInt(top.split(" ")[1]);
        //可以输入两个数
        int carCount = 2;
        List<Car> carList = new ArrayList<>();
        while (carCount-- > 0) {
            String car_point = scanner.nextLine();
            String car_order = scanner.nextLine();
            Car car = new Car(max_x, max_y, Integer.parseInt(car_point.split(" ")[0]),
                    Integer.parseInt(car_point.split(" ")[1]),
                    car_point.split(" ")[2].charAt(0),
                    car_order);
            carList.add(car);
        }
        calculate(carList);
    }
}

/**
 * 漫游车实体类
 */
class Car {
    //地图最大横坐标值
    int max_x;
    //地图最大纵坐标值
    int max_y;
    //当前漫游车横坐标
    int x;
    //当前漫游车纵坐标
    int y;
    //当前漫游车方向
    char direction;
    //接收的指令字符串
    String order;
    //将指令字符串转换成字符集合
    char[] orders;

    //构造函数
    public Car(int max_x, int max_y, int x, int y, char direction, String order) {
        this.max_x = max_x;
        this.max_y = max_y;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.order = order;
        orders = new char[order.length()];
        for (int i = 0; i < order.length(); i++) {
            orders[i] = order.charAt(i);
        }
    }

    //改变车当前方向
    public void changeDirection(char ord) {
        char result = 'N';
        if (ord == 'L') {
            if (direction == 'N') {
                result = 'W';
            } else if (direction == 'W') {
                result = 'S';
            } else if (direction == 'S') {
                result = 'E';
            } else if (direction == 'E') {
                result = 'N';
            }
            direction = result;
        } else if (ord == 'R') {
            if (direction == 'N') {
                result = 'E';
            } else if (direction == 'E') {
                result = 'S';
            } else if (direction == 'S') {
                result = 'W';
            } else if (direction == 'W') {
                result = 'N';
            }
            direction = result;
        }
    }

    //移动一步
    public void move() {
        if (direction == 'N') {
            if (y + 1 > 5) {
                System.err.println("小车再移动一步将超出纵坐标最大值");
            } else {
                y = y + 1;
            }
        } else if (direction == 'W') {
            if (x - 1 < 0) {
                System.err.println("小车再移动一步将超出横坐标最小值");
            } else {
                x = x - 1;
            }
        } else if (direction == 'S') {
            if (y - 1 < 0) {
                System.err.println("小车再移动一步将超出纵坐标最小值");
            } else {
                y = y - 1;
            }
        } else if (direction == 'E') {
            if (x + 1 > 5) {
                System.err.println("小车再移动一步将超出横坐标最大值");
            } else {
                x = x + 1;
            }
        }
    }

    @Override
    public String toString() {
        return x + " " + y + " " + direction;
    }
}