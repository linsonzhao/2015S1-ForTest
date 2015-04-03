package html5;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
 
import javax.imageio.ImageIO;
 
import org.apache.commons.codec.binary.Base64;
 
import com.github.sarxos.webcam.Webcam;
 
public class ClientServiceThread implements Runnable {
    Socket myClientSocket;
    Webcam webcam = Webcam.getDefault();
 
    public ClientServiceThread() {
        super();
    }
 
    ClientServiceThread(Socket s) {
//        myClientSocket = s;
    	myClientSocket = new Socket();
        webcam.setViewSize(new Dimension(480, 400));
        webcam.open();
    }
 
    public void run() {
        InputStream in = null;
        OutputStream out = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        System.out.println("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
 
        try {
            in = myClientSocket.getInputStream();
            out = myClientSocket.getOutputStream();
 
            int x = 1;
            while (true) {
                byte bytes2[] = new byte[1];
                for (int z = 0; z < 11; z++) {
                    in.read(bytes2);
                    System.out.print((char) bytes2[0]);
                }
                System.out.println();
                 
                System.out.println("start cap");
                BufferedImage image = resize(new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), 640, 480);
                //              BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                System.out.println("end cap");
                 
 
                System.out.println("start encode");
                baos.reset();
                ImageIO.write(image, "jpg", baos);
                //              ImageIO.write(image, "png", new File("testing.png"));
                baos.flush();
                byte[] bytes = baos.toByteArray();
                baos.close();
 
                //              out.write("fssss".getBytes());
 
                byte header[] = (String.format("%09d", Base64.encodeBase64(bytes).length)).getBytes();
                System.out.println("end encode");
 
                System.out.println("start transmit");
                out.write(header);
                out.flush();
 
                out.write(Base64.encodeBase64(bytes));
                out.flush();
                System.out.println("end transmit");
                 
                System.out.println("reply :" + bytes.length + "," + bytes + ", encoded len=" + Base64.encodeBase64(bytes).length);              x++;
 
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                myClientSocket.close();
                System.out.println("...Stopped");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
 
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
    
    public void main(String[] args){
    	
    }
}