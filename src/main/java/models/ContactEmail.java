package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class ContactEmail {
        @JsonProperty
        String name;
        @JsonProperty
        String fromEmail;
        @JsonProperty
        String toEmail;
        @JsonProperty
        String message;
        @JsonProperty
        String mailBody;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getFromEmail() {
            return fromEmail;
        }
        public void setFromEmail(String fromEmail) {
            this.fromEmail = fromEmail;
        }
        public String getToEmail() {
            return toEmail;
        }
        public void setToEmail(String toEmail) {
            this.toEmail = toEmail;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public String getMailBody() {
            return getName() + " (" + getFromEmail() + "), has sent the following message:\n" + getMessage();
        }
        public void setMailBody(String mailBody) {
            this.mailBody = mailBody;
        }


    }

