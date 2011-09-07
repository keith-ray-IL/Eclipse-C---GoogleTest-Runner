package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.cdt.internal.ui.CPluginImages;
import org.eclipse.cdt.internal.ui.wizards.filewizard.AbstractFileCreationWizard;
import org.eclipse.cdt.ui.CUIPlugin;
//based on @see org.eclipse.cdt.ui/org.eclipse.cdt.internal.ui.wizards.filewizard.NewSourceFileCreationWizard
public class NewSuiteFileCreationWizard extends AbstractFileCreationWizard {
    public NewSuiteFileCreationWizard() {
        super();
        setDefaultPageImageDescriptor(CPluginImages.DESC_WIZBAN_NEW_SOURCEFILE);
        setDialogSettings(CUIPlugin.getDefault().getDialogSettings());
        setWindowTitle("New Cute Suite File"); //$NON-NLS-1$
    }
    
    /*
     * @see Wizard#createPages
     */
    @Override
	public void addPages() {
        super.addPages();
        fPage = new NewSuiteFileCreationWizardPage();
        addPage(fPage);
        fPage.init(getSelection());
    }
}

//bug with selection at project resource
//regex issue