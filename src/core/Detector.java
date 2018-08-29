package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import readerXML.Values;

class Detector {

	// Se trata de la proporcion exacta entre la anchura y la altura de una
	// placa de matricula. Serian 52 cm. de anchura por 11 cm. de altura.
	static final double PROP_MATRICULAS = 4.73;
	static final double ERROR_A_PROP = 0.35;
	
	public Detector()
	{}
 
	/*
	 * Esta función se encarga de extraer el texto de una imagen dada
	 * [in] pathImg es la ruta de la imagen a tratar
	 * [in] nameImg es el nombre de la imagen a tratar
	 * [in] valuesXML es un obj que contiene valores de configuración leídos desde un XML
	 * [out] Devuelve el texto que detecta
	 */
	public String extractText(String pathImg, String nameImg, Values valuesXML) 
	{
		String text_ocr;
        ConvertOCR ocrObj = new ConvertOCR();
        String pathTess = valuesXML.getPathTess();
        text_ocr=ocrObj.ocr(pathImg+nameImg, pathTess);
        return text_ocr;
	}
	
	/*
	 * Esta funcion se encarga de detectar la matricula a partir de una imagen
	 * [in] valuesXML es un obj que contiene valores de configuración leídos desde un XML
	 */
	public String detectorMatriculas(Values valuesXML)
	{
		String pathImg = valuesXML.getPathImg();
		String nameImg = valuesXML.getNombreArchivo();
		String matricula="";
		
        Mat Main = Imgcodecs.imread(pathImg + nameImg); 							// Carga la imagen
        
        Mat imgMatricula = new Mat();
        Mat imgMatricula2 = new Mat();
        Mat imgMatricula3 = new Mat();
        Imgproc.pyrDown(Main, imgMatricula);							      		// Ajuste de tamaño
        Imgproc.pyrDown(Main, imgMatricula2);
        Imgproc.pyrDown(Main, imgMatricula3);
        
        Imgproc.cvtColor(imgMatricula, imgMatricula, Imgproc.COLOR_RGB2GRAY);  		// A escala de grises
        Imgproc.GaussianBlur(imgMatricula, imgMatricula, new Size(21,21),0);	    // Reducción de ruidos
        //Imgcodecs.imwrite(pathImg+"rgb2gray.png",imgMatricula);
	 
        // HoughLinesP
        Imgproc.cvtColor(imgMatricula3, imgMatricula3, Imgproc.COLOR_RGB2GRAY);  	// A escala de grises
        Imgproc.blur(imgMatricula3, imgMatricula3, new Size(3, 3));	    			// Reducción de ruidos
        //Imgcodecs.imwrite(pathImg+"rgb2gray.png",imgMatricula3);
        Imgproc.Canny(imgMatricula3, imgMatricula3, 50, 50*3);
        //Imgcodecs.imwrite(pathImg+"canny.png",imgMatricula3);
        
        Mat lines = new Mat(); 
        Imgproc.HoughLinesP(imgMatricula3, lines, 1, Math.PI / 180, 50, 50, 10); 
        for(int i = 0; i < lines.cols(); i++)
        { 
        	double[] val = lines.get(0, i); 
        	Imgproc.line(imgMatricula3, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2); 
        } 
        //Imgcodecs.imwrite(pathImg+"lines.png",imgMatricula3);
        //**********
	    
        Mat imgThereshold = new Mat();
        Imgproc.threshold(imgMatricula3, imgThereshold, 0, 255, Imgproc.THRESH_OTSU+Imgproc.THRESH_BINARY);
        //Imgcodecs.imwrite(pathImg+"umbral.png", imgThereshold);
        
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Mat hierarchy = new Mat();
	    Imgproc.findContours(imgThereshold, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE); //RETR_EXTERNAL CHAIN_APPROX_NONE
	    List<Rect> boundRect=new ArrayList<>();
	    
	    for(int i=0; i< contours.size();i++)
	    {
	    	MatOfPoint2f  mMOP2f1=new MatOfPoint2f();
	        MatOfPoint2f  mMOP2f2=new MatOfPoint2f();

	        contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
	        Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 3, true); 
	        mMOP2f2.convertTo(contours.get(i), CvType.CV_32S);

	        Rect appRect = Imgproc.boundingRect(contours.get(i));
	        
	        if (appRect.area()>30000)
	        {
	        	boundRect.add(appRect);
	        }

	    }
	    
	    Rect fBoundRect = filtrarRect(boundRect, imgMatricula2);
	    
	    // Si es buena, no necesita tratamiento extra
	    //if(checkProporcion(fBoundRect))	
	    if(!fBoundRect.empty())
	    {
	        Imgproc.rectangle(imgMatricula2,fBoundRect.br(), fBoundRect.tl(),new Scalar(0,255,0),3,8,0);  
		    Imgcodecs.imwrite(pathImg+"RectanguloMatricula.png",imgMatricula2);
		    
		    matricula = convertRectToImgForText(imgMatricula2, fBoundRect, pathImg, valuesXML);
	    }
	    
