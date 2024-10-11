package spark.ukla.ad_feature.bannerAd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.ad_feature.campaign.Campaign;
import spark.ukla.ad_feature.campaign.CampaignRepository;
import spark.ukla.entities.Image;
import spark.ukla.entities.Video;
import spark.ukla.repositories.ImageDbRepository;
import spark.ukla.repositories.VideoRepository;
import spark.ukla.services.implementations.FileLocationService;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Slf4j
@Service
@Transactional
public class BannerAdService {
    private final BannerAdRepository bannerAdRepository;
    private final CampaignRepository campaignRepository;
    private final FileLocationService fileLocationService;
    private final ImageDbRepository imageDbRepository;
    private final VideoRepository videoRepository;

    public BannerAdService(BannerAdRepository bannerAdRepository,CampaignRepository campaignRepository,FileLocationService fileLocationService,ImageDbRepository imageDbRepository,VideoRepository videoRepository) {
        this.fileLocationService = fileLocationService;
        this.imageDbRepository = imageDbRepository;
        this.videoRepository = videoRepository;
        this.bannerAdRepository = bannerAdRepository;
        this.campaignRepository = campaignRepository;
    }

    public BannerAd add(BannerAd bannerAd, Video video, Image image,Long campaignId) {
        if (video!=null){
            bannerAd.setVideo(video);
        }


        bannerAd.setImage(image);
        bannerAdRepository.save(bannerAd);
        Campaign campaign=campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("campaign not found ! "));
        List<BannerAd> bannerAdList=campaign.getBannerAds();
        bannerAdList.add(bannerAd);
        campaign.setBannerAds(bannerAdList);
        return bannerAd;
    }

    public void deleteById(Long idBannerAd) {
        BannerAd bannerAd=bannerAdRepository.findById(idBannerAd)
                .orElseThrow(() -> new EntityNotFoundException("bannerAd not found ! "));
        campaignRepository.removecampaign_banner_adsAssociation(idBannerAd);
        fileLocationService.deleteImage(bannerAd.getImage().getLocation());
        fileLocationService.deleteVideo(bannerAd.getVideo().getId());
        imageDbRepository.delete(bannerAd.getImage());
        videoRepository.delete(bannerAd.getVideo());
        bannerAdRepository.deleteById(idBannerAd);
    }


    public List<BannerAd> getBannerAdsWithPagination(int page, int size,Long id){
        Pageable pageable;
        Campaign campaign=campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("campaign not found ! "));

        if (page == 0 && size == 0) {
            return bannerAdRepository.findAll();

        }

        pageable = PageRequest.of(page - 1, size);

        List<Page<BannerAd>> bannerAdList=new ArrayList<>();
        for (BannerAd i:campaign.getBannerAds()
        ) {

            Page<BannerAd> bannerAdPage = bannerAdRepository.findAllById(pageable,i.getId());
            bannerAdList.add(bannerAdPage);
        }

        List<BannerAd> combinedBannerAds = new ArrayList<>();
        for (Page<BannerAd> b : bannerAdList) {
            combinedBannerAds.addAll(b.getContent());
        }
        Page<BannerAd> p =new PageImpl<>(combinedBannerAds, pageable, combinedBannerAds.size());
        return p.getContent();
    }
    public long getBannerAdCount() {
        return bannerAdRepository.count();
    }

    public BannerAd retrieveById(Long id) {
        Optional<BannerAd> bannerAd = bannerAdRepository.findById(id);

        return bannerAd.orElse(null);
    }

    public  boolean activate(Long bannerAdID){
        Optional<BannerAd> optionalBannerAd = bannerAdRepository.findById(bannerAdID);

        BannerAd bannerAd = optionalBannerAd.orElse(null);

        if (bannerAd!=null){
            bannerAd.setActive(true);
            return true;
        }else return false;
    }

    public boolean disable(Long bannerAdID){
        Optional<BannerAd> optionalBannerAd = bannerAdRepository.findById(bannerAdID);

        BannerAd bannerAd = optionalBannerAd.orElse(null);
        if (bannerAd!=null){
            if (!bannerAd.isActive()){
                return false;
            }
            bannerAd.setActive(true);
            return true;
        }else return false;

    }
    public String updateImage(Image image, Long id) {
        BannerAd bannerAd = bannerAdRepository.findById(id).orElse(null);
        if(bannerAd == null)
            return "banner not found";
        fileLocationService.deleteImage(bannerAd.getImage().getLocation());
        imageDbRepository.delete(bannerAd.getImage());
        bannerAd.setImage(image);
        bannerAdRepository.save(bannerAd);
        return "image updated";
    }
    public boolean updateAd(BannerAd bannerAd, long id) {
        BannerAd existingAd = bannerAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BannerAd not found ! "));



        existingAd.setBannerName(bannerAd.getBannerName());
        existingAd.setActive(bannerAd.isActive());
        existingAd.setRedirectionLink(bannerAd.getRedirectionLink());
        bannerAdRepository.save(existingAd);
        return true;
    }
    public String updateVideo(Video video, Long id) {
        BannerAd bannerAd = bannerAdRepository.findById(id).orElse(null);
        if(bannerAd == null)
            return  "banner not found";
        fileLocationService.deleteVideo(bannerAd.getVideo().getId());
        videoRepository.delete(bannerAd.getVideo());
        bannerAd.setVideo(video);
        bannerAdRepository.save(bannerAd);
        return null;
    }
    public boolean setBannerVideoToNull(Long idBanner){
        BannerAd bannerAd = bannerAdRepository.findById(idBanner).orElse(null);
        if(bannerAd == null)
            return  false;
        bannerAd.setVideo(null);
        bannerAdRepository.save(bannerAd);
        return true;
    }

    public List<Long> findAllByCountryCodeAndActive(CountryCode countryCode){
        List<Long> activeBannerAdsByCountryCode  = bannerAdRepository.findAllIdsByActiveAndCountryCode(true, countryCode);
        if(activeBannerAdsByCountryCode.isEmpty()){
            return Collections.emptyList();
        }
        return activeBannerAdsByCountryCode;
    }
}
