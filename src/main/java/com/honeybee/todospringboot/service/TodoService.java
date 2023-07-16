package com.honeybee.todospringboot.service;

import com.honeybee.todospringboot.entity.TodoEntity;
import com.honeybee.todospringboot.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

    public String testService(){
        TodoEntity entity = TodoEntity.builder().title("My first todo item").done(true).build();
        repository.save(entity);
        TodoEntity saveEntity = repository.findById(entity.getId()).get();
        return saveEntity.getTitle();
    }

    public List<TodoEntity> create(final TodoEntity entity){
        validate(entity);

        repository.save(entity);

        log.info("Entity ID : {} is saved",entity.getId());

        return repository.findByUserId(entity.getUserId());
    }

    public List<TodoEntity> retrieve(final String userId){
        return repository.findByUserId(userId);
    }

    // 람다식과 Optional을 사용한 코드
    public List<TodoEntity> update(final TodoEntity entity){
        validate(entity);

        final Optional<TodoEntity> original = repository.findById(entity.getId());

        original.ifPresent(todo-> {
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
        repository.save(todo);
        });

        return retrieve(entity.getUserId());
    }

    //Optional과 if문을 사용한 코드
    public List<TodoEntity> updateVer2(final TodoEntity entity){
        validate(entity);

        final Optional<TodoEntity> original = repository.findById(entity.getId());

       if(original.isPresent()){
           final TodoEntity todo = original.get();
           todo.setTitle(entity.getTitle());
           todo.setDone(entity.isDone());

           repository.save(todo);
       }

        return retrieve(entity.getUserId());
    }
    public List<TodoEntity> delete(TodoEntity entity) {
        try {
            repository.delete(entity);
        } catch (Exception e){
            log.error("error deleting entity",entity.getId(),e);
            throw new RuntimeException("error deleting entity" + entity.getId());
        }
        return retrieve(entity.getUserId());
    }

    private void validate(final TodoEntity entity){
        if(entity == null){
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null.");
        }
        if(entity.getUserId() == null){
            log.warn("Unknown user");
            throw new RuntimeException("Unknown user");
        }
    }

}
