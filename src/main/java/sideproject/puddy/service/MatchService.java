package sideproject.puddy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideproject.puddy.dto.dog.response.DogProfileDto;
import sideproject.puddy.dto.match.*;
import sideproject.puddy.dto.tag.TagDto;
import sideproject.puddy.dto.tag.TagListDto;
import sideproject.puddy.exception.CustomException;
import sideproject.puddy.exception.ErrorCode;
import sideproject.puddy.model.*;
import sideproject.puddy.repository.MatchRepository;
import sideproject.puddy.repository.PersonRepository;
import sideproject.puddy.security.util.SecurityUtil;
import sideproject.puddy.repository.ChatRepository;


import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final PersonRepository personRepository;
    private final ChatService chatService;
    private final AuthService authService;
    private final DogService dogService;
    private final ChatRepository chatRepository;

    // 위치, 매칭 여부 -> (성별, 나이, 반려견 정보)
    public RandomDogDetailListResponse getMatchingByDog(String type, Boolean neuter, List<TagDto> tag){
        // 현재 사용자 찾기
        Person currentUser = authService.findById(SecurityUtil.getCurrentUserId());

        // 현재 사용자와 매칭되지 않은, 근처의 사용자 찾기
        List<RandomDogDetailResponse> dogs = matchRepository.findNearPersonNotMatched(
                        SecurityUtil.getCurrentUserId(),
                        !currentUser.isGender(),
                        currentUser.getLongitude(),
                        currentUser.getLatitude()
                ).stream()
                .filter(person -> {
                    Dog mainDog = dogService.findByPersonAndMain(person);
                    return type == null || mainDog.getDogType().getContent().equals(type);
                })
                .filter(person -> {
                    Dog mainDog = dogService.findByPersonAndMain(person);
                    return neuter == null || mainDog.isNeuter() == neuter;
                })
                .filter(person -> {
                    Dog mainDog = dogService.findByPersonAndMain(person);
                    return tag == null || tag.isEmpty() || !Collections.disjoint(tag, mapTagsToDto(mainDog.getDogTagMaps()));
                })
                .map(person -> {
                    Dog mainDog = dogService.findByPersonAndMain(person);
                    // 채팅이나 매치가 이루어지지 않은 사용자만 선택
                    if((!chatRepository.existsByFirstPersonAndSecondPerson(person, currentUser) && !chatRepository.existsByFirstPersonAndSecondPerson(currentUser, person)) && !matchRepository.existsBySenderAndReceiver(currentUser, person)){
                        RandomDogProfileDto randomDogProfileDto = new RandomDogProfileDto(
                                mainDog.getName(),
                                mainDog.isGender(),
                                mainDog.getImage(),
                                mainDog.getDogType().getContent(),
                                calculateAge(mainDog.getBirth()),
                                mapTagsToDto(mainDog.getDogTagMaps())
                        );
                        return new RandomDogDetailResponse(
                                person.getId(),
                                person.getLogin(),
                                person.isGender(),
                                calculateAge(person.getBirth()),
                                person.getMainAddress(),
                                randomDogProfileDto
                        );
                    }
                    return null;
                })
                // null이 아닌 결과만 선택
                .filter(Objects::nonNull)
                .toList();

        return new RandomDogDetailListResponse(dogs);
    }


    public MatchSearchResponse getPersonProfileWhoPostLike() {
        Person currentUser = authService.findById(SecurityUtil.getCurrentUserId());
        List<Match> matches = matchRepository.findByReceiverId(currentUser.getId());

        List<MatchPersonProfileDto> matchPersonProfileDtoList = matches
                .stream()
                .map(match -> {
                    Long personId = match.getSender().getId();
                    Person person = authService.findById(personId);
                    Dog mainDog = dogService.findByPersonAndMain(person);

                    DogProfileDto dogProfileDto = new DogProfileDto(
                            mainDog.getName(),
                            mainDog.getImage()
                    );
                    return new MatchPersonProfileDto(
                            personId,
                            mainDog.isGender(),
                            dogProfileDto
                    );
                })
                .collect(Collectors.toList());

        return new MatchSearchResponse(matchPersonProfileDtoList);
    }

    public MatchSearchResponse getSuccessMatched() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<Match> matches = matchRepository.findByReceiverIdOrSenderId(currentUserId, currentUserId);

        List<MatchPersonProfileDto> result = new ArrayList<>();

        for (Match sending : matches) {
            Person sendingSender = sending.getSender();
            Person sendingReceiver = sending.getReceiver();
            for (Match received : matches) {
                Person receivedSender = received.getSender();
                Person receivedReceiver = received.getReceiver();

                if (sendingSender.equals(receivedReceiver) && sendingReceiver.equals(receivedSender) &&
                        !sendingReceiver.getId().equals(currentUserId)) {
                    result.add(new MatchPersonProfileDto(
                            receivedSender.getId(),
                            receivedSender.isGender(),
                            new DogProfileDto(
                                    dogService.findByPersonAndMain(receivedSender).getImage(),
                                    dogService.findByPersonAndMain(receivedSender).getName()
                            )
                    ));
                }
            }
        }
        return new MatchSearchResponse(result);
    }


    private int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    @Transactional
    public void likeProfile(Long receiverId) {
        Person sender = authService.findById(SecurityUtil.getCurrentUserId());
        Person receiver = authService.findById(receiverId);
        // 이미 매치된 경우에는 중복 생성하지 않도록 체크
        if (!matchRepository.existsBySenderAndReceiver(sender, receiver)) {
            matchRepository.save(new Match(sender, receiver));
            if (matchRepository.existsBySenderAndReceiver(receiver, sender)){
                matchRepository.delete(findBySenderAndReceiver(sender, receiver));
                matchRepository.delete(findBySenderAndReceiver(receiver, sender));
                chatService.saveChat(receiver, sender);
            }
        }
    }

    private List<TagDto> mapTagsToDto(List<DogTagMap> dogTagMaps) {
        return dogTagMaps.stream()
                .map(dogTagMap -> new TagDto(dogTagMap.getDogTag().getContent()))
                .collect(Collectors.toList());
    }
    public RandomDogDetailResponse getShowingByMatchDetail(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Dog mainDog = dogService.findByPersonAndMain(person);
        return new RandomDogDetailResponse(
                person.getId(),
                person.getLogin(),
                person.isGender(),
                calculateAge(person.getBirth()),
                person.getMainAddress(),
                new RandomDogProfileDto(
                        mainDog.getName(),
                        mainDog.isGender(),
                        mainDog.getImage(),
                        mainDog.getDogType().getContent(),
                        calculateAge(mainDog.getBirth()),
                        mapTagsToDto(mainDog.getDogTagMaps())
                )
        );
    }
    public Match findBySenderAndReceiver(Person sender, Person receiver){
        return matchRepository.findBySenderAndReceiver(sender, receiver).orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
    }

}