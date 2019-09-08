package softagi.firebase6;

public class UserModel
{
    private String email,username,mobilr,address,photo,id;

    public UserModel() {
    }

    public UserModel(String email, String username, String mobilr, String address, String photo, String id) {
        this.email = email;
        this.username = username;
        this.mobilr = mobilr;
        this.address = address;
        this.photo = photo;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobilr() {
        return mobilr;
    }

    public void setMobilr(String mobilr) {
        this.mobilr = mobilr;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
