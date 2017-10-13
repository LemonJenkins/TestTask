public class Main {

    public static void main(String[] args) {
        ImageCompareProcessor imageProcessor = new ImageCompareProcessor();
        imageProcessor.compare();
        imageProcessor.countCoefficient();
        imageProcessor.formAllGroup();
        imageProcessor.selectArea();

    }
}
