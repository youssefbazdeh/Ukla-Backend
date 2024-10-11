package spark.ukla.ad_feature.bannerAd;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.ad_feature.CountryCode;

import java.util.List;

@Repository
public interface BannerAdRepository extends JpaRepository<BannerAd,Long> {
    long count();
    Page<BannerAd> findAllById(Pageable pageable, Long id);
    @Query("SELECT b.id FROM BannerAd b WHERE b.active = :active AND b.countryCode = :countryCode")
    List<Long> findAllIdsByActiveAndCountryCode(@Param("active") boolean active, @Param("countryCode") CountryCode countryCode);

}
