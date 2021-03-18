package com.wb.spring.populate_test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:
 *
 * @author wangbin33
 * @date 2020/12/30 9:25
 */
public class TestMain {

	public static void main(String[] args) {
//		ApplicationContext acx = new ClassPathXmlApplicationContext("populate_test/populate.xml");
//		PService pService = (PService) acx.getBean("pService");
//		pService.printDao();
		int[][] arr = {
				{1,2,3},
				{4,5,6},
				{7,8,9}
		};
//		rotate(arr);
		spiralPrint(arr);
	}

	public static void rotate(int[][] array) {
		//遍历指针
		int i;
		//边界
		int l = 0, k = 0, m = array.length - 1, n = array[0].length - 1;
		//主循环
		while (true) {
			//遍历上面的行
			for (i = k; i <= n; i++) {
				System.out.println(array[l][i]);
			}
			//遍历完修改行的最小边界
			l ++;
			//遍历右侧的列
			for (i = l; i <= m; i++){
				System.out.println(array[i][n]);
			}
			//遍历完修改列的右边界
			n --;

			//检测行是否遍历完了，如果碰上了，说明该退出
			if (l > m) {
				break;
			}
			//遍历下面的行
			for (i = n; i >= k; i--) {
				System.out.println(array[m][i]);
			}
			//遍历完修改行的最大边界
			m--;
			//检测列是否遍历完了，如果碰上了，说明该退出
			if (k > n) {
				break;
			}
			//遍历左侧的列
			for (i = m; i >= l; i--) {
				System.out.println(array[i][k]);
			}
			//遍历完修改列的左边界
			k++;
		}
	}

	static void spiralPrint(int a[][]) {
		// 迭代器指针
		int i;
		int k = 0, l = 0, row = a.length, col = a[0].length;

        /*
        k - starting row index
        m - ending row index
        l - starting column index
        n - ending column index
        i - iterator
        */
		while (k < row && l < col) {
			// Print the first row from the remaining rows
			for (i = l; i < col; ++i) {
				System.out.print(a[k][i] + " ");
			}
			k++;
			// Print the last column from the remaining
			// columns
			for (i = k; i < row; ++i) {
				System.out.print(a[i][col - 1] + " ");
			}
			col--;
			// Print the last row from the remaining rows
			if (k < row) {
				for (i = col - 1; i >= l; --i) {
					System.out.print(a[row - 1][i] + " ");
				}
				row--;
			}
			// Print the first column from the remaining
			// columns
			if (l < col) {
				for (i = row - 1; i >= k; --i) {
					System.out.print(a[i][l] + " ");
				}
				l++;
			}
		}
	}
}
