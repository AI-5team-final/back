package com.example.backend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneToneDTO {
    @JsonProperty("total_score")
    private String totalScore;

    @JsonProperty("resume_score")
    private String resumeScore;

    @JsonProperty("selfintro_score")
    private String selfintroScore;

    @JsonProperty("opinion1")
    private String opinion1;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("eval_resume")
    private String evalResume;

    @JsonProperty("eval_selfintro")
    private String evalSelfintro;
}
