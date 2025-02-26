package org.acme;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import nu.pattern.OpenCV;
import org.bytedeco.tesseract.TessBaseAPI;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.bytedeco.leptonica.global.leptonica.*;
import org.bytedeco.javacpp.BytePointer;

import org.bytedeco.leptonica.PIX;
import org.opencv.objdetect.BarcodeDetector;

import org.opencv.objdetect.QRCodeDetector;
import org.opencv.photo.Photo;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

@QuarkusMain
public class OpenCVMain implements QuarkusApplication {

    static {
        OpenCV.loadLocally(); // <1>
    }

    protected static Scalar WHITE = new Scalar(255, 255, 255);
    protected static Scalar RED = new Scalar(36,12,255);
    protected static Scalar GREEN = new Scalar(36,255,12);
    protected static Scalar BLACK = new Scalar(0, 0, 0);

    @Override
    public int run(String... args) throws Exception {

        Mat hands = loadImage(Paths.get("src/main/resources/hand.jpg"));
        Mat cropped = this.centerCrop(hands, 400);
        this.saveImage(cropped, Paths.get("target/handsCropped.jpg"));


        return 0;
    }

    protected Mat takeSnapshot() {
        VideoCapture capture = new VideoCapture(0);

        Mat image = new Mat();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        capture.read(image);

        capture.release();
       return image;
    }

    protected void processVideo(Path src, Path dst) {
        VideoCapture capture = new VideoCapture();

        if (!capture.isOpened()) {
            capture.open(src.toAbsolutePath().toString());
        }

        double frmCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
        System.out.println("frmCount = " + frmCount);

        double fps = capture.get(Videoio.CAP_PROP_FPS);
        Size size = new Size(capture.get(Videoio.CAP_PROP_FRAME_WIDTH), capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));

        VideoWriter writer = new VideoWriter(dst.toAbsolutePath().toString(),
            VideoWriter.fourcc('a', 'v', 'c', '1'),
            fps, size, true);

        Mat img = new Mat();
        int i=0;
        while (true) {
            i++;
            capture.read(img);

            if (img.empty())
                break;
            writer.write(this.binaryBinarization(img));

        }

