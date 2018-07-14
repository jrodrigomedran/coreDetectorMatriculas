package core;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

class Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage imagen;
 
	public Panel() {
		super();
	}
 
	/*
	 * Convierte y escribe una Matriz en un objeto BufferedImage
	 */
	public boolean convierteMatABufferedImage(Mat matriz) {
		MatOfByte mb = new MatOfByte();
		Imgcodecs.imencode("ima.jpg", matriz, mb);
		try {
			this.imagen = ImageIO.read(new ByteArrayInputStream(mb.toArray()));
		} catch (IOException e) {
			e.printStackTrace();
			return false; // error
		}
		return true; // éxito
	}
 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.imagen == null)
			return;
		g.drawImage(this.imagen, 10, 10, this.imagen.getWidth(), this.imagen.getHeight(), null);
	}
}
