package org.apache.taverna.gis.client;

public class ComplexDataFormat {
	private String mimeType;
	private String encoding;
	private String schema;
	
	public ComplexDataFormat() {
	}
	
	public ComplexDataFormat(String mimeType, String encoding, String schema) {
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
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (!ComplexDataFormat.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final ComplexDataFormat otherFormat = (ComplexDataFormat) obj;
	    if ((this.mimeType == null) ? (otherFormat.mimeType != null) : !this.mimeType.equals(otherFormat.mimeType)) {
	        return false;
	    }
	    if ((this.schema == null) ? (otherFormat.schema != null) : !this.schema.equals(otherFormat.schema)) {
	        return false;
	    }
	    if ((this.encoding == null) ? (otherFormat.encoding != null) : !this.encoding.equals(otherFormat.encoding)) {
	        return false;
	    }
	    return true;
	}
}
