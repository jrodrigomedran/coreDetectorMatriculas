package core;

import java.sql.Connection;

import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
/*import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;*/
import org.opencv.core.Core;

import com.mongodb.client.MongoDatabase;

import readerXML.*;
import bbdd.*;

public class main {
 
	public static void main(String arg[]) throws InterruptedException {
		// Leer libreria nativa
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Reader readerXML = new Reader();
		String pathDir = System.getProperty("user.dir"); // Se extrae el directorio de ejecución
		String matriculaLeida="";
		String xmlPath= pathDir + "\\config.xml";

		try {
			Values vXML = readerXML.leerXML(xmlPath);
			if(vXML!=null)
			{
				matriculaLeida = new Detector().detectorMatriculas(vXML);
			}
			else
			{
				System.out.println("	[x] ERROR: No se ha podido realizar el procesamiento");
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		System.out.println("	[V] Procesamiento de imagen finalizado");
		
		JSONObject objJson = new JSONObject();
		
		// Se establece conexion con SQL y se genera el JSON
		try {
			if(readerXML.leerXMLbddSQL(xmlPath, 2).equals("true"))
			{
				ConnectSQL driver = new ConnectSQL();
				Connection connection = null;
				connection = driver.connectInforGen(xmlPath);
				
				objJson = driver.executeSQL(connection, matriculaLeida, xmlPath);
			}
			else
			{
				System.out.println("	[I] PostgreSQL deshabilitado");
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		// Se envía el JSON a MongoDB
		try {
			if(readerXML.leerXMLbddMongoDB(xmlPath, 2).equals("true"))
			{
				ConnectMongoDB driverMongo = new ConnectMongoDB(readerXML, xmlPath);
				MongoDatabase dbMongo = driverMongo.connectMongo();
				if(!objJson.isEmpty())
				{
					driverMongo.enviarMongo(objJson, dbMongo);
				}
			}
			else
			{
				System.out.println("	[I] MongoDB deshabilitado");
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		// Se crea el JFrame
		/*JFrame frame = new JFrame("Detección de matriculas");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
		Detector detectorMatriculas = new Detector();
		Panel panel = new Panel();
		frame.setSize(400, 400);
		frame.setBackground(Color.BLUE);
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
 
		// Se crea una matriz que contendrá la imagen
		Mat imagenDeWebCam = new Mat();
		VideoCapture webCam = new VideoCapture(0); // Se toma el control de la cámara
 
		if (webCam.isOpened()) {
			Thread.sleep(500); // Se interrumpe el thread para permitir que la webcam se inicialice
			while (true) {
				webCam.read(imagenDeWebCam);
				if (!imagenDeWebCam.empty()) {
					Thread.sleep(200); // Permite que la lectura se complete
					frame.setSize(imagenDeWebCam.width() + 40, imagenDeWebCam.height() + 60);
					// Invocamos la rutina de opencv que detecta rostros sobre la imagen obtenida por la webcam
					imagenDeWebCam = detectorMatriculas.detecta(imagenDeWebCam);
					// Muestra la imagen
					panel.convierteMatABufferedImage(imagenDeWebCam);
					panel.repaint();
				} else {
					System.out.println("No se capturó nada");
					break;
				}
			}
		}
		webCam.release(); // Se libera el recurso de la webcam*/
	}
}
