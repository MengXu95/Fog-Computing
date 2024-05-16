package mengxu.taskscheduling.utils;

public enum FileType {
    NONE(0), INPUT(1), OUTPUT(2);
    public final int value;
    private FileType(int fType){
        this.value = fType;
    }
}
