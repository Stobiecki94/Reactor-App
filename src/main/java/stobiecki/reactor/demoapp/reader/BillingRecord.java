package stobiecki.reactor.demoapp.reader;

import lombok.*;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillingRecord {

    private String firstName;
    private String lastName;
    private String type;
    private Date startTime;
    private int duration;

    public Object[] toArrayOfFields(){
        return new Object[]{getFirstName(), getLastName(), getType(), getStartTime(), getDuration()};
    }

    public String toCsvString() {
        return Arrays
                .stream(toArrayOfFields())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}
