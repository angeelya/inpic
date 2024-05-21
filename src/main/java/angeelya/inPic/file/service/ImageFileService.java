package angeelya.inPic.file.service;

import angeelya.inPic.exception_handling.exception.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class ImageFileService {
    private final Path path = Paths.get("files");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String MS_FAILED_SAVE = "Failed to save image file", MS_FAILED_GET = "Failed to get image file",
            MS_FAILED_DELETE="Failed to delete image file", MS_CREATE="Created ";
    ImageFileService() {
        File directory = new File(path.toString());
        if (!directory.exists()) {
            directory.mkdir();
            logger.info(MS_CREATE + directory);
        }
    }

    public void saveImage(MultipartFile file) throws FileException {
        try {
            Files.write(Path.of(path + "/" + file.getOriginalFilename()), file.getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new FileException(MS_FAILED_SAVE);
        }
    }

    public String getImage(String imgName) throws FileException {
        try {
            byte[] imageByte = Files.readAllBytes(new File(path+"/"+imgName).toPath());
                return Base64.getEncoder().encodeToString(imageByte);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new FileException(MS_FAILED_GET);
        }
    }

    public void deleteImage(String imgName) throws FileException {
        try {
            Files.delete(Path.of(path + "/" + imgName));
        } catch (IOException e)
        {
            logger.error(e.getMessage());
            throw new FileException(MS_FAILED_DELETE);
        }
    }

    public String getDirectoryPath() {
        return path.toString();
    }
}
