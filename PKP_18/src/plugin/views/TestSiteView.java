package plugin.views;

import experiment1.LocalSearch2;
import experiment1.LocalSearch2Job;
import experiment1.Result;
import experiment2.Experiment2;
import experiment2.Experiment2Job;
import experiment3.Experiment3;
import experiment3.Experiment3Job;
import gridSearch.GridSearchJob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import keywordProgramming.ExplanationVector;
import keywordProgramming.KeywordProgramming;
import keywordProgramming.KeywordProgrammingJob;
import keywordProgramming.Params;

import localSearch.LocalSearch;
import localSearch.LocalSearchJob;
import logging.LogControl;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ast.AstLocalCode;
import ast.Import;

import plugin.activator.Activator;
import plugin.preference.PreferenceInitializer;
import plugin.testSite.TestSite;
import plugin.testSite.TestSiteComparator;
import plugin.testSite.exp.TsSync;
import plugin.testSite.exp.TsThread;

public class TestSiteView extends org.eclipse.ui.part.ViewPart{

	private CheckboxTableViewer viewer; 
	private Composite fParent;
	
	private IAction importReloadAction;//�C���|�[�g���������[�h����A�N�V����
	
  	private IAction refreshAction;//��ʂ����t���b�V������A�N�V����
  	private IAction deleteAction;//���ڂ��폜����A�N�V����
  	private IAction localSearchAction;//���[�J���T�[�`������A�N�V����
  	private IAction gridSearchAction;//�O���b�h�T�[�`������A�N�V����
  	
  	private IAction settingAction;//�����̏d�݂̏����l�̐ݒ�A�N�V����
  	
  	private IAction selectAllAction;//�S�Ẵ^�X�N��I������A�N�V����
  	private IAction unSelectAllAction;//�S�Ẵ^�X�N��I����������A�N�V����
  	
  	private IAction countTaskAction;//�v���p�e�B�B(�^�X�N�����J�E���g����)
  	
  	private IAction selectedTaskPropetyAction;//�^�X�N�̃v���p�e�B��\������
  	
  	private IAction kpAction;//�L�[���[�h�v���O���~���O�̎��s�A�N�V����

  	private IAction arrangeTaskAction1;//�^�X�N�𐮗�����A�N�V����
  	private IAction arrangeTaskAction2;//�^�X�N�𐮗�����A�N�V����
  	
  	private IAction experiment1Action;//�����̂��߂̃A�N�V����
  	
  	private IAction experiment1Action_exp;//�����̂��߂̃A�N�V����
  	
  	private IAction experiment1_renzoku_Action;//�����̂��߂̃A�N�V����
  	
  	private IAction experiment1_renzoku_Action2;//�����̂��߂̃A�N�V����
  	
  	private IAction experiment1_renzoku_exp_Action;//�����̂��߂̃A�N�V����
  	
  	private IAction experiment1_renzoku_exp_short_Action;//�����̂��߂̃A�N�V����
  	
  	private IAction experiment1ByClassAction;//�����̂��߂̃A�N�V���� �N���X�ꊇ
  	
  	private IAction experiment2Action;//�����̂��߂̃A�N�V����
  	private IAction experiment2ByKeywordNumAction;//�����̂��߂̃A�N�V���� �@�w��L�[���[�h���̃^�X�N�̂ݎ��s�B
  	
  	private IAction experiment3ByKeywordNumAction;//����3 �����Q�̕p�x����
  	
  	private IAction experiment4Action;	//����4 �S�g�ݍ��킹���ρB
  	
  	private IAction experiment234Action;	//����234 �����ɂ��B
  	
  	private IAction taskViewAction;	//�^�X�N�ꗗ�A�N�V����
  	
  	private IAction taskRandomSelectAction;	//�^�X�N���Ԉ���

  	private IAction verifyAction;//�^�X�N�̐��������؃A�N�V����
  	
  	private IAction idFilterAction;//�^�X�N��id�ɂ��t�B���^�����O
  	private IAction classNameFilterAction;//�^�X�N��id�ɂ��t�B���^�����O
  	 
  	private String id_filtering = "";//�t�B���^�����O����id
  	private String className_filtering = "";//�t�B���^�����O����className
  	
  	private IAction openFileAction;//�^�X�N�̃e�L�X�g�t�@�C�����J��
  	
	@Override
	public void createPartControl(Composite parent) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		//�C���|�[�g���ǂݍ��݁B���X�i�̓o�^
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		window.getSelectionService().addSelectionListener(Import.listener);
		
		//�C���|�[�g���ǂݍ��݁B���炩���߃G�f�B�^��ŊJ���Ă���t�@�C���ɑΉ�����
//		AstLocalCode.clearImportDeclaration();
//		AstLocalCode.getImportDeclaration();
		
		fParent = parent;
		
//		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER);
//		Table table = viewer.getTable();
		
		Table table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		
		viewer = new CheckboxTableViewer(table);
		
		viewer.setContentProvider(new ArrayContentProvider());
		
		viewer.addDoubleClickListener(new ViewerDoubleClickListener(viewer));
	
		IdFilter idFilter = new IdFilter();
		viewer.addFilter(idFilter);
		
		ClassNameFilter classNameFilter = new ClassNameFilter();
		viewer.addFilter(classNameFilter);
		
//		TableViewerColumn viewerColumn_check = new TableViewerColumn(viewer, SWT.LEFT);
//		//viewerColumn_check.setLabelProvider(new ClassNameLabelProvider());
//		TableColumn tableColumn_check = viewerColumn_check.getColumn();
//		tableColumn_check.setText("�I��");
//		tableColumn_check.setWidth(100); // �������� pack()
//		viewerColumn_check.setEditingSupport(new CheckboxEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_id = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_id.setLabelProvider(new IdLabelProvider());
		TableColumn tableColumn_id = viewerColumn_id.getColumn();
		tableColumn_id.setText("ID");
		tableColumn_id.setWidth(150); // �������� pack()
		
		TableViewerColumn viewerColumn_className = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_className.setLabelProvider(new ClassNameLabelProvider());
		TableColumn tableColumn_className = viewerColumn_className.getColumn();
		tableColumn_className.setText("�����p�b�P�[�W�ƃN���X");
		tableColumn_className.setWidth(220); // �������� pack()
//		tableColumn_className.pack();
		
		/*
		TableViewerColumn viewerColumn_offset = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_offset.setLabelProvider(new OffsetLabelProvider());
		TableColumn tableColumn_offset = viewerColumn_offset.getColumn();
		tableColumn_offset.setText("�I�t�Z�b�g");
//		tableColumn_offset.setWidth(50); // �������� pack()
		tableColumn_offset.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		*/
		
		TableViewerColumn viewerColumn_startLine = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_startLine.setLabelProvider(new StartLineLabelProvider());
		TableColumn tableColumn_startLine = viewerColumn_startLine.getColumn();
		tableColumn_startLine.setText("�J�n�s");
//		tableColumn_startLine.setWidth(50); // �������� pack()
		tableColumn_startLine.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_location = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_location.setLabelProvider(new LocationLabelProvider());
		TableColumn tableColumn_location = viewerColumn_location.getColumn();
		tableColumn_location.setText("���P�[�V����");
		tableColumn_location.setWidth(150); // �������� pack()
//		tableColumn_text.pack();
		
