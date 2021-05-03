package com.tk.ui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.tk.ui.dashboard.Dashboard;

public class StartDashboard extends ApplicationWindow {

	private Dashboard dashboard;

	/**
	 * Create the application window.
	 */
	public StartDashboard() {
		super(null);
		setShellStyle(SWT.SHELL_TRIM);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(1, 73, 132));
		container.setLayout(new FormLayout());
		
		dashboard = new Dashboard(container, this, SWT.NONE);
		FormData fd_splashScreen = new FormData();
		fd_splashScreen.top = new FormAttachment(0, 30);
		fd_splashScreen.left = new FormAttachment(0, 30);
		fd_splashScreen.right = new FormAttachment(100, -10);
		fd_splashScreen.bottom = new FormAttachment(100, -10);
		dashboard.setLayoutData(fd_splashScreen);
		dashboard.setLayout(new GridLayout(1, false));
	
		return container;
	}

	
	protected void initializeBounds() {
		Rectangle b = Display.getCurrent().getBounds();
		getShell().setSize(b.width, 1600);
		getShell().setLocation(0, 0);
		getShell().setMaximized(true);

		dashboard.initializeWidgets();
	
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			StartDashboard window = new StartDashboard();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Application");
	}


}