        System.out.println(i);
        capture.release();
        writer.release();

    }

    protected String readBarcode(Mat img) {
        BarcodeDetector barcodeDetector = new BarcodeDetector();
        return barcodeDetector.detectAndDecode(img);
    }

    record QrCode(String content, Mat points) {

    }

    protected QrCode readQR(Mat img) {
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        Mat m = new Mat();
        String bc = qrCodeDetector.detectAndDecode(img, m);

        return new QrCode(bc, m);
    }

    protected Mat correctingPerspective(Mat img) {

        Mat imgCopy = this.edgeProcessing(img);

        this.saveImage(imgCopy, Paths.get("target/persCardEdge.jpg"));

        List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(imgCopy, allContours, new org.opencv.core.Mat(),
            Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_NONE);

        final Optional<MatOfPoint2f> matOfPoints = allContours.stream()
            .sorted((o1, o2) -> (int) (Imgproc.contourArea(o2, false) - Imgproc.contourArea(o1, false)))
            .limit(5)
            .map(cnt -> {
                MatOfPoint2f points2d = new MatOfPoint2f(cnt.toArray());
                final double peri = Imgproc.arcLength(points2d, true);
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(points2d, approx, 0.02 * peri, true);
                return approx;
            })
            .filter(approx -> approx.total() == 4)
            .findFirst();

        final MatOfPoint2f approxCorners = matOfPoints.get();

        MatOfPoint2f approxCornerns = arrange(approxCorners);

        MatOfPoint2f destCornerns  = getDestinationPoints(approxCornerns);


        Mat forPoints = new Mat();
        imgCopy.copyTo(forPoints);
        forPoints = drawPoints(forPoints, approxCornerns, Imgproc.MARKER_CROSS);
        forPoints = drawPoints(forPoints, destCornerns, Imgproc.MARKER_STAR);

        this.saveImage(forPoints, Paths.get("target/persCardPoints.jpg"));

        //org.opencv.core.Mat homography =  Calib3d.findHomography(approxCornerns, destCornerns, Calib3d.RANSAC, 3);
        final Mat perspectiveTransform = Imgproc.getPerspectiveTransform(approxCornerns, destCornerns);
        org.opencv.core.Mat dst = new org.opencv.core.Mat();
        Imgproc.warpPerspective(img, dst, perspectiveTransform, img.size());

        return dst;

    }

    protected Mat drawPoints(Mat mat, MatOfPoint2f points, int marker) {
        System.out.println(points.size());
        points
            .toList()
            .forEach(p -> drawPoint(mat, p, marker));

        return mat;
    }

    protected Mat drawPoint(Mat img, Point point, int marker) {
        Imgproc.drawMarker(img, point, WHITE, marker, 80, 15);
        return img;
    }

    public void perspectiveCorrection(Mat img){


        HighGui.imshow("Filtered", img);
        HighGui.waitKey(5000);

        // Masking, Thresholding and Contour detection
        org.opencv.core.Mat imgCopy = new org.opencv.core.Mat();
        img.copyTo(imgCopy);
        Imgproc.cvtColor(imgCopy, imgCopy, Imgproc.COLOR_BGR2GRAY);

        // by applying the filter, we get a nicely blurred image of the notebook and almost all the hand-written characters in it are gone
        /**org.opencv.core.Mat kernel = new org.opencv.core.Mat();
        org.opencv.core.Mat ones = org.opencv.core.Mat.ones( 5, 5, CvType.CV_32F );
        Core.multiply(ones, new Scalar(1/(double)(15)), kernel);
        org.opencv.core.Mat filtered = new org.opencv.core.Mat();
        Imgproc.filter2D(imgCopy, filtered, -1 , kernel );**/

        //Now, we apply threshold for binary classification of the image; i.e. classify each pixel as either black or white
        // The golas is detect the notebook contour with without any problems:
        Imgproc.threshold(imgCopy, imgCopy, 250, 255, Imgproc.THRESH_OTSU);
        //Mat filtered = this.edgeProcessing(img);
        HighGui.imshow("Threshold", imgCopy);
        HighGui.waitKey(5000);
        //detecting contour -  We are only interested in the largest contour:
        List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(imgCopy, allContours, new org.opencv.core.Mat(),
            Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_NONE);


        final Optional<MatOfPoint2f> matOfPoints = allContours.stream()
            .sorted((o1, o2) -> (int) (Imgproc.contourArea(o2, false) - Imgproc.contourArea(o1, false)))
            .limit(5)
            .map(cnt -> {
                MatOfPoint2f points2d = new MatOfPoint2f(cnt.toArray());
                final double peri = Imgproc.arcLength(points2d, true);
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(points2d, approx, 0.02 * peri, true);
                return approx;
            })
            .filter(approx -> approx.total() == 4)
            .findFirst();

        final MatOfPoint2f approxCorners = matOfPoints.get();

        MatOfPoint2f approxCornerns = arrange(approxCorners);

        MatOfPoint2f destCornerns  = getDestinationPoints(approxCornerns);
        System.out.println("dest_cornerns = " + destCornerns.dump());

        //org.opencv.core.Mat homography =  Calib3d.findHomography(approxCornerns, destCornerns, Calib3d.RANSAC, 3);
        final Mat perspectiveTransform = Imgproc.getPerspectiveTransform(approxCornerns, destCornerns);
        org.opencv.core.Mat dst = new org.opencv.core.Mat();
        Imgproc.warpPerspective(img, dst, perspectiveTransform, img.size());

        HighGui.imshow("Un_warped", dst);
        HighGui.waitKey(5000);
    }

    private MatOfPoint2f getDestinationPoints(MatOfPoint2f approx_corners) {
        Point[] pts = approx_corners.toArray();

        double w1 = calculateL2(pts[0], pts[1]);
        double w2 = calculateL2(pts[2], pts[3]);
        double width = Math.max(w1, w2);

        double h1 = calculateL2(pts[0], pts[2]);
        double h2 = calculateL2(pts[1], pts[3]);
        double height = Math.max(h1, h2);

        Point p0 = new Point(0,0);
        Point p1 = new Point(width -1,0);
        Point p2 = new Point(0, height -1);
        Point p3 = new Point(width -1, height -1);

        MatOfPoint2f  result = new MatOfPoint2f(p0, p1, p2, p3);
        return result;
    }

    private MatOfPoint2f arrange(MatOfPoint2f approx_corners){
        Point[] pts = approx_corners.toArray();
        MatOfPoint2f result = new MatOfPoint2f(pts[0], pts[3], pts[1], pts[2]);
        return result;
    }


    private double calculateL2(Point p1, Point p2) {
        double x1 = p1.x;
        double x2 = p2.x;
        double y1 = p1.y;
        double y2 = p2.y;

        double xDiff = Math.pow((x1 - x2), 2);
        double yDiff = Math.pow((y1 - y2), 2);

        return Math.sqrt(xDiff + yDiff);
    }



    record QRDetection(String value, Mat points){}

    private QRDetection qrCode(Mat photo) {
        QRCodeDetector qrCodeDetector = new QRCodeDetector();

        Mat points = new Mat();
        Mat detecteBarcodePhoto = new Mat();
        String value = qrCodeDetector.detectAndDecode(photo, points, detecteBarcodePhoto);

        return new QRDetection(value, points);

    }

    private Mat webcamCapture() throws InterruptedException {
        VideoCapture capture = new VideoCapture(0);
        Mat img = new Mat();

        TimeUnit.SECONDS.sleep(3);

        capture.read(img);
        capture.release();

        return img;
    }

    private void processVideo() {
        VideoCapture capture = new VideoCapture();
        final boolean opened = capture.open("");

        if (opened) {
            double frmCount = 0;
            if( capture.isOpened()){
                frmCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
            }else {
                System.out.println("Capture is not opened!");
                return;
            }


            double fps = capture.get(Videoio.CAP_PROP_FPS);
            Size size = new Size(capture.get(Videoio.CAP_PROP_FRAME_WIDTH), capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
            VideoWriter writer = new VideoWriter("d:\\Eclipse_2019_06\\myWork\\OpenCV_Demo\\videos\\outputs\\video_out.mp4",
                VideoWriter.fourcc('m','p','g','4'),
                fps, size, true);

            Mat img = new Mat();
            while (true) {
                capture.read(img);
                if (img.empty()) break;

                writer.write(img);
            }

            writer.release();
        }

        capture.release();

    }

    protected Mat noiseReduction(Mat src) {
        Mat dst = new Mat();
        Photo.fastNlMeansDenoising(src, dst, 10);

        return dst;
    }

    protected Mat binaryBinarization(Mat src) {
        Mat binary = new Mat(src.rows(), src.cols(), src.type());

        Imgproc.threshold(src, binary, 100, 255, Imgproc.THRESH_BINARY);

        return binary;
    }

    protected Mat otsuBinarization(Mat src) {
        Mat binary = new Mat(src.rows(), src.cols(), src.type());

        Mat grey = toGreyScale(src);

        Imgproc.threshold(grey, binary, 100, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        return binary;
    }

    protected Mat edgeProcessing(Mat mat) {
        final Mat processed = new Mat();
        // Blur an image using a Gaussian filter
        Imgproc.GaussianBlur(mat, processed, new Size(7, 7), 1);

        this.saveImage(processed, Paths.get("target/edgesBlurCard.jpg"));

        // Switch from RGB to GRAY
        Imgproc.cvtColor(processed, processed, Imgproc.COLOR_RGB2GRAY);

        // Find edges in an image using the Canny algorithm
        Imgproc.Canny(processed, processed, 150, 25);

        this.saveImage(processed, Paths.get("target/edgesCannyCard.jpg"));

        // Dilate an image by using a specific structuring element
        Imgproc.dilate(processed, processed, new Mat(), new Point(-1, -1), 1);

        this.saveImage(processed, Paths.get("target/edgesDilateCard.jpg"));

        return processed;
    }

    /**private Mat paintContours(Mat original, Mat processed) {
        // Find contours of an image
        final List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(
            processed,
            allContours,
            new Mat(processed.size(), processed.type()),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        );

        final List<MatOfPoint> filteredContours = allContours.stream()
            .filter(contour -> Imgproc.contourArea(contour) > 500)
            .toList();

        Imgproc.drawContours(
            original,
            filteredContours,
            -1, // Negative value indicates that we want to draw all of contours
            GREEN, // Green color
            1
        );

        return original;
    }**/

    private Mat overlayImage(Mat backgroun, Mat foregroun, Point location) throws IOException {

        Mat bg = new Mat();
        Mat fg = new Mat();

        Imgproc.cvtColor(backgroun, bg, Imgproc.COLOR_RGB2RGBA);
        Imgproc.cvtColor(foregroun, fg, Imgproc.COLOR_RGB2RGBA);

        for (int y = (int) Math.max(location.y , 0); y < bg.rows(); ++y) {

            int fY = (int) (y - location.y);

            if(fY >= fg.rows())
                break;

            for (int x = (int) Math.max(location.x, 0); x < bg.cols(); ++x) {
                int fX = (int) (x - location.x);
                if(fX >= fg.cols()){
                    break;
                }

                double opacity;
                double[] finalPixelValue = new double[4];

                opacity = fg.get(fY , fX)[2];

                finalPixelValue[0] = bg.get(y, x)[0];
                finalPixelValue[1] = bg.get(y, x)[1];
                finalPixelValue[2] = bg.get(y, x)[2];
                finalPixelValue[3] = bg.get(y, x)[3];

                for(int c = 0;  c < bg.channels(); ++c){
                    if(opacity > 0){
                        double foregroundPx =  fg.get(fY, fX)[c];
                        double backgroundPx =  bg.get(y, x)[c];

                        float fOpacity = (float) (opacity / 255);
                        finalPixelValue[c] = ((backgroundPx * ( 1.0 - fOpacity)) + (foregroundPx * fOpacity));
                        if(c==3){
                            finalPixelValue[c] = fg.get(fY,fX)[3];
                        }
                    }
                }
                bg.put(y, x, finalPixelValue);
            }
        }

        return bg;

    }

    protected Mat fillRectangle(Mat src, Rect rect, Scalar color, double alpha) {

        final Mat overlay = src.clone();
        Imgproc.rectangle(overlay, rect, color, -1);

        Mat output = new Mat();
        Core.addWeighted(overlay, alpha, src, 1 - alpha, 0, output);

        return output;
    }

    protected Mat drawRectangle(Mat src, Rect rect, Scalar color) {
        Imgproc.rectangle(src, rect, color);
        return src;
    }

    protected Mat drawRectangleWithText(Mat original, Rect rectangle, Scalar color, String text) {

        final double fontScale = 0.9d;
        final int fontThickness = 3;
        final int rectangleThickness = 3;

        Mat destination = original.clone();

        final Size textSize = Imgproc.getTextSize(text, Imgproc.FONT_HERSHEY_SIMPLEX,
                                            fontScale, fontThickness, null);

        if (textSize.width > rectangle.width) {
            rectangle.width = (int) textSize.width;
        }

        Imgproc.rectangle(destination, rectangle, color, rectangleThickness);
        Imgproc.putText(destination, text, new Point(rectangle.x, rectangle.y - 10),
            Imgproc.FONT_HERSHEY_SIMPLEX, fontScale, color, fontThickness);

        return destination;
    }

    private Mat fromStream(InputStream is) throws IOException {
        final byte[] bytes = toByteArray(is);
        return Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);
    }

    private byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4];

        while ((nRead = is.readNBytes(data, 0, data.length)) != 0) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    private InputStream toStream(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        return new ByteArrayInputStream(matOfByte.toArray());
    }

    protected Mat centerCrop(Mat original, int cropSize) {

        // Calculate the center of the image
        int centerX = original.cols() / 2;
        int centerY = original.rows() / 2;

        // Calculate the top-left corner of the crop area
        int startX = centerX - (cropSize / 2);
        int startY = centerY - (cropSize / 2);

        // Ensure the crop area is within the image boundaries
        startX = Math.max(0, startX);
        startY = Math.max(0, startY);

        int cropWidth = Math.min(cropSize, original.cols() - startX);
        int cropHeight = Math.min(cropSize, original.rows() - startY);

        Rect r = new Rect(startX, startY, cropWidth, cropHeight);

        return new Mat(original, r);
    }

    private Mat resize(Mat original, double ratio) {
        Mat resized = new Mat();
        Imgproc.resize(original, resized, new Size(), ratio, ratio, Imgproc.INTER_LINEAR);

        return resized;
    }

    private Mat toGreyScale(Mat original) {
        Mat greyscale = new Mat();
        Imgproc.cvtColor(original, greyscale, Imgproc.COLOR_RGB2GRAY); // <1>
        return greyscale;
    }

    protected void saveImage(Mat mat, Path path) {
        Imgcodecs.imwrite(path.toAbsolutePath().toString(), mat);
    }

    protected Mat loadImage(Path image) {
        return Imgcodecs.imread(image.toAbsolutePath().toString());
    }

    private final Lock reentrantLock = new ReentrantLock();

    protected String scan(Path imagePath) {
        TessBaseAPI api = new TessBaseAPI();
        api.Init("src/main/resources", "eng"); // executes only once as takes a lot of time

        String content = "";
        PIX image = null;
        BytePointer outText = null;
        try {
            reentrantLock.lock();
            image = pixRead(imagePath.toFile().getAbsolutePath());

            api.SetImage(image);
            outText = api.GetUTF8Text();

            content = outText.getString();



        } finally {

            if (outText != null) {
                outText.deallocate();
            }
            if (image != null) {
                pixDestroy(image);
            }

            reentrantLock.unlock();
        }

        api.End(); // Free resources

        return content;
    }
}
