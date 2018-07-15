package readerXML;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class Reader {
	
	public Reader() {}
	
	/*
	 * Esta función extrae los valores del archivo de configuración y crea el objeto 
	 * [in] xmlPath es la ruta donde se encuentra el archivo de configuración
	 * [out] Devuelve el objeto con los valores leídos
	 */
	public Values leerXML(String xmlPath) throws ParserConfigurationException {
		File file = new File(xmlPath);
		if(file.exists())
		{
			String pathImg = "";
			String pathImgMatriculas = "";
			String nombreArch = "";
			String pathTess = "";
			int umbralCanny = 0;
			int factorUmbralCanny = 0;
			int umbralKernel1 = 0;
			int umbralKernel2 = 0;
			boolean typeFiltre = true;
			int aThresholdBlockSize = 0;
			int aThresholdC = 0;
			
			Values valuesXML = new Values(pathImg, pathImgMatriculas, nombreArch, pathTess, umbralCanny, factorUmbralCanny, 
					typeFiltre, umbralKernel1, umbralKernel2, aThresholdBlockSize, aThresholdC);
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			try {
				Document document = documentBuilder.parse(file);
				
				pathImg = document.getElementsByTagName("pathImg").item(0).getTextContent();
				pathImgMatriculas = document.getElementsByTagName("pathImgMatricula").item(0).getTextContent();
				nombreArch = document.getElementsByTagName("nombreArchivo").item(0).getTextContent();
				pathTess = document.getElementsByTagName("directorioTess").item(0).getTextContent();
				String umbralCanny_aux = document.getElementsByTagName("umbralCanny").item(0).getTextContent();
				String factorUmbralCanny_aux = document.getElementsByTagName("factor2UmbralCanny").item(0).getTextContent();
				
				String umbralSobel1_aux = document.getElementsByTagName("umbralKernel1").item(0).getTextContent();
				String umbralSobel2_aux = document.getElementsByTagName("umbralKernel2").item(0).getTextContent();
				
				typeFiltre = Boolean.parseBoolean(document.getElementsByTagName("filtros").item(0).getAttributes().getNamedItem("tipoFiltro").getNodeValue());
				
				String aThresholdBlockSize_aux = document.getElementsByTagName("aThresholdBlockSize").item(0).getTextContent();
				String aThresholdC_aux = document.getElementsByTagName("aThresholdC").item(0).getTextContent();
				
				valuesXML.setPathImg(pathImg);
				valuesXML.setPathImgMatriculas(pathImgMatriculas);
				valuesXML.setNombreArchivo(nombreArch);
				valuesXML.setPathTess(pathTess);
				
				umbralCanny = Integer.parseInt(umbralCanny_aux);
				factorUmbralCanny = Integer.parseInt(factorUmbralCanny_aux);
				umbralKernel1 = Integer.parseInt(umbralSobel1_aux);
				umbralKernel2 = Integer.parseInt(umbralSobel2_aux);
				aThresholdBlockSize = Integer.parseInt(aThresholdBlockSize_aux);
				aThresholdC = Integer.parseInt(aThresholdC_aux);
				
				valuesXML.setTypeFiltre(typeFiltre);
				valuesXML.setUmbralCanny(umbralCanny);
				valuesXML.setFactorCanny(factorUmbralCanny);
				valuesXML.setUmbralKernel1(umbralKernel1);
				valuesXML.setUmbralKernel2(umbralKernel2);
				valuesXML.setaThresholdBlockSize(aThresholdBlockSize);
				valuesXML.setaThresholdC(aThresholdC);
				
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			}
			return valuesXML;
		}
		else
		{
			System.out.println("	[x] ERROR: El archivo de configuración no se encuentra");
			return null;
		}
	}
	
	/*
	 * Esta función extrae los valores del archivo de configuración para la base de datos 
	 * [in] xmlPath es la ruta donde se encuentra el archivo de configuración
	 * [in] att atributo del xml a sacar
	 * [out] Devuelve el item con el valor leído
	 */
	public String leerXMLbddSQL(String xmlPath, int att) throws ParserConfigurationException{
		File file = new File(xmlPath);
		String atributo="";
		String item="";
		
		if(file.exists())
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			try {
				Document document = documentBuilder.parse(file);
				switch (att)
				{
					case 1 : atributo="dir";
							break;
					case 2 : atributo="enabled";
							break;
					case 3 : atributo="user";
							break;
					case 4 : atributo="password";
							break;
					default: break;
				}
				
				item = document.getElementsByTagName("postgreSQL").item(0).getAttributes().getNamedItem(atributo).getNodeValue();

			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return item;
	}
	
	/*
	 * Esta función devuelve el valor que iindica si el archivo Json debe ser creado o no
	 * [in] xmlPath es la ruta donde se encuentra el archivo de configuración
	 * [out] Devuelve el valor leído
	 */
	public String enabledJSONFile(String xmlPath) throws ParserConfigurationException{
		File file = new File(xmlPath);
		String item="";
		
		if(file.exists())
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			try {
				Document document = documentBuilder.parse(file);
				item = document.getElementsByTagName("fileJson").item(0).getAttributes().getNamedItem("createFile").getNodeValue();

			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return item;
	}
	
	/*
	 * Esta función extrae los valores del archivo de configuración para la base de datos 
	 * [in] xmlPath es la ruta donde se encuentra el archivo de configuración
	 * [in] att atributo del xml a sacar
	 * [out] Devuelve el item con el valor leído
	 */
	public String leerXMLbddMongoDB(String xmlPath, int att) throws ParserConfigurationException{
		File file = new File(xmlPath);
		String atributo="";
		String item="";
		
		if(file.exists())
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			try {
				Document document = documentBuilder.parse(file);
				switch (att)
				{
					case 1 : atributo="dir";
							break;
					case 2 : atributo="enabled";
							break;
					case 3 : atributo="port";
							break;
					case 4 : atributo="dbName";
						break;
					case 5 : atributo="collection";
						break;
					default: break;
				}
				
				item = document.getElementsByTagName("mongoDB").item(0).getAttributes().getNamedItem(atributo).getNodeValue();

			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return item;
	}
	
}
