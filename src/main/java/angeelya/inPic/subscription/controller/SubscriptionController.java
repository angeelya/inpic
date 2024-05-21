package angeelya.inPic.subscription.controller;

import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.CountSubscriptionResponse;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.dto.response.UserSubscriberResponse;
import angeelya.inPic.dto.response.UserSubscriptionResponse;
import angeelya.inPic.exception_handling.exception.*;
import angeelya.inPic.subscription.service.SubscriptionService;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class SubscriptionController {
    private final ValidationErrorsService validationErrorsService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/add/friend")
    public ResponseEntity<MessageResponse> addFriend(@RequestBody @Valid FriendAddRequest friendAddRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException, ForbiddenRequestException, ExistException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(subscriptionService.addFriend(friendAddRequest)));
    }
    @PostMapping("/counts")
    public ResponseEntity<CountSubscriptionResponse> getCounts(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(subscriptionService.getCounts(userInformationRequest));
    }
    @PostMapping("/all/by/user/subscription")
    public ResponseEntity<List<UserSubscriptionResponse>> getSubscriptions(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userInformationRequest));
    }
    @PostMapping("/all/by/user/subscriber")
    public ResponseEntity<List<UserSubscriberResponse>> getSubscribers(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(subscriptionService.getUserSubscribers(userInformationRequest));
    }
    @PostMapping("/delete/subscription")
    public ResponseEntity<MessageResponse> deleteSubscription(@RequestBody @Valid SubscriptionRequest subscriptionRequest, BindingResult bindingResult) throws ValidationErrorsException, DeleteDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(subscriptionService.deleteSubscription(subscriptionRequest)));
    }
    @PostMapping("/delete/subscriber")
    public ResponseEntity<MessageResponse> deleteSubscriber(@RequestBody @Valid SubscriberRequest subscriberRequest, BindingResult bindingResult) throws ValidationErrorsException, DeleteDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(subscriptionService.deleteSubscriber(subscriberRequest)));
    }
    @PostMapping("/check/subscription")
    public ResponseEntity<CheckFriendResponse> checkSubscription(@RequestBody @Valid SubscriptionRequest subscriptionRequest, BindingResult bindingResult) throws ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(subscriptionService.checkSubscription(subscriptionRequest));
    }
    @PostMapping("/check/subscriber")
    public ResponseEntity<CheckFriendResponse> checkSubscriber(@RequestBody @Valid SubscriberRequest subscriberRequest, BindingResult bindingResult) throws ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(subscriptionService.checkSubscriber(subscriberRequest));
    }
}
