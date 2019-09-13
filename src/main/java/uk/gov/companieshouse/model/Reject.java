package uk.gov.companieshouse.model;

public class Reject {
    private String[] englishReasons;
    private String[] welshReasons;

    public String[] getEnglishReasons() {
        return englishReasons;
    }

    public void setEnglishReasons(String[] englishReasons) {
        this.englishReasons = englishReasons;
    }

    public String[] getWelshReasons() {
        return welshReasons;
    }

    public void setWelshReasons(String[] welshReasons) {
        this.welshReasons = welshReasons;
    }

}
