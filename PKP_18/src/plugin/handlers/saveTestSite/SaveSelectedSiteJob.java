package plugin.handlers.saveTestSite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import keywordProgramming.KeywordProgramming;

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
public class SaveSelectedSiteJob extends Job {
	
	private List<ASTNode> node_list = new ArrayList<ASTNode>();
	
	private ICompilationUnit icu;
	private CompilationUnit cu;
	private String source;
	private ExecutionEvent event;
	private int numOfSites;
	private IStructuredSelection iss;
	private int numOfCompilationUnits;
	private int numOfWorkedCompilationUnits;
	private IProgressMonitor monitor;

	//���݂̃L�[���[�h�v���O���~���O�ł͐������o�����Ƃ��ł��Ȃ��^�X�N��r�����Ĉꊇ�ǉ�����B
	
	//�����N���X��` ���O
	private boolean flg_exculde_anonymous_class_declaration;
	//�o�͊֐��؂̈����ɒ萔���܂܂�Ă���@���O 
	private boolean flg_exculde_literals;
	//�֐��̃��x���ɂ��āA�A�����đ啶�����������̂����݂���Ƃ��B���O
	private boolean flg_exculde_contiguous_upper_cases;
	//���P�[�V�������^�L���X�g�i�_�E���L���X�g�j�̃^�X�N�����O.
	private boolean flg_exculde_type_down_cast;

	private boolean flg_exculde_all_false = false;

	//�֐��؂̍ő�̍���
	private int max_hight_function_tree;
	
	public SaveSelectedSiteJob(ExecutionEvent event, IStructuredSelection iss, int numOfCompilationUnits, int heightOfFunctionTree) {
		super("�^�X�N�̒ǉ�");
		// TODO Auto-generated constructor stub
		this.event = event;
		this.iss = iss;
		this.numOfCompilationUnits = numOfCompilationUnits;
		this.max_hight_function_tree = heightOfFunctionTree;
		
		//�v���t�@�����X�Œ�߂��l�̃Z�b�g
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		flg_exculde_anonymous_class_declaration = store.getBoolean(PreferenceInitializer.TASK_IMPORT_EXCLUDE_ANONYMOUS_CLASS_DECLARATION);
		flg_exculde_contiguous_upper_cases = store.getBoolean(PreferenceInitializer.TASK_IMPORT_EXCLUDE_CONTIGUOUS_UPPER_CASES);
		flg_exculde_literals = store.getBoolean(PreferenceInitializer.TASK_IMPORT_EXCLUDE_LITERALS);
		if(!flg_exculde_anonymous_class_declaration && !flg_exculde_contiguous_upper_cases && !flg_exculde_literals)
			flg_exculde_all_false = true;
		
		flg_exculde_type_down_cast = store.getBoolean(PreferenceInitializer.TASK_IMPORT_EXCLUDE_TYPE_DOWN_CAST);
		
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
		this.numOfSites = 0;
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
		
		return;
	}

	class ASTVisitorImpl extends ASTVisitor {
	    public void preVisit(ASTNode node) {
//	    	System.out.println("Type: " + ASTNode.nodeClassForType(node.getNodeType()));
//	    	System.out.println(node);
	    }
	    
	    /*
	     * ���\�b�h�R�[��
	     */
	    public boolean visit(MethodInvocation node) {
	    	
	    	//Enum�͎擾���Ȃ��B�����ł��Ȃ��B
	    	if(isChildOfEnumDeclaration(node))
	    		return super.visit(node);
	    	
//	    	System.out.println(node);
//	    	System.out.println(node.getLocationInParent());
	    	
	    	boolean exculde = false;
	    	if(!flg_exculde_all_false)//�ǂꂩ�P�ł����O�����݂���B
	    			exculde = checkExclude(node);
//	    	System.out.println(existLiterals);
	    	
	    	if(!exculde && !isInInnerClass(node)){
	    		//saveTestsite(node);
	    		node_list.add(node);
	    	}
	        return super.visit(node);
	    }
	    
