package angeelya.inPic.file.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.exception_handling.exception.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageFileService {
    private final Path path = Paths.get("image");
    private File directory;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String MS_FAILED_SAVE = "Failed to save images", MS_FAILED_GET = "Failed to get image";

    ImageFileService() {
        directory = new File(path.toString());
        if (!directory.exists()) {
            directory.mkdir();
            logger.info("Created" + directory);
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

    public Resource getImage(String imgName) throws FileException {
        try {
            Resource resource = new UrlResource(path.resolve(imgName).toUri());
            if (resource.exists() || resource.isFile() || resource.isReadable())
                return resource;
            else throw new FileException("Couldn't load the file.");
        } catch (MalformedURLException | FileException e) {
            logger.error(e.getMessage());
            throw new FileException(MS_FAILED_GET);
        }
    }

    public String getDirectoryPath() {
        return path.toString();
    }
}
