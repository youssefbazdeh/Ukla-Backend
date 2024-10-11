package spark.ukla.services.interfaces;

import spark.ukla.entities.User;
import spark.ukla.entities.bodyinfos.FemaleBodyInfo;
import spark.ukla.entities.bodyinfos.MaleBodyInfo;

public interface INutritionService {

    int calculateMaleBodyNeeds(MaleBodyInfo maleBodyInfos) ;
    int calculateFemaleBodyNeeds(FemaleBodyInfo femaleBodyInfos) ;
    float bodyMassIndicator (float weight, float height ) ;
    float idealWeightMale(float weight, float height) ;
    float idealWeightFemale(float weight, float height) ;
    float basicMetabolismMale(float weight, float height, int age ) ;
    float basicMetabolismFemale(float weight, float height, int age ) ;
    MaleBodyInfo saveBodyInfosMale(MaleBodyInfo maleBodyInfos,String username) ;
    FemaleBodyInfo saveBodyInfosFemale(FemaleBodyInfo femaleBodyInfos,String username) ;
    float calculatePhysicalActivityMale(float A, float B, float C, float D, float E, float F) ;
    float calculatePhysicalActivityFemale(float A, float B, float C, float D, float E) ;
    MaleBodyInfo getMaleBodyInfo(String username);
     FemaleBodyInfo getFemaleBodyInfo(String username) ;
}
