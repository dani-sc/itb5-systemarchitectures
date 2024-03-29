package beans;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ImageVisualizerBeanInfo extends SimpleBeanInfo {
	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor image = new PropertyDescriptor("image", ImageVisualizer.class);
			return new PropertyDescriptor[] { image };
		} catch (Exception e) {
			return super.getPropertyDescriptors();
		}
	}
}
