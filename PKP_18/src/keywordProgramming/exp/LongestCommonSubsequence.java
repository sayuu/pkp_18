package keywordProgramming.exp;

// �Œ����ʕ����n�񒷁iLCS�j�����߂�Java�v���O����
// http://d.hatena.ne.jp/sternheller/20090715/1247634042

public class LongestCommonSubsequence {

	// ���s�T���v��
	public static void main(String[] args) {
		System.out.println(LCS_Length("ABCBDABDA", "ABCBDAB"));
	}

	public static int LCS_Length(String str1, String str2) {
		int len1 = str1.length(), len2 = str2.length();
		int[][] lcs_table = new int[len1][len2]; // LCS�����i�[�����\
		
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				if (str1.charAt(i) == str2.charAt(j))
					lcs_table[i][j] = ((i == 0 || j == 0) ? 0 : lcs_table[i-1][j-1]) + 1;
				else if (str1.charAt(i) != str2.charAt(j))
					lcs_table[i][j] = Math.max(
							i == 0 ? 0 : lcs_table[i-1][j],
							j == 0 ? 0 : lcs_table[i][j-1]);
			}
		}
		
		// LCS�\�̕\��
//		for (int i = 0; i < len1; i++) {
//			for (int j = 0; j < len2; j++)
//				System.out.print(lcs_table[i][j] + " ");
//			System.out.println();
//		}
		
		// LCS�\�̍ł��E����LCS�����i�[����Ă���
		return lcs_table[len1-1][len2-1];
	}
}
