package src.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Project implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Timestamp start_time;
    private Timestamp end_time;
    /** seconds */
    private Integer duration;
    private Timestamp submit_time;
    private String coach_id;
    private Integer opt_status;
    private String lab_name;
    private String leader_id;
    private String aim;
    /** 比如 比赛项目 */
    private String type;
    private Integer deleted;

}
