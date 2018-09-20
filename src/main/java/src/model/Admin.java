package src.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Admin {

    private Long id;
    @JsonIgnore
    private String password;

}
