package src.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

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

    /** 非数据库属性 联表查询*/
    private List<Student> members;

    /** 防恶意插入 */
    public boolean check() {

//        if (type != null) {
            // TODO: - type 得设置仅限几种选择
//        }

        return duration >= 15 * 24 * 3600 && duration <= 120 * 24 * 3600;
    }



}
