package angeelya.inPic.user.service;

import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.UserRepository;
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
class UserGetServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserGetService userGetService;

    @SneakyThrows
    @Test
    void testGetUser() {
        Long user_id=1L;
        User user = User.builder().id(user_id).build();
        Mockito.when(userRepository.findById(user_id)).thenReturn(Optional.of(user));
        assertEquals(user,userGetService.getUser(user_id));
        Mockito.when(userRepository.findById(user_id)).thenReturn(Optional.empty());
        assertThrows(NotFoundDatabaseException.class,()->userGetService.getUser(user_id));
    }
}