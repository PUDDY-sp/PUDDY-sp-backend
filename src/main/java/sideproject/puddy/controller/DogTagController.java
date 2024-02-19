package sideproject.puddy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sideproject.puddy.dto.tag.TagListDto;
import sideproject.puddy.service.DogTagService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DogTagController {
    private final DogTagService dogTagService;
    @GetMapping("/dogtag")
    
    public TagListDto getTagList(){
        return dogTagService.findAll();
    }
}
