package org.apache.taverna.gis.client;

import java.util.List;

public class PortTypeDescriptor {
	private String name;
	private Integer depth;
	private boolean allowLiteralValues;
	private String handledReferenceSchemes;
	private Class<?> translatedElementType;
	private PortDataType portDataType;
	private boolean isRequired;
	private List<ComplexDataTypeDescriptor> supportedComplexFormats;
	private ComplexDataTypeDescriptor defaultComplexFormat;
	private List<String> supportedBoundingBoxFormats;
	private String defaultBoundingBoxFormat;
	
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
	public PortDataType getPortDataType() {
		return portDataType;
	}
	public void setPortDataType(PortDataType portDataType) {
		this.portDataType = portDataType;
	}
	public boolean isRequired() {
		return isRequired;
	}
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	public List<ComplexDataTypeDescriptor> getSupportedComplexFormats() {
		return supportedComplexFormats;
	}
	public void setSupportedComplexFormats(List<ComplexDataTypeDescriptor> supportedComplexFormats) {
		this.supportedComplexFormats = supportedComplexFormats;
	}
	public ComplexDataTypeDescriptor getDefaultComplexFormat() {
		return defaultComplexFormat;
	}
	public void setDefaultComplexFormat(ComplexDataTypeDescriptor defaultComplexFormat) {
		this.defaultComplexFormat = defaultComplexFormat;
	}
	public List<String> getSupportedBoundingBoxFormats() {
		return supportedBoundingBoxFormats;
	}
	public void setSupportedBoundingBoxFormats(List<String> supportedBoundingBoxFormats) {
		this.supportedBoundingBoxFormats = supportedBoundingBoxFormats;
	}
	public String getDefaultBoundingBoxFormat() {
		return defaultBoundingBoxFormat;
	}
	public void setDefaultBoundingBoxFormat(String defaultBoundingBoxFormat) {
		this.defaultBoundingBoxFormat = defaultBoundingBoxFormat;
	}
	
}
