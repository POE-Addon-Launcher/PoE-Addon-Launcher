package Helper;

/**
 * A quick sort method.
 * Sorts highest number to lowest number. ex: [3,1,5,22] -> [22,5,3,1]
 * Based on: http://www.java2novice.com/java-sorting-algorithms/quick-sort/
 */
public class QuickSorter
{
    private Release[] array;

    public void sort(Release[] inputArr)
    {
        if (inputArr == null || inputArr.length == 0)
        {
            return;
        }
        this.array = inputArr;
        int length = inputArr.length;
        quickSort(0, length - 1);
    }

    private void quickSort(int lowerIndex, int higherIndex)
    {

        int i = lowerIndex;
        int j = higherIndex;
        Release pivot = array[lowerIndex+(higherIndex-lowerIndex)/2];
        while (i <= j)
        {
            while (array[i].getNum() > pivot.getNum())
            {
                i++;
            }
            while (array[j].getNum() < pivot.getNum())
            {
                j--;
            }
            if (i <= j) {
                swap(i, j);
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSort(lowerIndex, j);
        if (i < higherIndex)
            quickSort(i, higherIndex);
    }

    private void swap(int i, int j)
    {
        Release temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
