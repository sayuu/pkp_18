package plugin.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ast.AstLocalCode;
import ast.Import;

import plugin.activator.Activator;
import plugin.testSite.TestSite;
import plugin.testSite.TestSiteComparator;

public class LogSiteView extends org.eclipse.ui.part.ViewPart{

	private CheckboxTableViewer viewer; 
	private Composite fParent;
	
  	private IAction refreshAction;//��ʂ����t���b�V������A�N�V����
  	private IAction moveTaskAction;//�������^�X�N�r���[�ɒǉ�����A�N�V����
  	private IAction settingAction;//�����̏d�݂̏����l�̐ݒ�A�N�V����
  	
  	private IWorkbenchWindow window;
  	
  	
	@Override
	public void createPartControl(Composite parent) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		//�C���|�[�g���ǂݍ��݁B���X�i�̓o�^
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
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

		
		//�`�F�b�N�{�b�N�X��ԊĎ��̂��߂̃��X�i
//		viewer.addCheckStateListener(new ICheckStateListener() {  
//		    public void checkStateChanged(CheckStateChangedEvent event) {  
//		    	TestSite site = (TestSite)event.getElement();
//		    	System.out.println(site.getSelectedString());
//		    
//		    }  
//		});  
		
//		TableViewerColumn viewerColumn_check = new TableViewerColumn(viewer, SWT.LEFT);
//		//viewerColumn_check.setLabelProvider(new ClassNameLabelProvider());
//		TableColumn tableColumn_check = viewerColumn_check.getColumn();
//		tableColumn_check.setText("�I��");
//		tableColumn_check.setWidth(100); // �������� pack()
//		viewerColumn_check.setEditingSupport(new CheckboxEditingSupport(viewer));
		
		TableViewerColumn viewerColumn_saveTime = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_saveTime.setLabelProvider(new SaveTimeLabelProvider());
		TableColumn tableColumn_saveTime = viewerColumn_saveTime.getColumn();
		tableColumn_saveTime.setText("�ۑ�����");
		tableColumn_saveTime.setWidth(190); // �������� pack()
		
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
		

		TableViewerColumn viewerColumn_text = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_text.setLabelProvider(new TextLabelProvider());
		TableColumn tableColumn_text = viewerColumn_text.getColumn();
		tableColumn_text.setText("�������ꂽ�o��");
		tableColumn_text.setWidth(320); // �������� pack()
//		tableColumn_text.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		

		TableViewerColumn viewerColumn_keyword = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_keyword.setLabelProvider(new KeywordLabelProvider());
		TableColumn tableColumn_keyword = viewerColumn_keyword.getColumn();
		tableColumn_keyword.setText("���̓L�[���[�h");
		tableColumn_keyword.setWidth(320); // �������� pack()
//		tableColumn_text.pack();
		//�Z�����G�f�B�b�g�\�ɂ���B
//		viewerColumn_keyword.setEditingSupport(new KeywordCellEditingSupport(viewer));
		

		TableViewerColumn viewerColumn_retType = new TableViewerColumn(viewer, SWT.LEFT);
		viewerColumn_retType.setLabelProvider(new ReturnTypeLabelProvider());
		TableColumn tableColumn_retType = viewerColumn_retType.getColumn();
		tableColumn_retType.setText("�]�܂����Ԃ�l");
		tableColumn_retType.setWidth(320); // �������� pack()
//		tableColumn_retType.pack();
//		viewerColumn.setEditingSupport(new MyEditingSupport(viewer));
		
		
		viewer.setInput(getItems());
		
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
		
		String path = TestSite.LogSiteFolder;//���O��ǂݎ��
		
	    File dir = new File(path);
	    if (!dir.exists()) {  
		    return null;
		}

	    File[] classFolders = dir.listFiles();
	    for (File folder: classFolders) {
	        File[] files = folder.listFiles();
	        for(File file: files){
	        	//�o�͕����񃍃O�t�@�C���͏����B
	        	if(file.getName().matches(".*out\\.txt$") == false){
		        	TestSite t = new TestSite(file, true);
		        	list.add(t);
	        	}
	        }
	    }
	    //���Ԃ̍~���Ƀ\�[�g����B
	    Collections.sort(list, new TestSiteComparator());
		return list;
	}
	
	private void registerAction(){
		//���t���b�V���A�N�V�������쐬
		refreshAction = new Action(){
			public void run(){
				viewer.setInput(getItems());
			}
		};
		refreshAction.setText("���t���b�V��");
//		refreshAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_REFRESH));
		refreshAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.browser", "$nl$/icons/elcl16/nav_refresh.gif"));

		//�������w�K�^�X�N�r���[�Ɉړ�����A�N�V�������쐬
		moveTaskAction = new Action(){
			public void run(){
				//�I�����ꂽ�A�C�e���𓾂�
				TestSite[] list = getCheckedItems();
				
				//�^�X�N���I������Ă��Ȃ��B
				if(list.length == 0){
					MessageDialog.openWarning(fParent.getShell(), "�w�K�^�X�N�r���[�ւ̈ړ�", "�ړ�����^�X�N���`�F�b�N�{�b�N�X�őI�����Ă��������B");
					return;
				}
				
				boolean yn = MessageDialog.openQuestion(fParent.getShell(),
						"�w�K�^�X�N�Ƃ��ēo�^����",
						"�I�����ꂽ "+ list.length + " �̗������w�K�^�X�N�Ƃ��Ēǉ����܂��B");
			
				//"No"�Ȃ牽�����Ȃ��B
				if(yn == false){
					return;
				}
				
				//�w�K�^�X�N�̏ꏊ�ɍĕۑ�
				for(int i = 0; i < list.length; i++){
					list[i].copyAndMoveFileToTestSiteFolder();
				}
				//�r���[�̃��t���b�V��
				TestSiteView view = (TestSiteView) window.getActivePage().findView("jp.ac.hokudai.eng.complex.kussharo.sayuu.kp.testSiteView");
				view.refresh();
			}
		};
		moveTaskAction.setText("�������w�K�^�X�N�r���[�Ɉړ�����");
//		moveTaskAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMG_EXPORT));
		moveTaskAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui", "$nl$/icons/full/etool16/export_wiz.gif"));


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

		//�A�N�V�������c�[���o�[�ƃv���_�E�����j���[�ɑg�ݍ��ށB
		IActionBars bars = getViewSite().getActionBars();
		
		bars.getToolBarManager().add(refreshAction);
		bars.getToolBarManager().add(settingAction);
		bars.getToolBarManager().add(moveTaskAction);
		
		bars.getMenuManager().add(refreshAction);
		bars.getMenuManager().add(settingAction);
		bars.getMenuManager().add(moveTaskAction);
		
		
		//�A�N�V�������c�[���o�[�ƃ|�b�v�A�b�v���j���[�ɑg�ݍ��ށB
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// TODO Auto-generated method stub
				manager.add(refreshAction);
				manager.add(settingAction);
				manager.add(moveTaskAction);
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
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
}