	    /*
	     * new �ł̃C���X�^���X����
	     */
	    public boolean visit(ClassInstanceCreation node) {
	    	
	    	//Enum�͎擾���Ȃ��B�����ł��Ȃ��B
	    	if(isChildOfEnumDeclaration(node))
	    		return super.visit(node);
	    	
//	    	System.out.println(node);
//	    	System.out.println(node.getLocationInParent());
	    	boolean exculde = false;
	    	if(!flg_exculde_all_false)//�ǂꂩ�P�ł����O�����݂���B
	    			exculde = checkExclude(node);
//	    	System.out.println(existLiterals);
	    	
	    	if(!exculde && !isInInnerClass(node)){
//	    		saveTestsite(node);
	    		node_list.add(node);
	    	}
	        return super.visit(node);
	    }
	}
	
	/*
	 * Enum�`�F�b�N
	 * 
	 */
	private boolean isChildOfEnumDeclaration(ASTNode node){
		//�N���X�錾���擾����B
		while(!(node instanceof TypeDeclaration) && node != null){
			//enum��null���A���Ă��Ă��܂��I�I�@EnumDeclaration�@������ׂ����B ���߂��BresolveBinding�@���Ȃ��B
			node = node.getParent();
		}
		
		if(node == null)
			return true;
		else
			return false;
	}
	
	
	public int getNumOfSites(){
		return numOfSites;
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
			
		    cu.accept(new ASTVisitorImpl());
		    int count = 1;
		    for(ASTNode node: node_list){
		    	//���[�U�[���������L�����Z������
				if(monitor.isCanceled()){
					return;
				}
		    	saveTestsite(node);
		    	String className = AstUtil.getParentTypeNameWithPackageName(node);
				monitor.setTaskName(count +"(���݂̃N���X�̏I���^�X�N��)/"+ node_list.size() + "(���݂̃N���X�̑S�^�X�N��), " + numOfWorkedCompilationUnits + "(�I���N���X��)/" + numOfCompilationUnits + "�i�S�N���X���j, ���v�I���^�X�N��= " + numOfSites + "\n�N���X��: " + className);
				count++;
		    }
		    node_list.clear();
		    numOfWorkedCompilationUnits++;
			monitor.worked(numOfWorkedCompilationUnits);
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
		int height = checkHeight(node);
		
		//�w�荂���ȏ�̃^�X�N�����O
		if(height > max_hight_function_tree){
			return;
		}
		
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
    	List<String> classesInActiveEditor = null; //new ArrayList<String>(KeywordProgramming.getImportedTypes());
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
        
        //�L���X�g�̃^�X�N�����O����B exculde=on && ������"�^�L���X�g"�ɓ�����
        if(flg_exculde_type_down_cast && location.equals("�^�L���X�g"))
        	return;
        
        TestSite ts = new TestSite(class_name, offset, offset, selected_line_start, selected_line_end, selected_length, selected_text, desiredReturnType, location, classesInActiveEditor, functionsInActiveEditor, false);
		ts.setASTNode(node);
		
//		for(String m:ts.methodList){
//			System.out.println(m);
//		}
//		if(ts.isSelectedFunctionWithXtimesFrequency(1)){
//			System.out.println(ts.getId());
//			System.out.println("freq>=1:" + ts.isSelectedFunctionWithXtimesFrequency(1));
//			System.out.println("freq>=3:" + ts.isSelectedFunctionWithXtimesFrequency(3));
//			System.out.println("freq>=5:" + ts.isSelectedFunctionWithXtimesFrequency(5));
//			
//			ts.printFunctionList();
//		}
		
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
		numOfSites++;
        
	}

