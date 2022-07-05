package com.interview.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;


@Component
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Getter
@Setter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AllArgsConstructor
@Builder
public class GenericServiceResponse {

    @JsonProperty("status")
    private int status;

    @JsonProperty("timestamp")
    private Timestamp timestamp = new Timestamp(new Date().getTime());

    @JsonProperty("message")
    private String message;

    @JsonProperty("errormessage")
    private String errorMessage;

    @JsonProperty("data")
    private Object data;

    @JsonIgnore
    private Object tempData;

    @JsonProperty("redirectUrl")
    private String redirectUrl;

    @JsonProperty("httpStatus")
    private HttpStatus httpStatus;

    @JsonProperty("totalPages")
    private int totalPages;

    @JsonProperty("totalElements")
    private long totalElements;

    public GenericServiceResponse() {
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.status = httpStatus.value();
        this.httpStatus = httpStatus;
    }
}
