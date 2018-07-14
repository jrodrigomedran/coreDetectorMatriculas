package core;

import java.io.File;
import net.sourceforge.tess4j.*;

public class ConvertOCR {
	
	public ConvertOCR() {}
	
	/*
	 * Esta función es la encargada de aplicar el OCR sobre una imagen para extraer su texto
	 * Este OCR es Tesseract 4
	 * [in] nameImg es el nombre de la imagen a tratar
	 * [in] pathTess es la ruta donde se encuentran las librerías de Tess4J
	 * [out] Devuelve el texto que detecta
	 */
	public String ocr(String nameImg, String pathTess) {
		String textImg = "";
		File imageFile = new File(nameImg);
		Tesseract instance = new Tesseract();
		instance.setDatapath(pathTess);
		instance.setLanguage("eng");
		instance.setTessVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		try 
		{
			textImg = instance.doOCR(imageFile);
		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}
		return textImg;
	}
}
