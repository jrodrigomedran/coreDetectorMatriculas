package bbdd;

import javax.xml.parsers.ParserConfigurationException;

import org.bson.Document;
import org.json.simple.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import readerXML.Reader;

public class ConnectMongoDB {
	
	Reader readerXML = new Reader();
	String xmlPath;
	
	public ConnectMongoDB(Reader readerXML, String xmlPath) {
		this.readerXML = readerXML;
		this.xmlPath = xmlPath;
	}
	
	/*
	 * Esta función sirve para realizar la conexión con la base de datos MongoDB
	 * [out] Devuelve la conexión creada
	 */
	@SuppressWarnings("resource")
	public MongoDatabase connectMongo() throws ParserConfigurationException
	{
		String dirMongo = readerXML.leerXMLbddMongoDB(xmlPath, 1);
		int port = Integer.parseInt(readerXML.leerXMLbddMongoDB(xmlPath, 3));
		String bdName = readerXML.leerXMLbddMongoDB(xmlPath, 4);
		
		System.out.println("	[?] Estableciendo conexión con MongoDB...");
		MongoClient mongoConnect = new MongoClient(dirMongo, port);
		MongoDatabase dbMongo = mongoConnect.getDatabase(bdName);
		System.out.println("	[V] Conexión con MongoDB establecida");
		return dbMongo;
	}
	
	/*
	 * Esta función sirve para realizar la escritura en MongoDB del Json creado
	 * [in] json Es el json que se ha creado con anterioridad
	 * [in] dbMongo Es la conexión de mongo
	 */
	public void enviarMongo(JSONObject json, MongoDatabase dbMongo) throws ParserConfigurationException
	{
		String collName = readerXML.leerXMLbddMongoDB(xmlPath, 5);
		
		MongoCollection<Document> collection = dbMongo.getCollection(collName);
		Document docJson = Document.parse(json.toString());
		collection.insertOne(docJson);
	}
}