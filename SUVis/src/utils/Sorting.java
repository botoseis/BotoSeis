/*
 * Sorting.java 
 * 
 * Project: BotoSeis
 *
 * Federal University of Para.
 * Department of Geophysics
 */
package utils;

/**
 * The Sorting class group various SU sorting functions.
 * Its a port to java of the codes found in the SU sources.
 * 
 * @author Williams Lima * 
 */
public class Sorting {

    public static void qkfind(int m, int n, float a[]) /*****************************************************************************
    Partially sort an array so that the element a[m] has the value it
    would have if the entire array were sorted such that 
    a[0] <= a[1] <= ... <= a[n-1]
     ******************************************************************************
    Input:
    m		index of element to be found
    n		number of elements in array a
    a		array[n] to be partially sorted
    Output:
    a		array[n] partially sorted
     ******************************************************************************
    Notes:
    This function is adapted from procedure find by
    Hoare, C.A.R., 1961, Communications of the ACM, v. 4, p. 321.
     ******************************************************************************
    Author:  Dave Hale, Colorado School of Mines, 01/13/89
     *****************************************************************************/
    {
        final int NSMALL = 7;	/* size of array for which insertion sort is fast */
        int j, k, p, q;
        int[] jk = new int[2];
        /* initialize subarray lower and upper bounds to entire array */
        p = 0;
        q = n - 1;

        /* while subarray can be partitioned efficiently */
        while (q - p > NSMALL) {

            /* partition subarray into two subarrays */
            qkpart(a, p, q, jk);
            j = jk[0];
            k = jk[1];
            /* if desired value is in lower subarray, then */
            if (m <= j) {
                q = j;
            } /* else, if desired value is in upper subarray, then */ else if (m >= k) {
                p = k;
            } /* else, desired value is between j and k */ else {
                return;
            }
        }

        /* completely sort the small subarray with insertion sort */
        qkinss(a, p, q);
    }

    public static void qksort(int n, float a[]) /*****************************************************************************
    Sort an array such that a[0] <= a[1] <= ... <= a[n-1]
     ******************************************************************************
    Input:
    n		number of elements in array a
    a		array[n] containing elements to be sorted
    Output:
    a		array[n] containing sorted elements
     ******************************************************************************
    Notes:
    n must be less than 2^NSTACK, where NSTACK is defined above.
    This function is adapted from procedure quicksort by
    Hoare, C.A.R., 1961, Communications of the ACM, v. 4, p. 321;
    the main difference is that recursion is accomplished
    explicitly via a stack array for efficiency; also, a simple
    insertion sort is used to sort subarrays too small to be
    partitioned efficiently.
     ******************************************************************************
    Author:  Dave Hale, Colorado School of Mines, 01/13/89
     *****************************************************************************/
    {
        final int NSTACK = 50;	/* maximum sort length is 2^NSTACK */
        final int NSMALL = 7;	/* size of array for which insertion sort is fast */

        int[] pstack;
        int[] qstack;
        int p, q, top = 0;
        int j, k;

        int[] jk = new int[2];

        pstack = new int[NSTACK];
        qstack = new int[NSTACK];

        /* initialize subarray lower and upper bounds to entire array */
        pstack[top] = 0;
        qstack[top++] = n - 1;

        /* while subarrays remain to be sorted */
        while (top != 0) {
            /* get a subarray off the stack */
            p = pstack[--top];
            q = qstack[top];

            /* while subarray can be partitioned efficiently */
            while (q - p > NSMALL) {

                /* partition subarray into two subarrays */
                qkpart(a, p, q, jk);
                j = jk[0];
                k = jk[1];

                /* save larger of the two subarrays on stack */
                if (j - p < q - k) {
                    pstack[top] = k;
                    qstack[top++] = q;
                    q = j;
                } else {
                    pstack[top] = p;
                    qstack[top++] = j;
                    p = k;
                }
            }
            /* use insertion sort to finish sorting small subarray */
            qkinss(a, p, q);
        }
    }

