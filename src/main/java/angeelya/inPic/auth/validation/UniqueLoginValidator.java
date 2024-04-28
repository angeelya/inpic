package angeelya.inPic.auth.validation;

import angeelya.inPic.auth.repository.UserRepository;
import angeelya.inPic.model.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin,String> {
    @Autowired
    private UserRepository userRepository;
    @Override
    public void initialize(UniqueLogin constraintAnnotation) {
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        Optional<User> user = userRepository.findByLogin(login);
        return user.isEmpty();
    }
}
