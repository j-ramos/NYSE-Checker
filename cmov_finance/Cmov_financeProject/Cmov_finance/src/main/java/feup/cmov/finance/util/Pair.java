package feup.cmov.finance.util;

/**
 * Created by Joel on 25-11-2013.
 */
public class Pair<L,R> {
    private L l;
    private R r;
    public Pair(L l, R r){
        this.l = l;
        this.r = r;
    }
    public L getKey(){ return l; }
    public R getValue(){ return r; }
    public void setKey(L l){ this.l = l; }
    public void setValue(R r){ this.r = r; }
}