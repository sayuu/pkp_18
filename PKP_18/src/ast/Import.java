package ast;

import java.util.ArrayList;
import java.util.List;

import keywordProgramming.KeywordProgramming;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;

/**
 * �^����
 * ���ݕҏW����
 * �G�f�B�^�[�A�p�b�P�[�W�A�v���W�F�N�g�Ȃǂ���
 * �C���|�[�g���邽�߂̃��\�b�h�Q
 * 
 * @author sayuu
 *
 */
public class Import {

	   

	//�C���|�[�g���ɑ��݂���L���Ȍ^�����郊�X�g
	public static List<String> classesInImports = new ArrayList<String>();
	//�C���|�[�g���ɑ��݂���L���Ȋ֐������郊�X�g
	public static List<String> functionsInImports = new ArrayList<String>();
	
	//���݃G�f�B�^�ŕҏW���̃R�[�h����擾����^�Ɗ֐�
	public static List<String> classesInLocalCode = new ArrayList<String>();
	public static List<String> functionsInLocalCode = new ArrayList<String>();
	
	//���݃G�f�B�^�ŕҏW���̃N���X����������p�b�P�[�W����擾����^�Ɗ֐�
	public static List<String> classesInPackage = new ArrayList<String>();
	public static List<String> functionsInPackage = new ArrayList<String>();

	//���݃G�f�B�^�ŕҏW���̃N���X����������v���W�F�N�g����擾����^�Ɗ֐�
	public static List<String> classesInProject = new ArrayList<String>();
	public static List<String> functionsInProject = new ArrayList<String>();
		
	public static void clearImportDeclaration(){
		classesInImports.clear();
		functionsInImports.clear();
	}
	
	/*
	 * �C���|�[�g���̂݁A
	 * ���ݑI�����Ă���t�@�C�����ω������ۂɎ擾����B
	 */
//	private static IWorkbenchPart currentSourcepart = null;//���ݑI�����Ă���\�[�X
	private static String  currentSourcepartTitle = null;//���ݑI�����Ă���\�[�X�̃^�C�g��
	private static String  currentPackageName = null;//���ݑI�����Ă���\�[�X�̃p�b�P�[�W��
	
	public static ISelectionListener listener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
        	
