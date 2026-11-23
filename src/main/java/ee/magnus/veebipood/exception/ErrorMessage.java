package ee.magnus.veebipood.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ErrorMessage {
    //"timestamp": "2026-11-12T17:21:19.5742",
    //"status": 500,
    //"error": "Internal Server Error",
    //"path": "/products"
    private Date timestammp;
    private int status;
    private String error;

}
