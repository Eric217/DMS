package src.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Notification {
    private Long id;
    private String type; // 标题的感觉
    private String content;
    private String from;
    private Timestamp time;

}
