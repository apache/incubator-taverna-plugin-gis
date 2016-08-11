package org.apache.taverna.gis.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.opengis.wps.x100.CRSsType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;

public class PortDataDescriptorFactory {

	private static PortDataDescriptorFactory instance;
	
	protected PortDataDescriptorFactory(){}
	
	public static PortDataDescriptorFactory getInstance()
	{
		if (instance==null)
			return new PortDataDescriptorFactory();
		else
			return instance;
	}
	
	public IPortDataDescriptor getPortDataDescriptor(InputDescriptionType inputPort) {
		
		if (inputPort.getLiteralData() != null)
		{
			LiteralPortDataDescriptor literalPort = new LiteralPortDataDescriptor();
			
			literalPort.setName(inputPort.getIdentifier().getStringValue());
			literalPort.setDepth(getInputPortDepth(inputPort));
			literalPort.setAllowLiteralValues(true);
			literalPort.setHandledReferenceSchemes(null); // is not used in Taverna
			literalPort.setTranslatedElementType(String.class);
			literalPort.setRequired(inputPort.getMinOccurs().compareTo(BigInteger.valueOf(1))>0?true:false);
			
			return literalPort;
		}
		
		if (inputPort.getComplexData() != null)
		{
			ComplexPortDataDescriptor complexPort = new ComplexPortDataDescriptor();
			
			complexPort.setName(inputPort.getIdentifier().getStringValue());
			complexPort.setDepth(getInputPortDepth(inputPort));
			complexPort.setAllowLiteralValues(true);
			complexPort.setHandledReferenceSchemes(null); // is not used in Taverna
			complexPort.setTranslatedElementType(String.class);
			complexPort.setRequired(inputPort.getMinOccurs().compareTo(BigInteger.valueOf(1))>0?true:false);
			
			complexPort.setSupportedComplexFormats(getInputPortSupportedComplexFormats(inputPort));
			complexPort.setDefaultComplexFormat(getInputPortDefaultComplexFormat(inputPort));
			
			return complexPort;
		}

		if (inputPort.getBoundingBoxData() != null)
		{
			BBoxPortDataDescriptor bboxPort = new BBoxPortDataDescriptor();
			
			bboxPort.setName(inputPort.getIdentifier().getStringValue());
			bboxPort.setDepth(getInputPortDepth(inputPort));
			bboxPort.setAllowLiteralValues(true);
			bboxPort.setHandledReferenceSchemes(null); // is not used in Taverna
			bboxPort.setTranslatedElementType(String.class);
			bboxPort.setRequired(inputPort.getMinOccurs().compareTo(BigInteger.valueOf(1))>0?true:false);
			
			bboxPort.setSupportedBoundingBoxFormats(getInputPortSupportedBoundingBoxFormats(inputPort));
			bboxPort.setDefaultBoundingBoxFormat(getInputPortDefaultBoundingBoxFormats(inputPort));
			
			return bboxPort;
		}
		
		return null;
		
	}
	
	public IPortDataDescriptor getPortDataDescriptor(OutputDescriptionType outputPort)
	{
		if (outputPort.getLiteralOutput() != null)
		{
			LiteralPortDataDescriptor literalPort = new LiteralPortDataDescriptor();
			
			literalPort.setName(outputPort.getIdentifier().getStringValue());
			literalPort.setDepth(getOutputPortDepth(outputPort));
			
			return literalPort;
		}
		
		if (outputPort.getComplexOutput() != null)
		{
			ComplexPortDataDescriptor complexPort = new ComplexPortDataDescriptor();
			
			complexPort.setName(outputPort.getIdentifier().getStringValue());
			complexPort.setDepth(getOutputPortDepth(outputPort));
			
			complexPort.setSupportedComplexFormats(getOutputPortSupportedComplexFormats(outputPort));
			complexPort.setDefaultComplexFormat(getOutputPortDefaultComplexFormat(outputPort));
			
			return complexPort;
		}

		if (outputPort.getBoundingBoxOutput() != null)
		{
			BBoxPortDataDescriptor bboxPort = new BBoxPortDataDescriptor();
			
			bboxPort.setName(outputPort.getIdentifier().getStringValue());
			bboxPort.setDepth(getOutputPortDepth(outputPort));
			
			bboxPort.setSupportedBoundingBoxFormats(getOutputPortSupportedBoundingBoxFormats(outputPort));
			bboxPort.setDefaultBoundingBoxFormat(getOutputPortDefaultBoundingBoxFormats(outputPort));
			
			return bboxPort;
		}
		
		return null;
		
	}
	
	/**
	 * @param input port
	 * @return List of supported formats
	 */
	private List<ComplexDataFormat> getInputPortSupportedComplexFormats(InputDescriptionType inputPort)
	{
		List<ComplexDataFormat> supportedComplexFormats = new ArrayList<ComplexDataFormat>();
		
		if (inputPort.getComplexData()==null)
			return supportedComplexFormats;
		else
		{
			ComplexDataCombinationsType complexDataSupportedTypes = inputPort.getComplexData().getSupported();
			
			if (complexDataSupportedTypes.sizeOfFormatArray()==0)
				return supportedComplexFormats;
			
			for(ComplexDataDescriptionType format : complexDataSupportedTypes.getFormatArray())
			{
				supportedComplexFormats.add(new ComplexDataFormat(format.getMimeType(),format.getEncoding(), format.getSchema()));
			}
		}
		
		return supportedComplexFormats;
	}
	
