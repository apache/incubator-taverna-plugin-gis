package org.apache.taverna.gis.client;

public class PortDataTypeDescriptroFactory {

	public IPortDataDescriptor getPortDataType(PortDataType portDataType) {
		switch (portDataType) {
		case COMPLEX_DATA:
			return new ComplexPortDataDescriptor();
		case BOUNDING_BOX_DATA:
			return new BBoxPortDataTypeDescriptor();

		default:
			return new LiteralPortDataDescriptor();
		}
	}
}
