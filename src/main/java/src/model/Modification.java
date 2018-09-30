package src.model;

import lombok.Data;

@Data
public class Modification {

    private Long id;

    private Long pid; // unchangeable
    private String name;
    private String description;
    private Integer duration;
    private String coach_id;
    private Integer opt_status;
    private String leader_id;
    private String aim;
    private String type;

    private String members;

}
