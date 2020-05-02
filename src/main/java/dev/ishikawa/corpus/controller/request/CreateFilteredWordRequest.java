package dev.ishikawa.corpus.controller.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateFilteredWordRequest {
    @NotNull
    private String word;
}
