package sideproject.puddy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sideproject.puddy.dto.dog.request.PostDogRequest;
import sideproject.puddy.dto.dog.request.UpdateDogRequest;
import sideproject.puddy.dto.dog.response.DogDetailResponse;
import sideproject.puddy.service.DogService;
import sideproject.puddy.service.RegisterNumberService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DogController {
    private final DogService dogService;
    private final RegisterNumberService registerNumberService;
    
    @PostMapping("/dog")
    public ResponseEntity<String> postDog(@RequestBody PostDogRequest postDogRequest){
        return dogService.saveDog(postDogRequest);
    }
    @PostMapping("/{personId}/dog")
    public ResponseEntity<String> postDogBySignUp(@PathVariable Long personId, @RequestBody PostDogRequest request){
        return dogService.saveDogBySignUp(personId, request);
    }
    @PatchMapping("/dog/{dogId}")
    public ResponseEntity<String> updateDog(@PathVariable Long dogId, @RequestBody UpdateDogRequest request){
        return dogService.updateDog(dogId, request);
    }
    @GetMapping("/dog/{dogId}")
    public DogDetailResponse getDogDetail(@PathVariable Long dogId){
        return dogService.findDog(dogId);
    }
    @DeleteMapping("/dog/{dogId}")
    public ResponseEntity<String> deleteDog(@PathVariable Long dogId){
        return dogService.deleteDog(dogId);
    }
    @GetMapping("/dog/check")
    public boolean checkRegisterNum(@RequestParam Long registerNum){
        return registerNumberService.existRegisterNum(registerNum);
    }
    @PatchMapping("/dog/{dogId}/change-main")
    public ResponseEntity<String> updateMainDog(@PathVariable Long dogId){
        return dogService.updateMainDog(dogId);
    }
}
