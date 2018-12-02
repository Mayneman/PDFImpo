package impoClasses;

import java.io.File;

public class Config {

    File inputSource;
    File tempSource;
    Integer timeDelay;

    public Config(File inputSource, File tempSource, Integer timeDelay) {
        this.inputSource = inputSource;
        this.tempSource = tempSource;
        this.timeDelay = timeDelay;
    }

    public File getInputSource() {
        return inputSource;
    }

    public void setInputSource(File inputSource) {
        this.inputSource = inputSource;
    }

    public File getTempSource() {
        return tempSource;
    }

    public void setTempSource(File outputSource) {
        this.tempSource = outputSource;
    }

    public Integer getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(Integer timeDelay) {
        this.timeDelay = timeDelay;
    }
}