	private ComplexDataFormat getInputPortDefaultComplexFormat(InputDescriptionType inputPort)
	{
		ComplexDataFormat defaultFormat = null;
		
		if (inputPort.getComplexData()==null)
			if (inputPort.getComplexData().getDefault()!=null)
				if(inputPort.getComplexData().getDefault().getFormat()!=null)
				{
					ComplexDataDescriptionType outputDefaultFormat = inputPort.getComplexData().getDefault().getFormat();
					defaultFormat = new ComplexDataFormat(outputDefaultFormat.getMimeType(),outputDefaultFormat.getEncoding(),outputDefaultFormat.getSchema());
				}
					
		return defaultFormat;
		
	}
	
	private List<String> getInputPortSupportedBoundingBoxFormats(InputDescriptionType inputPort)
	{
		List<String> supportedBoundingBoxFormats = new ArrayList<String>();
		
		if (inputPort.getBoundingBoxData()==null)
			return supportedBoundingBoxFormats;
		else
		{
			CRSsType boundingBoxDataSupportedTypes = inputPort.getBoundingBoxData().getSupported();
			
			if (boundingBoxDataSupportedTypes.sizeOfCRSArray()==0)
				return supportedBoundingBoxFormats;
			
			for(String format : boundingBoxDataSupportedTypes.getCRSArray())
			{
				supportedBoundingBoxFormats.add(format);
			}
			
		}
		
		return supportedBoundingBoxFormats;

	}
	
	private String getInputPortDefaultBoundingBoxFormats(InputDescriptionType inputPort)
	{
		String defaultFormat = null;
		
		if (inputPort.getBoundingBoxData()==null)
			if (inputPort.getBoundingBoxData().getDefault()!=null)
				if(inputPort.getBoundingBoxData().getDefault().getCRS()!=null)
				{
					defaultFormat = inputPort.getBoundingBoxData().getDefault().getCRS();
				}
					
		return defaultFormat;
		
	}
	
	/**
	 * @param input port
	 * @return List of supported formats
	 */
	private List<ComplexDataFormat> getOutputPortSupportedComplexFormats(OutputDescriptionType outputPort)
	{
		List<ComplexDataFormat> supportedComplexFormats = new ArrayList<ComplexDataFormat>();
		
		if (outputPort.getComplexOutput()==null)
			return supportedComplexFormats;
		else
		{
			ComplexDataCombinationsType complexDataSupportedTypes = outputPort.getComplexOutput().getSupported();
			
			if (complexDataSupportedTypes.sizeOfFormatArray()==0)
				return supportedComplexFormats;
			
			for(ComplexDataDescriptionType format : complexDataSupportedTypes.getFormatArray())
			{
				supportedComplexFormats.add(new ComplexDataFormat(format.getMimeType(),format.getEncoding(), format.getSchema()));
			}
		}
		
		return supportedComplexFormats;
	}
	
	private ComplexDataFormat getOutputPortDefaultComplexFormat(OutputDescriptionType outputPort)
	{
		ComplexDataFormat defaultFormat = null;
		
		if (outputPort.getComplexOutput()==null)
			if (outputPort.getComplexOutput().getDefault()!=null)
				if(outputPort.getComplexOutput().getDefault().getFormat()!=null)
				{
					ComplexDataDescriptionType outputDefaultFormat = outputPort.getComplexOutput().getDefault().getFormat();
					defaultFormat = new ComplexDataFormat(outputDefaultFormat.getMimeType(),outputDefaultFormat.getEncoding(),outputDefaultFormat.getSchema());
				}
					
		return defaultFormat;
		
	}
	
	private List<String> getOutputPortSupportedBoundingBoxFormats(OutputDescriptionType outputPort)
	{
		List<String> supportedBoundingBoxFormats = new ArrayList<String>();
		
		if (outputPort.getBoundingBoxOutput()==null)
			return supportedBoundingBoxFormats;
		else
		{
			CRSsType boundingBoxDataSupportedTypes = outputPort.getBoundingBoxOutput().getSupported();
			
			if (boundingBoxDataSupportedTypes.sizeOfCRSArray()==0)
				return supportedBoundingBoxFormats;
			
			for(String format : boundingBoxDataSupportedTypes.getCRSArray())
			{
				supportedBoundingBoxFormats.add(format);
			}
			
		}
		
		return supportedBoundingBoxFormats;

	}
	
	private String getOutputPortDefaultBoundingBoxFormats(OutputDescriptionType outputPort)
	{
		String defaultFormat = null;
		
		if (outputPort.getBoundingBoxOutput()==null)
			if (outputPort.getBoundingBoxOutput().getDefault()!=null)
				if(outputPort.getBoundingBoxOutput().getDefault().getCRS()!=null)
				{
					defaultFormat = outputPort.getBoundingBoxOutput().getDefault().getCRS();
				}
					
		return defaultFormat;
		
	}
	
	/**
	 * @param inputPort
	 * @return
	 */
	private int getInputPortDepth(InputDescriptionType inputPort)
	{
		// The input has cardinality (Min/Max Occurs) of 1 when it returns 1 value and greater than 1  when it 
		// returns multiple values 
		// if compareTo returns 1 then first value (MaxOccurs) is greater than 1. it means that there is more than one occurrence 
		// therefore the depth is greater than 0
		int depth = ((inputPort.getMaxOccurs().compareTo(BigInteger.valueOf(1))==1) ? 1 : 0);
		
		return depth;
	}
	
	/**
	 * @param inputPort
	 * @return
	 */
	private int getOutputPortDepth(OutputDescriptionType outputPort)
	{
		// TODO: Calculate output port depth
		int depth = 0;
		
		return depth;
	}
}
