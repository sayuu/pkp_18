package ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import ast.AstNodeLocationMapper.FindResult;

/**
 * �\�[�X�R�[�h�̎��ӏ󋵂��擾����B
 */
public class AstLocalCode {

	//���݃G�f�B�^���ɑ��݂���L���Ȋ֐������郊�X�g
	private static List<String> classesInActiveEditor = new ArrayList<String>();
	//���݃G�f�B�^���ɑ��݂���L���Ȋ֐������郊�X�g
	private static List<String> functionsInActiveEditor = new ArrayList<String>();
	//���[�U�[�̓��̓L�[���[�h
	private static String keywords;
	//�]�܂����Ԃ�l�̌^
	private static String desiredReturnType;
	//�L�[���[�h�̊J�noffset
	private static int keyword_head_offset;
	//�u������镶����
	private static int replacement_length;
	//���ݑI�𒆂̃N���X���i�p�b�P�[�W�����j
    private static String class_name;
	private static int original_offset;	//����offset

    //���P�[�V����
  	/*
  	 * Return�� �̒�
	 * If���@�̒�
	 * �錾��
	 * �����
	 * �n�̕�
	 * ���\�b�h�̈����̒�
	 * �ȂǁB
  	 */
  	private static String location;
  
    /*
     * �G�f�B�^�ŊJ����Ă���N���X���i�p�b�P�[�W�����j���擾����B
     */
	public static String getClassName() {
		return class_name;
	}
	
	public static List<String> getClasses(){
		return classesInActiveEditor;
	}
	
	public static List<String> getFunctions(){
		return functionsInActiveEditor;
	}
	
	public static String getKeywords(){
		return keywords;
	}
	
	public static String getDesiredReturnType(){
		return desiredReturnType;
	}
	
	public static int getKeywordHeadOffset(){
		return keyword_head_offset;
	}
	
	public static int getReplacementLength(){
		return replacement_length;
	}
	
	public static String getLocation(){
		return location;
	}
	
	public static int getOriginalOffset(){
		return original_offset;
	}
	
	//�ϐ���S�ăN���A����B
	public static void clear(){
		classesInActiveEditor.clear();
		functionsInActiveEditor.clear();
		keywords = null;
		desiredReturnType = null;
		keyword_head_offset = -1;
		replacement_length = -1;
	}

    
	
