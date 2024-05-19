package angeelya.inPic.search.service;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.database.model.Search;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.ImageRepository;
import angeelya.inPic.database.repository.SearchRepository;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.file.service.ImageFileService;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.request.SearchImageRequest;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ImageRepository imageRepository;
    private final ImageFileService imageFileService;
    private final SearchRepository searchRepository;
    private final UserService userService;

    public List<ImageResponse> searchImage(SearchImageRequest searchImageRequest) throws FileException, NotFoundDatabaseException, NoAddDatabaseException {
        String key = searchImageRequest.getKey();
        saveRequestHistory(key, searchImageRequest.getUser_id());
        List<Image> images = imageRepository.findByNameIsLikeIgnoreCaseOrCategory_CategoryIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(key, key, key);
        if (images.isEmpty()) throw new NotFoundDatabaseException("No images found");
        List<ImageResponse> imageResponses = images.stream().map(image -> {
            try {
                return new ImageResponse(image.getId(), imageFileService.getImage(image.getImgName()), image.getName());
            } catch (FileException e) {
                logger.error(e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (imageResponses.isEmpty()) throw new FileException("Such image does not exist");
        return imageResponses;
    }

    private void saveRequestHistory(String text, Long user_id) throws NotFoundDatabaseException, NoAddDatabaseException {
        User user = userService.getUser(user_id);
        saveSearch(Search.builder().text(text).user(user).build());
    }

    public List<Search> getSearchesHistory(UserInformationRequest userInformationRequest) throws NotFoundDatabaseException {
        List<Search> searches = searchRepository.findByUser_Id(userInformationRequest.getUser_id());
        if (searches.isEmpty()) throw new NotFoundDatabaseException("Such search history does not exist");
        return searches;
    }

    private void saveSearch(Search search) throws NoAddDatabaseException {
        try {
            searchRepository.save(search);
        } catch (DataAccessException e) {
            throw new NoAddDatabaseException("Failed to save search");
        }
    }
}
