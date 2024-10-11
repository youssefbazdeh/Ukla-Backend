package spark.ukla.ad_feature.bannerAdImpressions;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.ad_feature.bannerAd.BannerAd;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerAdImpression implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    long userId;
    int clicks=0;
    int views=0;
    int clicksOnSkip=0;

    //Plays till 25, 50, 75, 100
    int pt25=0;
    int pt50=0;
    int pt75=0;
    int pt100=0;

    @ManyToOne(cascade = CascadeType.ALL)
    BannerAd bannerAd ;

    public void incrementViews() {
        this.views++;
    }
    public void incrementClicks() {
        this.clicks++;
    }
    public void incrementclicksOnSkip() {
        this.clicksOnSkip++;
    }
    public void incrementPt25() {
        this.pt25++;
    }
    public void incrementPt50() {
        this.pt50++;
    }
    public void incrementPt75() {
        this.pt75++;
    }
    public void incrementPt100() {
        this.pt100++;
    }
}