	/**
	 * �\�[�X�R�[�h�̎��ӏ󋵂��擾����B
	 * ��̓I�ɂ́A
	 * offset�ʒu����A���̓L�[���[�h�ƕԂ�l�̌^�ƁA���ӂ̌^(Type)�ƁA�֐�(Function)���擾���郁�\�b�h 
	 * 
	 * @param source 1�N���X���̃\�[�X�R�[�h
	 * @param offset ���݂̃L�����b�g�ʒu
	 * @param isCompleteCode ���S�ȃR�[�h���A�s���S�i�L�[���[�h���́j���B
	 * @param cu	1�N���X����CompilationUnit (null�ł�OK)
	 * @param nearestAstNode	���݂̃L�����b�g�ʒu��AstNode (null�ł�OK)
	 */
	public static void getLocalInfomation(String source, int offset, int selected_length, boolean isCompleteCode, ICompilationUnit icu, CompilationUnit cu, ASTNode nearestAstNode) {
		boolean flg_exist_cu = false;
		boolean flg_exist_astnode = false;
		if(cu != null){
			flg_exist_cu = true;
		}
		if(nearestAstNode != null){
			flg_exist_astnode = true;
		}
		String subS = source.substring(0, offset);
		int offsetLineStart = subS.lastIndexOf('\n') + 1;		//���݃L�����b�g�����݂���s�̓���offset
		String line_head_to_caret = subS.substring(offsetLineStart, offset);//�s������L�����b�g�ʒu�܂Ő؂�����������
		
		/*
		 *  ���݂̃L�����b�g�ʒu�ɂ���nearestAstNode���擾����O�ɁA�O�������K�v�B
		 *  �����ŃL�[���[�h���\�[�X����؂����Ă����B
		 *
		 *  ���ɕϐ��錾�ȂǃL�[���[�h�����̑O��"="������ꍇ�́A
		 *  nearestAstNode�̓��e�����������邱�Ƃ������B
		 *
		 */
		original_offset = offset;//����offset�̕ێ�
		//�L�[���[�h�s�Ɋ܂܂�镶���ɂ���ďꍇ�����B
		if( isCompleteCode == false ){	//&& line_head_to_caret.indexOf("=") != -1 && line_head_to_caret.indexOf("!=") == -1){
			//"="���܂ނȂ�Α�����ł���Ƃ݂Ȃ��B
			/*
			 * �Ⴆ��
			 *  int output = new integer input|
			 * �Ƃ����s���������Ƃ��ɁA�L�����b�g�ʒu���O��
			 * "new integer input"���L�[���[�h�Ƃ݂Ȃ��āA�����A
			 * int output = ���c��B
			 *
			 * �܂��A�L�[���[�h��������c���Ă����Ȃ���
			 * nearestAstNode�ɑ΂��āA
			 * getLastVariableDeclarationStatement()
			 * ���g�p�����ۂɁA������Ƃ��Ă�ASTNode���擾�ł��Ȃ��炵���B
			 *
			 * �����āA����keyword��"new"�Ȃǂ̗\���̏ꍇ�ɂ��A
			 * ������Ƃ��Ă�ASTNode���擾�ł��Ȃ��̂ŁA
			 *
			 * �ȑO�͌��̃L�[���[�h���������c���āE�E�E�Ƃ���Ă������A
			 * ������~�߂āA�K���ȕ�����"ABC"�����Ă�����
			 * �ォ��폜���邱�Ƃɂ���B
			 *
			 * ���̌��ICompilationUnit��copy (getWorkingCopy)
			 * ���g�����Ƃɂ���āA
			 * ����source�ɂ͎�������ɍςނ悤�ɂ����B
			 * ���������Čォ�猳source�̏C���̕K�v�͖����Ȃ����B
			 *
			 */
			int idx_equal = line_head_to_caret.lastIndexOf("=");
			/*
			 * �L�����b�g�ʒu(offset)����"="�̈ʒu(idx_equal)�܂Ŗ߂�B
			 * substring��+1���Ȃ��ƍŌ�̕����܂�ł���Ȃ��B
			 *
			 * ����ŁAsource�̏��߂���"="�܂Ŏ��āA
			 * �����offset�ȍ~��source�Ƃ����t����B
			 *
			 */
			String first_keyword = "ABC";
			// ["="�܂ł�source] + "ABC" + [�L�����b�g�ʒu(offset)�ȍ~��source]
			source = source.substring(0, offsetLineStart + idx_equal + 1) + first_keyword + source.substring(offset);
			//source�̕ҏW�ɍ��킹�āAoffset���ړ����Ȃ���΂Ȃ�Ȃ��B
			//�폜����keyword�̕����폜���āA"ABC"�̕���ǉ�����B
			offset = offset - (line_head_to_caret.length() - idx_equal) + 3;
		}

		//�G�f�B�^��̃\�[�X�R�[�h���̎擾

		// ���݃G�f�B�^�ŕҏW���̃\�[�X�t�@�C�����擾����B
		/*
		 *  AST��
		 *  ICompilationUnit������AString �����
		 *  �ǂ��炩��ł������\�����A
		 *  String ���琶������ƁA
		 *  setResolveBindings(true) ���@�\���Ȃ��̂ŁA
		 *  Bindings���~�������́AICompilationUnit���琶������K�v������B
		 *  �ڂ����́AsetResolveBindings()��JavaDoc���Q�ƁB
		 *
		 *�@����ɁA
		 *  �����Ɍ��ݕҏW����ICompilationUnit��
		 *�@�ҏW�����ɁAgetWorkingCopy ���āA
		 *�@���̃R�s�[�ɑ΂��āA
		 *�@��ŕҏW����source���Z�b�g���āA
		 *  AST�𐶐����Ă���B
		 *�@
		 */
		if(icu == null){
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			IEditorPart editorPart = page.getActiveEditor();
			IEditorInput editorInput = editorPart.getEditorInput();
			icu = (ICompilationUnit) editorInput.getAdapter(IJavaElement.class);
		}
		//���ݑI�𒆂̃N���X���i�p�b�P�[�W�����j�̎擾
		try {
			class_name = icu.getTypes()[0].getFullyQualifiedParameterizedName();
		} catch (JavaModelException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		}
		ICompilationUnit icu_copy = null;
		IBuffer buffer = null;

		try {
			icu_copy = icu.getWorkingCopy(new NullProgressMonitor());
			buffer = icu_copy.getBuffer();
		} catch (JavaModelException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		buffer.setContents(source);

		//���ݕҏW���̃\�[�X�t�@�C��(1�̃N���X)��AST���쐬����
		if(isCompleteCode == false || cu == null){
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(icu_copy);
			parser.setResolveBindings(true);//Bindings�̃Z�b�g
			parser.setBindingsRecovery(true);
			parser.setStatementsRecovery(true);
			ASTNode rootAstNode = parser.createAST(new NullProgressMonitor());//���� rootAstNode �� ASTNode.COMPILATION_UNIT �ł���B
			cu = (CompilationUnit)rootAstNode;
		}
		
		//�C���|�[�g���֌W
		//Import.getImportDeclaration�� ��ɍs���Ă������ƂɂȂ����B
		//classesInActiveEditor.addAll(Import.classesInImports);
		//functionsInActiveEditor.addAll(Import.functionsInImports);
		
		//�ϐ��֌W
		List<ITypeBinding> classesInLocalVariables = new ArrayList<ITypeBinding>();

		//���݂̃L�����b�g�ʒu�ɂ���astNode���擾����
		if(isCompleteCode == false){
			FindResult findResult = AstNodeLocationMapper.findNode(cu, offset);
			nearestAstNode = findResult.getNearestNode();
		}else if(isCompleteCode == true && nearestAstNode == null){
			FindResult findResult = AstNodeLocationMapper.findNode(cu, offset + selected_length);
			nearestAstNode = findResult.getNearestNode();
		}
		
		String parentTypeNameOfNearestAstNode = AstUtil.getParentTypeNameWithPackageName(nearestAstNode);
		
		//���ݕҏW���̃N���X�̏��(Type��Function)���擾����
		List<AbstractTypeDeclaration> cu_types = cu.types();
//
		for(int i = 0; i < cu_types.size(); i++){
			AbstractTypeDeclaration atd = cu_types.get(i);
			ITypeBinding itb = atd.resolveBinding();
			boolean type_contains_nearestAstNode = false;	//nearestAstNode���������Ă���N���X���ǂ���
			if(parentTypeNameOfNearestAstNode.equals(itb.getQualifiedName()))
				type_contains_nearestAstNode = true;
			
			AstUtil.createFunctionDbString(itb, itb.getQualifiedName(), functionsInActiveEditor, false, true);
//			if(type_contains_nearestAstNode){
//				//�N���X�������ƕ�����ɂ����̂ŏ����B
//				//AstUtil.createFunctionDbString(itb, "", functionsInActiveEditor, false);
//				AstUtil.createFunctionDbString(itb, "this", functionsInActiveEditor, false);
//			}
			/*
			 * className�͓����ŁA
			 * ���̊֐������X�[�p�[�^�C�v�Œ�`����Ă�����̂��擾����B
			 */
			for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
				AstUtil.createFunctionDbString(super_itb, itb.getQualifiedName(), functionsInActiveEditor, false, true);
//				if(type_contains_nearestAstNode){
//					//�N���X�������ƕ�����ɂ����̂ŏ����B
//					//AstUtil.createFunctionDbString(super_itb, "", functionsInActiveEditor, false);
//					AstUtil.createFunctionDbString(super_itb, "this", functionsInActiveEditor, false);
//				}
			}
			
			List<List<String>> pairs = new ArrayList<List<String>>();
			AstUtil.getParentChildPairsLists(itb, pairs);
			AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInActiveEditor);
			
			
			//�t�B�[���h��type���擾����(generics�̂���)
			for(IVariableBinding ivb: itb.getDeclaredFields()){
				if(type_contains_nearestAstNode || Modifier.isPublic(ivb.getModifiers()))
					classesInLocalVariables.add(ivb.getType());
			}
		}
//
	
