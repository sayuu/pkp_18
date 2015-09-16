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
 * �h���b�N���đI��������������^�X�N�Ƃ��Ēǉ�����A�N�V�������s���N���X�B
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
		//�C���|�[�g���ǂݍ��݁B���X�i�̓o�^
//		window.getSelectionService().addSelectionListener(AstLocalCode.listener);
		//�C���|�[�g���ǂݍ��݁B���炩���߃G�f�B�^��ŊJ���Ă���t�@�C���ɑΉ�����
		Import.clearImportDeclaration();
		Import.getImportDeclaration(null);
		
		ISelection is = HandlerUtil.getCurrentSelectionChecked(event);
		
		if(is instanceof ITextSelection){
			// �A�N�e�B�u�G�f�B�^���擾
		    IEditorPart editor = window.getActivePage().getActiveEditor();
		    
		    // �G�f�B�^���̑I��͈͂��擾
		    ITextSelection textSelection = (ITextSelection)
		            ((ITextEditor)editor).getSelectionProvider().getSelection();
 
		    //���ݑI�𒆂̍s���擾
			/*
			 * �e�L�X�g��I�������āA
			 * �ȉ��̒l���擾����B
			 * 
			 * offset: �I�t�Z�b�g
			 * selected_text: �I���e�L�X�g
			 * selected_length: �I���e�L�X�g��
			 * selected_line_start: �I���e�L�X�g�J�n�s�ԍ�
			 * selected_line_end: �I���e�L�X�g�I���s�ԍ�
			 */
	        int offset = textSelection.getOffset();
	        String selected_text = textSelection.getText().trim();
	        //�Ō��";"�����B
	        if(selected_text.matches(".*;$")){
	        	selected_text = selected_text.substring(0, selected_text.length()-1);
	        }
	        
	        int selected_length = textSelection.getLength();
	        int selected_line_start = textSelection.getStartLine();
	        int selected_line_end = textSelection.getEndLine();
	        
	        //�]�܂����Ԃ�l�̌^
	    	String desiredReturnType = null;
	    	//���P�[�V����
	    	String location = null;
	    	//���݃G�f�B�^���ɑ��݂���L����Type�����郊�X�g
	    	List<String> classesInActiveEditor = new ArrayList<String>();
	    	//���݃G�f�B�^���ɑ��݂���L���Ȋ֐������郊�X�g
	    	List<String> functionsInActiveEditor = new ArrayList<String>();
	    	
		    // Java�G�f�B�^�̏ꍇ�A�G�f�B�^����Ώۂ� IJavaElement ���擾�ł���
		    IJavaElement javaElement = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
		    
		    if (javaElement instanceof ICompilationUnit) {
		        // ICompilationUnit �̓\�[�X��������Java�N���X��\��
		        ICompilationUnit icu = (ICompilationUnit)javaElement;
		        String source;
		        try {
		        	source = icu.getSource();
					AstLocalCode.getLocalInfomation(source, offset, selected_length, true, null, null, null);
					desiredReturnType = AstLocalCode.getDesiredReturnType();
					location = AstLocalCode.getLocation();
					classesInActiveEditor.addAll(AstLocalCode.getClasses());
					functionsInActiveEditor.addAll(AstLocalCode.getFunctions());
					AstLocalCode.clear();//�g������̓N���A����B
				} catch (JavaModelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
		        //���ݑI�𒆂̃N���X���i�p�b�P�[�W�����j���擾
		        String class_name = null;
				try {
					class_name = icu.getTypes()[0].getFullyQualifiedParameterizedName();
					boolean yn = MessageDialog.openQuestion(
								window.getShell(),
								"�w�K�^�X�N�ǉ�",
								"�w�K�^�X�N��\""+class_name+"\"�N���X��\n�I��͈�\""+selected_text+"\"��ǉ����܂�");
					
					//"No"�Ȃ牽�����Ȃ��B
					if(yn == false){
						return null;
					}
				} catch (JavaModelException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}

				TestSite ts = new TestSite(class_name, offset, offset, selected_line_start, selected_line_end, selected_length, selected_text, desiredReturnType, location, classesInActiveEditor, functionsInActiveEditor, false);
				
				//�t�@�C�����쐬����B
				ts.createNewFile();
				
				//�r���[�̃��t���b�V��
				TestSiteView view = (TestSiteView) window.getActivePage().findView("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.testSiteView");
				view.refresh();
				
		    }
		}
		return null;
	}
}
