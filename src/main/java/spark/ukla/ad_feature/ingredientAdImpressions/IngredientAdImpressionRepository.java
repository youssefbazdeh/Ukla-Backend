package spark.ukla.ad_feature.ingredientAdImpressions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;

import java.util.List;

@Repository
public interface IngredientAdImpressionRepository extends JpaRepository<IngredientAdImpression, Long> {
    @Transactional
    @Modifying
    @Query("update IngredientAdImpression ing set ing.views = ing.views + 1 where ing.id in :ids")
    void incrementViews(@Param("ids") List<Long> IngredientAdImpressionIds);

    @Transactional
    @Modifying
    @Query("update IngredientAdImpression ing set ing.impressions = ing.impressions + 1 where ing.id in :ids")
    void incrementImpressions(@Param("ids") List<Long> IngredientAdImpressionIds);

    List<IngredientAdImpression> findByIngredientAd(IngredientAd ingredientAd);
    boolean existsByUserIdAndIngredientAd(long userID,IngredientAd ingredientAd);
}
