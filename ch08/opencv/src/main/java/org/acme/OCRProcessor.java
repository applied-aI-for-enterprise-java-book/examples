package org.acme;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;

import org.bytedeco.tesseract.TessBaseAPI;
import static org.bytedeco.leptonica.global.leptonica.pixRead;

import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

public class OCRProcessor {

    private static final ReentrantLock reentrantLock = new ReentrantLock();
    private static final TessBaseAPI api;

    // Static block for one-time initialization
    static {
        System.out.println("Init 1");
        api = new TessBaseAPI();
        if (api.Init("src/main/resources", "eng") != 0) {
            throw new RuntimeException("Could not initialize Tesseract.");
        }
        System.out.println("Init 2");
    }

    public static String extractText(Path imagePath) {
        String content = "";

        // Using try-with-resources for automatic resource management
        try (
             PIX image = pixRead(imagePath.toFile().getAbsolutePath())) {

            System.out.println("Read image");

            // Lock the code that uses the shared resource
            reentrantLock.lock();
            try {
                api.SetImage(image);
                System.out.println("Set image");
                final BytePointer bytePointer = api.GetUTF8Text();

                System.out.println("Put");
                content = bytePointer.getString();
                bytePointer.close();
                System.out.println("String");
            } finally {
                reentrantLock.unlock();
            }

        } catch (Exception e) {
            // Log or handle the exception
            e.printStackTrace();
        }

        return content;
    }

    // Method to clean up resources
    public static void cleanup() {
        api.End();  // Free resources when the application is done
    }
}
