package spark.ukla.foodIntakeEstimation.EstimationMeal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spark.ukla.entities.User;
import spark.ukla.foodIntakeEstimation.EstimationRecipe.EstimationRecipe;
import spark.ukla.foodIntakeEstimation.EstimationRecipe.EstimationRecipeRepository;
import spark.ukla.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class EstimationMealTest {
    @Mock
    private EstimationMealRepository estimationMealRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EstimationRecipeRepository estimationRecipeRepository;

    private EstimationMealService estimationMealService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        estimationMealService = new EstimationMealService(estimationMealRepository, userRepository, estimationRecipeRepository);
    }

    @DisplayName("Getting all the estimationMeals method")
    @Test
    void givenEstimationMealsList_whenGetAll_thenReturnEstimationMealsList() {
        // Arrange
        List<EstimationMeal> expectedMeals = new ArrayList<>();
        when(estimationMealRepository.findAll()).thenReturn(expectedMeals);

        // Act
        List<EstimationMeal> result = estimationMealService.getAll();

        // Assert
        Assertions.assertEquals(expectedMeals, result);
        verify(estimationMealRepository).findAll();
    }

    @DisplayName("Getting all the estimationMeals by user method")
    @Test
    void givenEstimationMealsListAndUserId_whenFindAllByUserId_thenReturnEstimationMealsListByUser() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername(username)).thenReturn(user);

        List<EstimationMeal> expectedMeals = new ArrayList<>();
        when(estimationMealRepository.findAllByUserId(user.getId())).thenReturn(expectedMeals);

        // Act
        List<EstimationMeal> result = estimationMealService.getAllByUserId(username);

        // Assert
        Assertions.assertEquals(expectedMeals, result);
        verify(userRepository).findByUsername(username);

        verify(estimationMealRepository, times(1)).findAllByUserId(user.getId());
    }
    @DisplayName("Adding an estimationMeal")
    @Test
    void givenEstimationMealObject_whenAdd_thenReturnTrue() {
        // Arrange
        EstimationMeal estimationMeal = new EstimationMeal();
        String username = "testuser";
        User user = new User();
        // Act
        Boolean result = estimationMealService.add(estimationMeal, username);

        // Assert
        Assertions.assertTrue(result);
        verify(estimationMealRepository).save(estimationMeal);
    }

    @DisplayName("Deleting an estimationMeal")
    @Test
    void givenEstimationMealsId_whenDelete_thenReturnTrue() {
        // Arrange
        Long id = 1L;

        // Act
        boolean result = estimationMealService.delete(id);

        // Assert
        Assertions.assertTrue(result);
        verify(estimationMealRepository).deleteById(id);
    }

    @DisplayName("Updating an estimationMeal name")
    @Test
    void givenEstimationMealsIdandName_whenUpdateName_thenReturnTrue() {
        // Arrange
        Long id = 1L;
        String estimationMealName = "Updated Meal";

        Optional<EstimationMeal> estimationMealOptional = Optional.of(new EstimationMeal());
        when(estimationMealRepository.findById(id)).thenReturn(estimationMealOptional);

        // Act
        Boolean result = estimationMealService.updatename(id, estimationMealName);

        // Assert
        Assertions.assertTrue(result);
        verify(estimationMealRepository).updateMealname(id, estimationMealName);
    }

    @DisplayName("Getting one estimationMeal")
    @Test
    void givenEstimationMealsId_whenGetOne_thenReturnEstimationMeal() {
        // Arrange
        Long id = 1L;
        EstimationMeal expectedMeal = new EstimationMeal();
        Optional<EstimationMeal> estimationMealOptional = Optional.of(expectedMeal);
        when(estimationMealRepository.findById(id)).thenReturn(estimationMealOptional);

        // Act
        Optional<EstimationMeal> result = estimationMealService.getOne(id);

        // Assert
        Assertions.assertEquals(estimationMealOptional, result);
        verify(estimationMealRepository).findById(id);
    }

    @DisplayName("Removing an estimationRecipe from an estimationMeal")
    @Test
    void givenEstimationMealsIdandEstimationRecipeId_whenRemoveEstimationRecipe_thenReturnTrue() {
        // Arrange
        Long idMeal = 1L;
        Long idEstimationRecipe = 2L;

        EstimationMeal estimationMeal = new EstimationMeal();
        estimationMeal.setId(idMeal);

        EstimationRecipe estimationRecipe = new EstimationRecipe();
        estimationRecipe.setId(idEstimationRecipe);

        when(estimationMealRepository.findById(idMeal)).thenReturn(Optional.of(estimationMeal));
        when(estimationRecipeRepository.findById(idEstimationRecipe)).thenReturn(Optional.of(estimationRecipe));

        // Act
        boolean result = estimationMealService.removeEstimationRecipe(idMeal, idEstimationRecipe);

        // Assert
        Assertions.assertTrue(result);
        Assertions.assertTrue(estimationMeal.getEstimationRecipes().isEmpty());
        verify(estimationMealRepository).findById(idMeal);
        verify(estimationRecipeRepository).findById(idEstimationRecipe);
        verify(estimationRecipeRepository).save(estimationRecipe);
        verify(estimationMealRepository).save(estimationMeal);
    }
}
