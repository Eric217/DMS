package src.model;

import lombok.Data;

import java.util.List;

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

    /** 以 @ 分隔成员 id 按道理，修改成员也应该维护一张伪参与表，，，还是算了。
     *  不打算用这个属性的（下面替代），但是表里有。。 */
    private String members;

    /** 非数据库属性，为了返回给客户端时方便看到成员名字 */
    private List<Student> member_list;

}
