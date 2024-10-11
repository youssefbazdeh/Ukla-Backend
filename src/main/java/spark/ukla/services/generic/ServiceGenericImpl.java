package spark.ukla.services.generic;


import lombok.extern.slf4j.Slf4j;
import spark.ukla.repositories.generic.GenericRepository;

import javax.annotation.Resource;
import java.util.List;
@Slf4j
public class ServiceGenericImpl<T> implements ServiceGeneric<T> {


    @Resource
    protected GenericRepository<T> genericRepository;

    @Override
    public List<T> findAll() throws Exception {
        return genericRepository.findAll();
    }

    @Override
    public T save(T entity) throws Exception {
        return genericRepository.save(entity);
    }

    @Override
    public void delete(Long id) throws Exception {

        genericRepository.deleteById(id);
    }
    @Override
    public T findById(Long id) throws Exception {

        return this.genericRepository.findById(id).orElse(null);
    }
}
