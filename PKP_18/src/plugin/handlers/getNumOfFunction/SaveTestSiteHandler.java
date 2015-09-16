package plugin.handlers.getNumOfFunction;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;

import plugin.testSite.TestSite;
import plugin.views.TestSiteView;

import ast.AstLocalCode;
import ast.Import;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
/*
 * ドラックして選択した文字列をタスクとして追加するアクションを行うクラス。
 * 
 */
public class SaveTestSiteHandler extends AbstractHandler {
	
	/**
	 * The constructor.
	 */
	public SaveTestSiteHandler() {
		
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		//インポート文読み込み。リスナの登録
//		window.getSelectionService().addSelectionListener(AstLocalCode.listener);
		//インポート文読み込み。あらかじめエディタ上で開いてあるファイルに対応する
		Import.clearImportDeclaration();
		Import.getImportDeclaration(null);
		
		ISelection is = HandlerUtil.getCurrentSelectionChecked(event);
		
		if(is instanceof ITextSelection){
			// アクティブエディタを取得
		    IEditorPart editor = window.getActivePage().getActiveEditor();
		    
		    // エディタ内の選択範囲を取得
		    ITextSelection textSelection = (ITextSelection)
		            ((ITextEditor)editor).getSelectionProvider().getSelection();
 
		    //現在選択中の行を取得
			/*
			 * テキストを選択させて、
			 * 以下の値を取得する。
			 * 
			 * offset: オフセット
			 * selected_text: 選択テキスト
			 * selected_length: 選択テキスト長
			 * selected_line_start: 選択テキスト開始行番号
			 * selected_line_end: 選択テキスト終了行番号
			 */
	        int offset = textSelection.getOffset();
	        String selected_text = textSelection.getText().trim();
	        //最後の";"を取る。
	        if(selected_text.matches(".*;$")){
	        	selected_text = selected_text.substring(0, selected_text.length()-1);
	        }
	        
	        int selected_length = textSelection.getLength();
	        int selected_line_start = textSelection.getStartLine();
	        int selected_line_end = textSelection.getEndLine();
	        
	        //望ましい返り値の型
	    	String desiredReturnType = null;
	    	//ロケーション
	    	String location = null;
	    	//現在エディタ内に存在する有効なTypeを入れるリスト
	    	List<String> classesInActiveEditor = new ArrayList<String>();
	    	//現在エディタ内に存在する有効な関数を入れるリスト
	    	List<String> functionsInActiveEditor = new ArrayList<String>();
	    	
		    // Javaエディタの場合、エディタから対象の IJavaElement を取得できる
		    IJavaElement javaElement = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
		    
		    if (javaElement instanceof ICompilationUnit) {
		        // ICompilationUnit はソース情報を持つJavaクラスを表す
		        ICompilationUnit icu = (ICompilationUnit)javaElement;
		        String source;
		        try {
		        	source = icu.getSource();
					AstLocalCode.getLocalInfomation(source, offset, selected_length, true, null, null, null);
					desiredReturnType = AstLocalCode.getDesiredReturnType();
					location = AstLocalCode.getLocation();
					classesInActiveEditor.addAll(AstLocalCode.getClasses());
					functionsInActiveEditor.addAll(AstLocalCode.getFunctions());
					AstLocalCode.clear();//使った後はクリアする。
				} catch (JavaModelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
		        //現在選択中のクラス名（パッケージ名も）を取得
		        String class_name = null;
				try {
					class_name = icu.getTypes()[0].getFullyQualifiedParameterizedName();
					boolean yn = MessageDialog.openQuestion(
								window.getShell(),
								"学習タスク追加",
								"学習タスクに\""+class_name+"\"クラスの\n選択範囲\""+selected_text+"\"を追加します");
					
					//"No"なら何もしない。
					if(yn == false){
						return null;
					}
				} catch (JavaModelException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

				TestSite ts = new TestSite(class_name, offset, offset, selected_line_start, selected_line_end, selected_length, selected_text, desiredReturnType, location, classesInActiveEditor, functionsInActiveEditor, false);
				
				//ファイルを作成する。
				ts.createNewFile();
				
				//ビューのリフレッシュ
				TestSiteView view = (TestSiteView) window.getActivePage().findView("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.testSiteView");
				view.refresh();
				
		    }
		}
		return null;
	}
}
