package org.apache.taverna.gis.client;

public interface IPortDataDescriptor {
	public String getName();
	public void setName(String name);
	public Object getValue();
	public void setValue(Object value);
	public Integer getDepth();
	public void setDepth(Integer depth);
	public boolean isAllowLiteralValues();
	public void setAllowLiteralValues(boolean allowLiteralValues);
	public Object getHandledReferenceSchemes();
	public void setHandledReferenceSchemes(Object handledReferenceSchemes);
	public Class<?> getTranslatedElementType();
	public void setTranslatedElementType(Class<?> translatedElementType);
	public boolean isRequired();
	public void setRequired(boolean isRequired);
	
}
