package org.apache.taverna.gis.client.impl;

import java.util.List;

public class TypeDescriptor {
	private String name;
	private Integer depth;
	private boolean allowLiteralValues;
	private String handledReferenceSchemes;
	private Class<?> translatedElementType;
	private boolean isRequired;
	private List<String> supportedFormats;
	private String defaultFormat;
	
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
	public boolean isRequired() {
		return isRequired;
	}
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	public List<String> getSupportedFormats() {
		return supportedFormats;
	}
	public void setSupportedFormats(List<String> supportedFormats) {
		this.supportedFormats = supportedFormats;
	}
	public String getDefaultFormat() {
		return defaultFormat;
	}
	public void setDefaultFormat(String defaultFormat) {
		this.defaultFormat = defaultFormat;
	}
	
}
