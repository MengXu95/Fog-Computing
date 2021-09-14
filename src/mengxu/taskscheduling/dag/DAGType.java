package mengxu.taskscheduling.dag;

public enum DAGType {
    //small
    CyberShake30(0), Epigenomics24(1), Inspiral30(2),
    Montage25(3), Sipht30(4),
    //middle
    CyberShake50(5), Epigenomics46(6), Inspiral50(7),
    Montage50(8), Sipht60(9),
    //large
    CyberShake100(10), Epigenomics100(11), Inspiral100(12),
    Montage100(13), Sipht100(14),
    //huge
    CyberShake1000(15), Epigenomics997(16), Inspiral1000(17),
    Montage1000(18), Sipht1000(19);

    public int value;
    DAGType(int fType){
        this.value = fType;
    }
}
