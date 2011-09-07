package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.cdt.managedbuilder.ui.wizards.CDTConfigWizardPage;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPage;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
public class NewCuteSuiteWizardCustomPage extends MBSCustomPage {

	private final CDTConfigWizardPage configPage;
	private final IWizardPage startingWizardPage;
	
	public NewCuteSuiteWizardCustomPage(CDTConfigWizardPage configWizardPage, IWizardPage startingWizardPage){
		pageID="ch.hsr.ifs.cutelauncher.ui.NewCuteSuiteWizardCustomPage";
		this.configPage = configWizardPage;
		this.startingWizardPage = startingWizardPage;
	}

	@Override
	public IWizardPage getNextPage() {
		return configPage;
	}

	@Override
	public IWizardPage getPreviousPage() {
		return startingWizardPage;
	}
	
	@Override
	protected boolean isCustomPageComplete() {
		if(suitenameText.getText().equals("")){
			//since IWizard#canFinish() cannot be overwritten from this class, thus unable to disable the finish button, 
			//we will need to use a default name for empty textfield as a work around
			errmsg="Please enter a suite name.";
			return false;
		}
		if(!suitenameText.getText().matches("\\w+")){
			errmsg="invalid suite name. Only alphanumeric and underscore.";
			return false;
		}
		errmsg=null;
		return true;
	}

	public String getName() {
		return "Set Suite Name";
	}
	
	private Composite composite=null;
	private Label suitenameLabel=null; 
	private Text suitenameText=null;
	
	public void createControl(Composite parent) {
		createControl(parent, true);
	}
	public void createControl(Composite parent,final boolean flag) {
		composite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(3, true);
		layout.marginHeight = 0;
		layout.marginWidth = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridData gd;
		gd = new GridData();
		gd.horizontalSpan =1;
		suitenameLabel=new Label(composite,SWT.NONE);
		suitenameLabel.setText("Test Suite Name:");
		suitenameLabel.setLayoutData(gd);
		
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan =2;
		suitenameText = SWTFactory.createSingleText(composite, 2);
		suitenameText.setLayoutData(gd); 
		suitenameText.setText("DefaultSuiteName");
	    suitenameText.addModifyListener(new ModifyListener() {
	    	public void modifyText(ModifyEvent e){
	    	if(flag){	
				IWizardContainer iwc=getWizard().getContainer();
				//have to call one by one as 
				//org.eclipse.jface.wizard.Wizard.Dialog.update() is protected 
				iwc.updateButtons();
				iwc.updateMessage();
				iwc.updateTitleBar();
				iwc.updateWindowTitle();}}
		});	
	}
	
	public String getSuiteName(){
		if(suitenameText==null ||suitenameText.getText().equals("")||!suitenameText.getText().matches("\\w+"))return "suite";
		return suitenameText.getText();
	}
	//for unit testing
	public void setSuiteName(String s){
		suitenameText.setText(s);
	}
	public void dispose() {
		composite.dispose();
	}

	public Control getControl() {
		return composite==null?null:composite;
	}

	public String getDescription() {
		return "for the user to specify a custom suite name.";
	}

	String errmsg=null;
	public String getErrorMessage() {
		return errmsg;
	}

	public Image getImage() {
		return wizard.getDefaultPageImage();
	}

	public String getMessage() {
		return "New Test Suite Name";
	}

	public String getTitle() {
		return "Suite Name";
	}

	public void performHelp() {
	}

	public void setDescription(String description) {
	}

	public void setImageDescriptor(ImageDescriptor image) {
	}

	public void setTitle(String title) {
	}

	public void setVisible(boolean visible) {
		composite.setVisible(visible);
	}

}
