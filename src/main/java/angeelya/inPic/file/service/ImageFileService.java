package angeelya.inPic.file.service;

import angeelya.inPic.database.model.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageFileService {
    private final Path path = Paths.get("image");
    private File directory;
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    ImageFileService() {
        directory = new File(path.toString());
        if (!directory.exists()) {
            directory.mkdir();
            logger.info("Created" + directory);
        }
    }
    public byte[] getImage(String path,String imgName) throws IOException {
        return Files.readAllBytes(new File(path+imgName).toPath());
    }
    public void saveImage(byte[] image,String name) throws IOException {
       Files.write(Path.of(path + name),image);
    }
    public String getDirectoryPath() {
        return path.toString();
    }
}
