package src.factory;

import src.service.ProjectService;
import src.service.StudentService;


public class ServiceFactory {

    public static StudentService studentService() {
        return new StudentService();
    }

    public static ProjectService projectService() {
        return new ProjectService();
    }


}
