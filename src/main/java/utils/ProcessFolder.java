package utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import com.sun.istack.internal.Nullable;
import controller.Controller;
import impoClasses.HotFolderConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;

public class ProcessFolder {

    private File sourceFolder;
    private File destFolder;
    private Integer impoNup;
    private Integer unitQuant;
    private Integer backs;
    private HotFolderConfig hotFolderConfig;
    private Controller parent;
    private boolean hasBeenHashed = false;

    private File tempFolder;
    private String eventPath;

    private File fullTemp;
    private File secondTemp;


    private String fullFolderName;
    private String partFolderName;

    public ProcessFolder(File sourceFolder, HotFolderConfig hotFolderConfig, File tempFolder, String eventPath, Controller parent, Boolean hasBeenHashed) {
        this.sourceFolder = sourceFolder;
        this.destFolder = hotFolderConfig.getOutputFolder();
        this.impoNup = hotFolderConfig.getImpoNup();
        this.unitQuant = hotFolderConfig.getUnitQuant();
        this.backs = hotFolderConfig.getBacks();
        this.hotFolderConfig = hotFolderConfig;
        this.tempFolder = tempFolder;
        this.eventPath = eventPath;
        this.parent = parent;
        this.hasBeenHashed = hasBeenHashed;
    }



