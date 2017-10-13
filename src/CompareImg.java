import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.arraycopy;
import static java.lang.System.setOut;

public class CompareImg {


    BufferedImage img1 = null;
    BufferedImage img2 = null;
    BufferedImage img3 = null;
    ArrayList<Integer[]> masPoint = compare();
    Integer[][] lengths = new Integer[masPoint.size()][masPoint.size()];
    Integer rs;
    Integer rmin;
    Integer k = 2;
    Integer rg = null;
    ArrayList<ArrayList<Integer[]>> grupp = new ArrayList<>();

    ArrayList<Integer[]> compare() {
        ArrayList<Integer[]> masPoin = new ArrayList<>();
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
                if ((rgb1 - rgb2) < 0) {
                    if (((rgb1 - rgb2) * (-1)) / 255 > 0.1) {
                        masPoin.add(new Integer[]{x, y});
                    }
                } else if ((rgb1 - rgb2) / 255 > 0.1) {
                    masPoin.add(new Integer[]{x, y});
                }
            }
        }

        for (int i = 0; i < masPoin.size() - 1; i++) {
            Integer[] p = new Integer[2];
            arraycopy(masPoin.get(i), 0, p, 0, 2);
           // img3.setRGB(p[0], p[1], 255);
        }
        return masPoin;
    }

    void countRminRs() {
        Integer[] p1 = new Integer[2];
        Integer[] p2 = new Integer[2];
        Integer[][] length = new Integer[masPoint.size()][masPoint.size()];
        Integer[] minLengths = new Integer[length.length];
        Integer sum = 0;
        for (int i = 0; i < masPoint.size(); i++) {
            arraycopy(masPoint.get(i), 0, p1, 0, 2);
            for (int j = 0; j < masPoint.size(); j++) {
                arraycopy(masPoint.get(j), 0, p2, 0, 2);
                length[i][j] = (int) Math.pow((p1[0] - p2[0]), 2) + (int) Math.pow((p1[1] - p2[1]), 2);
                lengths[i][j] = (int) Math.pow((p1[0] - p2[0]), 2) + (int) Math.pow((p1[1] - p2[1]), 2);
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
        rs = (int) Math.ceil(sum / length.length);
        for (int i = length.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (length[j][1] > length[j + 1][1]) {
                    int t = length[j][1];
                    length[j][1] = length[j + 1][1];
                    length[j + 1][1] = t;
                }
            }
        }
        rmin = length[0][1];
    }

    private void formGrup(int idPoint) {
        ArrayList<Integer[]> m = new ArrayList<>();
        rg = rs + (rs - rmin) * k;
        for (int k = 0; k < lengths.length - 1; k++) {
            if (lengths[idPoint][k] < rg && masPoint.get(k).equals(new Integer[]{0,0})) {
                m.add(masPoint.get(k));
                masPoint.set(k, new Integer[]{0, 0});
            }
        }
        grupp.add(m);
    }

    void formAllGrup(){
        for (int k =0; k<masPoint.size(); k++){
            formGrup(k);
        }
    }

    void selectAnArea(){
        int top = img3.getHeight() + 1;
        int right = -1;
        int bottom = -1;
        int left = img3.getWidth() + 1;
        Integer[] point = new Integer[2];
        for(int k = 0; k < grupp.size();k++){
            for (int i = 0; i < grupp.get(k).size();i++){//TUT NE RABOTAET
                System.out.println(i);// u meny ne vivodit na ekran a doljen
               point = grupp.get(k).get(i);
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
                System.out.println(i +"__"+top);
                img3.setRGB(i, top, 255);
            }
            for (int i = left; i <= right; i++) {
                System.out.println(i +"__"+top);
                img3.setRGB(i, bottom, 255);
            }
            for (int i = top; i <= bottom; i++) {
                System.out.println(i +"__"+top);
                img3.setRGB(left, i, 255);
            }
            for (int i = top; i <= bottom; i++) {
                System.out.println(i +"__"+top);
                img3.setRGB(right, i, 255);
            }

        }
        try {
            File outputfile = new File("saved.png");
            ImageIO.write(img3, "png", outputfile);
        } catch (IOException e) {
        }
    }
}
