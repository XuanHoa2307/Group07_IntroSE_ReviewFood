package com.example.reviewfood;

public class Report extends ReportID{
    private String reporterID;
    private boolean isNutidy;
    private boolean isViolence;
    private boolean isHarassment;
    private boolean isFalseInfomation;
    private boolean isSpam;
    private boolean isSuicide;
    private boolean isHateSpeech;
    private boolean isTerrorism;
    private boolean isInvolvesAChild;
    private boolean isOther;

    public Report(){
        this.reporterID = "";
        this.isNutidy = false;
        this.isViolence = false;
        this.isHarassment = false;
        this.isFalseInfomation = false;
        this.isSpam = false;
        this.isSuicide = false;
        this.isHateSpeech = false;
        this.isTerrorism = false;
        this.isInvolvesAChild = false;
        this.isOther = false;
    }
    public Report(String reporterID, boolean isNutidy, boolean isViolence, boolean isHarassment, boolean isFalseInfomation, boolean isSpam, boolean isSuicide, boolean isHateSpeech, boolean isTerrorism, boolean isInvolvesAChild, boolean isOther) {
        this.reporterID = reporterID;
        this.isNutidy = isNutidy;
        this.isViolence = isViolence;
        this.isHarassment = isHarassment;
        this.isFalseInfomation = isFalseInfomation;
        this.isSpam = isSpam;
        this.isSuicide = isSuicide;
        this.isHateSpeech = isHateSpeech;
        this.isTerrorism = isTerrorism;
        this.isInvolvesAChild = isInvolvesAChild;
        this.isOther = isOther;
    }

    public String getReporterID() {
        return reporterID;
    }

    public boolean isNutidy() {
        return isNutidy;
    }

    public boolean isViolence() {
        return isViolence;
    }

    public boolean isHarassment() {
        return isHarassment;
    }

    public boolean isFalseInfomation() {
        return isFalseInfomation;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public boolean isSuicide() {
        return isSuicide;
    }

    public boolean isHateSpeech() {
        return isHateSpeech;
    }

    public boolean isTerrorism() {
        return isTerrorism;
    }

    public boolean isInvolvesAChild() {
        return isInvolvesAChild;
    }

    public boolean isOther() {
        return isOther;
    }

    public void setReporterID(String reporterID) {
        this.reporterID = reporterID;
    }

    public void setNutidy(boolean nutidy) {
        isNutidy = nutidy;
    }

    public void setViolence(boolean violence) {
        isViolence = violence;
    }

    public void setHarassment(boolean harassment) {
        isHarassment = harassment;
    }

    public void setFalseInfomation(boolean falseInfomation) {
        isFalseInfomation = falseInfomation;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public void setSuicide(boolean suicide) {
        isSuicide = suicide;
    }

    public void setHateSpeech(boolean hateSpeech) {
        isHateSpeech = hateSpeech;
    }

    public void setTerrorism(boolean terrorism) {
        isTerrorism = terrorism;
    }

    public void setInvolvesAChild(boolean involvesAChild) {
        isInvolvesAChild = involvesAChild;
    }

    public void setOther(boolean other) {
        isOther = other;
    }
}
