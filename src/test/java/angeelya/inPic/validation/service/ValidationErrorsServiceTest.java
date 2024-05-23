package angeelya.inPic.validation.service;

import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ValidationErrorsServiceTest {
    @InjectMocks
    ValidationErrorsService validation;
    @SneakyThrows
    @Test
    void testValidation() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        assertThrows(ValidationErrorsException.class, () -> validation.validation(bindingResult));
        BindingResult bindingResultTrue = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        validation.validation(bindingResultTrue);

    }
}