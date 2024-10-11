package spark.ukla.creator_recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CreatorRecipeRepository extends JpaRepository<CreatorRecipe,Long> {
    boolean existsByTitle(String title);
    List<CreatorRecipe> findAllByCreator(String username);
    int deleteCreatorRecipeById(Long id);
}
