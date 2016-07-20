package org.apache.taverna.gis.client.impl;

public class TypeDescriptor {
	private String name;
	private Integer depth;
	private boolean allowLiteralValues;
	private String handledReferenceSchemes;
	private Class<?> translatedElementType;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	public boolean isAllowLiteralValues() {
		return allowLiteralValues;
	}
	public void setAllowLiteralValues(boolean allowLiteralValues) {
		this.allowLiteralValues = allowLiteralValues;
	}
	public String getHandledReferenceSchemes() {
		return handledReferenceSchemes;
	}
	public void setHandledReferenceSchemes(String handledReferenceSchemes) {
		this.handledReferenceSchemes = handledReferenceSchemes;
	}
	public Class<?> getTranslatedElementType() {
		return translatedElementType;
	}
	public void setTranslatedElementType(Class<?> translatedElementType) {
		this.translatedElementType = translatedElementType;
	}
	
}
