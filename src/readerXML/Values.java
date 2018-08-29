package readerXML;

public class Values {
	
	public String pathImg;
	public String pathImgMatriculas;
	public String pathWeb;
	public String nombreArchivo;
	public String pathTess;
	public int umbralCanny;
	public int factorCanny;
	public int umbralKernel1;
	public int umbralKernel2;
	public boolean typeFiltre;
	public int aThresholdBlockSize;
	public int aThresholdC;

	public Values(String pathImg, String pathImgMatriculas, String pathWeb, String nombreArchivo, String pathTess,
			int umbralCanny, int factorCanny, boolean typeFiltre,
			int umbralKernel1, int umbralKernel2, int aThresholdBlockSize,
			int aThresholdC) {
		super();
		this.pathImg = pathImg;
		this.pathImgMatriculas = pathImgMatriculas;
		this.pathWeb = pathWeb;
		this.pathTess = pathTess;
		this.nombreArchivo = nombreArchivo;
		this.umbralCanny = umbralCanny;
		this.factorCanny = factorCanny;
		this.typeFiltre = typeFiltre;
		this.umbralKernel1 = umbralKernel1;
		this.umbralKernel2 = umbralKernel2;
		this.aThresholdBlockSize = aThresholdBlockSize;
		this.aThresholdC = aThresholdC;
	}

	public String getPathTess() {
		return pathTess;
	}

	public void setPathTess(String pathTess) {
		this.pathTess = pathTess;
	}
	
	public String getPathWeb() {
		return pathWeb;
	}

	public void setPathWeb(String pathWeb) {
		this.pathWeb = pathWeb;
	}

	public String getPathImgMatriculas() {
		return pathImgMatriculas;
	}

	public void setPathImgMatriculas(String pathImgMatriculas) {
		this.pathImgMatriculas = pathImgMatriculas;
	}

	public int getaThresholdBlockSize() {
		return aThresholdBlockSize;
	}

	public void setaThresholdBlockSize(int aThresholdBlockSize) {
		this.aThresholdBlockSize = aThresholdBlockSize;
	}

	public int getaThresholdC() {
		return aThresholdC;
	}

	public void setaThresholdC(int aThresholdC) {
		this.aThresholdC = aThresholdC;
	}

	public int getUmbralKernel1() {
		return umbralKernel1;
	}

	public void setUmbralKernel1(int umbralSobel1) {
		this.umbralKernel1 = umbralSobel1;
	}

	public int getUmbralKernel2() {
		return umbralKernel2;
	}

	public void setUmbralKernel2(int umbralSobel2) {
		this.umbralKernel2 = umbralSobel2;
	}

	public boolean isTypeFiltre() {
		return typeFiltre;
	}

	public void setTypeFiltre(boolean typeFiltre) {
		this.typeFiltre = typeFiltre;
	}

	public String getPathImg() {
		return pathImg;
	}

	public void setPathImg(String pathImg) {
		this.pathImg = pathImg;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	
	public int getUmbralCanny() {
		return umbralCanny;
	}

	public void setUmbralCanny(int umbralCanny) {
		this.umbralCanny = umbralCanny;
	}

	public int getFactorCanny() {
		return factorCanny;
	}

	public void setFactorCanny(int factorCanny) {
		this.factorCanny = factorCanny;
	}
	
}
