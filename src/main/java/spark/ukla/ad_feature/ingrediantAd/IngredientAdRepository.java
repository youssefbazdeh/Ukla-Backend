package spark.ukla.ad_feature.ingrediantAd;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spark.ukla.ad_feature.CountryCode;

import java.util.List;


@Repository
public interface IngredientAdRepository extends JpaRepository<IngredientAd, Long> {

    IngredientAd findByIngredientIdAndCountryCode(long id,CountryCode countryCode);
    long count();
    Page<IngredientAd> findAllById(Pageable pageable,Long id);

    @Query("SELECT ing from IngredientAd ing WHERE ing.ingredientId IN :idsList AND ing.active = :active AND ing.countryCode = :countryCode")
    List<IngredientAd> getAllByIngredientIdAndActiveAndCountryCode(List<Long> idsList, boolean active, CountryCode countryCode);
}
