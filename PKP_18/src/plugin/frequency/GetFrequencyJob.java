package plugin.frequency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import keywordProgramming.KeywordProgramming;
import keywordProgramming.Type;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;

import ast.AstLocalCode;
import ast.AstUtil;
import ast.Import;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
/*
 * �I�������G�������g���̃e�X�g�T�C�g��S�ă^�X�N�Ƃ��Ēǉ��A�ۑ�����N���X�B
 * 
 */
public class GetFrequencyJob extends Job {
	
	private static HashMap<String, Integer> function_frequency = new HashMap<String, Integer>();
	private ICompilationUnit icu;
	private CompilationUnit cu;
	private String source;
	private ExecutionEvent event;
	private IStructuredSelection iss;
	private int numOfCompilationUnits;
	private int numOfWorkedCompilationUnits;
	private IProgressMonitor monitor;

	//���݂̃L�[���[�h�v���O���~���O�ł͐������o�����Ƃ��ł��Ȃ��^�X�N��r�����Ĉꊇ�ǉ�����B
	
	public GetFrequencyJob(ExecutionEvent event, IStructuredSelection iss, int numOfCompilationUnits, int heightOfFunctionTree) {
		super("�^�X�N�̒ǉ�");
		// TODO Auto-generated constructor stub
		this.event = event;
		this.iss = iss;
		this.numOfCompilationUnits = numOfCompilationUnits;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		this.monitor = monitor;
		monitor.beginTask("�^�X�N�̒ǉ�������", numOfCompilationUnits);
		try {
			execute();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//�L�����Z�����ꂽ
		if (monitor.isCanceled()) {
		    return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
	
	private void execute() throws ExecutionException{
		
		function_frequency.clear();
		
		Iterator it1 = iss.iterator();
		//�^�X�N�̕ۑ�
		while(it1.hasNext()){
			//���[�U�[���������L�����Z������
			if(monitor.isCanceled()){
				return;
			}
			Object selectionObject = it1.next();
			if (!(selectionObject instanceof IJavaElement))
				continue;
			IJavaElement javaElement = (IJavaElement) selectionObject;
			switch(javaElement.getElementType()){
				case IJavaElement.COMPILATION_UNIT:
					icu = (ICompilationUnit) javaElement;
					saveICompilationUnit(icu);
					break;
				case IJavaElement.PACKAGE_FRAGMENT:
					IPackageFragment pf = (IPackageFragment) javaElement;
					saveIPackageFragment(pf);
					break;
				case IJavaElement.PACKAGE_FRAGMENT_ROOT:
					IPackageFragmentRoot pfr = (IPackageFragmentRoot) javaElement;
					saveIPackageFragmentRoot(pfr);
					break;
				case IJavaElement.JAVA_PROJECT:
					IJavaProject p = (IJavaProject) javaElement;
					try {
						IPackageFragmentRoot []root_arr = p.getPackageFragmentRoots();
						for(IPackageFragmentRoot root: root_arr){
							saveIPackageFragmentRoot(root);
						}
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
		
		//���ʕ\��
		printFrequency();
		
		return;
	}

	class ASTVisitorImpl extends ASTVisitor {

	    /*
	     * ���\�b�h�R�[��
	     */
	    public boolean visit(MethodInvocation node) {
	    	//DBString �ɂ��āAhash(DBString, count)�ɓ����B 	
	     	
	    	String parentClass = node.resolveMethodBinding().getDeclaringClass().getBinaryName();
 			List<String> list =  AstUtil.iMethodBindingToDbStringStatic1(node.resolveMethodBinding(), parentClass, false, false, false);
 			for(String dbString:list){
 				if(function_frequency.containsKey(dbString)){
 					int freq = function_frequency.get(dbString);
 					function_frequency.put(dbString, ++freq);
 				}else{
 					function_frequency.put(dbString, 1);
 				}
 			}
	        return super.visit(node);
	    }
	    
	    /*
	     * new �ł̃C���X�^���X����
	     */
	    public boolean visit(ClassInstanceCreation node) {
	    	//DBString �ɂ��āAhash(DBString, count)�ɓ����B 	
 	
	    	String parentClass = node.resolveConstructorBinding().getDeclaringClass().getBinaryName();
 			List<String> list =  AstUtil.iMethodBindingToDbStringStatic1(node.resolveConstructorBinding(), parentClass, false, false, false);
 			for(String dbString:list){
 				if(function_frequency.containsKey(dbString)){
 					int freq = function_frequency.get(dbString);
 					function_frequency.put(dbString, ++freq);
 				}else{
 					function_frequency.put(dbString, 1);
 				}
 			}
	        return super.visit(node);
	    }
	}
	
	
	private void saveIPackageFragmentRoot(IPackageFragmentRoot pfr) {
		//���[�U�[���������L�����Z������
		if(monitor.isCanceled()){
			return;
		}
		try {
			if (pfr.getKind() == IPackageFragmentRoot.K_SOURCE){
				for(IJavaElement je: pfr.getChildren()){
					saveIPackageFragment((IPackageFragment)je);
				}
				
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveIPackageFragment(IPackageFragment pf) {
		//���[�U�[���������L�����Z������
		if(monitor.isCanceled()){
			return;
		}
		try {
			ICompilationUnit []icu_arr = pf.getCompilationUnits();
			for(ICompilationUnit icu: icu_arr){
				this.icu = icu;
				saveICompilationUnit(icu);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveICompilationUnit(ICompilationUnit icu){
		//���[�U�[���������L�����Z������
		if(monitor.isCanceled()){
			return;
		}
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
	    parser.setResolveBindings(true);
	    parser.setSource(icu);
	    try {
			source = icu.getSource();
			cu = (CompilationUnit) parser.createAST(new NullProgressMonitor());
			
			//�g�p�O�ɃN���A����B
			Import.clearImportDeclaration();
			Import.getImportDeclaration(cu);
			
			//CompilationUnit �̒��g�𒲂ׂ�B
			getFrequencyFromCompilationUnit();
			
		    cu.accept(new ASTVisitorImpl());
		    numOfWorkedCompilationUnits++;
			monitor.worked(numOfWorkedCompilationUnits);
			
			monitor.setTaskName(numOfWorkedCompilationUnits + "(�I���N���X��)/" + numOfCompilationUnits + "�i�S�N���X���j");
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}

	private void printFrequency() {
		//TreeMap�̐擪���珇�Ԃɉ�ʕ\������
		
		//��r�̂��߂̃N���X���w�肵�� TreeMap �𐶐�
		TreeMap<String, Integer> treeMap =
		     new TreeMap<String, Integer>(new IntegerMapComparator(function_frequency));
		// TreeMap �ɑS���̑g���R�s�[(���̂Ƃ��Ƀ\�[�g�����)
		treeMap.putAll(function_frequency);
		// TreeMap �̕\��
		Set<String> set = treeMap.keySet();  // �\�[�g����Ă���
		Iterator<String> it = set.iterator();
		
		//System.out.println("=== start ===");
		while (it.hasNext()) {
		    String key = it.next();
		    int value = treeMap.get(key);
		    System.out.println(value + "," + key);
		}
		//System.out.println("=== end ===");
	}
	
	private void getFrequencyFromCompilationUnit(){

//		cu.get
//		int offset = cu.getExtendedStartPosition(node);
//		int selected_length = cu.getExtendedLength(node);
//		
//        int selected_line_start = cu.getLineNumber(offset);
//        int selected_line_end = cu.getLineNumber(offset + selected_length);
//        
		 //���ݑI�𒆂̃N���X���i�p�b�P�[�W�����j���擾
		String class_name = null;
        try {
        	if(icu.getTypes().length > 0)
        		class_name = icu.getTypes()[0].getFullyQualifiedParameterizedName();
//        	else
//        		System.out.println("");
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	private void saveTestsite(ASTNode node){
		//�g�p�O�ɃN���A����B
//		AstLocalCode.clearImportDeclaration();
//		AstLocalCode.getImportDeclaration(cu);
		
		String selected_text = node.toString();
		
		//System.out.println(height + ": " + selected_text);
		
		
//		int offset = node.getStartPosition();
//		int selected_length = node.getLength();
		
		int offset = cu.getExtendedStartPosition(node);
		int selected_length = cu.getExtendedLength(node);
		
        int selected_line_start = cu.getLineNumber(offset);
        int selected_line_end = cu.getLineNumber(offset + selected_length);
        
        //�]�܂����Ԃ�l�̌^
    	String desiredReturnType = null;
    	//���P�[�V����
    	String location = null;
    	//���݃G�f�B�^���ɑ��݂���L����Type�����郊�X�g
    	List<String> classesInActiveEditor = null ; //new ArrayList<String>(KeywordProgramming.getImportedTypes());
    	//���݃G�f�B�^���ɑ��݂���L���Ȋ֐������郊�X�g
    	List<String> functionsInActiveEditor = null; //new ArrayList<String>(KeywordProgramming.getImportedFunctions());
    	
        AstLocalCode.getLocalInfomation(source, offset, selected_length, true, icu, cu, node);
		desiredReturnType = AstLocalCode.getDesiredReturnType();
		location = AstLocalCode.getLocation();
		classesInActiveEditor.addAll(AstLocalCode.getClasses());
		functionsInActiveEditor.addAll(AstLocalCode.getFunctions());

		AstLocalCode.clear();//�g������̓N���A����B
		
		 //���ݑI�𒆂̃N���X���i�p�b�P�[�W�����j���擾
		String class_name = null;
        try {
			class_name = icu.getTypes()[0].getFullyQualifiedParameterizedName();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
        TestSite ts = new TestSite(class_name, offset, offset, selected_line_start, selected_line_end, selected_length, selected_text, desiredReturnType, location, classesInActiveEditor, functionsInActiveEditor, false);
		ts.setASTNode(node);
		
		if(ts.isSelectedFunctionWithXtimesFrequency(1)){
			System.out.println(ts.getId());
			System.out.println("freq>=1:" + ts.isSelectedFunctionWithXtimesFrequency(1));
			System.out.println("freq>=3:" + ts.isSelectedFunctionWithXtimesFrequency(3));
			System.out.println("freq>=5:" + ts.isSelectedFunctionWithXtimesFrequency(5));
			
			ts.printFunctionList();
		}
		
//		if(ts.isFunctionWithXtimesFrequency(1)){
//			System.out.println("all freq>=1:" + ts.isFunctionWithXtimesFrequency(1));
//			System.out.println("all freq>=3:" + ts.isFunctionWithXtimesFrequency(3));
//			System.out.println("all freq>=5:" + ts.isFunctionWithXtimesFrequency(5));
//			
//			ts.printSimpleNameList();
//			ts.printFunctionList();
//		}
		//�t�@�C�����쐬����B
		ts.createNewFile();
        
	}

}
