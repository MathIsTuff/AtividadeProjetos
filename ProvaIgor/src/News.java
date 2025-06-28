import com.google.gson.annotations.SerializedName;

public class News {
    public int id;

    @SerializedName("titulo")
    public String title;

    @SerializedName("introducao")
    public String intro;

    @SerializedName("data_publicacao")
    public String publicationDate;

    @SerializedName("link")
    public String link;

    @SerializedName("tipo")
    public String type;

    @Override
    public boolean equals(Object o) {
        if (o instanceof News) {
            return ((News) o).id == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
