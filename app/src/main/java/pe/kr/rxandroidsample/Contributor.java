package pe.kr.rxandroidsample;

import com.google.gson.annotations.SerializedName;

public class Contributor {
    @SerializedName("login")
    public String name;
    public long contributions;
    public int id;
    public String avatar_url;

    public Contributor(){}

    public Contributor(String login, long contributions) {
        this.name = login;
        this.contributions = contributions;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "name='" + name + '\'' +
                ", contributions=" + contributions +
                ", id=" + id +
                ", avatar_url='" + avatar_url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Contributor temp = (Contributor) obj;
        if(temp.name.equals(this.name)) {
            return true;
        }

        return false;
    }
}