	/*
	 * �R�̏��O�Ώۃ^�X�N�ɓ��Ă͂܂邩���`�F�b�N����B
	 * 1
	 * node�̈����ɒ萔���܂܂�Ă��邩�`�F�b�N����B
	 * �����͍ċA�I�Ɉ�ԉ��̎q�܂Ō���B
	 * �萔��KP�ł͐����ł��Ȃ��̂ŏ��O�������Ƃ��ɗp����B
	 * 
	 * 2
	 * �啶�����A�����郉�x�������݂��邩���`�F�b�N����BisUpperCaseContiguous ���g�p�B
	 * 
	 * 3
	 * �����N���X��` �ł��邩���`�F�b�N����B������ClassInstanceCreation�̂Ƃ��̂݁B
	 * 
	 */
	boolean checkExclude(MethodInvocation mi){
		
		//����
		for(Object e: mi.arguments()){
			int num = checkExcludeInternal(e);
			if(num == 1)
				return true;
		}
		
		//���V�[�o
		Expression e = mi.getExpression();
		int num = checkExcludeInternal(e);
		if(num == 1)
			return true;
		else if(num == -1)
			return false;
		
		return false;
	}
	
	boolean checkExclude(InfixExpression ie){
		List<Expression> e_list = new ArrayList<Expression>();
		e_list.add(ie.getLeftOperand());
		e_list.add(ie.getRightOperand());
		
		for(Expression e: e_list){
			int num = checkExcludeInternal(e);
			if(num == 1)
				return true;
		}
		return false;
	}

	boolean checkExclude(PrefixExpression pe){
		int num = checkExcludeInternal(pe.getOperand());
			if(num == 1)
				return true;
		return false;
	}
	
	boolean checkExclude(ClassInstanceCreation cic){
		//�����N���X��`�͈������Ƃ��ł��Ȃ��B
		if(flg_exculde_anonymous_class_declaration)
		if(cic.getAnonymousClassDeclaration() != null){
//			System.out.println("�����N���X�ł��B");
			return true;
		}
		
		for(Object e: cic.arguments()){
			int num = checkExcludeInternal(e);
			if(num == 1)
				return true;
		}
		return false;
	}

	/*
	 * checkExclude�̒��Ŏg�����\�b�h�B
	 * �Ԃ�l
	 * 1 : true
	 * -1: false
	 * 0 : �܂�����ł��Ȃ��B�i�̂Ŏ��̈����֐i�ށB�j
	 */
	private int checkExcludeInternal(Object e) {
		//1�ԉ��܂ł�����B
		if(e instanceof MethodInvocation){
			MethodInvocation m = (MethodInvocation)e;
			if(checkExclude(m))
				return 1;
			else
				return -1;
		}else if(e instanceof ClassInstanceCreation){
			ClassInstanceCreation c = (ClassInstanceCreation)e;
			if(checkExclude(c))
				return 1;
			else
				return -1;
		}else if(e instanceof InfixExpression){
			// + �Ƃ� - �Ƃ���t����̂͂��������ł��Ȃ��B
			return 1;
//			InfixExpression ie = (InfixExpression)e;
//			if(checkExclude(ie))
//				return 1;
//			else
//				return -1;
		}else if(e instanceof PrefixExpression){
			return 1;
			// + �Ƃ� - �Ƃ���t����̂͂��������ł��Ȃ��B
//			PrefixExpression pe = (PrefixExpression)e;
//			if(checkExclude(pe))
//				return 1;
//			else
//				return -1;
		}else if(e instanceof ArrayAccess){
			//ArrayAccess aa = (ArrayAccess)e;
			return 1;//���s�ł͔z��ɂ͑Ή����Ȃ��B
		}else if(e instanceof CastExpression){
			//�A���S���Y����L���X�g�͐����ł��Ȃ��B
			return 1;
		}else if(e instanceof InstanceofExpression){
			//instanceof�\���͐����ł��Ȃ��B
			return 1;
		}else if(e instanceof ThisExpression){
			//���ݖ��Ή�
			return 1;
		}else if(e instanceof ParenthesizedExpression){
			// �������t���̎��A
			// ����Ȃ̂Ƃ��B�@((DoubleData)d).getSampleRate()
			//�\���ł��Ȃ��B
			return 1;
		}
		
		//MethodInvocation�ł��AClassInstanceCreation�ł��AInfixExpression�ł��Ȃ��A������Ȃ��ꍇ�B
		if(flg_exculde_literals)
		if(e instanceof NumberLiteral || e instanceof CharacterLiteral || e instanceof NullLiteral || e instanceof BooleanLiteral || e instanceof StringLiteral || e instanceof TypeLiteral)
			return 1;
		
		//�啶�����A�����郉�x�������݂��邩���`�F�b�N����B
		if(flg_exculde_contiguous_upper_cases)
		if(e instanceof Expression){
			Expression ex = (Expression)e;
			String name = ex.toString();
			if(isUpperCaseContiguous(name))
				return 1;
		}
		return 0;
	}
	
