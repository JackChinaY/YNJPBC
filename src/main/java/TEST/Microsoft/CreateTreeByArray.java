package TEST.Microsoft;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 二叉树操作
 */
//通过先序方式将数组依次添加到二叉树
public class CreateTreeByArray {
    public static class Node {
        Node left = null;  //左子树
        Node right = null; //右子树
        int data;      //数据域

        //初始化节点
        public Node(int data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }

        public Node() {

        }
    }

    private Node root = null; //根节点
    private List<Node> list = null; //节点列表，用于将数组元素转化为节点

    public Node getRoot() {
        return this.root;
    }

    //将将数组转化为一颗二叉树，转换规则：依次为节点列表中节点的左右孩子赋值。左孩子为i*2+1,右孩子为i*2+2
//    @SuppressWarnings("unchecked")
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
        } catch (Exception e) {
//                 e.printStackTrace();
//            System.out.println(" -> null");
            System.out.println();
        } finally {
//            System.out.println();
        }

    }

    //先序遍历二叉树
    public void Indorder(Node root) {
        if (root == null) {
            return;
        }
        System.out.print(root.data + " ");
        Indorder(root.left);  //递归输出左子树
        Indorder(root.right); //递归遍历右子树
    }

    //中序遍历二叉树
    public void inOrderTraverse(Node root) {
        if (root == null) {
            return;
        }
        inOrderTraverse(root.left);  //遍历左子树
        System.out.print(root.data + " ");
        inOrderTraverse(root.right); //遍历右子树
    }

    //后序遍历
    public void postOrderTraverse(Node root) {
        if (root == null) {
            return;
        }
        postOrderTraverse(root.left);  //遍历左子数节点
        postOrderTraverse(root.right); //遍历右子树节点
        System.out.print(root.data + " "); //从下往上遍历
    }
    //实现广度遍历需要的队列
    private LinkedList<Node> queue = new LinkedList<>();
    //第n层最右节点的标志
    private boolean leftMost = false;

    public boolean processChild(Node child) {
        if (child != null) {
            if (!leftMost) {
                queue.addLast(child);
            } else {
                return false;
            }
        } else {
            leftMost = true;
        }
        return true;
    }

    public boolean isCompleteTree(Node root) {
        //空树也是完全二叉树
        if (root == null) return true;
        //首先根节点入队列
        queue.addLast(root);
        while (!queue.isEmpty()) {
            Node node = queue.removeFirst();
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

    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        CreateTreeByArray createTreeByArray = new CreateTreeByArray();
        int m = 21;
        int[] arrays = new int[m];
        for (int i = 0; i < m; i++) {
            arrays[i] = i + 1;
        }
        createTreeByArray.createTreeByArray(arrays);
//        System.out.println("===============================");
//        System.out.print("先序遍历:");
//        createTreeByArray.Indorder(createTreeByArray.list.get(0));
//        System.out.println();
//        System.out.print("中序遍历:");
//        createTreeByArray.inOrderTraverse(createTreeByArray.list.get(0));
//        System.out.println();
//        System.out.print("后序遍历:");
//        createTreeByArray.postOrderTraverse(createTreeByArray.list.get(0));
//        System.out.println();
        if (createTreeByArray.isCompleteTree(createTreeByArray.root)){
            System.out.println("完全二叉树");
        }else {
            System.out.println("非完全二叉树");
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - begin) + "ms");
    }

}
