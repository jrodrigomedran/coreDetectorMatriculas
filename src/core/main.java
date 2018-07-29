package core;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
 
	static WatchService wService;
	static WatchKey key;
    
	public static void main(String arg[]) throws InterruptedException {
		
		// Leer libreria nativa
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Reader readerXML = new Reader();
		String pathDir = System.getProperty("user.dir"); // Se extrae el directorio de ejecución
		String xmlPath= pathDir + "\\config.xml";

		try {
			Values vXML = readerXML.leerXML(xmlPath);
			Path directoryPath = FileSystems.getDefault().getPath(vXML.pathImg);
			if (!Files.exists(directoryPath)) {
	            System.out.println(String.format("	[X] El directorio %s no exite", directoryPath.toString()));
	        }
	        System.out.println(String.format("	[I] Monitorizando cambios en " +  directoryPath.toString()));
	        try {
				wService = FileSystems.getDefault().newWatchService();
				directoryPath.register(wService, StandardWatchEventKinds.ENTRY_CREATE);
				while(true)
				{
					controlChangesDirectory(vXML, readerXML, xmlPath);
				}
	        } catch (Exception e) 
	        {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			//detectorMatriculasMain(vXML, readerXML, xmlPath);
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
	
	/*
	 * Esta función se encarga de procesar la imagen y extraer la matricula
	 * para extraer la matricula
	 * [in] vXML objeto con todos los datso leídos del XML de configuración
	 * [in] readerXML objeto que lee el archivo de configuración
	 * [in] xmlPath ruta del archivo de configuración
	 */
	public static void detectorMatriculasMain(Values vXML, Reader readerXML, String xmlPath)
	{
		String matriculaLeida="";
		if(vXML!=null)
		{
			matriculaLeida = new Detector().detectorMatriculas(vXML);
		}
		else
		{
			System.out.println("	[x] ERROR: No se ha podido realizar el procesamiento");
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
					
				objJson = driver.executeSQL(connection, matriculaLeida, xmlPath, vXML);
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
	}
	
	/*
	 * Esta función se encarga de detectar la creación de alguna imagen y de procesarla
	 * para extraer la matricula
	 * [in] vXML objeto con todos los datso leídos del XML de configuración
	 * [in] readerXML objeto que lee el archivo de configuración
	 * [in] xmlPath ruta del archivo de configuración
	 */
	public static void controlChangesDirectory(Values vXML, Reader readerXML, String xmlPath) throws Exception
	{
		/* Wait until we get some events */
        key = wService.take();
        if (key.isValid()) 
        {
	        List<WatchEvent<?>> events = key.pollEvents();
	        for(WatchEvent<?> event: events) 
	        {
	        	/* In the case of ENTRY_CREATE events the context is a relative */
	            Path path = (Path)event.context();
	            Kind<?> kindOfEvent = event.kind();
	            System.out.println(String.format("	[I] Evento '%s' detectado en '%s'", kindOfEvent.name(),path));
	            if(!path.toString().equals("RectanguloMatricula.png") && !path.toString().equals("_temp_MATRICULA_Cut.png"))
	            {
	            	vXML.setNombreArchivo(path.toString());
	            	TimeUnit.SECONDS.sleep(1);
	               	detectorMatriculasMain(vXML, readerXML, xmlPath);
	            }
	        }
        }
        /* once an key has been processed,  */
        boolean valid = key.reset();
        System.out.println(String.format("	[I] Tratamiento finalizado: %s", valid));
    }
}
