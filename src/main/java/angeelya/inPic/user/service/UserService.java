package angeelya.inPic.user.service;

import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.UserRepository;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    public User getUser(Long user_id) throws NotFoundDatabaseException {
        Optional<User> user = userRepository.findByIdAndUserImageNotNullOrUserImageNull(user_id);
        if (user.isEmpty()) throw new NotFoundDatabaseException("User not found");
        return user.get();
    }

}
