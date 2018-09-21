package src.model.assistance;

import lombok.Data;
import src.model.Admin;
import src.model.Laboratory;
import src.model.Student;

@Data
public class UserTypeModel {
    private int role;
    private Admin admin;
    private Student student;
    private Laboratory lab;
}
