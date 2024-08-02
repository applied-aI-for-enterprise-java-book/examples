package org.acme;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

@QuarkusMain
public class OpenCVMain implements QuarkusApplication {

    static {
        OpenCV.loadShared(); // <1>
    }

    @Override
    public int run(String... args) throws Exception {

        Mat hands = loadImage("hand.jpg");

        return 0;
    }


    private Mat resize(Mat original, double ratio) {
        Mat resized = new Mat();
        Size newSize = new Size(original.rows()*ratio, original.
    }

    private Mat toGreyScale(Mat original) {
        Mat greyscale = new Mat();
        Imgproc.cvtColor(original, greyscale, Imgproc.COLOR_RGB2GRAY); // <1>
        return greyscale;
    }

    private void saveImage(Mat mat, String path) {
        Imgcodecs.imwrite(path, mat);
    }

    private Mat loadImage(String image) {
        return Imgcodecs.imread(image);
    }
}
