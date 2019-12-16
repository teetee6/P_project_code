package org.techtown.myapplication;

public class Nickname {
    String character;
    String nickname;

    public Nickname(String character, String nickname) {
        this.character = character;
        this.nickname = nickname;
    }

    public Nickname(String character) {
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
