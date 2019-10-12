package com.wildtigerrr.StoryOfCamelot.bin;

import com.wildtigerrr.StoryOfCamelot.web.service.DataProvider;
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

    private DataProvider dataProvider;

    public FileProcessing() {
    }

    @SuppressWarnings("unused")
    @Autowired
    FileProcessing(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public InputStream getFile(String path) {
        return dataProvider.getObject(path);
    }

    public void saveFile(String path, String name, File file) {
        dataProvider.saveFile(path + name, file);
    }

    public void saveFile(String path, String name, String data) {
        dataProvider.saveString(path + name, data);
    }

    public File getOverlaidImagesAsFile(String pathBack, String pathFront, String name, String extension) throws IOException {
        return inputStreamToFile(
                bufferedImageToInputStream(
                        overlayImages(pathBack, pathFront)
                ),
                name,
                extension
        );
    }

    private BufferedImage overlayImages(String pathBack, String pathFront) throws IOException {
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
            inputStreamToFileOutputStream(stream, path);
            return path.toFile();
        }
        return null;
    }

    private void inputStreamToFileOutputStream(InputStream stream, Path path) throws IOException {
        FileOutputStream out = new FileOutputStream(path.toFile());
        byte[] buffer = new byte[1024];
        int len;
        while ((len = stream.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    private InputStream bufferedImageToInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    private BufferedImage overlayImages(InputStream inputBack, InputStream inputFront) throws IOException {
        BufferedImage imageBack = ImageIO.read(inputBack);
        overlayImages(imageBack, ImageIO.read(inputFront));
        return imageBack;
    }

    private void overlayImages(BufferedImage imageBack, BufferedImage imageFront) {
        Graphics2D g = imageBack.createGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g.drawImage(imageBack, 0, 0, null);
        g.drawImage(imageFront, 0, 0, null);
        g.dispose();
    }

}