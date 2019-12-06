package org.techtown.myapplication.ui.notifications;

public class Voice {
    String nation;      // 동화책 나라.  "국내" "해외"
//    String character;   // 목소리이름.  "엄마" "뽀로로"
    int character;

    public Voice(String nation,Integer character) {
        this.nation = nation;
        this.character = character;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Integer getCharacter() {
        return character;
    }

    public void setCharacter(Integer character) {
        this.character = character;
    }
}