		//���݈ʒu�̃��\�b�h��`���擾����B(���݈ʒu�����\�b�h��`���ł̂ݓ���)
		MethodDeclaration methodDec = AstUtil.getParentMethodDeclaration(nearestAstNode);

		if(methodDec != null){
			//���\�b�h�̃p�����[�^�̎擾
			List<SingleVariableDeclaration> params = methodDec.parameters();
			for(SingleVariableDeclaration s: params){
				IVariableBinding ivb =  s.resolveBinding();
				functionsInActiveEditor.addAll(AstUtil.iVariableBindingToDbString(ivb, "", false, true));
				classesInLocalVariables.add(ivb.getType());
			}
		}
		//���\�b�h�̃u���b�N�����Ő錾����Ă��郍�[�J���ϐ��̎擾

		List<ASTNode> vdsList = AstUtil.getUpperAllVariableDeclarationStatements(nearestAstNode);
		if(vdsList != null)
		for(ASTNode n: vdsList){
			if(n.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT){
				VariableDeclarationStatement vds = (VariableDeclarationStatement)n;
				List<VariableDeclarationFragment> vdfList = vds.fragments();//���ꂪ���������ԂƂ������̂��ǂ������
				VariableDeclarationFragment vdf = vdfList.get(0);//1���������͂��Ȃ̂ł�������B
				IVariableBinding ivb =  vdf.resolveBinding();
				functionsInActiveEditor.addAll(AstUtil.iVariableBindingToDbString(ivb, "", false, true));
				classesInLocalVariables.add(ivb.getType());
				
			}else if(n.getNodeType() == ASTNode.SINGLE_VARIABLE_DECLARATION){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)n;
				IVariableBinding ivb = svd.resolveBinding();
				functionsInActiveEditor.addAll(AstUtil.iVariableBindingToDbString(ivb, "", false, true));
				classesInLocalVariables.add(ivb.getType());
			}else if(n.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION){//for���̒��̃J�E���^i�Ȃǂ̎擾
				VariableDeclarationExpression vde = (VariableDeclarationExpression)n;
				List<VariableDeclarationFragment> vdfList = vde.fragments();//���ꂪ���������ԂƂ������̂��ǂ������
				VariableDeclarationFragment vdf = vdfList.get(0);//1���������͂��Ȃ̂ł�������B
				IVariableBinding ivb =  vdf.resolveBinding();
				functionsInActiveEditor.addAll(AstUtil.iVariableBindingToDbString(ivb, "", false, true));
				classesInLocalVariables.add(ivb.getType());
			}
		}

		//�V��������ꂽType�̂����AGenerics�̂��̂ɂ��āA����Type�ɏ�������Function��V������������B
		for(ITypeBinding t: classesInLocalVariables){
			classesInActiveEditor.add(AstUtil.getITypeBindingName(t, false));//Type�Ƃ��Ēǉ�����B
			if(t.isParameterizedType()){
				// Object<Something> �̌`�B ��̓I�ȃN���X�������Ă���B
				//�ł��Ainterface Collections<? extends E> �͂������ɂ��Ă��܂��B�܂��������B
				AstUtil.createFunctionDbString(t, t.getQualifiedName(), functionsInActiveEditor, false, false);
			}
		}

		// ������̏ꍇ�̂� 
		//if(line_head_to_caret.indexOf("=") != -1 && line_head_to_caret.indexOf("!=") == -1){
			offset =  original_offset;//��ŕύX��������offset�ɖ߂��B
		//}
		