    public void run(){
        ArrayList<File> files = getDirContents(sourceFolder);
        if(files.size() == 0){
            try {
                deleteFolder(sourceFolder);
            } catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
        if(!hasBeenHashed){
            hashtagDuplication(files);
            files = getDirContents(sourceFolder);
        }
        if(backs > 1){
            backJob(files);
            return;
        }

        files = getDirContents(sourceFolder);
        ArrayList<Integer> copyNums = findSortIterations(files.size());
        createTempFolders(copyNums);

        if((files.size()%impoNup == 0 || sheetsRequired(copyNums.get(1)).equals(unitQuant)) || hotFolderConfig.getStepAndRepeat()) {
            moveFiles(files);
            files = getDirContents(fullTemp);
            concatPDF(files, fullFolderName);
            cleanup();
        } else {
            moveFilesTwo(copyNums, files);
            if(fullTemp != null) {
                files = getDirContents(fullTemp);
                concatPDF(files, fullFolderName);
            }
            files = getDirContents(secondTemp);
            concatPDF(files, partFolderName);
            cleanup();
        }


    }

    private ArrayList<File> getDirContents(File folder){
        File[] files = folder.listFiles();
        if(files == null){
            try {
                deleteFolder(folder);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        ArrayList<File> finalFiles = new ArrayList<File>();
        for(File file : files){
            if(file.getPath().endsWith(".pdf") && !file.getPath().startsWith(".")){
                finalFiles.add(file);
            }
        }
        return finalFiles;
    }

    private void deleteFolder(File folder) throws IOException {
        Files.walk(folder.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private void hashtagDuplication(ArrayList<File> files) {
        for(File file: files){
            String fileName = file.toPath().toString();
            if(fileName.contains("#")){
                String s = fileName.substring(fileName.indexOf("#") + 1);
                s = s.substring(0, s.lastIndexOf("#"));
                Integer multiply = Integer.parseInt(s);
                for(int i = 2; i <= multiply; i++){
                    String dest = fileName.substring(0,fileName.length()-4) + "(" + i + "of" + multiply + ")" + fileName.substring(fileName.length()-4);
                    try {
                        Files.copy(file.toPath(), new File(dest).toPath());
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                file.renameTo(new File(fileName.substring(0,fileName.length()-4) + "(1of" + multiply + ")" + fileName.substring(fileName.length()-4)));
            }
        }
    }

    private void moveFiles(ArrayList<File> files){
        for(Integer i = 0; i < files.size(); i++){
            String fileName = files.get(i).getPath();
            fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            fileName = fullTemp + File.separator + fileName;
            try {
                Files.copy(files.get(i).toPath(), new File(fileName).toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void moveFilesTwo(ArrayList<Integer> copyNums, ArrayList<File> files){
        Integer iterationNum = 0;
        Integer fullImpNum = 0;
        String fileName;
        while (fullImpNum < copyNums.get(0) && iterationNum < impoNup) { //Full sets of files that fit to the impoNup specs
            String fullTempString = fullTemp.getPath();
            try {
                fileName = files.get(fullImpNum * impoNup + iterationNum).getPath();
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
                fileName = fullTempString + File.separator + fileName;
                Files.copy(files.get(fullImpNum * impoNup + iterationNum).toPath(),new File(fileName).toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
            iterationNum++;
            if (iterationNum >= impoNup) {
                fullImpNum++;
                iterationNum = 0;
            }
        }
        Integer count = 1;
        while (count <= copyNums.get(2)) { //Partial files duplicated to fit the impoNup
            String partTempString = secondTemp.getPath();
            Integer partImpNum = copyNums.get(1);
            while (partImpNum > 0) {
                try {
                    fileName = files.get(copyNums.get(0) * impoNup + partImpNum - 1).getPath();
                    fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
                    fileName = partTempString + File.separator + fileName.substring(0, fileName.length() -4) + count + fileName.substring(fileName.length()-4);
                    Files.copy(files.get(copyNums.get(0) * impoNup + partImpNum - 1).toPath(), new File(fileName).toPath());
                }catch (IOException e) {
                    e.printStackTrace();
                }
                partImpNum--;
            }
            count++;
        }
    }
    
    private void backJob(ArrayList<File> files){
        tempFolder.mkdir();
        for(File file: files){
            try {
                Document document = new Document();
                PdfReader reader = new PdfReader(file.toPath().toString());
                String fileName = file.toPath().toString();
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf('.'));
                PdfCopy copy = new PdfSmartCopy(document, new FileOutputStream(tempFolder + File.separator + fileName + ".pdf"));
                document.open();
                for(int page = 2; page <= backs+1; page++) {
                    PdfImportedPage startPage = copy.getImportedPage(reader, 1);
                    PdfImportedPage importedPage = copy.getImportedPage(reader, page);
                    copy.addPage(startPage);
                    copy.addPage(importedPage);

                }
                document.close();
                reader.close();
            } catch (Exception e){
                System.out.println("Back job error.");
                e.printStackTrace();
            }
        }
        System.out.println("FINISHED BACKS, COMMENCING MERGE PDF");
        parent.newActivity("FINISHED BACKS, COMMENCING MERGE PDF");
        HotFolderConfig tempConfig = new HotFolderConfig(hotFolderConfig);
        System.out.println(tempConfig.getUnitQuant());
        tempConfig.setUnitQuant(tempConfig.getUnitQuant()/tempConfig.getBacks());
        tempConfig.setBacks(1);
        ProcessFolder processFolder = new ProcessFolder(tempFolder, tempConfig,tempFolder, eventPath, this.parent, true);
        processFolder.run();
        try {
            deleteFolder(sourceFolder);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createTempFolders(ArrayList<Integer> copyNums){
        int fullNum;
        int partNum;
        //TEMP
        if (copyNums.get(0) == 0 && sheetsRequired(copyNums.get(1)) < unitQuant && !hotFolderConfig.getStepAndRepeat()) {
            partNum = (int) Math.ceil(sheetsRequired(copyNums.get(1))/(double) hotFolderConfig.getOriginalBacks());
            secondTemp = new File(tempFolder + " run_" + partNum);
            partFolderName = destFolder.toPath() + File.separator + eventPath + " run_" + partNum +".pdf";
            createNewFolder(secondTemp);
        }
        //FULL
        else if (copyNums.get(1) == 0 || sheetsRequired(copyNums.get(1)).equals(unitQuant) || hotFolderConfig.getStepAndRepeat()) {
            fullTemp = new File(tempFolder + " run_" + unitQuant);
            fullFolderName = destFolder.toPath() + File.separator + eventPath + " run_" + unitQuant +".pdf";
            createNewFolder(fullTemp);
        } else {
            //BOTH
            partNum = (int) Math.ceil(sheetsRequired(copyNums.get(1))/(double) hotFolderConfig.getOriginalBacks());
            fullTemp = new File(tempFolder + " run_" + unitQuant);
            fullFolderName = destFolder.toPath() + File.separator + eventPath + " run_" + unitQuant +".pdf";
            createNewFolder(fullTemp);
            secondTemp = new File(tempFolder + " run_" + partNum);
            partFolderName = destFolder.toPath() + File.separator + eventPath + " run_" + partNum +".pdf";
            createNewFolder(secondTemp);
        }

    }

    private void createNewFolder(File file){
        if (!file.exists()) {
            try{
                file.mkdir();
            }
            catch(SecurityException se){
                System.out.println(se);
            }
        }
    }

    private  ArrayList<Integer> findSortIterations(Integer fileLength){
        Integer quotient = fileLength / impoNup;
        quotient = ~~quotient; //Removes decimal
        Integer remainder = fileLength % impoNup;
        Integer copies = 0;
        if(remainder != 0) {
            copies = impoNup / remainder;
            copies = ~~copies; //Removes decimal
        }
        ArrayList<Integer> copyNums = new ArrayList<>();
        copyNums.add(quotient);
        copyNums.add(remainder);
        copyNums.add(copies);
        return copyNums;
    }

    private void concatPDF(ArrayList<File> files, String folder){
        Document document = new Document();
        try{
            PdfCopy copy = new PdfSmartCopy(document, new FileOutputStream(folder));
            document.open();
            for(File file: files){
                PdfReader reader = new PdfReader(file.getCanonicalPath());
                copy.addDocument(reader);
                reader.close();
            }
            document.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Integer sheetsRequired(Integer num){
        double doub =  ((double) unitQuant / (impoNup / num));
        return ((int) Math.ceil(doub));
    }

    private void cleanup(){
        try {
            if (fullTemp != null) {
                deleteFolder(fullTemp);
            }
            if (secondTemp != null) {
                deleteFolder(secondTemp);
            }
            deleteFolder(sourceFolder);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