    private static void qkpart(float[] a, int p, int q, int[] out) /*****************************************************************************
    quicksort partition (FOR INTERNAL USE ONLY):
    Take the value x of a random element from the subarray a[p:q] of
    a[0:n-1] and rearrange the elements in this subarray in such a way
    that there exist integers j and k with the following properties:
    p <= j < k <= q, provided that p < q
    a[l] <= x,  for p <= l <= j
    a[l] == x,  for j < l < k
    a[l] >= x,  for k <= l <= q
    Note that this effectively partitions the subarray with bounds
    [p:q] into lower and upper subarrays with bounds [p:j] and [k:q].
     ******************************************************************************
    Input:
    a		array[p:q] to be rearranged
    p		lower bound of subarray; must be less than q
    q		upper bound of subarray; must be greater then p
    Output:
    a		array[p:q] rearranged
    j		upper bound of lower output subarray
    k		lower bound of upper output subarray
     ******************************************************************************
    Notes:
    This function is adapted from procedure partition by
    Hoare, C.A.R., 1961, Communications of the ACM, v. 4, p. 321.
     ******************************************************************************
    Author:  Dave Hale, Colorado School of Mines, 01/13/89
     *****************************************************************************/
    /*
     * Williams:
     *      Now output is on out array. out[0] -> j and out[1] ->k
     */ {
        final int FM = 7875;		/* constants used to generate random pivots */
        final int FA = 211;
        final int FC = 1663;
        int pivot, left, right;
        float apivot, temp;
        int seed = 0;

        /* choose random pivot element between p and q, inclusive */
        seed = (seed * FA + FC) % FM;
        pivot = (int) (p + (q - p) * (float) seed / (float) FM);
        if (pivot < p) {
            pivot = p;
        }

        if (pivot > q) {
            pivot = q;
        }
        apivot = a[pivot];

        /* initialize left and right pointers and loop until break */
        for (left = p, right = q;;) {
            /*
             * increment left pointer until either
             * (1) an element greater than the pivot element is found, or
             * (2) the upper bound of the input subarray is reached
             */
            while (a[left] <= apivot && left < q) {
                left++;
            }

            /*
             * decrement right pointer until either
             * (1) an element less than the pivot element is found, or
             * (2) the lower bound of the input subarray is reached
             */


            while (a[right] >= apivot && right > p) {
                right--;
            }

            /* if left pointer is still to the left of right pointer */
            if (left < right) {
                /* exchange left and right elements */
                temp = a[left];
                a[left++] = a[right];
                a[right--] = temp;
            } /* else, if pointers are equal or have crossed, break */ else {
                break;
            }
        }
        /* if left pointer has not crossed pivot */
        if (left < pivot) {

            /* exchange elements at left and pivot */
            temp = a[left];
            a[left++] = a[pivot];
            a[pivot] = temp;
        } /* else, if right pointer has not crossed pivot */ else if (pivot < right) {

            /* exchange elements at pivot and right */
            temp = a[right];
            a[right--] = a[pivot];
            a[pivot] = temp;
        }
        /* left and right pointers have now crossed; set output bounds */
        out[0] = right;
        out[1] = left;
    }

    private static void qkinss(float a[], int p, int q) /*****************************************************************************
    quicksort insertion sort (FOR INTERNAL USE ONLY):
    Sort a subarray bounded by p and q so that
    a[p] <= a[p+1] <= ... <= a[q]
     ******************************************************************************
    Input:
    a		subarray[p:q] containing elements to be sorted
    p		lower bound of subarray; must be less than q
    q		upper bound of subarray; must be greater then p
    Output:
    a		subarray[p:q] sorted
     ******************************************************************************
    Notes:
    Adapted from Sedgewick, R., 1983, Algorithms, Addison Wesley, p. 96.
     ******************************************************************************
    Author:  Dave Hale, Colorado School of Mines, 01/13/89
     *****************************************************************************/
    {
        int i, j;
        float ai;

        for (i = p + 1; i <=
                q; i++) {
            for (ai = a[i], j = i; j >
                    p && a[j - 1] > ai; j--) {
                a[j] = a[j - 1];
            }

            a[j] = ai;
        }
    }
    /* Variable declaration */
}