//�L�[���[�h�̓X�y�[�X��؂��String
		keywords = line_head_to_caret.trim();//�O���ƌ���̋󔒕������폜
		String line_head_to_caret_deleted_forward_spaces = line_head_to_caret.replaceAll("^\\s+", "");//�O���̋󔒕����݂̂��폜
		keyword_head_offset = offset - line_head_to_caret_deleted_forward_spaces.length();//�L�[���[�h�̊J�noffset
//�Ԃ�l�̌^�̐錾�A�f�t�H���g�l�̐ݒ�
		desiredReturnType = "java.lang.Object";
		
		AstUtil.keywords = keywords;
		AstUtil.keyword_head_offset = keyword_head_offset;
		AstUtil.desiredReturnType = desiredReturnType;
		AstUtil.japanese_label = "���̑�";
//�Ԃ�l�̌^�̏���
		if(isCompleteCode == false){
//			int nodeType = AstUtil.inspectNodeType(nearestAstNode);
//			System.out.println(AstUtil.nodeTypeToString(nodeType));
			
			getKeywordAndDesiredReturnType(nearestAstNode, methodDec);
			
//			AstUtil.inspectDesiredReturnTypeAndKeywords(methodDec, nearestAstNode, nodeType);
			
			//�ǂ���AST�����ŏ�������̂͂���ς��_�����B
//			AstUtil.inspectDesiredReturnTypeAndLocation2(methodDec, nearestAstNode, false);
			
		}else{
	//		StructuralPropertyDescriptor spd =  nearestAstNode.getLocationInParent();
	//		System.out.println("node: " + nearestAstNode);
	//		System.out.println("node type: " + ASTNode.nodeClassForType(nearestAstNode.getNodeType()));
	//		System.out.println("node type parent: " + ASTNode.nodeClassForType(nearestAstNode.getParent().getNodeType()));
	//		System.out.println("toString: " + spd.toString());
	//		System.out.println("getClass: " + spd.getClass());
	//		System.out.println("getNodeClass: " + spd.getNodeClass().getName());
	//		System.out.println("getNodeClass: " + spd.getNodeClass().getSimpleName());
	//		System.out.println("getNodeClass: " + spd.getNodeClass().getCanonicalName());
	//		System.out.println("id: " + spd.getId());
	//		System.out.println("is child list: " + spd.isChildListProperty());
	//		System.out.println("is child: " + spd.isChildProperty());
	//		System.out.println("is simple: " + spd.isSimpleProperty());
			
//			AstUtil.inspectDesiredReturnTypeAndLocation(methodDec, nearestAstNode, flg_exist_astnode);
			
			
			if(flg_exist_cu == false){
				int nodeType = AstUtil.inspectNodeType(nearestAstNode);
				AstUtil.inspectDesiredReturnTypeAndKeywords(methodDec, nearestAstNode, nodeType);
			}else{
				AstUtil.inspectDesiredReturnTypeAndLocation(methodDec, nearestAstNode, flg_exist_astnode);
			}
			
			keywords = AstUtil.keywords;
			keyword_head_offset = AstUtil.keyword_head_offset;
			desiredReturnType = AstUtil.desiredReturnType;
			location = AstUtil.japanese_label;
	//		location = AstUtil.nodeTypeToJapanese(nodeType);
		}


		desiredReturnType = AstUtil.formatTypeName(desiredReturnType);
