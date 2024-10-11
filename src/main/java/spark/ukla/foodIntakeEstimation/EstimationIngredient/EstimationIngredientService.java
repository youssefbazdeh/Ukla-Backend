package spark.ukla.foodIntakeEstimation.EstimationIngredient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spark.ukla.entities.Image;
import spark.ukla.entities.enums.Unit;
import spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity.EstimationIngredientQuantityRepository;

import java.util.List;

@Slf4j
@Service
public class EstimationIngredientService implements IEstimationIngredientService {
    private final EstimationIngredientRepository estimationIngredientRepository;
    private final EstimationIngredientQuantityRepository estimationIngredientQuantityRepository;


    public EstimationIngredientService(EstimationIngredientRepository estimationIngredientRepository,
                                       EstimationIngredientQuantityRepository estimationIngredientQuantityRepository) {
        this.estimationIngredientRepository = estimationIngredientRepository;
        this.estimationIngredientQuantityRepository = estimationIngredientQuantityRepository;
    }

    @Override
    public List<EstimationIngredient> SearchByName(String name) {

        return estimationIngredientRepository.searchEstimationIngredientByName(name);
    }

    @Override
    public List<EstimationIngredient> getAll() {
    return (List<EstimationIngredient>) estimationIngredientRepository.findAll();
            }

    @Override
    public Boolean add(EstimationIngredient estimationIngredient, Image image) {
        estimationIngredient.setImage(image);
        estimationIngredientRepository.save(estimationIngredient);
        return true;
    }

    @Override
    public boolean delete(Long id) {
        estimationIngredientRepository.deleteById(id);
        return true;
    }

    @Override
    public Boolean update(EstimationIngredient estimationIngredient) {
        boolean status = false;
        Long id = estimationIngredient.getId();
        if (estimationIngredientRepository.findById(id).isPresent()) {
            String name = estimationIngredient.getName();
            String unit = String.valueOf(estimationIngredient.getUnit());
            estimationIngredientRepository.update(id, name, Unit.valueOf(unit));
            status = true;
        }
        return status;
    }
}

