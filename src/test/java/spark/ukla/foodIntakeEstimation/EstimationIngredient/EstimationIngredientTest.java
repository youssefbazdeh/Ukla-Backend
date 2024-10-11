package spark.ukla.foodIntakeEstimation.EstimationIngredient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spark.ukla.entities.Image;
import spark.ukla.entities.enums.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EstimationIngredientTest {
    @Mock
    private EstimationIngredientRepository estimationIngredientRepository;


    @InjectMocks
    private EstimationIngredientService estimationIngredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenEstimationIngredientName_whenSearchByName_thenReturnEstimationIngredientList() {
        String name = "Ingredient";
        List<EstimationIngredient> expectedIngredients = new ArrayList<>();
        // Add expected ingredients to the list

        when(estimationIngredientRepository.searchEstimationIngredientByName(name))
                .thenReturn(expectedIngredients);

        List<EstimationIngredient> actualIngredients = estimationIngredientService.SearchByName(name);

        assertEquals(expectedIngredients, actualIngredients);
        verify(estimationIngredientRepository, times(1))
                .searchEstimationIngredientByName(name);
    }

    @Test
    void givenEstimationIngredientList_whenGetAll_thenReturnEstimationIngredientList() {
        List<EstimationIngredient> expectedIngredients = new ArrayList<>();
        // Add expected ingredients to the list

        when(estimationIngredientRepository.findAll())
                .thenReturn(expectedIngredients);
        System.out.println(expectedIngredients+"expectedIngredients");
        List<EstimationIngredient> actualIngredients = estimationIngredientService.getAll();
        System.out.println(actualIngredients+"actualIngredients");
        assertEquals(expectedIngredients, actualIngredients);
        verify(estimationIngredientRepository, times(1)).findAll();
    }

    @Test
    void givenEstimationIngredientObject_whenAdd_thenReturnTrue() {
        EstimationIngredient estimationIngredient = new EstimationIngredient();
        Image image = new Image();

        boolean result = estimationIngredientService.add(estimationIngredient, image);

        assertTrue(result);
        verify(estimationIngredientRepository, times(1)).save(estimationIngredient);
    }

    @Test
    void givenEstimationIngredientId_whenDelete_thenReturnTrue() {
        Long id = 1L;

        boolean result = estimationIngredientService.delete(id);

        assertTrue(result);
        verify(estimationIngredientRepository, times(1)).deleteById(id);
    }

    @Test
    void givenEstimationIngredient_whenUpdate_thenReturnTrue() {
        Long id = 1L;
        EstimationIngredient estimationIngredient = new EstimationIngredient();
        estimationIngredient.setId(id);
        estimationIngredient.setName("Updated Ingredient");
        estimationIngredient.setUnit(Unit.g);

        when(estimationIngredientRepository.findById(id))
                .thenReturn(Optional.of(estimationIngredient));
        when(estimationIngredientRepository.update(id, estimationIngredient.getName(), estimationIngredient.getUnit()))
                .thenReturn(1);

        boolean result = estimationIngredientService.update(estimationIngredient);

        assertTrue(result);
        verify(estimationIngredientRepository, times(1)).findById(id);
        verify(estimationIngredientRepository, times(1))
                .update(id, estimationIngredient.getName(), estimationIngredient.getUnit());
    }

}