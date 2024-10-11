package spark.ukla.ad_feature.client;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.ad_feature.campaign.Campaign;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Client implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String companyName;
    String phoneNumber;
    String email;
    String taxRegistrationNumber;
    String address;

    @OneToMany(cascade = CascadeType.ALL)
    List<Campaign> campaignList;
}
