package com.zosh.DTO;



public class AiResponse {
    private String reply;     // formatted AI text reply for frontend
    private String raw;       // raw JSON returned from Ollama (for debugging)
    private boolean ok;
    private String error;

    // getters & setters
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public String getRaw() { return raw; }
    public void setRaw(String raw) { this.raw = raw; }
    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
