package org.apache.taverna.gis.client;

public class LiteralPortDataDescriptor implements IPortDataDescriptor {
	private String name;
	private Object value;
	private Integer depth;
	private boolean allowLiteralValues;
	private Object handledReferenceSchemes;
	private Class<?> translatedElementType;
	private boolean isRequired;
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public Object getValue() {
		return value;
	}
	@Override
	public void setValue(Object value) {
		this.value = value;
	}
	@Override
	public Integer getDepth() {
		return depth;
	}
	@Override
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	@Override
	public boolean isAllowLiteralValues() {
		return allowLiteralValues;
	}
	@Override
	public void setAllowLiteralValues(boolean allowLiteralValues) {
		this.allowLiteralValues = allowLiteralValues;
	}
	@Override
	public Object getHandledReferenceSchemes() {
		return handledReferenceSchemes;
	}
	@Override
	public void setHandledReferenceSchemes(Object handledReferenceSchemes) {
		this.handledReferenceSchemes = handledReferenceSchemes;
	}
	@Override
	public Class<?> getTranslatedElementType() {
		return translatedElementType;
	}
	@Override
	public void setTranslatedElementType(Class<?> translatedElementType) {
		this.translatedElementType = translatedElementType;
	}
	@Override
	public boolean isRequired() {
		return isRequired;
	}
	@Override
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
}
