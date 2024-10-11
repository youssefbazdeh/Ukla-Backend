package spark.ukla.creator_feature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.User;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    boolean existsByIdAndFollowersContains(Long creatorId, User user);

    Creator findByUsername(String username);

    @Modifying
    @Query(value ="INSERT INTO creator_followers (creator_id, followers_id) VALUES (:creatorId, :followers_id)", nativeQuery = true)
    void addFollower(@Param("creatorId") Long creatorId, @Param("followers_id") Long followers_id);
    @Modifying
    @Query(value = "DELETE cf FROM creator_followers cf WHERE cf.followers_id = :followers_id", nativeQuery = true)
    void removeCreator_followersAssociation(@Param("followers_id") Long followers_id);
}
