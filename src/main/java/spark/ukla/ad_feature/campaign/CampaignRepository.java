package spark.ukla.ad_feature.campaign;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign,Long> {
    Page<Campaign> findAll(Pageable pageable);
    long count();

    List<Campaign> findAllByClientId(Long id);

    @Modifying
    @Query(value = "DELETE cba FROM campaign_banner_ads cba WHERE cba.banner_ads_id = :banner_ads_id", nativeQuery = true)
    void removecampaign_banner_adsAssociation(@Param("banner_ads_id") Long banner_ads_id);

    @Modifying
    @Query(value = "DELETE cia FROM campaign_ingredient_ads cia WHERE cia.ingredient_ads_id = :ingredient_ads_id", nativeQuery = true)
    void removecampaign_ingredient_adsAssociation(@Param("ingredient_ads_id") Long ingredient_ads_id);
}
