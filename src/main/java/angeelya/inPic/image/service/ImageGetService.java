package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageGetService {
    private final ImageRepository imageRepository;
    private static final String MS_NOT_FOUND_IMAGE="Image data not found";
    public Image getImage(Long id) throws NotFoundDatabaseException {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND_IMAGE);
        return image.get();
    }
}
