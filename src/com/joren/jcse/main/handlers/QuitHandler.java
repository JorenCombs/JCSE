package com.joren.jcse.main.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class QuitHandler {
	@Execute
	public void execute(IWorkbench workbench, Shell shell){
		if (MessageDialog.openConfirm(shell, "Confirmation",
				"Shutting down the stock market could lead to economic chaos!  Are you sure you want to continue?")) {
			workbench.close();
		}
	}
}
