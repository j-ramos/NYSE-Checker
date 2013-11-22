package feup.cmov.finance.stock;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Tiago on 19-11-2013.
 */
public class Value implements Serializable {

    private Integer value;
    private Date date;


    public void Value(Integer  value, Date date)
    {
        this.value=value;
        this.date=date;
    }

    public Integer getValue() {
        return value;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }
}
