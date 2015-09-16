package plugin.handlers.getNumOfFunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import plugin.views.TestSiteView;

public class GetElementHandler extends AbstractHandler{

	private static int numOfCompilationUnits;
	private static ExecutionEvent event;
	
	public static List<IJavaElement> elements = new ArrayList<IJavaElement>();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		GetElementHandler.event = event;
		GetElementHandler.numOfCompilationUnits = 0;
		ISelection is = HandlerUtil.getCurrentSelectionChecked(event);
		IStructuredSelection iss;
		
		if(is instanceof IStructuredSelection)
			 iss = (IStructuredSelection)is;
		else 
			return null;
		
		Iterator it = iss.iterator();
		
		//タスク数のカウント
		while(it.hasNext()){
			Object selectionObject = it.next();
			if (!(selectionObject instanceof IJavaElement))
				continue;
			IJavaElement javaElement = (IJavaElement) selectionObject;
			switch(javaElement.getElementType()){
				case IJavaElement.COMPILATION_UNIT:
					numOfCompilationUnits++;
					break;
				case IJavaElement.PACKAGE_FRAGMENT:
					IPackageFragment pf = (IPackageFragment) javaElement;
					numOfCompilationUnits += getNumOfCompilationUnits(pf);
					break;
				case IJavaElement.PACKAGE_FRAGMENT_ROOT:
					IPackageFragmentRoot pfr = (IPackageFragmentRoot) javaElement;
					numOfCompilationUnits += getNumOfCompilationUnits(pfr);
					break;
				case IJavaElement.JAVA_PROJECT:
					IJavaProject pjt = (IJavaProject) javaElement;
					numOfCompilationUnits += getNumOfCompilationUnits(pjt);
					break;
				case IJavaElement.CLASS_FILE:
					IClassFile icf = (IClassFile) javaElement;
					IType type = icf.getType();
					getChildren(type);
					break;
				default:
					break;
			}
		}
		
//		boolean yn = MessageDialog.openQuestion( HandlerUtil.getActiveShell(event), "タスクの一括追加", "合計" + numOfCompilationUnits+ "個のクラスが存在します。追加しますか？");
//		if(yn == false){
//			return null;
//		}
		
		
		
		
		
//		try {
//			job.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		refreshView();
//		MessageDialog.openInformation( HandlerUtil.getActiveShell(event), "タスクの追加完了", "合計" +job.getNumOfSites()+ "個のタスクを追加しました。");
		
		return null;
	}

/*
 * コンストラクタ、メソッド、フィールドを取得
 */
	private void getChildren(IType type) {
		try {
			
			IJavaElement ch[] = type.getChildren();
			for(IJavaElement ele: ch){
//				//パブリックなもののみ追加する。
				boolean flg = false;
				switch(ele.getElementType()){
				
				case IJavaElement.FIELD:
					//System.out.print("フィールド");
					 IField field = (IField)ele;
					 flg = Flags.isPublic(field.getFlags());
					break;
				case IJavaElement.METHOD:
					IMethod method = (IMethod)ele;
					 flg = Flags.isPublic(method.getFlags());
					break;
				}
				
				if(flg)
					elements.add(ele);
				
				System.out.println(ele);
			}
			
			
		} catch (JavaModelException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	
	private int getNumOfCompilationUnits(IJavaProject p){
		int sum = 0;
		try {
			IPackageFragmentRoot []root_arr = p.getPackageFragmentRoots();
			for(IPackageFragmentRoot root: root_arr){
				sum += getNumOfCompilationUnits(root);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sum;
	}
	
	private int getNumOfCompilationUnits(IPackageFragmentRoot pfr){
		int sum = 0;
		try {
			if (pfr.getKind() == IPackageFragmentRoot.K_SOURCE){
				for(IJavaElement je: pfr.getChildren()){
					sum += getNumOfCompilationUnits((IPackageFragment)je);
				}
				
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sum;
	}
	
	private int getNumOfCompilationUnits(IPackageFragment pf){
		int sum = 0;
		try {
			ICompilationUnit []icu_arr = pf.getCompilationUnits();
			sum = icu_arr.length;
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sum;
	}
	
	private static void refreshView(){
		 //ビューのリフレッシュ
		TestSiteView view;
		try {
			view = (TestSiteView) HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage().findView("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.testSiteView");
			view.refresh();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 protected static void showResults() {
	        Display.getDefault().asyncExec(new Runnable() {
	           public void run() {
	              getReservationCompletedAction().run();
	           }
	        });
	     }

	protected static Action getReservationCompletedAction() {
		return new Action("学習タスクの一括追加 終了") {
			public void run() {
				try {
					MessageDialog.openInformation(getShell(), 
					  "学習タスクの一括追加", 
					  "学習タスクの追加が完了しました。");
					refreshView();
				} catch (ExecutionException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		};
	}
	
	protected static Shell getShell() throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		return window.getShell();
	}

}