	/*
	 * �啶�����A�����đ����Ƃ��A
	 * true��Ԃ��B 
	 */
	boolean isUpperCaseContiguous(String name){
		int count = 0;
		for(int i=0; i < name.length(); i++){
			char c = name.charAt(i);
			if(Character.isUpperCase(c)) {
				count++;
				if(count > 1){//2��ȏ�啶�����A��
					return true;
				}
			}else{
				count = 0;
			}
		}
		return false;
	}
	
	/*
	 * �����N���X�錾�̒��̃m�[�h���B
	 */
	boolean isInInnerClass(ASTNode node){
		//�N���X�錾���擾����B
		do{
			if(node == null)
				return false;	//class�ł͂Ȃ��āAenum�錾�̏ꍇ�B
			node = node.getParent();
			
		}while(!(node instanceof TypeDeclaration));
		
		StructuralPropertyDescriptor spd = node.getLocationInParent();
		String id = spd.getId();
	/*
		���ʂ̐錾
		ChildListProperty[org.eclipse.jdt.core.dom.CompilationUnit,types]
		�����N���X�ł̐錾
		ChildListProperty[org.eclipse.jdt.core.dom.TypeDeclaration,bodyDeclarations]
	*/
//		System.out.println(id);
		if(id.equals("bodyDeclarations"))
			return true;
		else 
			return false;
	}
	
	int checkHeight(Object o){
		if(o instanceof MethodInvocation){
			return checkHeight((MethodInvocation)o);
		}else if(o instanceof ClassInstanceCreation){
			return checkHeight((ClassInstanceCreation)o);
		}
		return 1;//�������g
	}
	
	int checkHeight(MethodInvocation mi){
		//���� 
		int height_me = 1;
		
		int height_left = 0;//���V�[�o�̕�
		
		//static �̏ꍇ�@���V�[�o�̍���0
		IMethodBinding imb = mi.resolveMethodBinding();
		int mod = imb.getModifiers();
		if(Modifier.isStatic(mod)){
			height_left = 0;
		}else{
			height_left = checkHeightL(mi);
		}
		
		
		//���������Ƃ��́A�������g�{���V�[�o�i���Ӂj
		int ret_h = height_me + height_left;
		
		//������������Ƃ��A�ǂ̈����̍�����max���B
		int max_height_right = 0;
		
		//����
		for(Object e: mi.arguments()){
			int h = checkHeight(e);
			if(h > max_height_right)
				max_height_right = h;
		}
		
		if(max_height_right > height_left)
			ret_h = height_me + max_height_right;
		
		return ret_h;
	}
	
	int checkHeight(ClassInstanceCreation cic){
		int height = 1;//�������g
		
		//����
		for(Object e: cic.arguments()){
			int num = checkHeight(e);
			height += num;
		}
		
		return height;
	}
	
	//���V�[�o�̍���������B
	int checkHeightL(MethodInvocation mi){
		int h = 1;
		Expression ex = mi.getExpression();
		
		if(ex == null)
			h = 0;
		if(ex instanceof MethodInvocation){
			h += checkHeightL((MethodInvocation)ex);
		}
		
		return h;
	}
	
}