		TableViewerColumn viewerColumn_retType = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_retType.setLabelProvider(new ReturnTypeLabelProvider());
		TableColumn tableColumn_retType = viewerColumn_retType.getColumn();
		tableColumn_retType.setText("�]�܂����Ԃ�l");
		tableColumn_retType.setWidth(320); // �������� pack()
//		tableColumn_retType.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_text = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_text.setLabelProvider(new TextLabelProvider());
		TableColumn tableColumn_text = viewerColumn_text.getColumn();
		tableColumn_text.setText("�����������o��(�I�������e�L�X�g)");
		tableColumn_text.setWidth(320); // �������� pack()
//		tableColumn_text.pack();
		viewerColumn_text.setEditingSupport(new TextCellEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_keyword = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_keyword.setLabelProvider(new KeywordLabelProvider());
		TableColumn tableColumn_keyword = viewerColumn_keyword.getColumn();
		tableColumn_keyword.setText("���̓L�[���[�h");
		tableColumn_keyword.setWidth(320); // �������� pack()
//		tableColumn_text.pack();
		//�Z�����G�f�B�b�g�\�ɂ���B
		viewerColumn_keyword.setEditingSupport(new KeywordCellEditingSupport(viewer));

		TableViewerColumn viewerColumn_saveTime = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_saveTime.setLabelProvider(new SaveTimeLabelProvider());
		TableColumn tableColumn_saveTime = viewerColumn_saveTime.getColumn();
		tableColumn_saveTime.setText("�ۑ�����");
		tableColumn_saveTime.setWidth(190); // �������� pack()
		
		viewer.setInput(getItems());
//		viewer.setAllChecked(true);
		
		//���t���b�V���E�A�N�V�����ǉ��B
		registerAction();
	}

