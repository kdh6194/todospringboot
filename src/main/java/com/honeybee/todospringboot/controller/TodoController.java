package com.honeybee.todospringboot.controller;

import com.honeybee.todospringboot.dto.ResponseDTO;
import com.honeybee.todospringboot.dto.TodoDTO;
import com.honeybee.todospringboot.entity.TodoEntity;
import com.honeybee.todospringboot.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired
//    private tdsv tdsv; 분명히 service어노테이션에 네이밍을 했는데 왜 에러가 발생했지?
    // 그냥 value를 지정해서 사용하지 말자 이점이 하나도 없다.
    private TodoService todoService;

    @GetMapping("/testbody")
    public ResponseDTO<String> testController() {
        List<String> list = new ArrayList<>();
        list.add("Hello World!! Test ResponseDTO");
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return response;
    }

    @GetMapping("/testentity")
    public ResponseEntity<?> testControllerEntity() {
        List<String> list = new ArrayList<>();
        list.add("Hello World!! Test ResponseEntity");
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        // http status를 400으로 설정
        return ResponseEntity.badRequest().body(response);
//        return ResponseEntity.ok().body(response); 정상적으로 응답을 반환한다면 ok메서드를 사용
    }

    @GetMapping("/test")
    public ResponseEntity<?> testTodo() {
        String str = todoService.testService();
        List<String> list = new ArrayList<>();
        list.add(str);
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId,
                                        @RequestBody TodoDTO dto) {
        try {
            // (1) TodoEntity로 변환한다.
            TodoEntity entity = TodoDTO.toEntity(dto);

            // (2) id를 null로 초기화 한다. 생성 당시에는 id가 없어야 하기 때문이다.
            entity.setId(null);

            // (3) Authentication Bearer Token을 통해 받은 userId를 넘긴다.
            entity.setUserId(userId);

            // (4) 서비스를 이용해 Todo엔티티를 생성한다.
            List<TodoEntity> entities = todoService.create(entity);

            // (5) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO리스트로 변환한다.

            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            // (6) 변환된 TodoDTO리스트를 이용해ResponseDTO를 초기화한다.
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            // (7) ResponseDTO를 리턴한다.
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // (8) 혹시 예외가 나는 경우 dto대신 error에 메시지를 넣어 리턴한다.

            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId) {
        // (1) 서비스 메서드의 retrieve메서드를 사용해 Todo리스트를 가져온다
        List<TodoEntity> entities = todoService.retrieve(userId);

        // (2) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO리스트로 변환한다.
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        // (6) 변환된 TodoDTO리스트를 이용해ResponseDTO를 초기화한다.
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        // (7) ResponseDTO를 리턴한다.
        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId,
                                        @RequestBody TodoDTO dto) {

        // (1) dto를 entity로 변환한다.
        TodoEntity entity = TodoDTO.toEntity(dto);

        // (2) Authentication Bearer Token을 통해 받은 userId를 넘긴다.
        entity.setUserId(userId);

        // (3) 서비스를 이용해 entity를 업데이트 한다.
        List<TodoEntity> entities = todoService.update(entity);

        // (4) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO리스트로 변환한다.
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        // (5) 변환된 TodoDTO리스트를 이용해ResponseDTO를 초기화한다.
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        // (6) ResponseDTO를 리턴한다.
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId,
                                        @RequestBody TodoDTO dto) {
        try {

            // (1) TodoEntity로 변환한다.
            TodoEntity entity = TodoDTO.toEntity(dto);

            // (2) Authentication Bearer Token을 통해 받은 userId를 넘긴다.
            entity.setUserId(userId);

            // (3) 서비스를 이용해 entity를 삭제 한다.
            List<TodoEntity> entities = todoService.delete(entity);

            // (4) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO리스트로 변환한다.
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            // (5) 변환된 TodoDTO리스트를 이용해ResponseDTO를 초기화한다.
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            // (6) ResponseDTO를 리턴한다.
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // (8) 혹시 예외가 나는 경우 dto대신 error에 메시지를 넣어 리턴한다.
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

}
