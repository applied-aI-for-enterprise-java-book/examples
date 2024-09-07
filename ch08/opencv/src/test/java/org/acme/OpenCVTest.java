package org.acme;

import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

class OpenCVTest {


    final OpenCVMain main;
    final Mat hand;
    final Mat card;
    final Mat barcode;
    final Mat qrcode;
    final Path ballVideo;

    public OpenCVTest() {
        this.main = new OpenCVMain();
        this.hand = main.loadImage(Paths.get("src/main/resources/hands.jpg"));
        this.card = main.loadImage(Paths.get("src/main/resources/card.jpg"));
        this.barcode = main.loadImage(Paths.get("src/main/resources/barcode.jpg"));
        this.qrcode = main.loadImage(Paths.get("src/main/resources/qrcode.png"));
        this.ballVideo = Paths.get("src/main/resources/ball.mp4");
    }

    @Test
    void testPerspetiveCorrection() {

        main.perspectiveCorrection(hand);
        //main.saveImage(automaticPerspective, "target/a.jpg");
    }

    @Test
    void testCroppedImages() {
        Mat croppedHand = main.centerCrop(hand, 400);
        main.saveImage(croppedHand, Paths.get("target/handsCropped.jpg"));
    }

    @Test
    void testDrawRectangle() {

        Rect rect = new Rect(167, 135, 288, 262);

        Mat handRectangle = main.drawRectangleWithText(this.hand, rect, OpenCVMain.BLACK, "Hand");
        this.main.saveImage(handRectangle, Paths.get("target/handRectangle.jpg"));

        Mat handFillRectangle = main.fillRectangle(this.hand, rect, OpenCVMain.GREEN, 0.4);
        this.main.saveImage(handFillRectangle,Paths.get("target/handFillRectangle.jpg"));
    }

    @Test
    void testBinarization() {
        Mat binaryH = this.main.binaryBinarization(this.hand);
        Mat otsuH = this.main.otsuBinarization(this.hand);

        this.main.saveImage(binaryH, Paths.get("target/binaryH.jpg"));
        this.main.saveImage(otsuH, Paths.get("target/otsuH.jpg"));

    }

    @Test
    void testEdgeDetection() {
        Mat edges = this.main.edgeProcessing(this.card);
        this.main.saveImage(edges, Paths.get("target/edgesCard.jpg"));

        List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(edges, allContours,
            new org.opencv.core.Mat(),
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_NONE);


        final List<MatOfPoint> matOfPoints = allContours.stream()
            .sorted((o1, o2) -> (int) (Imgproc.contourArea(o2, false) - Imgproc.contourArea(o1, false)))
            .map(cnt -> {
                MatOfPoint2f points2d = new MatOfPoint2f(cnt.toArray());
                final double peri = Imgproc.arcLength(points2d, true);
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(points2d, approx, 0.02 * peri, true);
                return approx;
            })
            .filter(approx -> approx.total() >= 4)
            .map(mt2f -> {
                MatOfPoint approxf1 = new MatOfPoint();
                mt2f.convertTo(approxf1, CvType.CV_32S);
                return approxf1;
            })
            .limit(1)
            .toList();

        Mat contours = this.card.clone();

        Imgproc.drawContours(contours, matOfPoints, -1, OpenCVMain.GREEN, 5);

        this.main.saveImage(contours, Paths.get("target/edgesCardContour.jpg"));
    }

    @Test
    public void correctPerspective() {
        Mat dst = this.main.correctingPerspective(this.card);

        this.main.saveImage(dst, Paths.get("target/persCard.jpg"));
    }

    @Test
    public void testBarcode() {
        String readBarcode = this.main.readBarcode(this.barcode);
        System.out.println(readBarcode);
    }

    @Test
    public void testQrCode() {

        OpenCVMain.QrCode qr = this.main.readQR(this.qrcode);
        System.out.println("+" + qr.content());

        Mat copyQr = new Mat();
        this.qrcode.copyTo(copyQr);

        final Mat pointsMat = qr.points();

        List<Point> points = toPoints(pointsMat);
        points.forEach(p -> Imgproc.drawMarker(copyQr, p, OpenCVMain.GREEN, Imgproc.MARKER_CROSS, 5, 10));
        this.main.saveImage(copyQr,Paths.get("target/qrEdges.jpg"));

    }

    private List<Point> toPoints(Mat pointsMat) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i< pointsMat.cols(); i++) {
            Point p = new Point(pointsMat.get(0, i));
            points.add(p);
        }

        return points;

    }

    @Test
    public void processVideo() {
        this.main.processVideo(this.ballVideo, Paths.get("target/ballbin.mp4"));
    }

    @Test
    public void testWebcam() {
        Mat image = this.main.takeSnapshot();

        this.main.saveImage(image, Paths.get("target/cam.jpg"));
    }

    @Test
    public void djlImage() throws IOException {
        Path pic = Paths.get("src/main/resources/hands.jpg");
        Image img = BufferedImageFactory.getInstance().fromFile(pic);
        int width = img.getWidth();
        int height = img.getHeight();
        Image leftHalfImg = img.getSubImage(0, 0, width / 2, height);

        leftHalfImg.save(new FileOutputStream("target/lresizedHands.jpg"), "jpg");
    }

}