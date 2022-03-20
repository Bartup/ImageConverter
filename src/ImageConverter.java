import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.util.Iterator;

public class ImageConverter {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        String srcPath;
        String dstPath;
        String suffixSrc;
        String suffixDst;
        int height;
        int width;
        String type;

        System.out.println("Enter the source folder path: ");
        srcPath = scanner.nextLine();
        Path source = Paths.get(srcPath);
        suffixSrc = srcPath.replaceAll("(?<=^)(.*)(?=\\\\)" , "");
        suffixSrc = suffixSrc.replace("\\","");

        System.out.println("Enter the destination folder path: ");
        dstPath = scanner.nextLine();
        suffixDst = dstPath.replaceAll("(?<=^)(.*)(?=\\\\)" , "");
        suffixDst = suffixDst.replace("\\","");



        System.out.println("Enter wanted height: ");
        height = scanner.nextInt();
        System.out.println("Enter wanted width: ");
        width = scanner.nextInt();
        System.out.println("Enter wanted type: ");
        type = scanner.next();

        copyDirectory(srcPath,dstPath);

        try (Stream<Path> stream = Files.walk(source, Integer.MAX_VALUE)) {
            List<String> collect = stream
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());

            for(int i = 0; i < collect.size();i++){
                if(!collect.get(i).endsWith("jpg")){
                    collect.remove(collect.get(i));
                }
            }
            for(int i = 0; i < collect.size();i++){
                if(!collect.get(i).endsWith("jpg")){
                    collect.remove(collect.get(i));
                }
            }

            for (String pathname : collect){
                File fileToConvert = new File(pathname);
                File fileConverted = new File(pathname.replace(suffixSrc,suffixDst));
                resizeImage(fileToConvert,fileConverted,height,width,type);
                compression(pathname.replace(suffixSrc,suffixDst), type);
            }
        }

    }

    private static void resizeImage(
            File originalImage, File resizedImage, int width, int height, String format)
            throws IOException{
        BufferedImage original = ImageIO.read(originalImage);
        BufferedImage resized = new BufferedImage(width,height,original.getType());
        Graphics2D graphics2D = resized.createGraphics();
        graphics2D.drawImage(original,0,0,width,height,null);
        graphics2D.dispose();
        ImageIO.write(resized,format,resizedImage);
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void compression(String directory, String type) throws IOException {
        File input = new File(directory);
        BufferedImage image = ImageIO.read(input);

        File compressedImageFile = new File(directory);
        OutputStream outputStream = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(type);
        ImageWriter writer = (ImageWriter) writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.5f);  // Change the quality value you prefer
        writer.write(null, new IIOImage(image, null, null), param);

        outputStream.close();
        ios.close();
        writer.dispose();
    }
}
