/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule für Technik
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Emanuel Graf - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.AbstractCWizard;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSWizardHandler;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.graphics.Image;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;

/**
 * @author Emanuel Graf
 *
 */
public class NewCuteProjectWizard extends AbstractCWizard {

	@Override
	public EntryDescriptor[] createItems(boolean supportedOnly, IWizard wizard) {
		CuteWizardHandler handler = getHandler(wizard);
		IToolChain[] tcs = ManagedBuildManager.getExtensionsToolChains(MBSWizardHandler.ARTIFACT, new CuteBuildPropertyValue().getId());
		for (int j=0; j<tcs.length; j++) {
			if (!supportedOnly || isValid(tcs[j], true, wizard)) {
				handler.addTc(tcs[j]);
			}
		}
		EntryDescriptor data = getEntryDescriptor(handler);
		return new EntryDescriptor[] {data};

	}

	protected EntryDescriptor getEntryDescriptor(CuteWizardHandler handler) {
		Image proImg = CuteLauncherPlugin.getImageDescriptor("obj16/cute_app.gif").createImage();
		return new EntryDescriptor("ch.hsr.ifs.cutelauncher.projectType", null, "Cute Project", true, handler, proImg);
	}

	protected CuteWizardHandler getHandler(IWizard wizard) {
		return new CuteWizardHandler(parent, wizard);
	}
	
	

}
