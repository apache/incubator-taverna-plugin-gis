package org.apache.taverna.gis.client;

public class ComplexDataTypeDescriptor {
	private String mimeType;
	private String encoding;
	private String schema;
	
	public ComplexDataTypeDescriptor() {
	}
	
	public ComplexDataTypeDescriptor(String mimeType, String encoding, String schema) {
		this.mimeType = mimeType;
		this.encoding = encoding;
		this.schema = schema;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	
}
