package angeelya.inPic.recommedation.service;

import angeelya.inPic.database.model.Action;
import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.ActionRepository;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.image.service.ImageGetService;
import angeelya.inPic.user.service.UserGetService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActionServiceTest {
    @Mock
    ActionRepository actionRepository;
    @Mock
    ImageGetService imageGetService;
    @Mock
    UserGetService userGetService;
    @Mock
    CategoryRecommendationService categoryRecommendationService;
    @Mock
    ImageRecommendationService imageRecommendationService;
    @InjectMocks
    ActionService actionService;

    @SneakyThrows
    @Test
    void testSetGrade() {
        Long user_id = 1L, image_id = 1L;
        Image image = Image.builder().id(image_id).user(User.builder().id(2L).build()).build();
        User user = User.builder().id(user_id).build();
        Mockito.when(imageGetService.getImage(image_id)).thenReturn(image);
        Action action = Action.builder().image(image).user(user).grade(0.25).build();
        Mockito.when(actionRepository.findAll()).thenReturn(List.of(action));
        Mockito.when(actionRepository.findByUser_IdAndImage_Id(user_id, image_id)).thenReturn(action);
        Mockito.when(actionRepository.save(action)).thenReturn(action);
        actionService.setGrade(user_id,image_id,true);
        assertEquals(0.5,action.getGrade());
        actionService.setGrade(user_id,image_id,false);
        assertEquals(0.25,action.getGrade());
        Mockito.when(actionRepository.findByUser_IdAndImage_Id(user_id, image_id)).thenReturn(null);
        Mockito.when(userGetService.getUser(user_id)).thenReturn(user);
        actionService.setGrade(user_id,image_id,true);
        assertEquals(0.25,action.getGrade());
        Mockito.when(actionRepository.findAll()).thenReturn(List.of());
        assertThrows(NotFoundDatabaseException.class,()->actionService.setGrade(user_id,image_id,true));
    }
}