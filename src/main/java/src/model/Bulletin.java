package src.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Bulletin {

    private Long id;
    private String title;
    private String content;
    private Timestamp time;
    private Long read_count;
    private String from;

}
