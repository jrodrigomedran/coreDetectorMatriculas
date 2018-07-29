package bbdd;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;

import readerXML.Reader;
import readerXML.Values;

public class ConnectSQL {

	public ConnectSQL() {
	}
	
	/*
	 * Esta función sirve para crear la conexión con la base de datos de información general
	 * [in] xmlPath es la ruta donde se encuentra el archivo de configuración
	 * [out] Devuelve la conexión creada
	 */
	public Connection connectInforGen(String xmlPath) throws ParserConfigurationException {
		System.out.println("	[?] Conectando a Base de Datos ...");
		Reader readerXML = new Reader();
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return null;
		}

		System.out.println("	[V] Conectado a Base de Datos");
		System.out.println("	[V] PostgreSQL JDBC Driver Registrado!");
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(
					readerXML.leerXMLbddSQL(xmlPath, 1), readerXML.leerXMLbddSQL(xmlPath, 3),
					readerXML.leerXMLbddSQL(xmlPath, 4));
		} catch (SQLException e) {
			System.out.println("	[X] ERROR: Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection != null) {
			System.out.println("	[V] Tienes el control de la Base de datos");
		} else {
			System.out.println("	[X] ERROR: Failed to make connection!");
		}
		
		return connection;
	}
	
	/*
	 * Esta función sirve para realizar la consulta SQL con la base de datos de información general
	 * [in] connection Es la conexión creada anteriormente
	 * [in] matricula Matricula detectada
	 * [in] xmlPath es la ruta donde se encuentra el archivo de configuración
	 * [out] Devuelve la conexión creada
	 */
	public JSONObject executeSQL(Connection connection, String matricula, String xmlPath, Values vXML)
	{
		Statement sStatement;
		ResultSet sResultSet;
		String dniProp="";
		String nombreCompleto="";
		String marca="";
		String modelo="";
		String razon="";
		String fecha="";
		String localizacion="";
		String direccionCompleta="";
		
		if(matricula.isEmpty() || (connection == null))
		{
			System.out.println("	[X] Consulta no valida");
			return null;
		}
		else
		{
			// Consulta para acceder a los datos del vehiculo y del propietario
			try {
				sStatement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				System.out.println("	[?] Elaborando consulta ...");
				sResultSet = sStatement.executeQuery("SELECT * FROM vehiculo \r\n" + 
						"INNER JOIN propietario ON vehiculo.dni_v=propietario.dni\r\n" + 
						"INNER JOIN infraccion ON vehiculo.infraccion=infraccion.id\r\n" + 
						"INNER JOIN direccion ON propietario.direccion=direccion.id WHERE matricula='"+matricula+"'");
			
				while (sResultSet.next())
				{
					dniProp = sResultSet.getString("dni_v");
					nombreCompleto = sResultSet.getString("nombre") + " " + sResultSet.getString("apellidos");
					marca = sResultSet.getString("marca");
					modelo = sResultSet.getString("modelo");
					razon = sResultSet.getString("razon");
					fecha = sResultSet.getString("fecha");
					localizacion = sResultSet.getString("procedencia");
					direccionCompleta = sResultSet.getString("tCalle") + " " + sResultSet.getString("nCalle") + " nro. " + sResultSet.getString("numero")
										+ " Pso. " + sResultSet.getString("piso") + ", " + sResultSet.getString("localidad") + ", " + sResultSet.getString("comunidad")
										+ ", " + sResultSet.getString("pais");
	            }
				
				System.out.println("	[V] Consulta correcta");
			} catch (SQLException e) {
				System.out.println("	[X] Error en la consulta");
				e.printStackTrace();
			}
			
			JSONObject objJson = new JSONObject();
			try {
				objJson = createJson(matricula, dniProp, nombreCompleto, marca, modelo, razon, fecha, localizacion, direccionCompleta, xmlPath, vXML);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return objJson;
		}
	}
	
	/*
	 * Esta función sirve para crear el Json que servirá para enviar los datos a Mongo
	 * [in] (all) Datos extraidos de la la base de datos
	 * [in] xmlPath es la ruta donde se encuentra el archivo de configuración
	 * [out] Devuelve la conexión creada
	 */
	@SuppressWarnings("unchecked")
	public JSONObject createJson(
			String matricula,
			String dniProp,
			String nombreCompleto,
			String marca,
			String modelo,
			String razon,
			String fecha,
			String localizacion,
			String direccionCompleta,
			String xmlPath,
			Values vXML) throws IOException
	{
		Date fechaActual = new Date();
		Reader readerXML = new Reader();
		String fechaAct=new SimpleDateFormat("dd-MM-yyyy").format(fechaActual);
		String urlFoto = vXML.getPathWeb()+"MATRICULA_"+matricula+".png";
		
		JSONObject objJson = new JSONObject();
		objJson.put("Matricula", matricula);
		objJson.put("urlFoto", urlFoto);
		
		JSONObject itemProp = new JSONObject();
		itemProp.put("DNI", dniProp);
		itemProp.put("NombreCompleto", nombreCompleto);
		itemProp.put("Direccion", direccionCompleta);
		objJson.put("Propietario", itemProp);
		
		objJson.put("Marca", marca);
		
		objJson.put("Modelo", modelo);
		
		JSONObject itemInfrac = new JSONObject();
		itemInfrac.put("Razon", razon);
		itemInfrac.put("Fecha", fecha);
		itemInfrac.put("Procedencia", localizacion);
		objJson.put("Infraccion", itemInfrac);
		
		objJson.put("FechaDeteccion", fechaActual.toString()); 
		
		try {
			if(readerXML.enabledJSONFile(xmlPath).equals("true"))
			{
				FileWriter file = new FileWriter(matricula + "_" + fechaAct + ".json");
						
				try {
					file.write(objJson.toJSONString());
					System.out.println("	[?] Creando archivo JSON...");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				file.flush();
				file.close();
				
				System.out.println("	[V] JSON creado correctamente");
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return objJson;
	}
}
