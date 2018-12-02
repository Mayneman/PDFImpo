package impoClasses;
import java.io.File;

public class HotFolderConfig {

    private String inputFolder;
    private File outputFolder;
    private Integer unitQuant;
    private Integer impoNup;
    private Integer backs;
    private Integer originalBacks;

    private boolean stepAndRepeat;

    public HotFolderConfig(String inputFolder, File outputFolder, Integer unitQuant, Integer impoNup, Integer backs, boolean stepAndRepeat){
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.unitQuant = unitQuant;
        this.impoNup = impoNup;
        this.backs = backs;
        this.originalBacks = backs;
        this.stepAndRepeat = stepAndRepeat;
    }

    public HotFolderConfig(HotFolderConfig hotFolderConfig){
        this.inputFolder = hotFolderConfig.getInputFolder();
        this.outputFolder = hotFolderConfig.outputFolder;
        this.unitQuant = hotFolderConfig.unitQuant;
        this.impoNup = hotFolderConfig.getImpoNup();
        this.backs = hotFolderConfig.getBacks();
        this.originalBacks = hotFolderConfig.getOriginalBacks();
        this.stepAndRepeat = hotFolderConfig.stepAndRepeat;
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public Integer getUnitQuant() {
        return unitQuant;
    }

    public void setUnitQuant(Integer unitQuant) {
        this.unitQuant = unitQuant;
    }

    public Integer getImpoNup() {
        return impoNup;
    }

    public void setimpoNup(Integer impoNup) {
        this.impoNup = impoNup;
    }

    public Integer getBacks() {return backs;}

    public void setBacks(Integer backs) {this.backs = backs;}

    public boolean getStepAndRepeat() {return stepAndRepeat;}

    public void setStepAndRepeat(boolean stepAndRepeat) {this.stepAndRepeat = stepAndRepeat;}

    public Integer getOriginalBacks() {
        return originalBacks;
    }

    public void setOriginalBacks(Integer oldBacks) {
        this.originalBacks = oldBacks;
    }

}
