package src.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import src.eric.Tools;

@Data
@NoArgsConstructor
public class Laboratory {

    private Long id;
    private String name;
    private String classroom;
    private String description;
    private String leader_id;
    private String create_time;

    public boolean check() {
        if (Tools.isNullOrTrimEmp(name, classroom, description))
            return false;
        name = name.trim();
        classroom = classroom.trim();
        return true;
    }

}
