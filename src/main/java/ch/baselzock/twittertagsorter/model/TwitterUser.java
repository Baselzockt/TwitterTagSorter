package ch.baselzock.twittertagsorter.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterUser {
    private long id;
    private String name;
    private String description;
    @JsonAlias("protected")
    private boolean protectedAccount;
    private boolean verified;
    @JsonAlias("created_at")
    private String createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonGetter("protected")
    public boolean isProtectedAccount() {
        return protectedAccount;
    }

    @JsonSetter("protected")
    public void setProtectedAccount(boolean protectedAccount) {
        this.protectedAccount = protectedAccount;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @JsonGetter("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonSetter("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
