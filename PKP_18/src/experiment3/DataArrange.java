package experiment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import plugin.testSite.OutputCandidateLog;

public class DataArrange {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		//�����Q�C�R�C�S�@�S�Ă܂Ƃ߂��t�@�C���B
		String filename2 = "C:\\Users\\sayuu\\Desktop\\2.txt";
		String filename3 = "C:\\Users\\sayuu\\Desktop\\3.txt";
		String filename4 = "C:\\Users\\sayuu\\Desktop\\4.txt";
		
		printExp23(filename2);
		printExp23(filename3);
		printExp4(filename4);
		
	}

	private static void printExp23(String filename) {
		File file = new File(filename);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			ArrayList<String> list_num = new ArrayList<String>();
			ArrayList<String> list_c_all = new ArrayList<String>();
			ArrayList<String> list_c_1 = new ArrayList<String>();
			ArrayList<String> list_c_3 = new ArrayList<String>();
			ArrayList<String> list_c_5 = new ArrayList<String>();
			ArrayList<String> list_c_10 = new ArrayList<String>();
			
			
			String line;
			while ((line = br.readLine()) != null) {
				String arr[];
				if (line.startsWith("����")){
					arr = line.split("\t");
					list_num.add(arr[arr.length-1]);
				}else if(line.startsWith("�����o����")){
					arr = line.split("\t");
					list_c_all.add(arr[arr.length-1]);
				}else if(line.startsWith("���1�Ԗڂɏo����")){
					arr = line.split("\t");
					list_c_1.add(arr[arr.length-1]);
				}else if(line.startsWith("���3�Ԗڈȓ��ɐ����o��")){
					arr = line.split("\t");
					list_c_3.add(arr[arr.length-1]);
				}else if(line.startsWith("���5�Ԗڈȓ��ɐ����o��")){
					arr = line.split("\t");
					list_c_5.add(arr[arr.length-1]);
				}else if(line.startsWith("���10�Ԗڈȓ��ɐ����o��")){
					arr = line.split("\t");
					list_c_10.add(arr[arr.length-1]);
				}
			}
			
			System.out.println("list size=" + list_num.size());
			
			//9�܂ł̏ꍇ�S���ŁA54��
			if(list_num.size() != 54){
				System.out.println("�G���[�B");
				return;
			}
			
			System.out.println("1======================");
			printList(list_c_1);
			System.out.println("3======================");
			printList(list_c_3);
			System.out.println("5======================");
			printList(list_c_5);
			System.out.println("10======================");
			printList(list_c_10);
			System.out.println("all======================");
			printList(list_c_all);
			System.out.println("======================");
			
		} catch (FileNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
	
	private static void printExp4(String filename) {
		File file = new File(filename);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			ArrayList<String> list_num = new ArrayList<String>();
			ArrayList<String> list_c_all = new ArrayList<String>();
			ArrayList<String> list_c_1 = new ArrayList<String>();
			ArrayList<String> list_c_3 = new ArrayList<String>();
			ArrayList<String> list_c_5 = new ArrayList<String>();
			ArrayList<String> list_c_10 = new ArrayList<String>();
			
			
			String line;
			while ((line = br.readLine()) != null) {
				String arr[];
				if (line.startsWith("����")){
					arr = line.split("\t");
					list_num.add(arr[arr.length-1]);
				}else if(line.startsWith("�S��")){
					arr = line.split("\t");
					list_c_all.add(arr[arr.length-1]);
				}else if(line.startsWith("���1�Ԗڂɏo����")){
					arr = line.split("\t");
					list_c_1.add(arr[arr.length-1]);
				}else if(line.startsWith("���3�Ԗڂɏo����")){
					arr = line.split("\t");
					list_c_3.add(arr[arr.length-1]);
				}else if(line.startsWith("���5�Ԗڂɏo����")){
					arr = line.split("\t");
					list_c_5.add(arr[arr.length-1]);
				}else if(line.startsWith("���10�Ԗڂɏo����")){
					arr = line.split("\t");
					list_c_10.add(arr[arr.length-1]);
				}
			}
			
			System.out.println("list size=" + list_num.size());
			
			//9�܂ł̏ꍇ�S���ŁA36��
			if(list_num.size() != 36){
				System.out.println("�G���[�B");
				return;
			}
			
			System.out.println("1======================");
			printList4(list_c_1);
			System.out.println("3======================");
			printList4(list_c_3);
			System.out.println("5======================");
			printList4(list_c_5);
			System.out.println("10======================");
			printList4(list_c_10);
			System.out.println("all======================");
			printList4(list_c_all);
			System.out.println("======================");
			
		} catch (FileNotFoundException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
	
	private static void printList(ArrayList<String> list_c_1) {
		int col = 1;
		int raw = 0;
		for(int i = 0; i < list_c_1.size(); i++){
			System.out.println(list_c_1.get(i));
			if(raw == col ){
				System.out.println();
				raw = 0;
				col++;
			}else{
				raw++;
			}
		}
	}
	
	private static void printList4(ArrayList<String> list_c_1) {
		int col = 0;
		int raw = 0;
		for(int i = 0; i < list_c_1.size(); i++){
			System.out.println(list_c_1.get(i));
			if(raw == col ){
				System.out.println();
				raw = 0;
				col++;
			}else{
				raw++;
			}
		}
	}

}
