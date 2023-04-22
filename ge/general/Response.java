package com.ge.general;

public class Response  implements java.io.Serializable{
    private String responseMessage;
    private int followingState;

    public Response(String responseMessage, int followingState){
        this.setResponseMessage(responseMessage);
        this.setFollowingState(followingState);
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public int getFollowingState() {
        return followingState;
    }

    public void setFollowingState(int followingState) {
        this.followingState = followingState;
    }
}
