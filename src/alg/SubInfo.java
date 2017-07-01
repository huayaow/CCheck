package alg;

/**
 * Sub CA structure
 */
public class SubInfo {
    private int[] sub_pos ;
    private int sub_tway ;
    private int sub_length ;
    private double sub_cov ;
    
    public SubInfo( int t , int[] p ) {
        this.sub_pos = new int[p.length] ;
        System.arraycopy(p, 0, this.sub_pos, 0, p.length);
        this.sub_tway = t ;
        this.sub_length = p.length ;
        this.sub_cov = -1.0 ;
    }
    
    public int getTway() {
        return this.sub_tway ;
    }
    
    public int[] getPosition() {
        return this.sub_pos ;
    }
    
    public int getLength() {
        return this.sub_length ;
    }
    
    public double getCoverage() {
        return this.sub_cov ;
    }
    
    public void setCoverage( double c ) {
        this.sub_cov = c ;
    }
    
}