//�Ԃ�l�̌^�̏��� �����܂�

		replacement_length = keywords.length();
		//trim����B
		keywords = keywords.trim();
		//�L�[���[�h�̏���
		keywords = AstUtil.formatKeywords(keywords);
	}

	private static void getKeywordAndDesiredReturnType(ASTNode nearestAstNode,
			MethodDeclaration methodDec) {
		//�L�[���[�h�s�Ɋ܂܂�镶���ɂ���ďꍇ�����B
		if(keywords.indexOf("!=") != -1){
			//���l�̔�r
			int id = keywords.indexOf("!=");
			keyword_head_offset += id + 2;	// �u���J�n�ʒu��"!="�̎��̕�������B
			keywords = keywords.substring(id + 1);
			desiredReturnType = "java.lang.Integer";
		}else if(keywords.indexOf("=") != -1){//"="��"return"��"="�̕��̗D��x���������Ȃ����task05�ɑΉ��ł��Ȃ��I
			//"="���܂ޕ�����Ȃ�ΐ錾���������͑�����ł���Ƃ݂Ȃ��B
			//"="�̉E�ӂ��L�[���[�h�B���ӂ�desiredType�ƔF���B
			int id = keywords.indexOf("=");
			keyword_head_offset += id + 1;// �u���J�n�ʒu��"="�̎��̕�������B
			keywords = keywords.substring( id + 1 );//"="�̎��̈ʒu����؂���
			//�ϐ��錾�̃m�[�h���擾����
			VariableDeclarationStatement v = AstUtil.getLastVariableDeclarationStatement(nearestAstNode);
			if(v != null){
				Type t =  v.getType();
				desiredReturnType = AstUtil.getITypeBindingName(t.resolveBinding(), false);
			}else{
				//v=null�Ȃ�Α�����̂͂��B
				Assignment assign = AstUtil.getLastAssignmentNode(nearestAstNode);
				if(assign != null){
					desiredReturnType = AstUtil.getITypeBindingName(assign.resolveTypeBinding(), false);
				}
			}
		}else if(keywords.startsWith("return")){
			//�擪�̕�����"return"�Ȃ�΁A���̊֐��̕Ԃ�l���AdesiredType
			keyword_head_offset += 7;// 6("return"�̒���) + 1(�X�y�[�X�̕�) �����L�[���[�h�J�n�ʒu�������B
			if(keywords.length() > 7)
				keywords = keywords.substring(7);//"return"�̎��̈ʒu����؂���
			else
				keywords = "";
			desiredReturnType = AstUtil.getITypeBindingName(methodDec.resolveBinding().getReturnType(), false);
		}else if(keywords.indexOf("(") != -1){
			//"("���܂ޕ�����Ȃ�΁A"("�̎�O�̊֐��̈�����desiredType
			//�����J�b�R������ꍇ�ǂ�����񂾂낤�H
			//x(y(z(keywords)))�Ƃ����ꍇ�AlineIncludeKeywords��"x(y(z(keywords"�ƂȂ�B
			//�uz�����߂�����̌^�v�Ƃ������ƂɂȂ�B
			//z�̈�����1�Ȃ�ȒP���Ȃ��B2�ȏゾ�Ƃǂ��Ȃ�񂾂낤�B
			//"x(y(z(val1, val2, keywords"�@����ȏꍇ�B
			//�uz��3�Ԗڂ̈����̌^�Ɉ�v�v�Ƃ������ƂɂȂ�B
			//���������͌�񂵂ɂ���ׂ��B
			int id = keywords.lastIndexOf("(");//�Ō��"("
			keyword_head_offset += id + 2;// �u���J�n�ʒu��"("�̎��̎��̕�������B
			keywords = keywords.substring(id + 1);// "("�̎��̕������炪�L�[���[�h�ł���B
			MethodInvocation mi = AstUtil.getParentMethodInvocation(nearestAstNode);
//			if(isCompleteCode)
//				mi = AstUtil.getParentMethodInvocation(mi);//�������ꂽ�R�[�h�̂Ƃ��́A�����P������ǂ�B
			if(nearestAstNode.getNodeType() == ASTNode.SIMPLE_NAME)
				mi = AstUtil.getParentMethodInvocation(mi);	//SimpleName�Ȃ�΁A�����P������ǂ�B
			ITypeBinding[] itbs = mi.resolveMethodBinding().getParameterTypes();
			desiredReturnType = AstUtil.getITypeBindingName(itbs[0], false);//���������͌��
		}
	}

}
