package sideproject.puddy.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DogTagMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "dog_id")
    private Dog dog;
    @ManyToOne
    @JoinColumn(name = "dogTag_id")
    private DogTag dogTag;

    public DogTagMap(Dog dog, DogTag dogTag) {
        this.dog = dog;
        this.dogTag = dogTag;
    }
}
