package TEST.Microsoft;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 二叉树操作
 */
//通过先序方式将数组依次添加到二叉树
public class BinaryTree {
    /**
     * 节点
     */
    public static class Node {
        Node left;  //左子树
        Node right; //右子树
        int data;      //数据域

        public Node() {
            this.left = null;
            this.right = null;
        }

        //初始化节点
        public Node(int data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }


    }

    private Node root = null; //根节点
    private List<Node> list = null; //节点列表，用于将数组元素转化为节点

    /**
     * 创建一个二叉树
     * 将将数组转化为一颗二叉树，转换规则：依次为节点列表中节点的左右孩子赋值。左孩子为i*2+1,右孩子为i*2+2
     */
    public void createTreeByArray(int[] array) {
        this.list = new ArrayList<>();
        //将数组添加到节点列表
        for (int i = 0; i < array.length; i++) {
            list.add(new Node(array[i]));
        }
        System.out.println("头节点->" + list.get(0).data);
        this.root = new Node(list.get(0).data); //根节点
        //为二叉树指针赋值
        try {
            for (int j = 0; j < (list.size() / 2); j++) {
                //为左子树赋值  j*2+1
                list.get(j).left = list.get(j * 2 + 1);
//                System.out.print("节点" + list.get(j).data + "的左右子树： " + list.get(j * 2 + 1).data + "<- " + list.get(j).data);
                //为右子树赋值 j*2+2
                list.get(j).right = list.get(j * 2 + 2);
//                System.out.println(" ->" + list.get(j * 2 + 2).data);
            }
//            list.get(4).left = null;
            list.get(7).left = new Node(12);
        } catch (Exception e) {
//                 e.printStackTrace();
//            System.out.println(" -> null");
            System.out.println();
        } finally {
//            System.out.println();
        }

    }

    /**
     * 先序遍历（NLR）
     */
    public void NLR(Node root) {
        if (root == null) {
            return;
        }
        System.out.print(root.data + " ");
        NLR(root.left);  //递归输出左子树
        NLR(root.right); //递归遍历右子树
    }

    /**
     * 中序遍历（LNR）
     */
    public void LNR(Node root) {
        if (root == null) {
            return;
        }
        LNR(root.left);  //遍历左子树
        System.out.print(root.data + " ");
        LNR(root.right); //遍历右子树
    }

    /**
     * 后序遍历(LRN)
     */
    public void LRN(Node root) {
        if (root == null) {
            return;
        }
        LRN(root.left);  //遍历左子数节点
        LRN(root.right); //遍历右子树节点
        System.out.print(root.data + " "); //从下往上遍历
    }

    /**
     * -------判断一个二叉树是否为完全二叉树------
     */
    //实现广度遍历需要的队列
    private LinkedList<Node> queue = new LinkedList<>();
    //第n层最右节点的标志
    private boolean leftMost = false;

    /**
     * 判断一个二叉树是否为完全二叉树 使用广度遍历
     * 广度优先英文缩写为BFS即Breadth FirstSearch。其过程检验来说是对每一层节点依次访问，访问完一层进入下一层，而且每个节点只能访问一次
     */
    public boolean isCompleteTree(Node root) {
        //空树也是完全二叉树
        if (root == null) return true;
        //首先根节点入队列
        queue.addLast(root);
        while (!queue.isEmpty()) {
            Node node = queue.removeFirst();
//            System.out.println(node.data);
            //处理左子结点
            if (!processChild(node.left))
                return false;
            //处理右子结点
            if (!processChild(node.right))
                return false;
        }
        //广度优先遍历完毕，此树是完全二叉树
        return true;
    }

    //处理子节点
    private boolean processChild(Node child) {
        if (child == null) {
            leftMost = true;//如果该节点为空，则说明已触及到最后一个节点，至此已经是完全二叉树
            return true;
        } else {
            if (leftMost) {
                return false;//在已经是完全二叉树的情况下，如果还有节点，则说明该二叉树不是完全二叉树
            } else {
                queue.addLast(child);//在还未检测到是完全二叉树的情况下，就将该节点加入队列
                return true;
            }
        }
    }

    /*** -------------------深度优先--------------------*/
    //栈
    private Stack<Node> nodeStack = new Stack<>();

    /**
     * 深度优先（DFS即Depth First Search），使用栈，先进后出
     */
    public void DFS(Node root) {
        nodeStack.push(root);
        while (!nodeStack.isEmpty()) {
            Node node = nodeStack.pop();
            System.out.print(node.data + " ");
            if (node.right != null) {
                nodeStack.push(node.right);
            }
            if (node.left != null) {
                nodeStack.push(node.left);
            }
        }
    }

    /*** --------------------广度优先-----------------*/
    //队列
    private LinkedList<Node> nodeLinkedList = new LinkedList<>();

    /**
     * 广度优先（BFS即Breadth First Search），使用队列，先进后先
     */
    public void BFS(Node root) {
        nodeLinkedList.push(root);
        while (!nodeLinkedList.isEmpty()) {
            Node node = nodeLinkedList.pop();
            System.out.print(node.data + " ");
            if (node.left != null) {
                nodeLinkedList.add(node.left);
            }
            if (node.right != null) {
                nodeLinkedList.add(node.right);
            }
        }
    }

    /**
     * 判断二叉树是否是平衡二叉树，使用后序遍历的思想，每个分支中，最底层是0，依次往上+1，同层判断Math.abs(left - right) > 1，如果大于1.则不是平衡二叉树
     */
    public boolean isBalanceTree(Node node) {
        if (maxDeepBlance(node) == -1) {
            return false;
        } else {
            return true;
        }
    }

    private int maxDeepBlance(Node node) {
        if (node == null) {
            return 0;
        }
        int left = maxDeepBlance(node.left);
        int right = maxDeepBlance(node.right);
        if (left == -1 || right == -1 || Math.abs(left - right) > 1) {
            return -1;
        }
        return Math.max(left, right) + 1;
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        BinaryTree createTreeByArray = new BinaryTree();
        int m = 11;
        int[] arrays = new int[m];
        for (int i = 0; i < m; i++) {
            arrays[i] = i + 1;
        }
        createTreeByArray.createTreeByArray(arrays);
        System.out.println("===============================");
        System.out.print("先序遍历:");
        createTreeByArray.NLR(createTreeByArray.list.get(0));
        System.out.println();
        System.out.print("中序遍历:");
        createTreeByArray.LNR(createTreeByArray.list.get(0));
        System.out.println();
        System.out.print("后序遍历:");
        createTreeByArray.LRN(createTreeByArray.list.get(0));
        System.out.println();
        if (createTreeByArray.isCompleteTree(createTreeByArray.list.get(0))) {
            System.out.println("该二叉树是完全二叉树");
        } else {
            System.out.println("该二叉树是非完全二叉树");
        }
        System.out.print("深度优先(DFS)顺序:");
        createTreeByArray.DFS(createTreeByArray.list.get(0));
        System.out.println();
        System.out.print("广度优先(BFS)顺序:");
        createTreeByArray.BFS(createTreeByArray.list.get(0));
        System.out.println();
        if (createTreeByArray.isBalanceTree(createTreeByArray.list.get(0))) {
            System.out.println("该二叉树是平衡二叉树");
        } else {
            System.out.println("该二叉树是非平衡二叉树");
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - begin) + "ms");
    }
}
