package plugin.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import plugin.testSite.TestSite;

/*
 * ビューワ上で選択したタスクの箇所にジャンプする
 */
public class ViewerDoubleClickListener implements IDoubleClickListener{
	private TableViewer viewer;
	
	public ViewerDoubleClickListener(TableViewer viewer){
		this.viewer = viewer;
	}
	
	@Override
	public void doubleClick(DoubleClickEvent event) {
		// TODO Auto-generated method stub
		ISelection selection = viewer.getSelection();
	    Object obj = ((IStructuredSelection) selection).getFirstElement();
    	TestSite site = (TestSite)obj;
	    
	    IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
	    IResource resource = null;
	    for(IProject p: wsroot.getProjects()){
	    	IJavaProject jp = JavaCore.create(p);
	    	if(jp.exists() == false)
	    		continue;
	    	try {
				IType type = jp.findType(site.getFullyQualifiedClassName());
				if(type != null && type.isBinary() == false){
					resource = type.getResource();
					break;//抜ける
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    //見つからなかった。
	    if(resource == null)
	    	return;
	    
	    Map attributes = new HashMap();
	    attributes.put(IMarker.CHAR_START, site.getOriginalOffset());
	    attributes.put(IMarker.CHAR_END, site.getOriginalOffset() + site.getSelectedLength());
//	    attributes.put(IMarker.LINE_NUMBER, new Integer(5));


	    IMarker marker;
		try {
			marker = resource.createMarker(IMarker.TEXT);
			
			marker.setAttributes(attributes);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editor = IDE.openEditor(page, (IFile) resource);
			if(editor != null)
				IDE.gotoMarker(editor, marker);
			else
				MessageDialog.openWarning(viewer.getControl().getShell(), "指定タスクにジャンプできません。", "指定されたタスクにジャンプできません。\nクラスおよびパッケージの名前が変更されたか、それらが削除された可能性があります。");
		  	marker.delete();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
