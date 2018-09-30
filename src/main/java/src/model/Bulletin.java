package src.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Bulletin {

    private Long id;
    private String title;
    private String content;
    private Timestamp time;
    private String from;

}
