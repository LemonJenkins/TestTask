import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.imageio.ImageIO;


import static java.lang.System.arraycopy;
import static java.lang.System.setOut;

public class CompareImages {

    BufferedImage img1 = null;
    BufferedImage img2 = null;
    BufferedImage img3 = null;
    ArrayList<Integer[]> masPoint = new ArrayList<>();
    ArrayList<ArrayList<Integer>> masMinLengthToPoint = new ArrayList<>();
    int minLength;
    int average;
    int[][][] areaaPoints;
    int coefficient = 1;

    void compare() {

        try {
            img1 = ImageIO.read(new File("1.png"));
            img2 = ImageIO.read(new File("2.png"));
        } catch (IOException e) {
        }
        img3 = img1;

        int width1 = img1.getWidth();
        int width2 = img2.getWidth();
        int height1 = img1.getHeight();
        int height2 = img2.getHeight();
        if ((width1 != width2) || (height1 != height2)) {
            System.err.println("Error: Images dimensions mismatch");
            System.exit(1);
        }

        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);
                if (rgb1 != rgb2) {
                    masPoint.add(new Integer[]{x, y});
                }
            }
        }

        for (int i = 0; i < masPoint.size() - 1; i++) {
            Integer[] p = new Integer[2];
            arraycopy(masPoint.get(i), 0, p, 0, 2);
            // img3.setRGB(p[0], p[1], 255);
        }


    }

    void countLenght() {

        for (int i = 0; i < masPoint.size() - 1; i++) {
            Integer[] p1 = new Integer[2];
            Integer[] p2 = new Integer[2];
            int minLeng[][] = new int[masPoint.size()][3];
            minLeng[0][2] = img1.getWidth() + 1;
            arraycopy(masPoint.get(i), 0, p1, 0, 2);

            for (int j = 0; j < masPoint.size() - 1; j++) {
                arraycopy(masPoint.get(j), 0, p2, 0, 2);
                int leng = (int) Math.pow((p1[0] - p2[0]), 2) + (int) Math.pow((p1[1] - p2[1]), 2);
                if (minLeng[i][2] > leng) {
                    ArrayList<Integer> m = new ArrayList<>();
                    m.add(p2[0]);
                    m.add(p2[1]);
                    m.add(leng);
                    masMinLengthToPoint.add(m);
                    minLeng[i][2] = leng;
                }
            }
        }
    }

    void minLeng() {
        int min = (img1.getWidth()) ^ 2;
        for (int i = 0; i < masMinLengthToPoint.size() - 1; i++) {
            if (min > masMinLengthToPoint.get(i).get(2)) {
                min = masMinLengthToPoint.get(i).get(2);
            }
        }
        minLength = min;
    }

    void averageDistance() {
        int sum = 0;
        for (int i = 0; i < masMinLengthToPoint.size() - 1; i++) {
            sum = sum + masMinLengthToPoint.get(i).get(2);
        }
        average = (int) Math.ceil(sum / masMinLengthToPoint.size());
    }

    private void groupFormation(int numberAria) {
        for (int i = 0; i < masPoint.size() - 1; i++) {
           // System.out.println(i);
            Integer[] p1 = new Integer[2];
            Integer[] p2 = new Integer[2];
            arraycopy(masPoint.get(i), 0, p1, 0, 2);

            for (int j = 0; j < masPoint.size() - 1; j++) {
                arraycopy(masPoint.get(j), 0, p2, 0, 2);
                if (p2[0] != img1.getWidth() + 1 && p2[1] != img1.getHeight() + 1) {
                    if ((int) Math.pow((p1[0] - p2[0]), 2) + (int) Math.pow((p1[1] - p2[1]), 2) < (average + (average - minLength) * coefficient)) {
                        areaaPoints[numberAria][areaaPoints[numberAria].length][0] = p2[0];
                        areaaPoints[numberAria][areaaPoints[numberAria].length][1] = p2[1];
                        p2[0] = img1.getHeight() + 1;
                        p2[1] = img1.getWidth() + 1;
                        masPoint.set(j,p2);

                    }
                }
            }
        }
    }

    void areasFormation() {
        for (int i =0; i < masPoint.size()-1; i++){
            groupFormation(i);
        }
    }

    void areaSelection() {
        int top = img3.getHeight() + 1;
        int right = -1;
        int bottom = -1;
        int left = img3.getWidth() + 1;

        for (int i = 0; i < areaaPoints.length - 1; i++) {


            for (int j = 0; j < areaaPoints[i].length - 1; j++) {
                if (areaaPoints[i][j][0] <= left) {
                    top = areaaPoints[i][j][0];
                }
                if (areaaPoints[i][j][0] >= right) {
                    right = areaaPoints[i][j][0];
                }
                if (areaaPoints[i][j][1] <= top) {
                    top = areaaPoints[i][j][1];
                }
                if (areaaPoints[i][j][1] >= bottom) {
                    top = areaaPoints[i][j][1];
                }
            }
        }
        for (int i = left; i <= right; i++) {
            img3.setRGB(i, top, 255);
        }
        for (int i = left; i <= right; i++) {
            img3.setRGB(i, bottom, 255);
        }
        for (int i = top; i <= bottom; i++) {
            img3.setRGB(left, i, 255);
        }
        for (int i = top; i <= bottom; i++) {
            img3.setRGB(right, i, 255);
        }

        try {
            File outputfile = new File("saved.png");
            ImageIO.write(img3, "png", outputfile);
        } catch (IOException e) {
        }

    }
}