	@Override
	public void setFocus() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		viewer.getControl().setFocus();
	}

	private List<TestSite> getItems(){
		List<TestSite> list = new ArrayList<TestSite>();
		
		String path = TestSite.TestSiteFolder;
		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

	    File[] classFolders = dir.listFiles();
	    for (File folder: classFolders) {
	        File[] files = folder.listFiles();
	        for(File file: files){
	        	TestSite t = new TestSite(file, false);
	        	list.add(t);
	        }
	    }
	    
	  //���Ԃ̍~���Ƀ\�[�g����B
	    TestSiteComparator comp = new TestSiteComparator();
	    Collections.sort(list, comp);
	    
		return list;
	}
	
	private void registerAction(){
		//���t���b�V���A�N�V�������쐬
		importReloadAction = new Action(){
			public void run(){
				Import.clearImportDeclaration();
				Import.getImportDeclaration(null);
			}
		};
		importReloadAction.setText("import�֐��̃����[�h");
				
		//���t���b�V���A�N�V�������쐬
		refreshAction = new Action(){
			public void run(){
				viewer.setInput(getItems());
			}
		};
		refreshAction.setText("���t���b�V��");
//		refreshAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_REFRESH));
//		refreshAction.setImageDescriptor(Activator.getImageDescriptor(Activator.IMG_REFRESH, "refresh.gif"));
		refreshAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.browser", "$nl$/icons/elcl16/nav_refresh.gif"));

		//�S�I���A�N�V�������쐬
		selectAllAction = new Action(){
			public void run(){
				viewer.setAllChecked(true);
			}
		};
		selectAllAction.setText("�S�đI��");
		selectAllAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/elcl16/step_done.gif"));

		//�S�I�������A�N�V�������쐬
		unSelectAllAction = new Action(){
			public void run(){
				viewer.setAllChecked(false);
			}
		};
		unSelectAllAction.setText("�S�đI������");
		unSelectAllAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.debug.ui", "$nl$/icons/full/dlcl16/disabled_co.gif"));

				
		//�폜�A�N�V�������쐬
		deleteAction = new Action(){
			public void run(){
				//�I�����ꂽ�A�C�e�����폜����B
				TestSite[] list = getCheckedItems();
				
				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "�^�X�N�̍폜", "�폜����^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"�^�X�N�̍폜",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N���폜���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				//�I�����ꂽ�t�@�C����S�č폜����B
				for(TestSite site:list){
					site.deleteFile();
				}
				//���t���b�V��
				viewer.setInput(getItems());
			}
		};
		deleteAction.setText("�^�X�N�폜");
//		deleteAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_DELETE));
		deleteAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/obj16/delete_obj.gif"));
		
		//�^�X�N�̃v���p�e�B�A�N�V����
		selectedTaskPropetyAction = new Action(){
			public void run(){
				//�I�����ꂽ�A�C�e�����폜����B
				TestSite[] list = getCheckedItems();
				
				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "�^�X�N�̃v���p�e�B", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				String line = "";
				
				for(TestSite site:list){
					line += site.toString();
				}
				
				MessageDialog.openInformation(fParent.getShell(), "�L�[���[�h�v���O���~���O", line);
			}
		};
		selectedTaskPropetyAction.setText("�^�X�N�̃v���p�e�B");

		//�L�[���[�h�v���O���~���O�̃A�N�V�������쐬
		kpAction = new Action(){
			public void run(){
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "�L�[���[�h�v���O���~���O", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"�L�[���[�h�v���O���~���O",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��ăL�[���[�h�v���O���~���O���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
					
				//�R���\�[����ʕ\��
				Activator.showConsoleView();
				
				//���������Ȃ̂ŁA�W���u���g��
				KeywordProgrammingJob job = new KeywordProgrammingJob("�L�[���[�h�v���O���~���O�̎��s", list);
				job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
				job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
			}
		};
		kpAction.setText("�L�[���[�h�v���O���~���O���s");
//		localSearchAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_RUN));
		kpAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.cheatsheets", "$nl$/icons/elcl16/skip_task.gif"));

		
		//���[�J���T�[�`�A�N�V�������쐬
		localSearchAction = new Action(){
			public void run(){
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ă��[�J���T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "���[�J���T�[�`", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"���[�J���T�[�`",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��ă��[�J���T�[�`���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				
				LocalSearch ls = new LocalSearch(list, false);
				
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				//�X�e�b�v�����w��
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
				//�����܂ōs�����ۂ��B
				boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
				if(isConvergence == true)
					numOfSteps = -1;
				
				//�R���\�[����ʕ\��
				Activator.showConsoleView();
				
				//���������Ȃ̂ŁA�W���u���g��
				LocalSearchJob job = new LocalSearchJob("���[�J���T�[�`�̎��s", ls, numOfSteps);
				job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
				job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
			}
		};
		localSearchAction.setText("���[�J���T�[�`���s");
//		localSearchAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_RUN));
		localSearchAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.cheatsheets", "$nl$/icons/elcl16/start_task.gif"));

		
		//�O���b�h�T�[�`�A�N�V�������쐬
		gridSearchAction = new Action(){
			public void run(){
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ăO���b�h�T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "�O���b�h�T�[�`", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"�O���b�h�T�[�`",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��ăO���b�h�T�[�`���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				
				//�R���\�[����ʕ\��
				Activator.showConsoleView();
				
				//���������Ȃ̂ŁA�W���u���g��
				GridSearchJob job = new GridSearchJob("�O���b�h�T�[�`�̎��s", list);
				job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
				job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
			}
		};
		gridSearchAction.setText("�O���b�h�T�[�`���s");
//		gridSearchAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_RUN));
		gridSearchAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.debug.ui", "$nl$/icons/full/obj16/lrun_obj.gif"));

		
		//�ݒ�A�N�V�������쐬
		settingAction = new Action(){
			public void run(){
				//�v���t�@�����X���擾���āA�_�C�A���O�ɕ\������B
				PreferenceManager pm = PlatformUI.getWorkbench( ).getPreferenceManager();
		        IPreferenceNode kp_node = pm.find("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.preference.page.kp");
		        PreferenceManager pm2 = new PreferenceManager();
		        pm2.addToRoot(kp_node);
		        PreferenceDialog dialog1 = new PreferenceDialog(fParent.getShell(), pm2);
		        dialog1.open();
			}
		};
		
		settingAction.setText("�ݒ�");
//		settingAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_SETTINGS));
		settingAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.debug.ui", "$nl$/icons/full/elcl16/prop_ps.gif"));

		//�v���p�e�B��\������B
		countTaskAction = new Action(){
			public void run(){
				
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					//ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
					List<TestSite> ts_arr = (List<TestSite>) Arrays.asList(getCheckedItems());
					int sum = ts_arr.size();
//					HashMap<String, Integer> map = new HashMap<String, Integer>();
//					for(TestSite ts: ts_arr){
//						String name = ts.getPackageName();
//						if(map.containsKey(name)){
//							int count = map.get(name);
//							map.put(name, count+1);
//						}else{
//							map.put(name, 1);
//						}
//					}
//					int package_sum = map.size();
					
					HashSet<String> set = new HashSet<String>();
					for(TestSite ts: ts_arr){
						String name = ts.getPackageName();
						set.add(name);
					}
					int package_sum = set.size();
					set.clear();
					for(TestSite ts: ts_arr){
						String name = ts.getFullyQualifiedClassName();
						set.add(name);
					}
					int class_sum = set.size();
					
					HashMap<String, Integer> map = new HashMap<String, Integer>();
					for(TestSite ts: ts_arr){
						String name = ts.getLocation();
						if(map.containsKey(name)){
							int count = map.get(name);
							map.put(name, count+1);
						}else{
							map.put(name, 1);
						}
					}
					Iterator<String> it = map.keySet().iterator();
					String s = "";
			        while (it.hasNext()) {
			            String name = it.next();
			            s += name + " = " + map.get(name) + "\n";
			        }
					System.out.println(map.toString());
					String countTasksByClassName = countTasksByClassName(ts_arr);
					
					MessageDialog.openInformation(fParent.getShell(), "�r���[�̃v���p�e�B", "�r���[�ɕ\������Ă���\n�S�^�X�N��= " + sum
							+ "\n�S�p�b�P�[�W��= " + package_sum
							+ "\n�S�N���X��= " + class_sum + "\n"
							+ "�N���X��, �^�X�N��\n"
							+ countTasksByClassName);
				}
				
				

			}
			
			private String countTasksByClassName(List<TestSite> all_task_list) {
				//�N���X���ƃ^�X�N�̃��X�g�̃}�b�v
				TreeMap<String, ArrayList<TestSite>> class_task_map = new TreeMap<String, ArrayList<TestSite>>();
				for(TestSite task: all_task_list){
					String className = task.getFullyQualifiedClassName();
					//�}�b�v�Ƀ��X�g�����݂��Ȃ���Βǉ�
					if(!class_task_map.containsKey(className)){
						class_task_map.put(className, new ArrayList<TestSite>());
					}
					//���X�g�����o���āA�^�X�N��ǉ�����B
					ArrayList<TestSite> task_list = class_task_map.get(className);
					task_list.add(task);
				}
				
				Iterator<String> iter = class_task_map.keySet().iterator();
				String ret = "";
				while(iter.hasNext()){
					String className = iter.next();
					ArrayList<TestSite> task_list = class_task_map.get(className);
					ret += className + ", " + task_list.size() + "\n";
				}
				
				return ret;
			}
		};
		
		countTaskAction.setText("�v���p�e�B");
//				countTaskAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_SETTINGS));
		countTaskAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/obj16/generic_elements.gif"));


		//�^�X�N���R���\�[���ɕ\������B
		taskViewAction = new Action(){
			public void run(){
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
					for(TestSite ts: ts_arr){
						//System.out.println(ts.getId() + ", "+ ts.getSelectedString());
						System.out.println(ts.getSelectedString());
						for(String s: ts.getLocalFunctionNameKeywords()){
							System.out.print(s + ", ");
						}
						System.out.println();
						System.out.println();
					}
				}
			}
			
		};
		
		taskViewAction.setText("�^�X�N�ꗗ");
		
		//�^�X�N���Ԉ���
		taskRandomSelectAction = new Action(){
			public void run(){
				InputDialog dialog =
		                new InputDialog(
		                		fParent.getShell(),
		                        "�^�X�N",
		                        "�^�X�N���Ԉ����Ԋu����͂��Ă��������B",
		                        "3",
		                        null);
		        int ret = dialog.open();
		        if (ret != Window.OK){
		        	return;
		        }
		        
		        int number = Integer.valueOf(dialog.getValue());
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
					for(int i=0; i< ts_arr.size(); i++){
						if(i % number == 0){
							System.out.println(ts_arr.get(i).getId());
						}else{
							ts_arr.get(i).deleteFile();
						}
					}
				}
			}
			
		};
		
		taskRandomSelectAction.setText("�^�X�N�Ԉ���");
		
		//�^�X�N�����A�N�V����
		arrangeTaskAction1 = new Action(){
			public void run(){
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> all_task_list = (ArrayList<TestSite>)o;
					arrangeTasksByClassName(all_task_list);
			        refresh();
				}
			}

			private void arrangeTasksByClassName(ArrayList<TestSite> all_task_list) {
				//�N���X���ƃ^�X�N�̃��X�g�̃}�b�v
				HashMap<String, ArrayList<TestSite>> class_task_map = new HashMap<String, ArrayList<TestSite>>();
				for(TestSite task: all_task_list){
					String className = task.getFullyQualifiedClassName();
					//�}�b�v�Ƀ��X�g�����݂��Ȃ���Βǉ�
					if(!class_task_map.containsKey(className)){
						class_task_map.put(className, new ArrayList<TestSite>());
					}
					//���X�g�����o���āA�^�X�N��ǉ�����B
					ArrayList<TestSite> task_list = class_task_map.get(className);
					task_list.add(task);
				}
				
				Iterator<String> iter = class_task_map.keySet().iterator();
				while(iter.hasNext()){
					String className = iter.next();
					ArrayList<TestSite> task_list = class_task_map.get(className);
					System.out.println("�J�n�F" + className + ", " + task_list.size());
					arrangeTasks(task_list);
				}
			}
			
			/*
			 * TestSite�̃��X�g����A
			 * �d����������폜����B
			 */
			private void arrangeTasks(ArrayList<TestSite> ts_arr) {
				List<Result> result_list = new ArrayList<Result>(); 
				
				for(TestSite ts: ts_arr){
					String str = ts.getSelectedString();
					int odr = -1;//���ʂ͕s���B
					int numKey = ts.getNumOfKeywords();
					int numLT = ts.getNumOfLocalTypes();
					int numLF = ts.getNumOfLocalFunctions();
					result_list.add(new Result(ts.getId(), str, odr, numKey, numLT, numLF));
				}
				System.out.println("==============�d����������폜����=================================");
				
				TreeSet<Result> result_set = new TreeSet<Result>();
				List<Result> deleted_list = new ArrayList<Result>(); 
				
				for(Result r : result_list){
					//�d�����폜
					if(!result_set.contains(r)){
						result_set.add(r);
						System.out.println(r.fTestSiteId + "\t" + r.fSelectedString);
					}else{
						deleted_list.add(r);
					}
				}
				
				System.out.println("=================�폜���ꂽ���̈ꗗ==============================");
				
				for(Result r: deleted_list){
				    System.out.println(r.fTestSiteId + "\t" + r.fSelectedString);
				    for(TestSite ts: ts_arr){
				    	if(r.fTestSiteId.equals(ts.getId())){
				    		ts.deleteFile();
				    	}
				    }
				}
			}
		};
		arrangeTaskAction1.setText("�^�X�N����(�d��������폜)");


		//�^�X�N�����A�N�V����
		arrangeTaskAction2 = new Action(){
			public void run(){
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> all_task_list = (ArrayList<TestSite>)o;
					arrangeTasksByClassName(all_task_list);
			        refresh();
				}
			}

			private void arrangeTasksByClassName(ArrayList<TestSite> all_task_list) {
				//�N���X���ƃ^�X�N�̃��X�g�̃}�b�v
				HashMap<String, ArrayList<TestSite>> class_task_map = new HashMap<String, ArrayList<TestSite>>();
				for(TestSite task: all_task_list){
					String className = task.getFullyQualifiedClassName();
					//�}�b�v�Ƀ��X�g�����݂��Ȃ���Βǉ�
					if(!class_task_map.containsKey(className)){
						class_task_map.put(className, new ArrayList<TestSite>());
					}
					//���X�g�����o���āA�^�X�N��ǉ�����B
					ArrayList<TestSite> task_list = class_task_map.get(className);
					task_list.add(task);
				}
				
				Iterator<String> iter = class_task_map.keySet().iterator();
				while(iter.hasNext()){
					String className = iter.next();
					ArrayList<TestSite> task_list = class_task_map.get(className);
					System.out.println("�J�n�F" + className + ", " + task_list.size());
					arrangeTasks(task_list);
				}
			}
			
			/*
			 * TestSite�̃��X�g����A
			 * generics���폜����B
			 */
			private void arrangeTasks(ArrayList<TestSite> ts_arr) {
				List<Result> result_list = new ArrayList<Result>(); 
				
				for(TestSite ts: ts_arr){
					String str = ts.getSelectedString();
					int odr = -1;//���ʂ͕s���B
					int numKey = ts.getNumOfKeywords();
					int numLT = ts.getNumOfLocalTypes();
					int numLF = ts.getNumOfLocalFunctions();
					result_list.add(new Result(ts.getId(), str, odr, numKey, numLT, numLF));
				}
				System.out.println("==============generics���폜����=================================");
				
				TreeSet<Result> result_set = new TreeSet<Result>();
				List<Result> deleted_list = new ArrayList<Result>(); 
				
				for(Result r : result_list){
					//generics���폜
					if(!r.fSelectedString.contains("<")){
						result_set.add(r);
						System.out.println(r.fTestSiteId + "\t" + r.fSelectedString);
					}else{
						deleted_list.add(r);
					}
				}
				
				System.out.println("=================�폜���ꂽ���̈ꗗ==============================");
				
				for(Result r: deleted_list){
				    System.out.println(r.fTestSiteId + "\t" + r.fSelectedString);
				    for(TestSite ts: ts_arr){
				    	if(r.fTestSiteId.equals(ts.getId())){
				    		ts.deleteFile();
				    	}
				    }
				}
			}
		};
		arrangeTaskAction2.setText("�^�X�N����(generics�폜)");

		//����1
		experiment1Action = new Action(){
			public void run(){

				System.out.println("���O�ɂj�o���邱�ƁI");
				
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ă��[�J���T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "����1", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"����1",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��Ď���1���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				
				LocalSearch2 ls = new LocalSearch2(list, false);
				
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				//�X�e�b�v�����w��
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
				//�����܂ōs�����ۂ��B
				boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
				if(isConvergence == true)
					numOfSteps = -1;
				
				//�R���\�[����ʕ\��
				Activator.showConsoleView();
				
				//���������Ȃ̂ŁA�W���u���g��
				LocalSearch2Job job = new LocalSearch2Job("����1�̎��s", ls, numOfSteps);
				job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
				job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
			}
		};
		experiment1Action.setText("�����P");
		
		//����1exp
		experiment1Action_exp = new Action(){
			public void run(){
				
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ă��[�J���T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "����1", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				String projectName = "";
				
				InputDialog dialog =
		                new InputDialog(
		                		fParent.getShell(),
		                        "�v���W�F�N�g���̓���",
		                        "�v���W�F�N�g������͂��Ă��������B",
		                        "",
		                        null);
		        int ret = dialog.open();
		        if (ret != Window.OK){
		        	return;
		        }
		        
		        projectName = dialog.getValue();
		        
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"����1",
						"�v���W�F�N�g��" + projectName + ". �I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��Ď���1���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				
				
				keywordProgramming.exp.Params para = new keywordProgramming.exp.Params();
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				keywordProgramming.exp.KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.LOCAL_BEST_R);
				para.const_freq = store.getDouble(PreferenceInitializer.CONST_FREQ);
				para.w_arr.add(0, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0));//��
				para.w_arr.add(1, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1));
				para.w_arr.add(2, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2));//��
				para.w_arr.add(3, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3));
				para.w_arr.add(4, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_4));
				
				para.separate_keywords = String.valueOf(store.getBoolean(PreferenceInitializer.SEPARATE_KEYWORDS));
				para.common_subsequence = store.getString(PreferenceInitializer.COMMON_SUBSEQUENCE);
				para.ld_delete = store.getInt(PreferenceInitializer.LD_DELETE);
				para.ld_replace = store.getInt(PreferenceInitializer.LD_REPLACE);
				para.ld_add = store.getInt(PreferenceInitializer.LD_ADD);
				
				para.shortened_input_keywords = store.getString(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS);
				
				long start = System.currentTimeMillis();
				
				runTasksUsingMultiThread(list, para, projectName, start);
				
			    long stop = System.currentTimeMillis();
			    //System.out.println("  ���s�ɂ�����������= " + (stop - start) + " �~���b");
				
			}

			
		};
		experiment1Action_exp.setText("�����Pexp");
		
		//����1
		experiment1_renzoku_exp_short_Action = new Action(){
			public void run(){
				
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ă��[�J���T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "����1", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"����1",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��Ď���1���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
			
				keywordProgramming.exp.Params para = new keywordProgramming.exp.Params();
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				//�d�݂͌Œ�
				keywordProgramming.exp.KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.LOCAL_BEST_R);
				para.const_freq = store.getDouble(PreferenceInitializer.CONST_FREQ);
				para.w_arr.add(0, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0));//��
				para.w_arr.add(1, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1));
				para.w_arr.add(2, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2));//��
				para.w_arr.add(3, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3));
				para.w_arr.add(4, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_4));
				
				//�L�[���[�h����(5���)
				for(int i = 0; i< 5; i++){
					para.shortened_input_keywords = store.getString(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS);
					
					//��������(2���)
					for(int j = 0; j< 2; j++){
						para.separate_keywords = String.valueOf(store.getBoolean(PreferenceInitializer.SEPARATE_KEYWORDS));
						
						//�ގ��x(3��� ld�͕ϐ��𓮂���)
						for(int k = 0; k< 3; k++){
							para.common_subsequence = store.getString(PreferenceInitializer.COMMON_SUBSEQUENCE);
							
							if(k == 2){
								int k_list[] = {0, 1, 2, 4, 8};
								for(int m = 0; m < k_list.length; m++){
									para.ld_delete = 1;
									para.ld_replace = 1 + k_list[m];
									para.ld_add = 1 + 2 * k_list[m];
									//runTasksUsingMultiThread(list, para);			
								}
							}else{
								//runTasksUsingMultiThread(list, para);
							}
						}
					}
				}
			}
		};
		experiment1_renzoku_exp_short_Action.setText("�����P�A��exp short");
		
		//����1
		experiment1_renzoku_Action = new Action(){
			public void run(){
//				System.out.println("���O�ɂj�o���邱�ƁI");
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ă��[�J���T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "����1", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"����1",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��Ď���1���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				
				double a_start = 0.1;
				double a_end = 0.5;
				double a_step = 0.1;
				double a_current = a_start;
				
				while(a_current < a_end){
					ExplanationVector.setConstFreq(a_current);
					
				
					double start = 0.0001;
					double end = 1.0;
					double step = 10;
					double current = start;
					
					//for(int i = 0; i< 2; i++){
					while(current < end){
						System.out.println("a= " + a_current + ", t5= " + current);
						
						ExplanationVector.setWeight(current, 4);
						
						
						LocalSearch2 ls = new LocalSearch2(list, false);
						
						IPreferenceStore store = Activator.getDefault().getPreferenceStore();
						
						//�X�e�b�v�����w��
						int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
						//�����܂ōs�����ۂ��B
						boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
						if(isConvergence == true)
							numOfSteps = -1;
						
						//�R���\�[����ʕ\��
						//Activator.showConsoleView();
						
						//���������Ȃ̂ŁA�W���u���g��
						LocalSearch2Job job = new LocalSearch2Job("����1�̎��s", ls, numOfSteps);
						job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
						job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
						try {
							job.join();
						} catch (InterruptedException e) {
							// TODO �����������ꂽ catch �u���b�N
							e.printStackTrace();
						}
						current *= step;
					}
					a_current += a_step;
				}				
			}
		};
		experiment1_renzoku_Action.setText("�����P�A��");
		
		
		//�ŐV�B
		//����1
		experiment1_renzoku_Action2 = new Action(){
			public void run(){
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ă��[�J���T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "����1", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"����1",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��Ď���1���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
							
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				keywordProgramming.Params para = new Params();
				
				//�L�[���[�h����(5���)
				//�Ȃ�
				//�ꉹ�폜, �擪�R�����A�P�����u���A�P�����}��
				for(int i = 0; i< 5; i++){
					
					switch(i){
					case 0:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_OFF;
						break;
					case 1:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_BOIN_DEL;
						break;
					case 2:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_FIRST_3;
						break;
					case 3:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_REPLACE;
						break;
					case 4:
						para.shortened_input_keywords = PreferenceInitializer.SHORTENED_INPUT_KEYWORDS_ADD;
						break;
					}
					
					store.setValue(PreferenceInitializer.SHORTENED_INPUT_KEYWORDS, para.shortened_input_keywords);//�L�[���[�h�Z�k��

				
					//��������(2���)
					//off, on
					for(int j = 0; j< 2; j++){
						
						switch(j){
						case 0:
							para.separate_keywords = "false";
							break;
						case 1:
							para.separate_keywords = "true";
							break;
						}
						store.setValue(PreferenceInitializer.SEPARATE_KEYWORDS, para.separate_keywords);//�L�[���[�h�Z�k��

						if((i == 0 && j == 0) || (i == 0 && j == 1)){
							continue;
						}
						//�ގ��x(6��� ld�͕ϐ��𓮂���)
						// �Ȃ��B
						// LCS1,2,3,4
						// LD (�폜�A�ǉ��A�u��)�R�X�g�𓮂����B(k=0 1 2 4 8)�Ȃ�5�ʂ�
						// �v�A10���
						for(int k = 0; k< 6; k++){
													
							switch(k){
							case 0:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_OFF;
								break;
							case 1:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS1;
								break;
							case 2:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS2;
								break;
							case 3:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS3;
								break;
							case 4:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LCS4;
								break;
							case 5:
								para.common_subsequence = PreferenceInitializer.COMMON_SUBSEQUENCE_LD;
								break;
							}
							
							store.setValue(PreferenceInitializer.COMMON_SUBSEQUENCE, para.common_subsequence);

							if(k == 5){
								//�Ƃ肠����LD���Ȃ��B
								
//								int k_list[] = {0, 1, 2, 4, 8};
//								for(int m = 0; m < k_list.length; m++){
//									para.ld_delete = 1;
//									para.ld_replace = 1 + k_list[m];
//									para.ld_add = 1 + 2 * k_list[m];
//									long start = System.currentTimeMillis();
//									runKP(list, store);
//								}
							}else{
								//���Z�b�g�@���h���̂��߁B
								para.ld_delete = 0;
								para.ld_replace = 0;
								para.ld_add = 0;
								long start = System.currentTimeMillis();
								runKP(list, store);
							}
						}
					}
				}		
			}

			private void runKP(TestSite[] list, IPreferenceStore store) {
				LocalSearch2 ls = new LocalSearch2(list, false);
			//�X�e�b�v�����w��
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
				
				//���������Ȃ̂ŁA�W���u���g��
				LocalSearch2Job job = new LocalSearch2Job("����1�̎��s", ls, numOfSteps);
				job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
				job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
				try {
					job.join();
//						Thread.sleep(3 * 1000);
				} catch (InterruptedException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
			}
			
		};
		experiment1_renzoku_Action2.setText("�����P�A��2");
				
		//����1
		experiment1_renzoku_exp_Action = new Action(){
			public void run(){
				
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ă��[�J���T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "����1", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"����1",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��Ď���1���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				
				// �����T�̕p�x�̏d��0�̂Ƃ��B
				keywordProgramming.exp.Params para = new keywordProgramming.exp.Params();
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				keywordProgramming.exp.KeywordProgramming.BEST_R = store.getInt(PreferenceInitializer.LOCAL_BEST_R);
				para.const_freq = 0;
				para.w_arr.add(0, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0));//��
				para.w_arr.add(1, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1));
				para.w_arr.add(2, -store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2));//��
				para.w_arr.add(3, store.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3));
				para.w_arr.add(4, 0.0);
				
				//runTasksUsingMultiThread(list, para);
				
				double a_start = 0.1;
				double a_end = 0.5;
				double a_step = 0.1;
				double a_current = a_start;
				
				while(a_current < a_end){

					double start = 0.0001;
					double end = 1.0;
					double step = 10;
					double current = start;
					
					//for(int i = 0; i< 2; i++){
					while(current < end){
						keywordProgramming.exp.Params para1 = new keywordProgramming.exp.Params();
						IPreferenceStore store1 = Activator.getDefault().getPreferenceStore();
						keywordProgramming.exp.KeywordProgramming.BEST_R = store1.getInt(PreferenceInitializer.LOCAL_BEST_R);
						para1.const_freq = a_current;
						para1.w_arr.add(0, -store1.getDouble(PreferenceInitializer.INITIAL_WEIGHT_0));//��
						para1.w_arr.add(1, store1.getDouble(PreferenceInitializer.INITIAL_WEIGHT_1));
						para1.w_arr.add(2, -store1.getDouble(PreferenceInitializer.INITIAL_WEIGHT_2));//��
						para1.w_arr.add(3, store1.getDouble(PreferenceInitializer.INITIAL_WEIGHT_3));
						
						
						para1.w_arr.add(4, current);
						
						//runTasksUsingMultiThread(list, para1);
						
						
						current *= step;
					}
					a_current += a_step;
				}				
			}
		};
		experiment1_renzoku_exp_Action.setText("�����P�A��exp para");
		
		//����1(�N���X����)
		experiment1ByClassAction = new Action(){
			public void run(){
				
				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "���͂����N���X���ƂɎ����P�����s���܂�", resultContainer); 
            	md.open();
            	String text = resultContainer.getText();
           	 	List<String> classNames = Arrays.asList(text.split("[, �@\t\n\r]", -1));
	
				Object o = viewer.getInput();
				if(o instanceof ArrayList){
					ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
					for(String className: classNames){
						if(!className.equals("")){
							ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
							for(TestSite ts: ts_arr){
								if(ts.getClassSingleName().equals(className) || ts.getFullyQualifiedClassName().equals(className)){
									ts_arr_by_class.add(ts);
								}
							}
							
							LocalSearch2 ls = new LocalSearch2(ts_arr_by_class.toArray(new TestSite[0]), false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//�X�e�b�v�����w��
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//�����܂ōs�����ۂ��B
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//�R���\�[����ʕ\��
							Activator.showConsoleView();
							
							//���������Ȃ̂ŁA�W���u���g��
							LocalSearch2Job job = new LocalSearch2Job("����1�̎��s", ls, numOfSteps);
							job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
							job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO �����������ꂽ catch �u���b�N
								e.printStackTrace();
							}
						}
					}
				}
				
			}
		};
		experiment1ByClassAction.setText("�����P(�N���X����)");
		
		
		//����2
		experiment2Action = new Action(){
			public void run(){
				//�e�t�@�C���ɂ��āA
				//�L�[���[�h�v���O���~���O�����Ă��炻�̌��ʂ�p���ă��[�J���T�[�`���s���B
				TestSite[] list = getCheckedItems();
				

				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "����2", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "�o���p�x���Ⴂ���ɂ��i���͂������j�̃L�[���[�h���N�G���Ƃ��܂��B", resultContainer); 
            	md.open();
            	String text = resultContainer.getText();
            	int num_of_keywords = Integer.parseInt(text);
            	
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"����2",
						"�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��Ď���2���s���܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				
				experiment2.Experiment2 ls = new experiment2.Experiment2(list, num_of_keywords, false, true);
				
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
				//�X�e�b�v�����w��
				int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
				//�����܂ōs�����ۂ��B
				boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
				if(isConvergence == true)
					numOfSteps = -1;
				
				//�R���\�[����ʕ\��
				Activator.showConsoleView();
				
				//���������Ȃ̂ŁA�W���u���g��
				experiment2.Experiment2Job job = new experiment2.Experiment2Job("����2�̎��s", ls, numOfSteps);
				job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
				job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
			}
		};
		experiment2Action.setText("����2");
		

		//����2(�����Ɋ܂܂��P�ꐔ���w��)
		/*
		 * �����Ɋ܂܂��P�ꐔx���w�肷��ƁA
		 * �����Ɋ܂܂��P�ꐔ��x�̃^�X�N�S�Ăɂ��Ď���2���s���B
		 * 
		 * ����2�͒P��̏o���p�x���������ɃN�G���[����؂�̂Ă���@�ŁA
		 * �N�G���[���̒P�ꐔ0~x�܂ł��s���B
		 */
		experiment2ByKeywordNumAction = new Action(){
			public void run(){
				
//				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "���͂����L�[���[�h���̃^�X�N�ɂ��Ď����Q�����s���܂�", resultContainer); 
//            	md.open();
//            	String text = resultContainer.getText();
//            	int num_of_keywords_in_label = Integer.parseInt(text);

//				Label resultContainer2 = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md2 = new MultiTextInputDialog(fParent.getShell(), "�o���p�x���Ⴂ���ɂ��i���͂������j�̃L�[���[�h���N�G���Ƃ��܂��B", resultContainer2); 
//            	md2.open();
//            	String text2 = resultContainer2.getText();
//            	int num_of_keywords_in_query = Integer.parseInt(text2);
//            	
            	for( int num_of_keywords_in_label = 1; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in�@���x��, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//�w��L�[���[�h���ƈ�v���Ă�����ǉ�����B
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 0; num_of_keywords_in_query <= num_of_keywords_in_label; num_of_keywords_in_query++){
							//if(!className.equals("")){
							System.out.println("in�@�N�G���[, " + num_of_keywords_in_query);
								
							Experiment2 ls = new Experiment2(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query, false, true);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//�X�e�b�v�����w��
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//�����܂ōs�����ۂ��B
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//�R���\�[����ʕ\��
							Activator.showConsoleView();
							
							//���������Ȃ̂ŁA�W���u���g��
							Experiment2Job job = new Experiment2Job("����2�̎��s", ls, numOfSteps);
							job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
							job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO �����������ꂽ catch �u���b�N
								e.printStackTrace();
							}
						}
					}
            	}
			}
		};
		experiment2ByKeywordNumAction.setText("����2(�p�x��̒P�ꂩ��؂�̂�)");
		

		//����3(�����Ɋ܂܂��P�ꐔ���w��)
		/*
		 * �����Ɋ܂܂��P�ꐔx���w�肷��ƁA
		 * �����Ɋ܂܂��P�ꐔ��x�̃^�X�N�S�Ăɂ��Ď���3���s���B
		 * 
		 * ����3�͒P��̏o���p�x�����Ȃ����ɃN�G���[����؂�̂Ă���@�ŁA
		 * �N�G���[���̒P�ꐔ0~x�܂ł��s���B
		 */
		experiment3ByKeywordNumAction = new Action(){
			public void run(){
				
//				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "���͂����L�[���[�h���̃^�X�N�ɂ��Ď���3�����s���܂�", resultContainer); 
//            	md.open();
//            	String text = resultContainer.getText();
//            	int num_of_keywords_in_label = Integer.parseInt(text);

//				Label resultContainer2 = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md2 = new MultiTextInputDialog(fParent.getShell(), "�o���p�x���Ⴂ���ɂ��i���͂������j�̃L�[���[�h���N�G���Ƃ��܂��B", resultContainer2); 
//            	md2.open();
//            	String text2 = resultContainer2.getText();
//            	int num_of_keywords_in_query = Integer.parseInt(text2);
            	
            	for( int num_of_keywords_in_label = 1; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in�@���x��, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//�w��L�[���[�h���ƈ�v���Ă�����ǉ�����B
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 0; num_of_keywords_in_query <= num_of_keywords_in_label; num_of_keywords_in_query++){
							//if(!className.equals("")){
							System.out.println("in�@�N�G���[, " + num_of_keywords_in_query);
								
							Experiment2 ls = new Experiment2(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query, false, false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//�X�e�b�v�����w��
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//�����܂ōs�����ۂ��B
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//�R���\�[����ʕ\��
							Activator.showConsoleView();
							
							//���������Ȃ̂ŁA�W���u���g��
							Experiment2Job job = new Experiment2Job("����3�̎��s", ls, numOfSteps);
							job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
							job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO �����������ꂽ catch �u���b�N
								e.printStackTrace();
							}
						}
					}
            	}
			}
		};
		experiment3ByKeywordNumAction.setText("����3(�p�x���̒P�ꂩ��؂�̂�)");
		

		//����4(�����Ɋ܂܂��P�ꐔ���w��)
		/*
		 * �����Ɋ܂܂��P�ꐔx���w�肷��ƁA
		 * �����Ɋ܂܂��P�ꐔ��x�̃^�X�N�S�Ăɂ��Ď���3���s���B
		 * 
		 * ����4�͒P��̐؂�̂Ă̑g�ݍ��킹���ׂĂ̕��ϒl�����߂�B
		 * 
		 * �N�G���[���̒P�ꐔ0~x�܂ł��s���B
		 */
		experiment4Action = new Action(){
			public void run(){
				
//				Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
//            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "���͂����L�[���[�h���̃^�X�N�ɂ��Ď���4�����s���܂�", resultContainer); 
//            	md.open();
//            	String text = resultContainer.getText();
//            	
            	//int num_of_keywords_in_label = Integer.parseInt(text);
            	
            	for( int num_of_keywords_in_label = 1; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in�@���x��, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//�w��L�[���[�h���ƈ�v���Ă�����ǉ�����B
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 1; num_of_keywords_in_query < num_of_keywords_in_label; num_of_keywords_in_query++){
							//�L�[���[�h��0 �ƃL�[���[�h��=���x���ܗL�L�[���[�h���̏ꍇ�́A
							//���̌��ʂƓ����ɂȂ�B�w�؂�̂Ă������̂ŁB�x
							//�s�v�Ȃ̂ŁA��΂��B
							
							
							//if(!className.equals("")){
							System.out.println("in�@�N�G���[, " + num_of_keywords_in_query);
								
							Experiment3 ls = new Experiment3(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query,num_of_keywords_in_label, false, false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//�X�e�b�v�����w��
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//�����܂ōs�����ۂ��B
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//�R���\�[����ʕ\��
							Activator.showConsoleView();
							
							//���������Ȃ̂ŁA�W���u���g��
							Experiment3Job job = new Experiment3Job("����4�̎��s", ls, numOfSteps);
							job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
							job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO �����������ꂽ catch �u���b�N
								e.printStackTrace();
							}
						}
					}
            	}
			}
		};
		experiment4Action.setText("����4(�S�Ă̒P��؂�̂Ă̑g�ݍ��킹�̕��ϒl)");
		
		
		
		experiment234Action = new Action(){
			public void run(){

//				System.out.println(" ========== ����2(�p�x��̒P�ꂩ��؂�̂�)�J�n ===================================== ");
//
//            	for( int num_of_keywords_in_label = 5; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){
//
//	            	System.out.println("in�@���x��, " + num_of_keywords_in_label);
//	            	
//					Object o = viewer.getInput();
//					if(o instanceof ArrayList){
//						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
//						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
//						
//						for(TestSite ts: ts_arr){
//							//�w��L�[���[�h���ƈ�v���Ă�����ǉ�����B
//							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
//								ts_arr_by_class.add(ts);
//							}
//						}
//						
//						for(int num_of_keywords_in_query = 0; num_of_keywords_in_query <= num_of_keywords_in_label; num_of_keywords_in_query++){
//							//if(!className.equals("")){
//							System.out.println("in�@�N�G���[, " + num_of_keywords_in_query);
//								
//							Experiment2 ls = new Experiment2(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query, false, true);
//							
//							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//							
//							//�X�e�b�v�����w��
//							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
//							//�����܂ōs�����ۂ��B
//							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
//							if(isConvergence == true)
//								numOfSteps = -1;
//							
//							//�R���\�[����ʕ\��
//							Activator.showConsoleView();
//							
//							//���������Ȃ̂ŁA�W���u���g��
//							Experiment2Job job = new Experiment2Job("����2�̎��s", ls, numOfSteps);
//							job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
//							job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
//							try {
//								job.join();
//							} catch (InterruptedException e) {
//								// TODO �����������ꂽ catch �u���b�N
//								e.printStackTrace();
//							}
//						}
//					}
//            	}
            	
				System.out.println(" ========== ����3(�p�x���̒P�ꂩ��؂�̂�)�J�n ===================================== ");

            	for( int num_of_keywords_in_label = 6; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in�@���x��, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//�w��L�[���[�h���ƈ�v���Ă�����ǉ�����B
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 0; num_of_keywords_in_query <= num_of_keywords_in_label; num_of_keywords_in_query++){
							//if(!className.equals("")){
							System.out.println("in�@�N�G���[, " + num_of_keywords_in_query);
								
							Experiment2 ls = new Experiment2(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query, false, false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//�X�e�b�v�����w��
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//�����܂ōs�����ۂ��B
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//�R���\�[����ʕ\��
							Activator.showConsoleView();
							
							//���������Ȃ̂ŁA�W���u���g��
							Experiment2Job job = new Experiment2Job("����3�̎��s", ls, numOfSteps);
							job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
							job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO �����������ꂽ catch �u���b�N
								e.printStackTrace();
							}
						}
					}
            	}
            	
            	System.out.println(" ========== ����4�J�n ===================================== ");
	
            	for( int num_of_keywords_in_label = 1; num_of_keywords_in_label <= 9; num_of_keywords_in_label++){

	            	System.out.println("in�@���x��, " + num_of_keywords_in_label);
	            	
					Object o = viewer.getInput();
					if(o instanceof ArrayList){
						ArrayList<TestSite> ts_arr = (ArrayList<TestSite>)o;
						ArrayList<TestSite> ts_arr_by_class = new ArrayList<TestSite>();
						
						for(TestSite ts: ts_arr){
							//�w��L�[���[�h���ƈ�v���Ă�����ǉ�����B
							if(ts.getNumOfKeywords() == num_of_keywords_in_label){
								ts_arr_by_class.add(ts);
							}
						}
						
						for(int num_of_keywords_in_query = 1; num_of_keywords_in_query < num_of_keywords_in_label; num_of_keywords_in_query++){
							//�L�[���[�h��0 �ƃL�[���[�h��=���x���ܗL�L�[���[�h���̏ꍇ�́A
							//���̌��ʂƓ����ɂȂ�B�w�؂�̂Ă������̂ŁB�x
							//�s�v�Ȃ̂ŁA��΂��B
							
							
							//if(!className.equals("")){
							System.out.println("in�@�N�G���[, " + num_of_keywords_in_query);
								
							Experiment3 ls = new Experiment3(ts_arr_by_class.toArray(new TestSite[0]), num_of_keywords_in_query,num_of_keywords_in_label, false, false);
							
							IPreferenceStore store = Activator.getDefault().getPreferenceStore();
							
							//�X�e�b�v�����w��
							int numOfSteps = store.getInt(PreferenceInitializer.LOCAL_BATCH_NUMBER_OF_STEPS);
							//�����܂ōs�����ۂ��B
							boolean isConvergence = store.getBoolean(PreferenceInitializer.LOCAL_BATCH_FLAG_CONVERGENCE);
							if(isConvergence == true)
								numOfSteps = -1;
							
							//�R���\�[����ʕ\��
							Activator.showConsoleView();
							
							//���������Ȃ̂ŁA�W���u���g��
							Experiment3Job job = new Experiment3Job("����4�̎��s", ls, numOfSteps);
							job.setUser(true);//���[�U�[�Ƀ|�b�v�A�b�v��\������B
							job.schedule();//�W���u��Eclipse�ɓo�^���Ă����܂��B���Ƃ�Eclipse���K�؂�run�����s���A�������Ă����
							try {
								job.join();
							} catch (InterruptedException e) {
								// TODO �����������ꂽ catch �u���b�N
								e.printStackTrace();
							}
						}
					}
            	}
			}
		};
		experiment234Action.setText("����234�ꊇ���s.");
		
		
        //����������
        verifyAction = new Action(){
            public void run(){
                TestSite[] list = getCheckedItems();

                //�^�X�N���I������Ă��Ȃ��B
                if(list.length == 0){
                    MessageDialog.openWarning(fParent.getShell(), "����������", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
                    return;
                }

                boolean yn = MessageDialog.openQuestion(fParent.getShell(),
                        "����������",
                        "�I�����ꂽ "+ list.length + " �̊w�K�^�X�N�ɑ΂��Đ��������؂��s���܂��B");

                //"No"�Ȃ牽�����Ȃ��B
                if(yn == false){
                    return;
                }
                int countOk = 0;
                int count_static = 0;
                int count_static_ = 0;
                int count_false = 0;
                for(TestSite site: list){
//                    if(site.verifyLabel2()){
//                        //System.out.println(site.getId() + ", "+ site.getFullyQualifiedClassName() + ", OK");
//                        countOk++;
//                    }else{
//                        //System.out.println(site.getId() + ", "+ site.getFullyQualifiedClassName() + ", NG");
//                    	System.out.println(site.getId() + ", "+ site.getFullyQualifiedClassName() + ", " + site.getSelectedString());
//                    }
                    String flg = site.verifyLabel2();
                    if(flg.equals("true")){
                    	countOk++;
                    }else if(flg.equals("static")){
                        count_static++;
//                        System.out.println("static, "+site.getId() + ", "+ site.getFullyQualifiedClassName() + ", " + site.getSelectedString());
//                        System.out.println(site.getId());
                    }else if(flg.equals("static?")){
                        count_static_++;
//                        System.out.println("static?, "+site.getId() + ", "+ site.getFullyQualifiedClassName() + ", " + site.getSelectedString());
//                        System.out.println(site.getId());
                    }else{
                    	//System.out.println("NG, "+site.getId() + ", "+ site.getFullyQualifiedClassName() + ", " + site.getSelectedString());
                    	System.out.println(site.getId());
                    	count_false++;
                    	site.deleteFile();
                    }
                    
//                    try{
//                    	site.verifyFunctions();
//                    }catch(Exception e){
//                    	e.printStackTrace();
//                    }
                    
                }
                System.out.println("OK= "+ countOk );
                System.out.println("static= "+ count_static);
                System.out.println("static?= "+ count_static_);
                System.out.println("false= "+ count_false);
                
            }
        };
        verifyAction.setText("����������");
        
		
        //�^�X�N��id�Ńt�B���^�����O
        idFilterAction = new Action(){
            public void run(){
            	Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "���͂���ID�Ń^�X�N���t�B���^�����O���܂�", resultContainer); 
            	md.open();
            	id_filtering = resultContainer.getText();
            	refresh();
            }
        };
        idFilterAction.setText("ID�Ńt�B���^�����O");
        

        //�^�X�N���N���X���Ńt�B���^�����O
        classNameFilterAction = new Action(){
            public void run(){
            	Label resultContainer = new Label(fParent.getShell(), SWT.NONE);
            	MultiTextInputDialog md = new MultiTextInputDialog(fParent.getShell(), "���͂����N���X���Ń^�X�N���t�B���^�����O���܂�", resultContainer); 
            	md.open();
            	className_filtering = resultContainer.getText();
            	refresh();
            }
        };
        classNameFilterAction.setText("�N���X���Ńt�B���^�����O");
        
        //�e�L�X�g�t�@�C�����J��
  		openFileAction = new Action(){
  			public void run(){
  				TestSite[] list = getCheckedItems();

                //�^�X�N���I������Ă��Ȃ��B
                if(list.length == 0){
                    MessageDialog.openWarning(fParent.getShell(), "����������", "�^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
                    return;
                }
                for(TestSite site: list){
                	site.openTextFile();
                }
  			}
  		};
      	openFileAction.setText("�e�L�X�g�t�@�C�����J��");
        
		//�A�N�V�������c�[���o�[�ƃv���_�E�����j���[�ɑg�ݍ��ށB
		IActionBars bars = getViewSite().getActionBars();
		
		bars.getToolBarManager().add(refreshAction);
		bars.getToolBarManager().add(deleteAction);
		bars.getToolBarManager().add(localSearchAction);
		bars.getToolBarManager().add(gridSearchAction);
		bars.getToolBarManager().add(kpAction);
		bars.getToolBarManager().add(settingAction);
		bars.getToolBarManager().add(selectAllAction);
		bars.getToolBarManager().add(unSelectAllAction);
		bars.getToolBarManager().add(countTaskAction);
		
		bars.getMenuManager().add(refreshAction);
		bars.getMenuManager().add(deleteAction);
		bars.getMenuManager().add(localSearchAction);
		bars.getMenuManager().add(gridSearchAction);
		bars.getMenuManager().add(kpAction);
		bars.getMenuManager().add(settingAction);
		bars.getMenuManager().add(selectAllAction);
		bars.getMenuManager().add(unSelectAllAction);
		bars.getMenuManager().add(countTaskAction);
		
		//�A�N�V�������c�[���o�[�ƃ|�b�v�A�b�v���j���[�ɑg�ݍ��ށB
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// TODO Auto-generated method stub
				manager.add(importReloadAction);
				
				manager.add(refreshAction);
				manager.add(deleteAction);
				manager.add(localSearchAction);
				manager.add(gridSearchAction);
				manager.add(kpAction);
				manager.add(settingAction);
				manager.add(selectAllAction);
				manager.add(unSelectAllAction);
				
				manager.add(countTaskAction);
				manager.add(selectedTaskPropetyAction);
				
				manager.add(taskViewAction);
				
				manager.add(openFileAction);
				
				
//				manager.add(taskRandomSelectAction);
				
//				manager.add(experiment1Action);
				
				//manager.add(experiment1Action_exp);
				
//				manager.add(experiment1_renzoku_Action);
//				manager.add(experiment1_renzoku_Action2); 
				
				//manager.add(experiment1_renzoku_exp_Action);

				
				
				
				//manager.add(experiment1ByClassAction);

				//manager.add(experiment2Action);
				//manager.add(experiment2ByKeywordNumAction);
				
				//manager.add(experiment3ByKeywordNumAction);
				
				//manager.add(experiment4Action);
				
				//manager.add(experiment234Action);
				
//				manager.add(verifyAction);
//				manager.add(idFilterAction);
//				manager.add(classNameFilterAction);
//				manager.add(arrangeTaskAction1);
//				manager.add(arrangeTaskAction2);
				
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	public void runTasksUsingMultiThread(TestSite[] list,
			keywordProgramming.exp.Params para, String projectName, long startTime) {
		int max_thread_num = 3;
		if(list.length < max_thread_num){
			max_thread_num = list.length;
		}
	    TsThread thread[];
	    TsSync sync;
	    
	    // �����I�u�W�F�N�g�̍쐬
	    sync = new TsSync();

	    // �q�X���b�h���z��̍쐬
	    thread = new TsThread[max_thread_num];

	    // �X���b�h�̋N��
	    for (int i = 0; i < list.length; ) {
	    	
	    	for(int j = 0; j < max_thread_num; j++){
	    		if(i >= list.length){
	    			break;
	    		}
		    	thread[j] = new TsThread(sync, para, new plugin.testSite.exp.TestSite(list[i]), projectName, startTime);
			    thread[j].start();
			    			    
			    i++;
	    	}
		      
		    try {
			 Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO �����������ꂽ catch �u���b�N
			 e.printStackTrace();
			}
		      
		    // �q�̏I����҂�
		    sync.waitSync();
		    
	    }
	    
	    System.out.println(sync.output);
	    
		  String name = "";
			name += projectName;
			name += ",";
			name += startTime;//�J�n���ԁ@
			name += ",";
			name += para.shortened_input_keywords;
			name += ",";
			name += para.separate_keywords;
			name += ",";
			name += para.common_subsequence;
			name += ",";
			name += para.ld_delete;
			name += ",";
			name += para.ld_replace;
			name += ",";
			name += para.ld_add;
			
		//�o�̓e�L�X�g�t�@�C���̍쐬
    	String savefilename = PreferenceInitializer.extFileFolder + name + ".txt";
    	
    	//�t�@�C����������΍쐬����
    	File txtFile = new File(savefilename);
    	if (!txtFile.exists()){
			try {
				txtFile.createNewFile();
			} catch (IOException e1) {
				// TODO �����������ꂽ catch �u���b�N
				e1.printStackTrace();
			}
    	}
    	
		//�t�H���_���Ȃ���΍쐬����B
		File dir = txtFile.getParentFile();
		if (!dir.exists()) {  
		    dir.mkdirs();
		}
		
		try{
			PrintWriter export_pw = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
			//��������
			export_pw.print(sync.output);
			//�I������
			export_pw.flush();
			export_pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	    
	}
	/*
	 * ��ʂ����t���b�V�����A�V���ɓo�^���ꂽ���̂�\������B
	 */
	public void refresh(){
		refreshAction.run();
	}
	
	/*
	 * �`�F�b�N�������̂̂ݎ擾����B
	 * 
	 * 
	 */
	public TestSite[] getCheckedItems(){
		/*
		Object[]����TestSite[]�̃L���X�g�͂ł��Ȃ��B
		TestSite[] site = (TestSite[])viewer.getCheckedElements();
		
		�Q�l�Fhttp://d.hatena.ne.jp/fumokmm/20080902/1220372739
		*/
		Object[] o = viewer.getCheckedElements();
		List<Object> site = Arrays.asList(o);
		return site.toArray(new TestSite[site.size()]);
	}
	
	
	 class IdFilter extends ViewerFilter{
		 /*
		  * select��3�Ԗڂ̈����� e ���A�e�[�u���̃��R�[�h�ɑΉ�����I�u�W�F�N�g�ɂȂ��Ă���A���̃��\�b�h��true��Ԃ������R�[�h�݂̂��\�������Ƃ����d�g�݂ł��B
		  * (�� Javadoc)
		  * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		  */
         public boolean select(Viewer viewer, Object parent, Object e) {
        	 if(id_filtering.equals(""))
        		 return true;
        	 TestSite site = (TestSite)e;
        	 //�J���}�A�󔒕���(���p�A�S�p�A�^�u)�A���s����؂蕶���Ƃ���B
        	 List<String> ids = Arrays.asList(id_filtering.split("[, �@\t\n\r]", -1));
        	 return ids.contains(site.getId());
         }
	 }
		
	 class ClassNameFilter extends ViewerFilter{
		 /*
		  * select��3�Ԗڂ̈����� e ���A�e�[�u���̃��R�[�h�ɑΉ�����I�u�W�F�N�g�ɂȂ��Ă���A���̃��\�b�h��true��Ԃ������R�[�h�݂̂��\�������Ƃ����d�g�݂ł��B
		  * (�� Javadoc)
		  * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		  */
         public boolean select(Viewer viewer, Object parent, Object e) {
        	 if(className_filtering.equals(""))
        		 return true;
        	 TestSite site = (TestSite)e;
        	 //�J���}�A�󔒕���(���p�A�S�p�A�^�u)�A���s����؂蕶���Ƃ���B
        	 List<String> classNames = Arrays.asList(className_filtering.split("[, �@\t\n\r]", -1));
        	 return (classNames.contains(site.getClassSingleName()) || classNames.contains(site.getFullyQualifiedClassName())); 
         }
	 }
	  
}
