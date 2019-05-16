package debug;

import org.junit.Test;

import static org.junit.Assert.*;

public class FindMedianSortedArraysTest {
	
	/** Example 1:
	* nums1 = [1, 3]
	* nums2 = [2]
	* The output would be 2.0
	
	* Example 2:
	* nums1 = [1, 2]
	* nums2 = [3, 4]
	* The output would be 2.5
	
	* Example 3:
	* nums1 = [1, 1, 1]
	* nums2 = [5, 6, 7]
	* The output would be 3.0
	
	* Example 4:
	* nums1 = [1, 1]
	* nums2 = [1, 2, 3]
	* The output would be 1.0
	*/
	@Test
	public void findMedianSortedArrays() {
		FindMedianSortedArrays f = new FindMedianSortedArrays();
		
		var a = new int[]{1, 3};
		var b = new int[]{2};
		assertEquals(2, f.findMedianSortedArrays(a, b), 0);
		
		a = new int[]{1, 2};
		b = new int[]{3, 4};
		assertEquals(2.5, f.findMedianSortedArrays(a, b), 0);
		
		a = new int[]{1, 1, 1};
		b = new int[]{5, 6, 7};
		assertEquals(3.0, f.findMedianSortedArrays(a, b), 0);
		
		a = new int[]{1, 1};
		b = new int[]{1, 2, 3};
		assertEquals(1.0, f.findMedianSortedArrays(a, b), 0);
	}
}