	    return matricula;
    }
	
	/*
	 * Esta funcion se encarga de transladar la imagen tratada al repositorio en caso de estar en la base de datos
	 * [in] matricula es lamatricula detectada
	 * [in] valuesXML es un obj que contiene valores de configuración leídos desde un XML
	 */
	public void moveFileImg(String matricula, Values valuesXML)
	{
		String pathImg = valuesXML.getPathImg();
		
		if((matricula != "") || (matricula != null))
	    {
	    	File file_temp = new File(pathImg+"RectanguloMatricula.png");
		    File file_final = new File(valuesXML.pathImgMatriculas+"MATRICULA_"+matricula+".png");
		    boolean success = file_temp.renameTo(file_final);
		    if(!success)
		    {
		    	System.out.println("	[X] Error al copiar imagen");
		    }
		    else
		    {
		    	System.out.println("	[V] Imagen copiada correctamente");
		    	file_temp.delete();
		    }
	    }
	}
	
	/*
	 * Esta funcion se encarga de comprobar si la matricula detectada se ajusta a las proporciones validas
	 * [in] fBoundRect Rectangulo con la matricula detectada
	 * [out] True/False si se cumple o no la proporción
	 */
	public boolean checkProporcion(Rect fBoundRect)
	{
		double altura = fBoundRect.height;
		double anchura = fBoundRect.width;
		double prop = 0;
		
		prop = anchura/altura;
		
		if((prop < (PROP_MATRICULAS-ERROR_A_PROP)) || (prop > (PROP_MATRICULAS+ERROR_A_PROP)))
		{
			System.out.println("	[X] Matricula con proporciones no válidas");
			return false;
		}
		else
		{
			System.out.println("	[I] Matricula con proporciones válidas");
			return true;
		}
	}
	
	/* 
	 * Filtra los rectángulos y se queda solamente con los que se encuentran en la 
	 * zona media de la imagen
	 * [in] boundRect es todo el conjunto de rectangulos detectados por el algoritmo
	 * [in] img es la imagen sobre la que se ha trabajado
	 * [out] se devuelve una lista con los rectángulos que son de interés 
	*/
	public Rect filtrarRect(List<Rect> boundRect, Mat img)
	{
	    List<Rect> filterBoundRect=new ArrayList<>();
		double ptoMaxY = img.height();
		double ptoMinY = 0;
		Rect rectMayor = new Rect();
		Rect rectAux = new Rect();
		// Se divide la pantalla en 4 porciones para coger solamente los rectangulos que tengan
		// todos sus puntos en los 2/4 centrales
		ptoMaxY=ptoMaxY/4;	
		ptoMinY=img.height()-ptoMaxY;
		
		for(int i=0; i<boundRect.size(); i++)
		{
			if(!((boundRect.get(i).br().y < ptoMaxY) || (boundRect.get(i).tl().y < ptoMaxY) 
					|| (boundRect.get(i).br().y > ptoMinY) || (boundRect.get(i).tl().y > ptoMinY)))
			{
				filterBoundRect.add(boundRect.get(i));
			}
		}
		
		if(filterBoundRect.size() > 1) 
		{
			for(int i=0; i<filterBoundRect.size(); i++)
			{
				rectAux = filterBoundRect.get(i);
				if(rectMayor.area() <= rectAux.area()) 
				{
					rectMayor = rectAux;
				}
			}
		}
		else
		{
			if(!filterBoundRect.isEmpty())
			{
				rectMayor = filterBoundRect.get(0);
			}
		}
		
		return rectMayor;
	}
	
	/* 
	 * Filtra los rectángulos por sus coordenadas y se queda solo con las más altas,
	 * tras esto, dibuja un rectángulo con las coordenadas máx y min
	 * [in] boundRect es todo el conjunto de rectangulos detectados por el algoritmo
	 * [in] img es la imagen sobre la que se ha trabajado
	 * [in] pathImg es la ruta para almacenar la imagen con el rectángulo
	 * [out] Devuelve el rectángulo que engloba toda la matricula
	*/
	public Rect selectRectTotal(List<Rect> boundRect, Mat img, String pathImg)
	{
		if(!boundRect.isEmpty())
		{
			double yMin = boundRect.get(0).br().y;
			double yMax = boundRect.get(0).br().y;
			double xMin = boundRect.get(0).br().x;
			double xMax = boundRect.get(0).br().x;
			
			for(int i=0; i<boundRect.size(); i++)
			{	
				/*System.out.println("br().y -- " + boundRect.get(i).br().y);
				System.out.println("tl().y -- " + boundRect.get(i).tl().y);*/
				
				if(boundRect.get(i).br().y < yMin)
				{
					yMin = boundRect.get(i).br().y;
				}
				else
				{
					if(boundRect.get(i).br().y > yMax)
					{
						yMax = boundRect.get(i).br().y;
					}
				}
				
				if(boundRect.get(i).tl().y < yMin)
				{
					yMin = boundRect.get(i).tl().y;
				}
				else
				{
					if(boundRect.get(i).tl().y > yMax)
					{
						yMax = boundRect.get(i).tl().y;
					}
				}
				
				if(boundRect.get(i).br().x < xMin)
				{
					xMin = boundRect.get(i).br().x;
				}
				else
				{
					if(boundRect.get(i).br().x > xMax)
					{
						xMax = boundRect.get(i).br().x;
					}
				}
				
				if(boundRect.get(i).tl().x < xMin)
				{
					xMin = boundRect.get(i).tl().x;
				}
				else
				{
					if(boundRect.get(i).tl().x > xMax)
					{
						xMax = boundRect.get(i).tl().x;
					}
				}
			}
			
			Rect rect = new Rect(new Point(xMin, yMin), new Point(xMax, yMax));
			
			Imgproc.rectangle(img, new Point(rect.br().x, rect.br().y), new Point(rect.tl().x, rect.tl().y), new Scalar(0,0,255),3,8,0);
			/*Imgproc.line(img, new Point(xMin, yMin), new Point(xMin, yMin), new Scalar(0,0,255),8,8,0);
			Imgproc.line(img, new Point(xMax, yMax), new Point(xMax, yMax), new Scalar(0,0,255),8,8,0);*/
			Imgcodecs.imwrite(pathImg+"MATRICULA.png",img);
			
			return rect;
		}
		else
		{
			System.out.println("	[X] ERROR: Error al extraer matrícula ");
			return null;
		}
	}
	
	/* 
	 * Esta función recorta un rectángulo dado de una imagen
	 * [in] img es la imagen sobre la que se ha trabajado
	 * [in] rect es el rectángulo que se quiere recortar
	 * [in] pathImg es la ruta para almacenar la imagen con el rectángulo
	 * [out] Devuelve la imagen recortada y el texto que haya en ella
	*/
	public String convertRectToImgForText(Mat img, Rect rect, String pathImg, Values valuesXML)
	{
		Mat cropImg = new Mat(img, rect);
		Imgproc.cvtColor(cropImg, cropImg, Imgproc.COLOR_RGB2GRAY);  		// A escala de grises
		//Imgproc.GaussianBlur(cropImg, cropImg, new Size(3,3),0);	    	// Reducción de ruidos

		//Imgproc.threshold(cropImg, cropImg, 0, 255, Imgproc.THRESH_OTSU+Imgproc.THRESH_BINARY);
		
		String nameImg = pathImg+"_temp_MATRICULA_Cut.png";
		File fileImg = new File(nameImg);
		Imgcodecs.imwrite(nameImg,cropImg);
		
		if(fileImg.exists())
		{
			String matricula = extractText(pathImg, "_temp_MATRICULA_Cut.png", valuesXML);	// Extraemos el texto de la matrícula
			matricula = checkMatricula(matricula);
		    if(matricula.length() == 0)
		    {
		    	System.out.println("	[X] ERROR: Matricula no reconocida");
		    }
		    else
		    {
		    	System.out.println("	[V] Matricula detectada: " + matricula);
		    	fileImg.delete();
		    }
		    return matricula;
		}
		else
		{
			System.out.println("	[X] ERROR: La imagen recorte no se ha creado");
			return null;
		}
	}
	
	/* 
	 * Esta función se utiliza para corregir posibles errores en la matriucla leída
	 * [in] matricula es la matricula detectada sobre la imagen de entrada
	 * [out] Devuelve la matricula corregida si es necesario
	*/
	public String checkMatricula(String matricula)
	{
		int cntL = 0;
		boolean bInitLetter = false;
		int cntD = 0;
		
		if(!matricula.isEmpty())
		{
			matricula = matricula.replace("\n", "");
			matricula = matricula.replace(" ", "");
			
			if(matricula.length() > 8)
			{
				matricula = matricula.substring(2, matricula.length());
			}
			
			// Sevuelve a comprobar el estado de la cadena matricula por si se ha vuelto 
			// vacía tras quitar los espacios y los salto de línea
			if(!matricula.isEmpty() && Character.isLetter(matricula.charAt(0)))
			{
				bInitLetter=true;
				
				for(int i=1; i<matricula.length();i++)
				{
					if(Character.isDigit(matricula.charAt(i)))
					{
						cntD++;
					}
					else
					{
						if(cntD==4)
						{
							cntL++;
						}
					}
				}
			}
			
			/*System.out.println("Letras: " + cntL);
			System.out.println("Letra INIT?: " + bInitLetter);
			System.out.println("Digitos: " + cntD);*/
			
			if(bInitLetter && (cntL==3))
			{
				matricula = matricula.substring(1, matricula.length());
			}
			
			if(Character.isDigit(matricula.charAt(matricula.length()-1)))
			{
				matricula = matricula.substring(0, matricula.length()-1);
			}
			
		}
		return matricula;
	}
}