package angeelya.inPic.user.service;

import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.UserRepository;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserGetService {
    private final UserRepository userRepository;
    private static final String MS_NOT_FOUND="User not found";

    public User getUser(Long user_id) throws NotFoundDatabaseException {
        Optional<User> user = userRepository.findById(user_id);
        if (user.isEmpty()) throw new NotFoundDatabaseException(MS_NOT_FOUND);
        return user.get();
    }

}
