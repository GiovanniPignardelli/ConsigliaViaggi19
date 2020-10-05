package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

public class HandshakeResponse{
    private String token;
    private String responseCode;

    public HandshakeResponse(String token, String responseCode) {
        this.token = token;
        this.responseCode = responseCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}