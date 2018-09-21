package src.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Notification {
    private Long id;
    private String type;
    private String content;
    private String from;
    private Timestamp time;

}
