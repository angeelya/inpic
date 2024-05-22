package angeelya.inPic.image.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImageGetServiceTest {
    @Mock
    ImageRepository imageRepository;
    @InjectMocks
    ImageGetService imageGetService;
    @SneakyThrows
    @Test
    void testGetImage() {
        Long image_id=1L;
        Image image = Image.builder().id(image_id).build();
        Mockito.when(imageRepository.findById(image_id)).thenReturn(Optional.ofNullable(image));
        Image actualImage= imageGetService.getImage(image_id);
        assertEquals(actualImage,image);
        Mockito.when(imageRepository.findById(image_id)).thenReturn(Optional.empty());
        assertThrows(NotFoundDatabaseException.class,() ->imageGetService.getImage(image_id));
    }
}