package src.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import src.eric.Tools;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import static src.eric.Constant.ProjectType;

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

    public boolean check() {
        if (Tools.isNullOrEmp(name, aim, lab_name, type))
            return false;

        boolean b = false;
        for (String i: ProjectType)
            if (type.equals(i)) {
                b = true; break;}

        return b && duration >= 15 * 24 * 3600 && duration <= 120 * 24 * 3600;
    }

}
