package com.tk.ui.dashboard;

import java.util.Properties;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public interface DynamicWidget {
	public void initialize();

	public Point getLocation();
	public void setLocation(Point point);
	
	public Point getSize();
	public Point getMinimumSize();
	public Point getMaximumSize();
	public void setSize(Point point);
	
	public Rectangle getBounds();
	
	public Properties storeConfiguration(Properties props);
	public void applyConfiguration(Properties props);

	public void minimize();
	public void restore();

	public void dispose();

	public void setRedraw(boolean b);

	public void refresh();
	public void setRefreshCount(int count);
}
