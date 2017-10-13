import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static java.lang.System.arraycopy;

public class ImageCompareProcessor {


    BufferedImage img1 = null;
    BufferedImage img2 = null;
    BufferedImage img3 = null;
    ArrayList<Integer[]> massPointsDifference = compare();
    Integer[][] distanceBetweenPoints = new Integer[massPointsDifference.size()][massPointsDifference.size()];
    Integer averageMinimumDistance;
    Integer minimumDistance;
    Integer coefficienProximityPointsInCloud = 1;
    Integer rangeCloud = null;
    ArrayList<ArrayList<Integer[]>> group = new ArrayList<>();

    void loadImg (){
        try {
            img1 = ImageIO.read(new File("1.png"));
            img2 = ImageIO.read(new File("2.png"));

        } catch (IOException e) {
            System.out.println("Error reading file!");
        }
        img3 = img1;
    }
    void compareSize(){

    }
    ArrayList<Integer[]> compare() {
        ArrayList<Integer[]> masPoin = new ArrayList<>();
        loadImg();
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
                if ((rgb1 - rgb2) < 0) {
                    if (((rgb1 - rgb2) * (-1)) / 255 > 0.1) {
                        masPoin.add(new Integer[]{x, y});
                    }
                } else if ((rgb1 - rgb2) / 255 > 0.1) {
                    masPoin.add(new Integer[]{x, y});
                }
            }
        }
        return masPoin;
    }

    void countRminRs() {
        Integer[] p1 = new Integer[2];
        Integer[] p2 = new Integer[2];
        Integer[][] length = new Integer[massPointsDifference.size()][massPointsDifference.size()];
        Integer[] minLengths = new Integer[length.length];
        Integer sum = 0;
        for (int i = 0; i < massPointsDifference.size(); i++) {
            arraycopy(massPointsDifference.get(i), 0, p1, 0, 2);
            for (int j = 0; j < massPointsDifference.size(); j++) {
                arraycopy(massPointsDifference.get(j), 0, p2, 0, 2);
                length[i][j] = (int) Math.pow((p1[0] - p2[0]), 2) + (int) Math.pow((p1[1] - p2[1]), 2);
                distanceBetweenPoints[i][j] = (int) Math.pow((p1[0] - p2[0]), 2) + (int) Math.pow((p1[1] - p2[1]), 2);
            }
        }

        for (int k = 0; k < (int) Math.ceil((length.length - 1) / 2); k++) {
            for (int i = length[k].length - 1; i > 0; i--) {
                for (int j = 0; j < i; j++) {
                    if (length[k][j] > length[k][j + 1]) {
                        int t = length[k][j];
                        length[k][j] = length[k][j + 1];
                        length[k][j + 1] = t;
                    }
                }
            }

        }

        for (int k = 0; k < length.length - 1; k++) {
            sum = sum + length[k][1];
        }
        averageMinimumDistance = (int) Math.ceil(sum / length.length);
        for (int i = length.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (length[j][1] > length[j + 1][1]) {
                    int t = length[j][1];
                    length[j][1] = length[j + 1][1];
                    length[j + 1][1] = t;
                }
            }
        }
        minimumDistance = length[0][1];
    }

    private void formGrup(int idPoint) {
        ArrayList<Integer[]> m = new ArrayList<>();
        rangeCloud = averageMinimumDistance + (averageMinimumDistance - minimumDistance) * coefficienProximityPointsInCloud;
        for (int k = 0; k < distanceBetweenPoints.length - 1; k++) {
            if (distanceBetweenPoints[idPoint][k] < rangeCloud && !massPointsDifference.get(k).equals(new Integer[]{0,0})) {
                m.add(massPointsDifference.get(k));
                massPointsDifference.set(k, new Integer[]{0, 0});
            }
        }
        group.add(m);
    }

    void formAllGrup(){
        for (int k =0; k<massPointsDifference.size(); k++){
            formGrup(k);
        }
    }

    void selectArea(){
        Integer[] point = new Integer[2];
        for(int k = 0; k < group.size();k++){
            int top = img3.getHeight() + 1;
            int right = 0;
            int bottom = 0;
            int left = img3.getWidth() + 1;
            for (int i = 0; i < group.get(k).size();i++){
               point = group.get(k).get(i);
               if(point[1] <= top){
                   top = point[1];
               }
               if(point[1] >= bottom){
                   bottom = point[1];
               }
               if(point[0] <= left ){
                   left = point[0];
               }
               if(point[0] >= right){
                   right = point[0];
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


        }
        try {
            File outputfile = new File("saved.png");
            ImageIO.write(img3, "png", outputfile);
        } catch (IOException e) {
            System.out.println("Error saving file");
        }

    }
}
