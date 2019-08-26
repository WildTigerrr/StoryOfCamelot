package com.wildtigerrr.StoryOfCamelot.bin;

import com.wildtigerrr.StoryOfCamelot.web.service.AmazonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service("filesProcessing")
public class FileProcessing {

    private AmazonClient amazonClient;

    public FileProcessing() {
    }

    @SuppressWarnings("unused")
    @Autowired
    FileProcessing(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    public InputStream getFile(String path) {
        return amazonClient.getObject(path);
    }

    public void saveFile(String path, String name, File file) {
        amazonClient.saveFile(path + name, file);
    }
    public void saveFile(String path, String name, String data) {
        amazonClient.saveString(path + name, data);
    }

    public InputStream overlayImages(InputStream inputBack, InputStream inputFront) throws IOException {
        BufferedImage imageBack;
        BufferedImage imageFront;
        imageBack = ImageIO.read(inputBack);
        imageFront = ImageIO.read(inputFront);
        overlayImages(imageBack, imageFront);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(imageBack, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }
    public InputStream overlayImages(String pathBack, String pathFront) throws IOException {
        return overlayImages(
                getFile(pathBack),
                getFile(pathFront)
        );
    }

    public File inputStreamToFile(InputStream stream, String name, String extension) throws IOException {
        if (stream == null) return null;
        Path path;
        path = Files.createTempFile(name, extension);
        if (path != null) {
            FileOutputStream out = new FileOutputStream(path.toFile());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return path.toFile();
        }
        return null;
    }

    private BufferedImage overlayImages(BufferedImage imageBack, BufferedImage imageFront) {
        Graphics2D g = imageBack.createGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g.drawImage(imageBack, 0, 0, null);
        g.drawImage(imageFront, 0, 0, null);
        g.dispose();
        return imageBack;
    }

}