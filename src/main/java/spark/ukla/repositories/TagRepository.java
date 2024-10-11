package spark.ukla.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Tag;

import java.util.Optional;
import java.util.Set;



@Repository
public interface TagRepository extends CrudRepository<Tag,Long> {
    Set<Tag> findByIdIn(Set<Long> tagId);
}