        	if (selection instanceof ITextSelection) {
        		String title = sourcepart.getTitle();
        		//null�܂��͌��ݑI�����Ă���\�[�X�̃^�C�g�����ω�����
            	if(currentSourcepartTitle == null || !currentSourcepartTitle.equals(title)){
            		currentSourcepartTitle = title;
            		
            		//�C���|�[�g�����擾����B
            		Import.clearImportDeclaration();
            		Import.getImportDeclaration(null);		
            		
            		
            		CompilationUnit cu = Import.getCompilationUnitOfActiveEditor();
            		if(cu == null)
            			return;
            		String packageName = cu.getPackage().getName().getFullyQualifiedName();
            		//�p�b�P�[�W�����ω�����.
            		if(currentPackageName == null || !currentPackageName.equals(packageName)){
            			currentPackageName = cu.getPackage().getName().getFullyQualifiedName();
            		}
            		
            	}
    		}
        	
        
        	
        }
    };
 
	/*
	 * �C���|�[�g���̓ǂݍ���
	 * 
	 * �������������Ă���p�b�P�[�W(�v���W�F�N�g)�̃N���X�����B
	 */
	public static void getImportDeclaration(CompilationUnit cu){
//		long start = System.currentTimeMillis();
		
		if(cu == null){
			cu = getCompilationUnitOfActiveEditor();
			if(cu == null)
				return;
		}
		
		getImportDeclarationFromCompilationUnit(cu);
		
	
		AstUtil.compactTypeDBstring(classesInImports);
		AstUtil.compactFunctionDBstring(functionsInImports);
		
		getImportDeclarationFromOtherPlaces(cu);
		
		/*
		 * imported_types��imported_fucntions��
		 * �N���A���Ă���A�V�����ǉ�����B
		 * �����ɁAtypes ��functions�ɂ��ǉ������B
		 */
		
//		//types��functions���N���A����B
//		KeywordProgramming.clearStaticTypeAndFunctionFields();
//		//imported_types��imported_fucntions���N���A����B
//		KeywordProgramming.clearImportStaticFields();
//		
//		//original����ǉ�����B
//		KeywordProgramming.putAllOriginalStaticFields();
//		
//		//import���� imported_types��imported_fucntions�ɒǉ�����B
//		//types��functions�ɂ������ɒǉ������B
//		KeywordProgramming.setImportedClassAndFunction(classesInImports, functionsInImports);
		
//		long stop = System.currentTimeMillis();
//		System.out.println("  ���s�ɂ�����������= " + (stop-start) + " �~���b�BAstLocalCode.getImportDeclaration");
	}

	/**
	 * cu��ImportDeclaration��������擾����B
	 * @param cu
	 */
	public static void getImportDeclarationFromCompilationUnit(CompilationUnit cu) {
		List<ImportDeclaration> cu_imports = cu.imports();
		for(ImportDeclaration impd: cu_imports){
			if (impd.isOnDemand() == false){
				//�Ƃ肠�����A�܂Ƃ߂�import��".*"�͔����B
				Name name = impd.getName();
				ITypeBinding itb = name.resolveTypeBinding();
				if(itb != null){
					//static��import��itb��null�ɂȂ�̂ŁA�Ƃ肠���������B
//					AstUtil.getImportDeclarationSuperTypes(impd, classesInImports, functionsInImports);
					String className = itb.getBinaryName();
					AstUtil.createFunctionDbString(itb, className, functionsInImports, true, false);
					
					/*
					 * className�͓����ŁA
					 * ���̊֐�������
					 * �X�[�p�[�^�C�v�̂��̂��擾����B
					 */
					for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
						AstUtil.createFunctionDbString(super_itb, className, functionsInImports, true, false);
						
					}
					
					
					List<List<String>> pairs = new ArrayList<List<String>>();
					AstUtil.getParentChildPairsLists(itb, pairs);
					AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
				}
			}else{
				//�܂Ƃ߂�import�̂Ƃ��B".*"
				Name name = impd.getName();
				IJavaElement javaElement = cu.getJavaElement();
				ICompilationUnit icu = (ICompilationUnit) javaElement;
				IJavaProject ijp = icu.getJavaProject();
				String packageName = name.getFullyQualifiedName();
				try {
					getAllClassesFromPackage(ijp, packageName);
				} catch (JavaModelException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * �G�f�B�^�ŕҏW���Ă���ȊO�́A
	 * ���̏ꏊ����̃C���|�[�g
	 * @param cu
	 */
	public static void getImportDeclarationFromOtherPlaces(CompilationUnit cu) {
		//���̏ꏊ����̃C���|�[�g
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String place_of_import_types = store.getString(PreferenceInitializer.IMPORT_TYPES_FROM);
		
		//���N���X�݂̂���C���|�[�g�̂Ƃ��B
		if(place_of_import_types.equals(PreferenceInitializer.IMPORT_TYPES_FROM_MY_CLASS))
			return;
		
		
		IJavaElement javaElement = cu.getJavaElement();
		ICompilationUnit icu = (ICompilationUnit) javaElement;
		IJavaProject ijp = icu.getJavaProject();
		
		/*
		 * �������������Ă���p�b�P�[�W���Œ�`����Ă���N���X�Q������B
		 */
		if(place_of_import_types.equals(PreferenceInitializer.IMPORT_TYPES_FROM_MY_PACKAGE)){

			try {
				if(icu.getPackageDeclarations() != null && icu.getPackageDeclarations().length > 0){
					IPackageDeclaration ipd = icu.getPackageDeclarations()[0];
					String myPackageName = ipd.getElementName();
					getAllClassesFromPackage(ijp, myPackageName);
				}
			} catch (JavaModelException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
			return;
		}
		
		/*
		 * �������������Ă���v���W�F�N�g���Œ�`����Ă���N���X�Q������B
		 */
		try {
			getAllClassesFromProject(ijp);
		} catch (JavaModelException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * ���݃G�f�B�^��ŕҏW����CompilationUnit���擾����
	 * 
	 * @return cu
	 */
	public static CompilationUnit getCompilationUnitOfActiveEditor() {
		CompilationUnit cu;
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		if(page == null)
			return null;
		
		IEditorPart editorPart = page.getActiveEditor();
		IEditorInput editorInput = editorPart.getEditorInput();
		ICompilationUnit icu = (ICompilationUnit) editorInput.getAdapter(IJavaElement.class);
		
		//java�̃\�[�X�R�[�h�łȂ��ꍇ�͔�����B
		if(icu == null)
			return null;
		
		//���ݕҏW���̃\�[�X�t�@�C��(1�̃N���X)��AST���쐬����
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(icu);
		parser.setResolveBindings(true);//Bindings�̃Z�b�g
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		ASTNode rootAstNode = parser.createAST(new NullProgressMonitor());//���� rootAstNode �� ASTNode.COMPILATION_UNIT �ł���B
		cu = (CompilationUnit)rootAstNode;
		return cu;
	}
	
	/**
	 * �w�� packageName �ȉ��ɂ���
	 * �N���X�����擾����
	 * 
	 * @param ijp
	 * @param packageName
	 * @throws JavaModelException
	 */
	private static void getAllClassesFromPackage(IJavaProject ijp, String packageName) throws JavaModelException{

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject project = root.getProject(ijp.getElementName());
        IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
        for (IPackageFragment mypackage : packages) {
        	if(mypackage.getElementName().equals(packageName))
            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
            	ICompilationUnit []icu_arr = mypackage.getCompilationUnits();
				for(ICompilationUnit icu2: icu_arr){
					if(icu2.getTypes().length > 0){
						IType type = icu2.getTypes()[0];
						String className = type.getFullyQualifiedParameterizedName();
						classesInImports.add(className);
						AstUtil.createFunctionDbString(type, className, functionsInImports, true, false);
						
						/*
						 * className�͓����ŁA
						 * ���̊֐�������
						 * �X�[�p�[�^�C�v�̂��̂��擾����B
						 */
	//					for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
	//						AstUtil.createDbString(super_itb, className, functionsInImports, true);
	//						
	//					}
						
						
						List<List<String>> pairs = new ArrayList<List<String>>();
						AstUtil.getParentChildPairsLists(type, pairs);
						AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
					}
				}
            }

		}
	}
	
	
	/**
	 * �w�� �p�b�P�[�W ijp �ȉ��ɂ���
	 * �N���X�����擾����
	 * 
	 * @param ijp
	 * @throws JavaModelException
	 */
	private static void getAllClassesFromProject(IJavaProject ijp) throws JavaModelException{

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject project = root.getProject(ijp.getElementName());
        IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
        for (IPackageFragment mypackage : packages) {
            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE || mypackage.getKind() == IPackageFragmentRoot.K_BINARY) {
            	ICompilationUnit []icu_arr = mypackage.getCompilationUnits();
				for(ICompilationUnit icu2: icu_arr){
					IType type = icu2.getTypes()[0];
					String className = type.getFullyQualifiedParameterizedName();
					classesInImports.add(className);
					AstUtil.createFunctionDbString(type, className, functionsInImports, true, false);
					
					/*
					 * className�͓����ŁA
					 * ���̊֐�������
					 * �X�[�p�[�^�C�v�̂��̂��擾����B
					 */
//					for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
//						AstUtil.createDbString(super_itb, className, functionsInImports, true);
//						
//					}
					
					
					List<List<String>> pairs = new ArrayList<List<String>>();
					AstUtil.getParentChildPairsLists(type, pairs);
					AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
				}
            }

		}
	}
/*
 * 	ijp.getAllPackageFragmentRoots();
 *
 *	�����낤�Ƃ���ƁA
 * �܂��G���[���łĂ��Ė������̃\�[�X�R�[�h���܂ރp�b�P�[�W�̒��g����邱�Ƃ��ł��Ȃ��B
 * 
 * ICompilationUnit []icu_arr = ipf.getCompilationUnits();
 * 
 * icu_arr���A��̔z��ɂȂ�B
 * 
 *
 /	
//	private static void getAllClassesFromPackage(IJavaProject ijp, String packageName) throws JavaModelException{
//		IPackageFragmentRoot []roots = ijp.getAllPackageFragmentRoots();
//		for(IPackageFragmentRoot root: roots){
//			if (root.getKind() == IPackageFragmentRoot.K_SOURCE){//src�t�H���_������B
//				for(IJavaElement je: root.getChildren()){//src�t�H���_�̒��̃p�b�P�[�W������B
//					IPackageFragment ipf = (IPackageFragment)je;
//					if(ipf.getElementName().equals(packageName)){//�w�肵���p�b�P�[�W�̂ݒǉ��B
//						ICompilationUnit []icu_arr = ipf.getCompilationUnits();
//						
////						IJavaElement j_c[] = ipf.getChildren();
////                    	Object o[] = ipf.getNonJavaResources();
////                        for(IJavaElement e: j_c){
////                        	System.out.println(e);
////                        }
////                            
////                        for(Object b:o){
////                        	System.out.println(b);
////                        }
//                        
//						for(ICompilationUnit icu2: icu_arr){
//							IType type = icu2.getTypes()[0];
//							String className = type.getFullyQualifiedParameterizedName();
//							classesInImports.add(className);
//							AstUtil.createFunctionDbString(type, className, functionsInImports, true);
//							
//
//							/*
//							 * className�͓����ŁA
//							 * ���̊֐�������
//							 * �X�[�p�[�^�C�v�̂��̂��擾����B
//							 */
////							for(ITypeBinding super_itb: AstUtil.getSuperTypes(itb)){
////								AstUtil.createDbString(super_itb, className, functionsInImports, true);
////								
////							}
//							
//							
//							List<List<String>> pairs = new ArrayList<List<String>>();
//							AstUtil.getParentChildPairsLists(type, pairs);
//							AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
//						}
//						IClassFile[] classes = ipf.getClassFiles();
//						for(IClassFile cf: classes){
//							
//						}
//						return;
//					}
//				}
//			}
//		}
//	}
//	
//	private static void getAllClassesFromProject(IJavaProject ijp) throws JavaModelException{
//		IPackageFragmentRoot []roots = ijp.getAllPackageFragmentRoots();
//		for(IPackageFragmentRoot root: roots){
//			if (root.getKind() == IPackageFragmentRoot.K_SOURCE){//src�t�H���_������B
//				for(IJavaElement je: root.getChildren()){//src�t�H���_�̒��̃p�b�P�[�W������B
//					IPackageFragment ipf = (IPackageFragment)je;
//					
//					ICompilationUnit []icu_arr = ipf.getCompilationUnits();
//					for(ICompilationUnit icu2: icu_arr){
//						
//						IType type = icu2.getTypes()[0];
//						String className = type.getFullyQualifiedParameterizedName();
//						classesInImports.add(className);
//						AstUtil.createFunctionDbString(type, className, functionsInImports, true);
//
//						List<List<String>> pairs = new ArrayList<List<String>>();
//						AstUtil.getParentChildPairsLists(type, pairs);
//						AstUtil.getImportDeclarationSuperTypesAndInterfaces(pairs, classesInImports);
//					}
//				}
//			}
//		}
//	}
	
//	private static void getAllClassesFromProject(IJavaProject ijp) throws JavaModelException{
//		IPackageFragmentRoot []roots = ijp.getAllPackageFragmentRoots();
//		for(IPackageFragmentRoot root: roots){
//			if (root.getKind() == IPackageFragmentRoot.K_SOURCE){//src�t�H���_������B
//				for(IJavaElement je: root.getChildren()){//src�t�H���_�̒��̃p�b�P�[�W������B
//					IPackageFragment ipf = (IPackageFragment)je;
//					ICompilationUnit []icu_arr = ipf.getCompilationUnits();
//					for(ICompilationUnit icu2: icu_arr){
//						
//						IType type = icu2.getTypes()[0];
//						String className = type.getFullyQualifiedParameterizedName();
//						classesInImports.add(className);
//						AstUtil.createDbString(type, className, functionsInImports, true);
//					}
//				}
//			}
//			
//			//java.lang �p�b�P�[�W�̎擾�B
//			/*
//			if(root.getElementName().equals("rt.jar")){
//				for(IJavaElement je: root.getChildren()){
//					IPackageFragment ipf = (IPackageFragment)je;
//					IClassFile[] classes = ipf.getClassFiles();
//					for(IClassFile cf: classes){
//						IType type = cf.getType();
//						String className = type.getFullyQualifiedParameterizedName();
//						
//						if(className.startsWith("java.lang.")){
//
//							System.out.println(className);
//							List<String> list = new ArrayList<String>();
//							AstUtil.createDbString(type, className, list, true);
//							for(String s :list){
//								System.out.println(s);
//							}
//						}
//					}
//				}
//				
//			}
//			*/
//		}
//	}
	
}
