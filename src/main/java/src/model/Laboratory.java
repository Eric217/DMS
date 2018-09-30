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
        return !Tools.isNullOrEmp(name, classroom, description);
    }

}
