package sideproject.puddy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideproject.puddy.dto.type.TypeListDto;
import sideproject.puddy.service.DogTypeService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DogTypeController {
    private final DogTypeService dogTypeService;
    @GetMapping("/dogtype")
    
    public TypeListDto getTagList(){
        return dogTypeService.findAll();
    }
}
