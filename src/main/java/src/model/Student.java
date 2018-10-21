package src.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Student implements Serializable {

    private String id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private Integer grade;
    private String college;
    private String major;
    private String introduce;
    private Timestamp punish_end;

}

