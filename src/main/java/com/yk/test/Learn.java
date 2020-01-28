package com.yk.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Learn
{
    final float iii = 3.14f;

    public static void main(String args[])
    {
        final float iii = 3.14f;
        int target = 1;
        int[][] a = new int[1][2];
        System.out.println(a.length);
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
            {
                if (a[i][j] == target)
                {
                    return;
                }
            }
        }
        ListNode listNode1 = new ListNode(1);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode3 = new ListNode(3);
        listNode1.next = listNode2;
        listNode2.next = listNode3;
        new Learn().printListFromTailToHead(listNode1);
    }

    public boolean Find(int target, int[][] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            for (int j = 0; j < array[i].length; j++)
            {
                if (array[i][j] == target)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Integer> printListFromTailToHead(ListNode listNode)
    {
        ArrayList<Integer> res = new ArrayList<>();
        ListNode cur = listNode;
        while (cur != null)
        {
            res.add(cur.val);
            cur = cur.next;
        }
        Collections.reverse(res);
        return res;
    }


    static class ListNode
    {
        int val;
        ListNode next = null;

        public ListNode(int val)
        {
            this.val = val;
        }
    }


}
