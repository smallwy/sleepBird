package Data;


import java.util.AbstractList;

public class ListList1<E> extends AbstractList<E> {


    public static void main(String[] args) {
        ListList1<Integer> list = new ListList1<>();
        list.addLast(1);
        list.addLast(2);
        System.out.println(list);
    }


    transient int size = 0;

    transient Node<E> first;

    private transient Node<E> last;


    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }


    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E set(int index, E element) {
        return super.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        if (index == size) {
            addLast(element);
        } else {
            addBefore(element, node(index));
        }
    }

    /*链表最后面添加元素*/
    public void addLast(E e) {
        final Node<E> lastNode = last;
        final Node<E> newNode = new Node<>(lastNode, e, null);
        //将新节点赋值为最后一个
        last = newNode;
        if (lastNode == null) {
            // //如果是第一个节点 last和fisrt都为newNode
            first = newNode;
        } else {
            //如果不是 第一个就插入在最后一个
            lastNode.next = newNode;
        }
        size++;
    }


    /*链表最前面添加元素  succ是要插入在这个节点前面*/
    public void addBefore(E e, Node<E> succ) {
        final Node<E> preNode = succ.prev;
        final Node<E> newNode = new Node<>(preNode, e, succ);
        succ.prev = newNode;

        //如果插入的是第一个节点
        if (preNode == null) {
            first = newNode;
        } else {
            preNode.next = newNode;
        }
        size++;
    }


    @Override
    public E remove(int index) {
        //要删除的节点
        Node<E> removeNode = node(index);
        Node<E> preNode = removeNode.prev;
        Node<E> nextNode = removeNode.next;

        //如果删除的是第一个元素
        if (preNode == null) {
            first = nextNode;
        } else {
            preNode.next = nextNode;
            removeNode.prev = null;
        }

        //删除的是最后一个
        if (nextNode == null) {
            last = preNode;
        } else {
            nextNode.prev = preNode;
        }

        removeNode.next = null;

        size--;

        return removeNode.item;
    }

    @Override
    public void clear() {
        first = null;
        last = null;
    }

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }


    /*判断要插入元素的节点位置*/
    Node<E> node(int index) {
        //要插入到那个位置
        //判断插入的位置是size一半的前面还是后面
        //当小于1/2size的
        if (index < (size >> 1)) {
            //拿到第一个元素
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                //依次拿到后面的节点
                x = x.next;
            return x;
            //如果插入的是后面1/2size的位置
        } else {
            //拿到最后一个节点
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                //依次拿到前面的节点
                x = x.prev;
            return x;
        }
    }

    @Override
    public String toString() {
        return "ListList1{}";
    }
}