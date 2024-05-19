package angeelya.inPic.database.repository;

import angeelya.inPic.database.model.Friend;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends CrudRepository<Friend,Long> {
    Integer countByUser_Id(Long user_id);
    Integer countBySubFriend_Id(Long user_id);
    List<Friend> findByUser_Id(Long user_id);
    List<Friend> findBySubFriend_Id(Long user_id);
    Optional<Friend> findBySubFriend_IdAndUser_Id(Long subscription_id, Long user_id );
    Optional<Friend> findByUser_IdAndSubFriend_Id(Long subscriber,Long user_id);
    void deleteBySubFriend_IdAndUser_Id(Long subscription_id, Long user_id);
    void deleteByUser_IdAndSubFriend_Id(Long subscriber,Long user_id);

}
