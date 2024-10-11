package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Step;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {
    Step findByInstruction(String instruction);
}