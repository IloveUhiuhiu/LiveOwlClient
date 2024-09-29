package com.client.liveowl.util;

import javafx.scene.control.Label;

public class ValidatedResponse {
    private Label responseLabel;
    private boolean isValid;

    public Label getResponseLabel() {
        return responseLabel;
    }

    public void setResponseLabel(Label responseLabel) {
        this.responseLabel = responseLabel;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public ValidatedResponse(Label responseLabel, boolean isValid) {
        this.responseLabel = responseLabel;
        this.isValid = isValid;
    }
}
