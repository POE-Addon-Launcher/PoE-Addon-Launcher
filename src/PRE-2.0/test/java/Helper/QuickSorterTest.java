package Helper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class QuickSorterTest
{
    QuickSorter quickSorter;
    private Release[] test_array_random_order;
    private Release[] test_array_sorted_l_h;
    private Release[] test_array_sorted_h_l;
    private Release[] test_array_one_element;
    private Release[] empty_array;
    private Release r1,r2,r3,r4,r5,r6,r7,r8,r9,r10;

    @Before
    public void init()
    {
        quickSorter = new QuickSorter();

        r1 = new Release(null);
        r2 = new Release(null);
        r3 = new Release(null);
        r4 = new Release(null);
        r5 = new Release(null);
        r6 = new Release(null);
        r7 = new Release(null);
        r8 = new Release(null);
        r9 = new Release(null);
        r10 = new Release(null);

        r1.setNum(1);
        r2.setNum(38);
        r3.setNum(83);
        r4.setNum(0);
        r5.setNum(-1);
        r6.setNum(3821);
        r7.setNum(12);
        r8.setNum(38);
        r9.setNum(93);
        r10.setNum(29);

        test_array_random_order = new Release[]{r1,r5,r9,r6,r10,r4,r2,r8,r7,r3};
        test_array_one_element = new Release[]{r2};
        test_array_sorted_h_l = new Release[]{r6,r9,r3,r2,r8,r10,r7,r1,r4,r5};
        test_array_sorted_l_h = new Release[]{r5,r4,r1,r7,r10,r8,r2,r3,r9,r6};
    }


    @Test
    public void simpleSortTest()
    {

        quickSorter.sort(test_array_random_order);
        assertArrayEquals(test_array_sorted_h_l, test_array_random_order);
    }

    @Test
    public void sortOneElement()
    {
        quickSorter.sort(test_array_one_element);
        assertArrayEquals(new Release[]{r8}, test_array_one_element);
    }

    @Test
    public void sortLowToHighArray()
    {
        quickSorter.sort(test_array_sorted_l_h);
        assertArrayEquals(test_array_sorted_h_l, test_array_sorted_l_h);
    }

    @Test
    public void sortEmptyArray()
    {
        quickSorter.sort(empty_array);
        assertArrayEquals(empty_array, empty_array);
    }
}