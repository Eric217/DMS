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

    private Student leader;

    public boolean check() {
        if (Tools.isNullOrTrimEmp(name, classroom, description))
            return false;
        name = name.trim();
        classroom = classroom.trim();
        if (leader_id != null && leader_id.trim().isEmpty())
            leader_id = null;
        return true;
    }

}
