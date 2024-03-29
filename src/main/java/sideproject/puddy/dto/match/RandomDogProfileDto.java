package sideproject.puddy.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import sideproject.puddy.dto.tag.TagDto;

import java.util.List;
@Data
@AllArgsConstructor
public class RandomDogProfileDto {
    private String name;
    private boolean gender;
    private String image;
    private String type;
    private Integer age;
    private List<TagDto> tags;
}
