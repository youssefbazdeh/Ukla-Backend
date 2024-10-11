package spark.ukla.services.generic;



import java.util.List;

public interface ServiceGeneric<T> {
    List<T> findAll() throws Exception;
    T save(T entity) throws Exception;
    void delete(Long id) throws Exception;
    T findById(Long id) throws Exception;
}
