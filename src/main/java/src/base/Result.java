package src.base;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Result {

    public Result(int status,String message){
        this.status = status;
        this.message = message;
    }

    private Integer status;
    private String message;
    private Object data=null;
}
