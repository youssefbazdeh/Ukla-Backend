package spark.ukla.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Review;
import spark.ukla.entities.enums.Status;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r JOIN r.recipe rec WHERE rec.status = :status")
    List<Review> findReviewsByRecipeStatus(@Param("status") Status status);
    Review findReviewsByRecipe_Id(Long recipeID);
    Page<Review> findReviewsByRecipeStatus(Pageable pageable,Status status);
    long countReviewByRecipeStatus(Status status);

}
