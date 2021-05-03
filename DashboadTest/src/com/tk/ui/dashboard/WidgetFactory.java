package com.tk.ui.dashboard;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;


public class WidgetFactory {
	
	public enum DynamicWidgetClass {
		NachrichtenWidget, BewerbungenWidget, BewerbungenGraphWidget, RechnungenWidget, TodoWidget
	}
	
	public static DynamicWidget createWidget(DynamicWidgetClass clazzName, Composite parent, DynamicWidgetContainer container, FormToolkit widgetToolkit, ApplicationWindow kisWindow){
	    DynamicWidget widget;
		switch (clazzName){
		case NachrichtenWidget:
			widget = new NachrichtenWidget(parent, container, widgetToolkit, SWT.TRANSPARENT, kisWindow);
			widget.setLocation(new Point(0,0));
			return widget;

	    default:
	    		return null;
		}
	}

	public static Class getWidgetClass(DynamicWidgetClass clazzName) {
		try {
			return Class.forName(clazzName.toString());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
