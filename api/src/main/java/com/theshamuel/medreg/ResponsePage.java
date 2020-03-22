package com.theshamuel.medreg;

import com.theshamuel.medreg.model.baseclasses.entity.BaseEntity;
import java.util.List;

/**
 * The Response class to grid.
 *
 * @author Alex Gladkikh
 */
public class ResponsePage {

    private List data;

    private long pos;

    private long total_count;

    public List<BaseEntity> getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(long total_count) {
        this.total_count = total_count;
    }
}
