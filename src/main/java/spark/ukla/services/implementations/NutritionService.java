package spark.ukla.services.implementations;

import org.springframework.stereotype.Service;
import spark.ukla.entities.User;
import spark.ukla.entities.bodyinfos.FemaleBodyInfo;
import spark.ukla.entities.bodyinfos.MaleBodyInfo;
import spark.ukla.repositories.FemaleBodyInfosRepository;
import spark.ukla.repositories.MaleBodyInfosRepository;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.interfaces.INutritionService;

import static java.lang.Math.round;

@Service
public class NutritionService implements INutritionService {
    private final MaleBodyInfosRepository maleBodyInfosRepository ;
    private final FemaleBodyInfosRepository femaleBodyInfosRepository;
    private final UserRepository userRepository;

    public NutritionService(MaleBodyInfosRepository maleBodyInfosRepository, FemaleBodyInfosRepository femaleBodyInfosRepository, UserRepository userRepository) {
        this.maleBodyInfosRepository = maleBodyInfosRepository;
        this.femaleBodyInfosRepository = femaleBodyInfosRepository;
        this.userRepository = userRepository;
    }

    @Override
    public int calculateMaleBodyNeeds(MaleBodyInfo maleBodyInfos) {
     float bodyMassIndicator = bodyMassIndicator(maleBodyInfos.getWeight(),maleBodyInfos.getHeight()) ;

     float calculationsWeight ;
     if(bodyMassIndicator >= 25 ){
         calculationsWeight= idealWeightMale(maleBodyInfos.getWeight(),maleBodyInfos.getHeight()) ;
     }
     else{
         calculationsWeight =maleBodyInfos.getWeight() ;
     }

     float basicMetabolism = basicMetabolismMale(calculationsWeight,maleBodyInfos.getHeight(),maleBodyInfos.getAge());

        float physicalActivityLevel = calculatePhysicalActivityMale(maleBodyInfos.getPhysicalActivityLevelA(),
                maleBodyInfos.getPhysicalActivityLevelB(),maleBodyInfos.getPhysicalActivityLevelC(),
                maleBodyInfos.getPhysicalActivityLevelD(),maleBodyInfos.getPhysicalActivityLevelE(),
                maleBodyInfos.getPhysicalActivityLevelF()
                ) ;



    return  round(basicMetabolism * physicalActivityLevel)   ;



    }

    @Override
    public int calculateFemaleBodyNeeds(FemaleBodyInfo femaleBodyInfos) {
        float bodyMassIndicator = bodyMassIndicator(femaleBodyInfos.getWeight(),femaleBodyInfos.getHeight()) ;

        float calculationsWeight ;
        if(bodyMassIndicator >= 25 ){
            calculationsWeight= idealWeightFemale(femaleBodyInfos.getWeight(),femaleBodyInfos.getHeight()) ;
        }
        else{
            calculationsWeight =femaleBodyInfos.getWeight() ;
        }

        float basicMetabolism = basicMetabolismFemale(calculationsWeight,femaleBodyInfos.getHeight(),femaleBodyInfos.getAge());

        float physicalActivityLevel = calculatePhysicalActivityFemale(femaleBodyInfos.getPhysicalActivityLevelA(),
                femaleBodyInfos.getPhysicalActivityLevelB(),femaleBodyInfos.getPhysicalActivityLevelC(),
                femaleBodyInfos.getPhysicalActivityLevelD(),femaleBodyInfos.getPhysicalActivityLevelE()) ;

        return  round(basicMetabolism * physicalActivityLevel)   ;

    }

    @Override
    public float bodyMassIndicator(float weight, float height) {
        //height in m
        return   weight / (height/100 * height/100) ;
    }

    @Override
    public float idealWeightMale(float weight, float height) {
        //height in cm
        return ((height)- 100) - (((height)-150)/4 ) ;
    }

    @Override
    public float idealWeightFemale(float weight, float height) {

        return  ((height)- 100) - (((height)-150)/2.5f ) ;
    }

    @Override
    public float basicMetabolismMale(float weight, float height, int age) {
        return (float) ( 259 * Math.pow( weight,0.48)  * Math.pow((height/100),0.5) * Math.pow(age,-0.13));

    }

    @Override
    public float basicMetabolismFemale(float weight, float height, int age) {
        return (float) (230* Math.pow( weight,0.48)  * Math.pow((height/100),0.5) * Math.pow(age,-0.13));
    }

    @Override
    public MaleBodyInfo saveBodyInfosMale(MaleBodyInfo maleBodyInfos,String username) {
        int calorieNeed = calculateMaleBodyNeeds(maleBodyInfos) ;
        maleBodyInfos.setCalorieNeed(calorieNeed);
        User user = userRepository.findByUsername(username) ;
        maleBodyInfos.setUser(user) ;
        if(maleBodyInfosRepository.existsByUser(user)){
            maleBodyInfos.setId(maleBodyInfosRepository.findByUser(user).getId());
        }
    return maleBodyInfosRepository.save(maleBodyInfos) ;
    }

    @Override
    public FemaleBodyInfo saveBodyInfosFemale(FemaleBodyInfo femaleBodyInfos,String username) {

        int calorieNeed = calculateFemaleBodyNeeds(femaleBodyInfos) ;
        femaleBodyInfos.setCalorieNeed(calorieNeed);
        User user = userRepository.findByUsername(username) ;
        femaleBodyInfos.setUser(user) ;
        if(femaleBodyInfosRepository.existsByUser(user)){
            femaleBodyInfos.setId(femaleBodyInfosRepository.findByUser(user).getId());
        }

    return femaleBodyInfosRepository.save(femaleBodyInfos) ;

    }

    @Override
    public float calculatePhysicalActivityMale(float A, float B, float C, float D, float E, float F) {

        return ( (A + B*1.5F + C*2.2F + D*3F + E*3.5F + F*5F ) /24) ;

    }

    @Override
    public float calculatePhysicalActivityFemale(float A, float B, float C, float D, float E) {
        return ((A + B*1.5F + C*2.2F + D*3F + E*3.5F)/24)  ;
    }


    @Override
    public MaleBodyInfo getMaleBodyInfo(String username) {
        User user = userRepository.findByUsername(username);
        return maleBodyInfosRepository.findByUser(user);
    }
    @Override
    public FemaleBodyInfo getFemaleBodyInfo(String username) {
        User user = userRepository.findByUsername(username);
        return femaleBodyInfosRepository.findByUser(user);
    }


    public FemaleBodyInfo getFemaleBodyInfoByUser(User user) {
        return femaleBodyInfosRepository.findByUser(user);
    }

    public MaleBodyInfo getMaleBodyInfoByUser(User user) {
        return maleBodyInfosRepository.findByUser(user);
    }







}
