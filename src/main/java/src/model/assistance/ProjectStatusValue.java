package src.model.assistance;

public interface ProjectStatusValue {
    int S_ALL = 0;
    int S_PROCESSING = 3;
    int S_CREATING = 1;
    int S_REJECTED = 2;
    int S_CANCELED = 4;
    int S_COMPLETE = 5;
    int S_OVERTIME = 6;
    int S_REQUESTING = 7; // 实验室管理员看的
}
